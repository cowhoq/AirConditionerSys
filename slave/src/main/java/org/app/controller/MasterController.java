package org.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器类，管理主机设备的请求
 * @author czl
 * @date 2024-5-15 16:25
 */
@RestController
public class MasterController {
    @PostMapping("/getSlaverTemp")
    public Double getSlaverTemp(@RequestParam int roomId){
        return (double) AirController.roomDictionary.getRoomValue(roomId,"setTemp");
    }

    @PostMapping("/getSlaverMode")
    public Object getSlaverMode(@RequestParam int roomId){
        return AirController.roomDictionary.getRoomValue(roomId,"mode");
    }

    @PostMapping("/getSlaverWind")
    public Object getSlaverWind(@RequestParam int roomId){
        return AirController.roomDictionary.getRoomValue(roomId,"wind");
    }

    @PostMapping("/getcurTemp")
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
