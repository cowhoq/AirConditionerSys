package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.entity.Request;
import org.app.entity.WorkStatus;
import org.app.service.MasterService;
import org.app.service.RoomService;
import org.app.service.UserService;
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

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    /**
     * 从机登录请求
     */
    @PostMapping("/slave-login")
    public R<String> login(String name, String password, Long roomId) {
        log.info("从机登录({}), name={}, password={}", roomId, name, password);
        if (userService.login(name, password))
            return R.error("用户名或密码错误");
        else {
            var room = roomService.getById(roomId);
            if (room == null)
                return R.error("你不是这个房间的主人");
            if (room.getInuse())
                return R.error("房间正在使用");
            room.setInuse(true);
            roomService.updateById(room);
            return R.success("开启成功");
        }
    }

    /**
     * 从机关机请求
     */
    @PostMapping("/slave-logout")
    public R<String> logout(Long roomId) {
        var room = roomService.getById(roomId);
        if (room == null)
            return R.error("关机失败");
        if (room.getInuse()) {
            room.setInuse(false);
            roomService.updateById(room);
            // 删除请求队列中从机的请求
            this.slavePowerOff(roomId);
            return R.success("关机成功");
        }
        return R.error("关机失败");
    }

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
    public R<String> slaveRequest(Long roomId, Integer setTemp, Integer curTemp, String mode) {
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

    /**
     * 从机暂停送风
     */
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
        if (roomId == null)
            return R.success(false);
        return R.success(masterService.contains(roomId));
    }
}
