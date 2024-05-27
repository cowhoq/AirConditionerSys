package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.entity.Period;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.app.entity.WorkStatus;
import org.app.service.MasterService;
import org.app.service.RequestService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端交互类
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@Slf4j
@RestController
public class FrontController {
    @Autowired
    private MasterService masterService;
    @Autowired
    private RequestService requestService;

    /**
     * 前端-主机开机
     */
    @GetMapping("/powerOn")
    public R<String> powerOn() {
        try {
            masterService.powerOn(null, null);
            return R.success("启动成功");
        } catch (Exception e) {
            return R.error("开机失败");
        }
    }

    /**
     * 前端-主机关机
     */
    @GetMapping("/powerOff")
    public R<Void> powerOff() {
        try {
            masterService.powerOff();
            return R.success(null);
        } catch (Exception e) {
            return R.error("关机失败");
        }
    }

    /**
     * 前端设置主机工作状态和温度
     */
    @PostMapping("/setWorkStatus")
    public R<String> setWorkMode(String workMode, Integer firstValue, Integer secondValue) {
        log.info("收到的参数: {}, {}, {}", workMode, firstValue, secondValue);
        if (workMode == null || firstValue == null || secondValue == null)
            return R.error("参数错误");

        var request = new WorkStatus();
        if (workMode.equals("HEATING"))
            request.setWorkmode(WorkMode.HEATING);
        else if (workMode.equals("REFRIGERATION"))
            request.setWorkmode(WorkMode.REFRIGERATION);
        else
            return R.error("参数错误");

        var range = new ArrayList<>(List.of(firstValue, secondValue));
        request.setRange(range);
        masterService.powerOn(request.getWorkmode(), request.getRange());
        return R.success("设置成功");
    }

    /**
     * 获取主机工作状态
     */
    @GetMapping("/getWorKMode")
    public R<String> getWorkMode() {
        return R.success(masterService.getWorkMode().toString());
    }


    /**
     * 获取主机工作状态和温度
     */
    @GetMapping("/getWorkStatus")
    public R<WorkStatus> getWorkStatus() {
        var response = new WorkStatus();
        response.setWorkmode(masterService.getWorkMode());
        response.setRange(masterService.getRange());
        return R.success(response);
    }

    /**
     * 获取从机状态
     */
    @GetMapping("/getSlaveStatus")
    public R<List<Request>> getAllSlaveStatus() {
        var list = masterService.getSlaveList();
        if (list != null)
            return R.success(list);
        return R.error("没有从机状态可以获取");
    }

    /**
     * 获取房间报表
     */
    @GetMapping("/getRoomTable")
    public R<List<Request>> getRoomTable(Long roomId) {
        var list = requestService.getRoomRequestList(roomId);
        if (list != null) return R.success(list);
        else return R.error("没有该房间报表！");
    }

    /**
     * 按年月日获取报表
     */
    @GetMapping("/getTable")
    public R<List<Request>> getTable(Period period) {
        var list = requestService.getRequestListByPeriod(period);
        if (list != null) return R.success(list);
        else return R.error("没有报表！");
    }
}
