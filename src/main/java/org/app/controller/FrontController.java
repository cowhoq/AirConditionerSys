package org.app.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.app.aop.CheckWorkMode;
import org.app.common.R;
import org.app.entity.dto.Period;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.app.entity.dto.WorkStatus;
import org.app.entity.dto.SlaveStatus;
import org.app.service.MasterService;
import org.app.service.RequestService;
import org.app.service.SlaveStatusService;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RestController
@RequestMapping({"/api", ""})
public class FrontController {
    @Autowired
    private MasterService masterService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private SlaveStatusService slaveStatusService;

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
    public R<String> powerOff() {
        try {
            masterService.powerOff();
            log.info("主机关机成功");
            return R.success("主机关机成功");
        } catch (Exception e) {
            return R.error("关机失败");
        }
    }

    @CheckWorkMode
    @PostMapping("/setWorkMode")
    public R<String> setWorkMode(@NotBlank String workMode) {
        log.info("设置主机工作模式: {}", workMode);

        if (workMode.equals("HEATING"))
            masterService.setWorkMode(WorkMode.HEATING);
        else if (workMode.equals("REFRIGERATION"))
            masterService.setWorkMode(WorkMode.REFRIGERATION);
        else
            return R.error("参数错误");
        return R.success("设置成功");
    }

    @CheckWorkMode
    @PostMapping("/setRange")
    public R<String> setRange(@NotNull Integer firstValue, @NotNull Integer secondValue) {
        log.info("主机工作的温度范围: {}, {}", firstValue * 100, secondValue * 100);
        var range = new ArrayList<>(List.of(firstValue * 100, secondValue * 100));
        masterService.setRange(range);
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
     * 获取主机工作状态和温度, 主机前端和从机后端共用的函数
     */
    @CheckWorkMode
    @GetMapping("/getWorkStatus")
    public R<WorkStatus> getWorkStatus() {
        var response = new WorkStatus();
        response.setWorkmode(masterService.getWorkMode());
        response.setRange(masterService.getRange());
        return R.success(response);
    }

    /**
     * 获取从机状态列表
     */
    @CheckWorkMode
    @GetMapping("/getSlaveStatus")
    public R<List<SlaveStatus>> getSlaveStatus() {
        var list = slaveStatusService.getSlaveStatusList();
        if (list != null) {
            for (var slaveStatus : list) {
                if (masterService.contains(slaveStatus.getRoomId()))
                    slaveStatus.setWind("送风");
                else
                    slaveStatus.setWind("无风");
            }
            return R.success(list);
        }
        return R.error("没有从机状态可以获取");
    }

    /**
     * 获取房间报表
     */
    @CheckWorkMode
    @GetMapping("/getRoomTable")
    public R<List<Request>> getRoomTable(@NotNull Long roomId) {
        var list = requestService.getRoomRequestList(roomId);
        if (list != null) return R.success(list);
        else return R.error("没有该房间报表！");
    }

    /**
     * 按年月日获取报表
     */
    @CheckWorkMode
    @GetMapping("/getTable")
    public R<List<Request>> getTable(@NotNull String period) {
        if (period == null)
            return R.error("参数错误");
        var list = requestService.getRequestListByPeriod(Period.valueOf(period));
        if (list != null) return R.success(list);
        else return R.error("没有报表！");
    }
}
