package org.app.common;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hbw
 * @date 2024/5/9 18:25
 */
public class BaseFunction {
    private static Double UPTEMP = 0.2;  // 房间每秒涨0.4°C
    private static Double highSpeed = 1.0;  // 高风速每分钟变化1度
    private static Double midSpeed = 0.8;  // 中风速每分钟变化0.8度
    private static Double lowSpeed = 0.6;  // 低风速每分钟变化0.6度
    private static double MAXTEMP = 40.0;

    public static boolean PowerOn(RoomDictionary roomDictionary, int roomId) {
        // 获取本地主机的 Ip
        InetAddress localhost;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String IpAddress = localhost.getHostAddress();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Long id = (long)roomId;
        requestBody.add("roomId", String.valueOf(id));
        requestBody.add("setTemp", String.valueOf((double) roomDictionary.getRoomValue(roomId, "setTemp")));
        requestBody.add("curTemp", String.valueOf((double) roomDictionary.getRoomValue(roomId, "curTemp")));
        requestBody.add("mode", String.valueOf((String) roomDictionary.getRoomValue(roomId, "mode")));
        requestBody.add("IpAddress", IpAddress);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://127.0.0.1:8080/OnSlaverPower", // 请求地址
                HttpMethod.POST,                        // 请求方法
                requestEntity,                          // 请求体和请求头
                Void.class                              // 返回类型
        );
        HttpStatus statusCode = response.getStatusCode();
        return statusCode == HttpStatus.OK;
    }
    public static double changeTemp(RoomDictionary roomDictionary, double curTemp, int roomId) {
        // off:手动关闭，温度不变
        // autoOff:自动关闭，温度变化
        // on:打开
        double setTemp = (double) roomDictionary.getRoomValue(roomId, "setTemp");
        String mode = (String) roomDictionary.getRoomValue(roomId, "mode");
        boolean haveWind = (boolean) roomDictionary.getRoomValue(roomId, "wind");

        if (roomDictionary.getRoomValue(roomId, "acStatus").equals("off")&& curTemp <= MAXTEMP) {
            curTemp += UPTEMP;
            // TODO: 发停止送风请求给主机
        }


        // 自动停止后升温
        if (roomDictionary.getRoomValue(roomId, "acStatus").equals("autoOff")) {
            curTemp += UPTEMP;
            if (Math.abs(curTemp - setTemp) >= 1 - 0.0001) // 重新启动
                if(PowerOn(roomDictionary,roomId))
                    roomDictionary.setRoomValue(roomId, "acStatus", "on");
        }
        // 空调打开了
        else if (roomDictionary.getRoomValue(roomId, "acStatus").equals("on")) {
            // 发送风请求给主机
            // 降温
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
                // 跨越设定值
                if (curTemp < setTemp) {
                    roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
                    // TODO: 发停止送风请求给主机
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
                // 跨越设定值
                if (curTemp > setTemp) {
                    roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
                    // TODO: 发停止送风请求给主机
                }
            } else curTemp += UPTEMP;  // 主机与从机冲突
            // 达到目标温度，自动停止
            if (Math.abs(curTemp - setTemp) < 0.0001) {
                roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
                // TODO: 发停止送风请求给主机
            }
        }

        return curTemp;
    }

    public static RoomDictionary changeSetTemp(RoomDictionary roomDictionary, int roomId, String mode) {
        double setTemp = (double) roomDictionary.getRoomValue(roomId, "setTemp");
        if (mode.equals("up"))
            roomDictionary.setRoomValue(roomId, "setTemp", setTemp + 1);
        else
            roomDictionary.setRoomValue(roomId, "setTemp", setTemp - 1);

        return roomDictionary;
    }

    public static RoomDictionary changeMode(RoomDictionary roomDictionary, int roomId, String mode) {
        roomDictionary.setRoomValue(roomId, "mode", mode);
        return roomDictionary;
    }
}


