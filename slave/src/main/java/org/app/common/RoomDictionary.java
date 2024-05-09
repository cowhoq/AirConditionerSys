package org.app.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hbw
 * @date 2024/5/9 17:08
 */
public class RoomDictionary {
    private Map<Integer, Map<String, Object>> roomDictionary;

    public RoomDictionary() {
        roomDictionary = new HashMap<>();
    }

    public void addRoom(int roomId) {
        roomDictionary.put(roomId, new HashMap<>());
        setRoomValue(roomId, "acStatus", "on");
        setRoomValue(roomId, "curTemp", 26.0);
        setRoomValue(roomId, "setTemp", 46.0);
        setRoomValue(roomId, "mode", "high");
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
