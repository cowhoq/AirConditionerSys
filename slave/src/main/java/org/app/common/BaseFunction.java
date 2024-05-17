package org.app.common;

import lombok.extern.slf4j.Slf4j;
import org.app.controller.AirController;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author hbw
 * @date 2024/5/9 18:25
 */
@Slf4j
@Service
public class BaseFunction {
   /* private static Double UPTEMP = 0.2;  // 房间每秒涨0.4°C
    private static Double highSpeed = 1.0;  // 高风速每分钟变化1度
    private static Double midSpeed = 0.8;  // 中风速每分钟变化0.8度
    private static Double lowSpeed = 0.6;  // 低风速每分钟变化0.6度
    private static double NowTEMP = 40.0;
    public class Constants {
        public static final String BASE_URL = "http://10.29.22.40:8080";
    }
    public static boolean PowerOn(RoomDictionary roomDictionary, int roomId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Long id = (long)roomId;
        requestBody.add("roomId", String.valueOf(id));
        requestBody.add("setTemp", String.valueOf((double) roomDictionary.getRoomValue(roomId, "setTemp")));
        requestBody.add("curTemp", String.valueOf((double) roomDictionary.getRoomValue(roomId, "curTemp")));
        requestBody.add("mode", String.valueOf((String) roomDictionary.getRoomValue(roomId, "mode")));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<R> response = restTemplate.exchange(
                Constants.BASE_URL + "/OnSlaverPower",  // 请求地址
                HttpMethod.POST,                        // 请求方法
                requestEntity,                          // 请求体和请求头
                R.class                              // 返回类型
        );
        HttpStatus statusCode = response.getStatusCode();
        R result = response.getBody();

        return result.getCode() == 1;
    }
    public static boolean PowerOff(int roomId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Long id = (long)roomId;
        requestBody.add("roomId", String.valueOf(id));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<R> response = restTemplate.exchange(
                Constants.BASE_URL + "/OffSlaverPower", // 请求地址
                HttpMethod.POST,                        // 请求方法
                requestEntity,                          // 请求体和请求头
                R.class                              // 返回类型
        );
        HttpStatus statusCode = response.getStatusCode();
        R result = response.getBody();

        if (result != null) {
            return (boolean)result.getData();
        }
        return false;
    }
    public static boolean NeedWind(int roomId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Long id = (long)roomId;
        log.info(String.valueOf(id));
        requestBody.add("roomId", String.valueOf(id));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<R> response = restTemplate.exchange(
                Constants.BASE_URL + "/sendAir", // 请求地址
                HttpMethod.POST,                        // 请求方法
                requestEntity,                          // 请求体和请求头
                R.class                              // 返回类型
        );
        R result = response.getBody();

        if (result != null) {
            return (boolean)result.getData();
        }
        return false;
    }
    private static int second = 15;
    private static boolean stop = false;
    public static double changeTemp(RoomDictionary roomDictionary, double curTemp, int roomId) {
        // off:手动关闭，温度不变
        // autoOff:自动关闭，温度变化
        // on:打开
        double setTemp = (double) roomDictionary.getRoomValue(roomId, "setTemp");
        String mode = (String) roomDictionary.getRoomValue(roomId, "mode");
        log.info(second+";"+stop);
        if (curTemp <= NowTEMP) {
            curTemp += UPTEMP;
        }
        if(curTemp >= NowTEMP) {
            curTemp -= UPTEMP;
        }
        if(roomDictionary.getRoomValue(roomId, "acStatus").equals("off") && stop == true){
            second--;
            if(second == 0){
                stop = false;
                if(BaseFunction.PowerOn(roomDictionary,roomId))
                    roomDictionary.setRoomValue(roomId,"acStatus","on");
            }
        }
        // 空调打开了
        if (roomDictionary.getRoomValue(roomId, "acStatus").equals("on")) {
            // 发送风请求给主机
            if(NeedWind(roomId))
                roomDictionary.setRoomValue(roomId,"wind",true);
            // 降温
            boolean haveWind = (boolean) roomDictionary.getRoomValue(roomId, "wind");
            if (curTemp > setTemp && haveWind) {
                switch (mode) {
                    case "SLOW":
                        curTemp -= lowSpeed;
                        break;
                    case "MEDDLE":
                        curTemp -= midSpeed;
                        break;
                    case "FAST":
                        curTemp -= highSpeed;
                        break;
                }
                if(curTemp <= setTemp){
                    if(PowerOff(roomId)){
                        roomDictionary.setRoomValue(roomId,"acStatus","off");
                        roomDictionary.setRoomValue(roomId,"wind","false");
                        second = 15;
                        stop = true;
                    }
                }
            }
            // 升温
            else if (curTemp < setTemp && haveWind) {
                switch (mode) {
                    case "SLOW":
                        curTemp += lowSpeed;
                        break;
                    case "MEDDLE":
                        curTemp += midSpeed;
                        break;
                    case "FAST":
                        curTemp += highSpeed;
                        break;
                }
                if(curTemp >= setTemp){
                    if(PowerOff(roomId)){
                        roomDictionary.setRoomValue(roomId,"acStatus","off");
                        roomDictionary.setRoomValue(roomId,"wind","false");
                        second = 15;
                        stop = true;
                    }
                }
            }
        }
        return curTemp;
    }

    public static RoomDictionary changeSetTemp(RoomDictionary roomDictionary, int roomId, String mode) {
        double setTemp = (double) roomDictionary.getRoomValue(roomId, "setTemp");
        if (mode.equals("up")) {
            if(setTemp < 25)    roomDictionary.setRoomValue(roomId, "setTemp", setTemp + 1);
        }
        else{
            if(setTemp > 18)    roomDictionary.setRoomValue(roomId, "setTemp", setTemp - 1);
        }
        return roomDictionary;
    }

    public static RoomDictionary changeMode(RoomDictionary roomDictionary, int roomId, String mode) {
        String old_mode = (String) roomDictionary.getRoomValue(roomId,"mode");
        roomDictionary.setRoomValue(roomId, "mode", mode);
        if(!PowerOn(roomDictionary,roomId)){
            roomDictionary.setRoomValue(roomId, "mode", old_mode);
        }
        return roomDictionary;
    }
    public static boolean Cheak_login(int roomId,String User,String Password){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Long id = (long)roomId;
        requestBody.add("roomId", String.valueOf(id));
        requestBody.add("User",User);
        requestBody.add("Password",Password);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                Constants.BASE_URL + "/SlaveLogin", // 请求地址
                HttpMethod.POST,                        // 请求方法
                requestEntity,                          // 请求体和请求头
                Void.class                              // 返回类型
        );
        HttpStatus statusCode = response.getStatusCode();
        return statusCode == HttpStatus.OK;
    }
    @Scheduled(fixedRate = 2000)
    public void getCurTemp() throws InterruptedException {
        int roomId = AirController.User_roomId;
        log.info(String.valueOf(roomDictionary.getRoomValue(roomId,"wind"))+";"+String.valueOf(roomDictionary.getRoomValue(roomId,"curTemp"))+";"+String.valueOf(roomDictionary.getRoomValue(roomId,"acStatus")));
        double curTemp = (double) roomDictionary.getRoomValue(roomId, "curTemp");
        double newCurTemp = BaseFunction.changeTemp(roomDictionary, curTemp, roomId);
        roomDictionary.setRoomValue(roomId, "curTemp", newCurTemp);
        //System.out.println();
    }*/
}


