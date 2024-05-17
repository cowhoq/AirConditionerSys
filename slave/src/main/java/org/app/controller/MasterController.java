package org.app.controller;

import org.app.common.R;
import org.app.service.SlaveService;
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
    /**
     * 获取设定温度
     * @return
     */
    @PostMapping("/setTemp")
    public R<Double> getSlaverTemp(){
        return R.success(SlaveService.getSetTemp());
    }

    /**
     * 获取当前风速
     * @return
     */
    @PostMapping("/Mode")
    public R<String> getSlaverMode(){
        return R.success(SlaveService.getMode());
    }

/*    @PostMapping("/Wind")
    public Object getSlaverWind(){
        return AirController.roomDictionary.getRoomValue(roomId,"wind");
    }*/

    /**
     * 获取当前房间温度
     * @return
     */
    @PostMapping("/curTemp")
    public R<Double> getcurTemp(){
        return R.success(SlaveService.getCurTemp());
    }
}
