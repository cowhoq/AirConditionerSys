package org.app.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hbw
 * @date 2024/5/9 17:08
 */
public class RoomDictionary {
    private final Map<Integer, Map<String, Object>> roomDictionary;

    public RoomDictionary() {
        roomDictionary = new HashMap<>();
    }

    public void addRoom(int roomId) {
        roomDictionary.put(roomId, new HashMap<>());
        // empty:房间空置，开房后empty->off，温度开始变化，empty状态下温度不变
        // off:手动关闭，温度变化
        // autoOff:自动关闭，温度变化
        // on:打开
        setRoomValue(roomId, "acStatus", "empty");
        setRoomValue(roomId, "curTemp", 30.0);
        setRoomValue(roomId, "setTemp", 24.0);
        setRoomValue(roomId, "mode", "FAST");
        // TODO:需要发送风请求给主机，根据主机的反馈判断是否有风
        setRoomValue(roomId, "wind", false);  // 是否有风
    }

    public void setRoomValue(int roomId, String key, Object value) {
        if (roomDictionary.containsKey(roomId)) {
            roomDictionary.get(roomId).put(key, value);
        } else {
            System.out.println("Room not found!");
        }
    }

    public Object getRoomValue(int roomId, String key) {
        if (roomDictionary.containsKey(roomId)) {
            return roomDictionary.get(roomId).get(key);
        } else {
            System.out.println("Room not found!");
            return null;
        }
    }
}