package org.app.controller;

import org.app.entity.WorkMode;
import org.app.entity.WorkStatus;
import org.app.service.MasterService;
import org.graalvm.collections.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 前端交互类
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@Controller
public class FrontController {
    @Autowired
    private MasterService masterService;


    /**
     * 前端-主机开机
     */
    @GetMapping("/powerOn")
    public ResponseEntity<Void> powerOn(){
        try {
            masterService.powerOn(null,null);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 前端-主机关机
     */
    @GetMapping("/powerOff")
    public ResponseEntity<Void> powerOff(){
        try {
            masterService.powerOff();
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 前端设置主机工作状态和温度
     *
     * @param workMode
     * @param firstValue
     * @param secondValue
     * @return
     */
    @PostMapping("/setWorkMode")
    public ResponseEntity<Void> setWorkMode(@RequestParam("workMode") WorkMode workMode,
                                            @RequestParam("firstValue") int firstValue,
                                            @RequestParam("secondValue") int secondValue) {
        try {
            var request = new WorkStatus();
            request.setWorkmode(workMode);
            request.setRange(Pair.create(firstValue, secondValue));
            masterService.powerOn(request.getWorkmode(), request.getRange());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
