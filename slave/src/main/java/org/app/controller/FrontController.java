package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.common.Status;
import org.app.service.SlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zfq
 */
@Slf4j
@RestController
public class FrontController {
    @Autowired
    SlaveService slaveService;

    /**
     * 获取从机状态
     */
    @GetMapping("/status")
    public R<Status> getStatus() {
        return R.success(SlaveService.getStatus());
    }

    /**
     * 从机开机
     */
    @PostMapping("/PowerOn")
    public R<Status> powerOn() {
        slaveService.setStatus(Status.ON);
        slaveService.powerOn();
        return R.success(slaveService.getStatus());
    }

    /**
     * 从机关机
     */
    @PostMapping("PowerOff")
    public R<Status> powerOff() {
        SlaveService.setStatus(Status.OFF);
        slaveService.powerOff();
        return R.success(SlaveService.getStatus());
    }

    /**
     * 修改设定温度，升高一度
     */
    @PostMapping("/upSetTemp")
    public R<Integer> upSetTemp() {
        return R.success(slaveService.upSetTemp(100));
    }

    /**
     * 降低一度
     */
    @PostMapping("/downSetTemp")
    public R<Integer> downSetTemp() {
        return R.success(slaveService.upSetTemp(-100));
    }

    /**
     * 修改风速
     */
    @PostMapping("/changeSpeed")
    public R<String> changeSpeed(String newSpeed) {
        slaveService.setMode(newSpeed);
        return R.success(newSpeed);
    }
}
