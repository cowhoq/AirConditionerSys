package org.app.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.app.common.R;
import org.app.common.Status;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zfq, czl
 */
@Slf4j
@Service
public class SlaveService {
    @Setter
    public static Long ROOM_ID = null;

    private static final Integer CHANGE_TEMP = 5;  // 房间每秒变化0.4°C

    private static final Integer HIGH_SPEED = 15;  // 高风速每分钟变化1度

    private static final Integer MID_SPEED = 13;  // 中风速每分钟变化0.8度

    private static final Integer LOW_SPEED = 11;  // 低风速每分钟变化0.6度

    private static final Integer NOW_TEMP = 2800; // 室外温度, 室内温度会向其靠拢

    public static final String BASE_URL = "http://10.29.223.8:8080";

    private static int second = 15;

    public static AtomicReference<Status> status = new AtomicReference<>(Status.OFF); // 从机状态

    private final AtomicInteger curTemp = new AtomicInteger(3000); // 当前温度

    private final AtomicInteger setTemp = new AtomicInteger(2400); // 设定温度

    private final AtomicReference<String> mode = new AtomicReference<>("FAST"); // 设定风速

    private final AtomicReference<Boolean> wind = new AtomicReference<>(false);

    public static void setStatus(Status _status) {
        status.set(_status);
    }

    public static Status getStatus() {
        return status.get();
    }

    public void setMode(String _mode) {
        mode.set(_mode);
        powerOn();
    }

    public String getMode() {
        return mode.get();
    }

    public void setSetTemp(Integer _setTemp) {
        setTemp.set(_setTemp);
    }

    public Integer getSetTemp() {
        return setTemp.get();
    }

    public Integer upSetTemp(int temp) {
        return setTemp.addAndGet(temp);
    }

    public Integer getCurTemp() {
        return curTemp.get();
    }

    public Boolean getWind() {
        return wind.get();
    }

    public Boolean  powerOn() {
        var restTemplate = new RestTemplate();
        var requestEntity
                = getRequestEntity(ROOM_ID, setTemp.get(), curTemp.get(), mode.get());
        var response = restTemplate.exchange(BASE_URL + "/OnSlaverPower",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return result.getCode() == 1;
        return false;
    }

    public Boolean powerOff() {
        var restTemplate = new RestTemplate();
        var requestEntity
                = getRequestEntity(ROOM_ID, null, null, null);
        var response = restTemplate.exchange(BASE_URL + "/OffSlaverPower",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return (boolean) result.getData();
        return false;
    }

    /**
     * @return 如果 result 不为 null, 则返回对应的值, 否则返回 null, 表示未获取到
     */
    public Boolean needWind() {
        var restTemplate = new RestTemplate();
        var requestEntity
                = getRequestEntity(ROOM_ID, getSetTemp(), getCurTemp(), getMode());
        var response = restTemplate.exchange(BASE_URL + "/sendAir",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return (boolean) result.getData();
        return null;
    }

    private HttpEntity<MultiValueMap<String, String>>
    getRequestEntity(Long roomId, Integer setTemp, Integer curTemp, String mode) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var requestBody = new LinkedMultiValueMap<String, String>();
        if (roomId != null)
            requestBody.add("roomId", String.valueOf(roomId));
        if (setTemp != null)
            requestBody.add("setTemp", String.valueOf(setTemp));
        if (curTemp != null)
            requestBody.add("curTemp", String.valueOf(curTemp));
        if (mode != null)
            requestBody.add("mode", mode);
        return new HttpEntity<>(requestBody, headers);
    }


    private Boolean checkPowerOff(Integer curTemp, Integer speed) {
        return (curTemp > setTemp.get() && curTemp - speed <= setTemp.get()) ||
                (curTemp < setTemp.get() && curTemp + speed >= setTemp.get());
    }

    private Integer getSpeed(String mode) {
        switch (mode) {
            case "SLOW":
                return LOW_SPEED;
            case "MEDDLE":
                return MID_SPEED;
            case "FAST":
                return HIGH_SPEED;
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void changeCurTemp() {
        if (curTemp.get() <= NOW_TEMP)
            curTemp.addAndGet(CHANGE_TEMP);

        if (curTemp.get() >= NOW_TEMP)
            curTemp.addAndGet(-CHANGE_TEMP);

        // 
        if (status.get().equals(Status.AUTO_OFF)) {
            if (NOW_TEMP - setTemp.get() >= 100 && powerOn()) {
                status.set(Status.ON);
            }
        }

        if (status.get().equals(Status.ON)) {
            // 主机允许送风
            var _wind = needWind();
            if (_wind != null) {
                wind.set(_wind);
                if (_wind) {
                    // 根据设定的风速获取温度变化速度
                    var speed = getSpeed(mode.get());
                    var adjustment = (curTemp.get() > setTemp.get()) ? -speed : speed;
                    curTemp.addAndGet(adjustment);

                    // 检查是否可以关机
                    if (checkPowerOff(curTemp.get(), speed) && powerOff()) {
                        status.set(Status.AUTO_OFF);
                        wind.set(false);
                    }
                    log.info(curTemp + ";" + setTemp + ";" + status + ";" + second);
                }
            } else
                log.info("主机关机或断开连接！");
        }

    }
}

