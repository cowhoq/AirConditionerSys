package org.app.controller;

import org.app.common.R;
import org.app.entity.Period;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.app.entity.WorkStatus;
import org.app.service.MasterService;
import org.app.service.RequestService;
import org.app.service.SlaveStatusService;
import org.graalvm.collections.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * 前端交互类
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@Controller
public class frontController {
    @Autowired
    private MasterService masterService;
    @Autowired
    private RequestService requestService;

    /**
     * 前端-主机开机
     */
    @GetMapping("/powerOn")
    public R<Void> powerOn(){
        try {
            masterService.powerOn(null,null);
            return R.success(null);
        }catch (Exception e){
            return R.error("开机失败");
        }
    }

    /**
     * 前端-主机关机
     */
    @GetMapping("/powerOff")
    public R<Void> powerOff(){
        try {
            masterService.powerOff();
            return R.success(null);
        }catch (Exception e){
            return R.error("关机失败");
        }
    }

    /**
     * 前端设置主机工作状态和温度
     *
     */
    @PostMapping("/setWorkStatus")
    public R<Void> setWorkMode(@RequestParam("workMode") WorkMode workMode,
                                            @RequestParam("firstValue") int firstValue,
                                            @RequestParam("secondValue") int secondValue) {
        try {
            var request = new WorkStatus();
            request.setWorkmode(workMode);
            request.setRange(Pair.create(firstValue, secondValue));
            masterService.powerOn(request.getWorkmode(), request.getRange());
            return R.success(null);
        } catch (Exception e) {
            return R.error("设置失败");
        }
    }
    /**
     * 获取主机状态
     */
    @PostMapping("/getWorkStatus")
    public R<WorkStatus> getWork(){
        var response = new WorkStatus();
        response.setWorkmode(masterService.getWorkMode());
        response.setRange(masterService.getRange());
        return R.success(response);
    }

    /**
     * 获取从机状态
     *
     */
    @GetMapping("/getSlaveStatus")
    public R<List<Request>> getAllSlaveStatus() {
        var list = masterService.getSlaveList();
        if(list !=null)
            return R.success(list);
        return R.error("没有从机状态可以获取");
    }

    /**
     * 获取房间报表
     */
    @GetMapping("/getRoomTable")
    public R<List<Request>> getRoomTable(Long roomId){
        var list = requestService.getRoomRequestList(roomId);
        if(list != null)    return R.success(list);
        else return R.error("没有该房间报表！");
    }

    /**
     * 按年月日获取报表
     */
    @GetMapping("/getTable")
    public R<List<Request>> getTable(Period period){
        var list = requestService.getRequestListByPeriod(period);
        if(list != null)    return R.success(list);
        else return R.error("没有报表！");
    }
}
