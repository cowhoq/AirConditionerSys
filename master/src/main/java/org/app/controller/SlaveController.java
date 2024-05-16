package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.entity.Request;
import org.app.entity.WorkStatus;
import org.app.service.MasterService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * 控制器类，管理从属设备的请求
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@Slf4j
@RestController
public class SlaveController {
    @Autowired
    private MasterService masterService;


    /**
     * 返回主机的工作模式和工作温度
     */
    @GetMapping(path = "/getMasterWork", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<WorkStatus> getMasterWorkParams() {
        log.info("getMasterWorkParams");
        var status = new WorkStatus();
        status.setWorkmode(masterService.getWorkMode());
        status.setRange(masterService.getRange());
        return R.success(status);
    }


    /**
     * 接收从机请求, 从机给出的每个参数都不应为 null
     */
    @PostMapping("/OnSlaverPower")
    public R<String> slaveRequest(Long roomId, double setTemp, double curTemp, String mode) {
        log.info("从机请求参数: {}, {}, {}, {}", roomId, setTemp, curTemp, mode);
        var request = new Request();
        request.setRoomId(roomId);
        request.setStopTemp(setTemp);
        request.setStartTemp(curTemp);
        request.setFanSpeed(mode);
        if (masterService.slaveRequest(request))
            return R.success(null);
        else
            return R.error(null);
    }


    @PostMapping("/OffSlaverPower")
    public R<Boolean> slavePowerOff(Long roomId) {
        log.info("从机请求关闭: {}", roomId);
        return R.success(masterService.slavePowerOff(roomId));
    }


    /**
     * 检查从机 roomId 是否在可送风集合中
     *
     * @param roomId 从机的 roomId
     * @return 如果在返回 true, 如果不在返回 false
     */
    @PostMapping("/sendAir")
    public R<Boolean> sendAir(Long roomId) {
        log.info("收到来自从机的查询: {}", roomId);
        return R.success(masterService.contains(roomId));
    }
}
