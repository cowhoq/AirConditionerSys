package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.app.common.BaseFunction;

import javax.annotation.PostConstruct;

/**
 * @author hbw
 * @date 2024/5/9 15:46
 */
@Slf4j
@RestController()
public class AirController {
    @PostConstruct
    public void init() {
        roomDictionary.addRoom(0);
    }

    /**
     * 开新房间，将从机状态由 empty 改为 off
     *
     * @return
     */
    @GetMapping("/getNewRoom/{roomId}")
    public String getNewRoom(@PathVariable("roomId") int roomId) throws InterruptedException {
        roomDictionary.setRoomValue(roomId, "acStatus", "off");
        // System.out.println(roomDictionary.getRoomValue(roomId, "curTemp"));
        return "OK";
    }


    /**
     * 获取从机状态
     *
     * @return 返回从机状态
     */
    @GetMapping("/{roomId}/status")
    public Object getStatus(@PathVariable("roomId") int roomId) {
        // TODO: 房间可能不存在, 以后需要增加异常处理
        return roomDictionary.getRoomValue(roomId, "acStatus");
    }

    /**
     * 切换从机状态
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/toggle")
    public Object toggleAC(@PathVariable("roomId") int roomId) {
        // TODO: 房间可能不存在, 以后需要增加异常处理
        Object acStatus = roomDictionary.getRoomValue(roomId, "acStatus");

        if (acStatus.equals("on")) {
            if (BaseFunction.PowerOff((roomId)))
                acStatus = "off";
        } else {
            if (BaseFunction.PowerOn(roomDictionary, roomId))
                acStatus = "on"; // 开机后将房间温度显示到控制面板上
        }
        roomDictionary.setRoomValue(roomId, "acStatus", acStatus);
        return roomDictionary.getRoomValue(roomId, "acStatus");
    }

    // 上一次请求

    /**
     * 修改设定温度，升高一度
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/upSetTemp")
    public String upSetTemp(@PathVariable("roomId") int roomId) throws InterruptedException {
        //asyncService.setRequest(roomId, "up");
        //TODO: 需要将设定温度传给前端,且设定温度不能超过限制
        return "OK";
    }

    /**
     * 修改设定温度，降低一度
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/downSetTemp")
    public String downSetTemp(@PathVariable("roomId") int roomId) throws InterruptedException {
        //asyncService.setRequest(roomId, "down");
        //TODO: 需要将设定温度传给前端,且设定温度不能超过限制
        return "OK";
    }

    /**
     * 修改风速请求：低速
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/lowSpeed")
    public String lowSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "SLOW");
        return "OK";
    }

    /**
     * 修改风速请求：中速
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/midSpeed")
    public String midSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "MEDDLE");
        return "OK";
    }

    /**
     * 修改风速请求：高速
     *
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/highSpeed")
    public String highSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "FAST");
        return "OK";
    }

    @PostMapping("/login")
    public R<String> login(Long roomId, String User, String Password) {
        if (BaseFunction.Cheak_login(roomId, User, Password)) {
            User_roomId = roomId;
            roomDictionary.addRoom(roomId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
