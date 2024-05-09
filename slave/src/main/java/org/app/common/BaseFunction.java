package org.app.common;

import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * @author hbw
 * @date 2024/5/9 18:25
 */
public class BaseFunction {
    private static Double UPTEMP = 0.2;  // 房间每秒涨0.4°C
    private static Double highSpeed = 1.0;  // 高风速每分钟变化1度
    private static Double midSpeed = 0.8;  // 中风速每分钟变化0.8度
    private static Double lowSpeed = 0.6;  // 低风速每分钟变化0.6度

    public static double changeTemp(RoomDictionary roomDictionary, double curTemp, int roomId) {
        // off:手动关闭，温度不变
        // autoOff:自动关闭，温度变化
        // on:打开
        double setTemp = (double) roomDictionary.getRoomValue(roomId, "setTemp");
        String mode = (String) roomDictionary.getRoomValue(roomId, "mode");


        // 自动停止后升温
        if (roomDictionary.getRoomValue(roomId, "acStatus").equals("autoOff")) {
            curTemp += UPTEMP;
            roomDictionary.setRoomValue(roomId, "curTemp", curTemp);
            if (Math.abs(curTemp - setTemp) >= 1 - 0.0001) // 重新启动
                roomDictionary.setRoomValue(roomId, "acStatus", "on");
        }
        // 空调打开了
        else if (roomDictionary.getRoomValue(roomId, "acStatus").equals("on")) {
            // 降温
            if (curTemp > setTemp) {
                switch (mode) {
                    case "low":
                        curTemp -= lowSpeed;
                        break;
                    case "mid":
                        curTemp -= midSpeed;
                        break;
                    case "high":
                        curTemp -= highSpeed;
                        break;
                }
                // 跨越设定值
                if (curTemp < setTemp) roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
            }
            // 升温
            else if (curTemp < setTemp) {
                switch (mode) {
                    case "low":
                        curTemp += lowSpeed;
                        break;
                    case "mid":
                        curTemp += midSpeed;
                        break;
                    case "high":
                        curTemp += highSpeed;
                        break;
                }
                // 跨越设定值
                if (curTemp > setTemp) roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
            }
            // 达到目标温度，自动停止
            if (Math.abs(curTemp - setTemp) < 0.0001) roomDictionary.setRoomValue(roomId, "acStatus", "autoOff");
        }

        return curTemp;
    }
}
