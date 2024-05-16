package org.app.controller;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.entity.WorkMode;
import org.app.service.MasterService;
import org.graalvm.collections.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;

/**
 * 前端交互类
 * @author czl
 * @date 2024-5-15 16:25
 */
@Controller
public class frontController {
    @Autowired
    private MasterService masterService;
    @Getter
    @Setter
    @NoArgsConstructor
    public class WorkStatus {
        private WorkMode workmode;
        private Pair<Integer, Integer> range;
    }

    /**
     * 前端-主机开机
     * @return
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
     * @return
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
     * @param workMode
     * @param firstValue
     * @param secondValue
     * @return
     */
    @PostMapping("/setWorkMode")
    public ResponseEntity<Void> setWorkMode(@RequestParam("workMode") WorkMode workMode,
                                            @RequestParam("firstValue") int firstValue,
                                            @RequestParam("secondValue") int secondValue){
        try{
            WorkStatus request = new WorkStatus();
            request.workmode = workMode;
            request.range = Pair.create(firstValue,secondValue);
            masterService.powerOn(request.workmode,request.range);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
