package org.app.service;

import org.app.common.BaseFunction;
import org.app.controller.AirController;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;
import static org.app.controller.AirController.roomDictionary;

/**
 * @author hbw
 * @date 2024/5/11 20:55
 */
@Service
public class AsyncService {

/*    @Async
    public void setRequest(int roomId, String type) throws InterruptedException {
        if (AirController.lastTime[roomId] == null) {
            AirController.lastTime[roomId] = type;
            sleep(3000);
            roomDictionary = BaseFunction.changeSetTemp(roomDictionary, roomId, AirController.lastTime[roomId]);
            AirController.lastTime[roomId] = null;
            // System.out.println(roomDictionary.getRoomValue(roomId, "setTemp"));
        } else AirController.lastTime[roomId] = type;

    }*/
}