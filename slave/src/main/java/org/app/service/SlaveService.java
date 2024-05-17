package org.app.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.app.common.BaseFunction;
import org.app.common.R;
import org.app.common.Status;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SlaveService {
    @Setter
    public static Long ROOM_ID = null;

    private static final Double CHANGE_TEMP = 0.2;  // 房间每秒变化0.4°C

    private static final Double HIGH_SPEED = 1.0;  // 高风速每分钟变化1度

    private static final Double MID_SPEED = 0.8;  // 中风速每分钟变化0.8度

    private static final Double LOW_SPEED = 0.6;  // 低风速每分钟变化0.6度

    private static final Double NOW_TEMP = 40.0; // 室外温度, 室内温度会向其靠拢

    public static final String BASE_URL = "http://10.29.22.40:8080";

    @Getter
    public static Status status; // 从机状态

    private static int second = 15;

    private static Double curTemp = 30.0; // 当前温度

    @Getter
    @Setter
    private static Double setTemp = 24.0; // 设定温度

    @Getter
    @Setter
    private static String mode; // 设定风速


    public Boolean powerOn() {
        var restTemplate = new RestTemplate();
        var requestEntity = getRequestEntity(ROOM_ID, setTemp, curTemp, mode);
        var response = restTemplate.exchange(BASE_URL + "/OnSlaverPower",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return result.getCode() == 1;
        return false;
    }

    public Boolean powerOff() {
        var restTemplate = new RestTemplate();
        var requestEntity = getRequestEntity(ROOM_ID, null, null, null);
        var response = restTemplate.exchange(BaseFunction.Constants.BASE_URL + "/OffSlaverPower",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return (boolean) result.getData();
        return false;
    }

    public Boolean needWind() {
        var restTemplate = new RestTemplate();
        var requestEntity = getRequestEntity(ROOM_ID, null, null, null);
        var response = restTemplate.exchange(BaseFunction.Constants.BASE_URL + "/sendAir",
                HttpMethod.POST, requestEntity, R.class);
        var result = response.getBody();
        if (result != null)
            return (boolean) result.getData();
        return false;
    }

    private HttpEntity<Map<String, String>> getRequestEntity(Long roomId, Double setTemp,
                                                             Double curTemp, String mode) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var requestBody = new HashMap<String, String>();
        if (roomId != null)
            requestBody.put("roomId", String.valueOf(roomId));
        if (setTemp != null)
            requestBody.put("setTemp", String.valueOf(setTemp));
        if (curTemp != null)
            requestBody.put("curTemp", String.valueOf(curTemp));
        if (mode != null)
            requestBody.put("mode", mode);
        return new HttpEntity<>(requestBody, headers);
    }


    private Boolean checkPowerOff(Double curTemp, Double speed) {
        return (curTemp > setTemp && curTemp - speed <= setTemp) ||
                (curTemp < setTemp && curTemp + speed >= setTemp);
    }

    private Double getSpeed(String mode) {
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

    @Scheduled(fixedRate = 2000)
    public void getCurTemp() {
        if (curTemp <= NOW_TEMP)
            curTemp += CHANGE_TEMP;

        if (curTemp >= NOW_TEMP)
            curTemp -= CHANGE_TEMP;

        // 如果是自动停机, 则等待 15 下(因为计数单位不一定是秒, 所以使用 "下" 这个字)
        if (status.equals(Status.AUTO_OFF)) {
            second--;
            if (second == 0 && powerOn()) {
                status = Status.ON;
                powerOn(); // 发送新的请求
            }
        }

        if (status.equals(Status.ON)) {
            // 主机允许送风
            if (needWind()) {
                // 根据设定的风速获取温度变化速度
                Double speed = getSpeed(mode);
                if (!curTemp.equals(setTemp)) {
                    curTemp += (curTemp > setTemp) ? -speed : speed;

                    // 检查是否可以关机
                    if (checkPowerOff(curTemp, speed) && powerOff()) {
                        status = Status.AUTO_OFF;
                        second = 15; // 等待 15 下才能再开机
                    }
                }
            }
        }
    }
}

