package org.app.controller;

import org.app.common.R;
import org.app.service.SlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器类，管理主机设备的请求
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@RestController
public class MasterController {
    @Autowired
    SlaveService slaveService;

    /**
     * 获取设定温度
     */
    @PostMapping("/setTemp")
    public R<Integer> getSlaverTemp() {
        return R.success(slaveService.getSetTemp());
    }

    /**
     * 获取当前风速
     */
    @PostMapping("/Mode")
    public R<String> getSlaverMode() {
        return R.success(slaveService.getMode());
    }

    /**
     * 获取当前房间温度
     */
    @PostMapping("/curTemp")
    public R<Integer> getCurTemp() {
        return R.success(slaveService.getCurTemp());
    }
}
