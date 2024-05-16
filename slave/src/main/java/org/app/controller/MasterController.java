package org.app.controller;

import org.app.common.RoomDictionary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.app.controller.AirController;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 控制器类，管理主机设备的请求
 * @author czl
 * @date 2024-5-15 16:25
 */
@Controller
public class MasterController {
    @PostMapping("/getSlaverTemp")
    @ResponseBody
    public Double getSlaverTemp(@RequestParam int roomId){
        double t =(double) AirController.roomDictionary.getRoomValue(roomId,"setTemp");
        return t;
    }
    @PostMapping("/getSlaverMode")
    @ResponseBody
    public Object getSlaverMode(@RequestParam int roomId){
        return AirController.roomDictionary.getRoomValue(roomId,"mode");
    }
    @PostMapping("/getSlaverWind")
    @ResponseBody
    public Object getSlaverWind(@RequestParam int roomId){
        return AirController.roomDictionary.getRoomValue(roomId,"wind");
    }
    @PostMapping("/getcurTemp")
    @ResponseBody
    public Object getcurTemp(@RequestParam int roomId){
        return AirController.roomDictionary.getRoomValue(roomId,"curTemp");
    }
    @GetMapping("/send")
    public ResponseEntity<Void> SendWind(){
        if(AirController.roomDictionary.getRoomValue(1,"acStatus") == "on"){
            AirController.roomDictionary.setRoomValue(1,"wind",true);
            return ResponseEntity.ok().build();
        }
        else return ResponseEntity.badRequest().build();
    }
}
