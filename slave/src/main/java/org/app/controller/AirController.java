package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.app.common.BaseFunction;
import org.app.common.RoomDictionary;

import javax.annotation.PostConstruct;

import static java.lang.Thread.sleep;

/**
 * @author hbw
 * @date 2024/5/9 15:46
 */
@Slf4j
@RestController()
public class AirController {
    public RoomDictionary roomDictionary = new RoomDictionary();

    //TODO： 主机模式，cold为制冷，hot为供暖，需要通过通信获得
    public String masterMode = "cold";


    @PostConstruct
    public void init() {
        // 启动后初始化五个房间
        roomDictionary.addRoom(1);
        roomDictionary.addRoom(2);
        roomDictionary.addRoom(3);
        roomDictionary.addRoom(4);
        roomDictionary.addRoom(5);
    }

    /**
     * 开新房间，将从机状态由 empty 改为 off
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
     * @return 返回从机状态
     */
    @GetMapping("/{roomId}/status")
    public Object getStatus(@PathVariable("roomId") int roomId) {
        // TODO: 房间可能不存在, 以后需要增加异常处理
        return roomDictionary.getRoomValue(roomId, "acStatus");
    }

    /**
     * 切换从机状态
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/toggle")
    public Object toggleAC(@PathVariable("roomId") int roomId) {
        // TODO: 房间可能不存在, 以后需要增加异常处理
        Object acStatus = roomDictionary.getRoomValue(roomId, "acStatus");
        if (acStatus.equals("on")) {
            acStatus = "off";
        }else acStatus = "on"; // 开机后将房间温度显示到控制面板上
        roomDictionary.setRoomValue(roomId, "acStatus", acStatus);
        return roomDictionary.getRoomValue(roomId, "acStatus");
    }

    /**
     * 修改设定温度，升高一度
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/upSetTemp")
    public String upSetTemp(@PathVariable("roomId") int roomId) {
        //TODO: 需要将设定温度传给前端,且设定温度不能超过限制
        roomDictionary = BaseFunction.changeSetTemp(roomDictionary, roomId, "up");
        return "OK";
    }

    /**
     * 修改设定温度，降低一度
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/downSetTemp")
    public String downSetTemp(@PathVariable("roomId") int roomId) {
        //TODO: 需要将设定温度传给前端,且设定温度不能超过限制
        roomDictionary = BaseFunction.changeSetTemp(roomDictionary, roomId, "down");
        return "OK";
    }

    /**
     * 修改风速请求：低速
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/lowSpeed")
    public String lowSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "low");
        return "OK";
    }

    /**
     * 修改风速请求：中速
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/midSpeed")
    public String midSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "mid");
        return "OK";
    }

    /**
     * 修改风速请求：高速
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/highSpeed")
    public String highSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "high");
        return "OK";
    }


    @Scheduled(fixedRate = 2000)
    public void getCurTemp() throws InterruptedException {
        // 假设有5个房间,房间温度开房才变化
        for (int roomId = 1; roomId <= 5; roomId++) {
            double curTemp = (double) roomDictionary.getRoomValue(roomId, "curTemp");
            System.out.print(roomId + ":" + String.format("%.1f", curTemp) + "\t");
            double newCurTemp = BaseFunction.changeTemp(roomDictionary, curTemp, roomId, masterMode);
            roomDictionary.setRoomValue(roomId, "curTemp", newCurTemp);
        }
        System.out.println();
    }



}
