package org.app.controller;
import lombok.extern.slf4j.Slf4j;
//import org.app.common.R;
import org.app.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.app.common.BaseFunction;
import org.app.common.RoomDictionary;
import javax.annotation.PostConstruct;

/**
 * @author hbw
 * @date 2024/5/9 15:46
 */
@Slf4j
@RestController()
public class AirController {

    @Autowired
    private AsyncService asyncService;

    public static RoomDictionary roomDictionary = new RoomDictionary();
    public static Integer User_roomId= 0;


    @PostConstruct
    public void init() {
        roomDictionary.addRoom(0);
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
            if(BaseFunction.PowerOff((roomId)))
                acStatus = "off";
        }else
        {
            if(BaseFunction.PowerOn(roomDictionary,roomId))
                acStatus = "on"; // 开机后将房间温度显示到控制面板上
        }
        roomDictionary.setRoomValue(roomId, "acStatus", acStatus);
        return roomDictionary.getRoomValue(roomId, "acStatus");
    }

        // 上一次请求

    /**
     * 修改设定温度，升高一度
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
     * @return 返回从机状态
     */
    @PostMapping(value = "/{roomId}/highSpeed")
    public String highSpeed(@PathVariable("roomId") int roomId) {
        //TODO: 需要将风速请求传给前端
        roomDictionary = BaseFunction.changeMode(roomDictionary, roomId, "FAST");
        return "OK";
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam int roomId,
                                      @RequestParam String User,
                                      @RequestParam String Password){
        if(BaseFunction.Cheak_login(roomId,User,Password)){
            User_roomId = roomId;
            roomDictionary.addRoom(roomId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
