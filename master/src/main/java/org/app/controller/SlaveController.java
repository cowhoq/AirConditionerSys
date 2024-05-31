package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.aop.CheckLogin;
import org.app.common.R;
import org.app.entity.Request;
import org.app.service.MasterService;
import org.app.service.RoomService;
import org.app.service.SlaveStatusService;
import org.app.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.app.MasterApplication.TEST;

/**
 * 控制器类，管理从属设备的请求
 *
 * @author czl
 * @date 2024-5-15 16:25
 */
@Slf4j
@RestController
@RequestMapping({"/api", ""})
public class SlaveController {
    @Autowired
    private MasterService masterService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SlaveStatusService slaveStatusService;

    @Value("${slave.defaultTemp}")
    private Integer slaveDefaultTemp;

    /**
     * 从机登录请求
     *
     * @return 登录成功后返回从机默认工作温度, 否则返回错误信息
     */
    @PostMapping("/slave-login")
    public R<Integer> login(String name, String password, Long roomId) {
        if (name == null || password == null || roomId == null)
            throw new IllegalArgumentException("登录参数错误");

        log.info("从机请求登录({}), name={}, password={}", roomId, name, password);
        if (!userService.login(name, password))
            return R.error("用户名或密码错误");
        else {
            var room = roomService.getById(roomId);
            if (room == null)
                return R.error("你不是这个房间的主人");
            if (room.getInuse())
                return R.error("房间正在使用");
            room.setInuse(true);
            roomService.updateById(room);
            slaveStatusService.register(roomId);
            log.info("从机({})登录成功", roomId);
            return R.success(slaveDefaultTemp);
        }
    }

    /**
     * 从机关机请求
     */
    @CheckLogin
    @PostMapping("/slave-logout")
    public R<String> logout(Long roomId) {
        var room = roomService.getById(roomId);
        if (room == null)
            return R.error("关机失败, roomId 不存在");
        if (room.getInuse()) {
            room.setInuse(false);
            roomService.updateById(room);
            // 删除请求队列中从机的请求
            masterService.slavePowerOff(roomId);
            slaveStatusService.unregister(roomId);
            log.info("从机({})关机成功", roomId);
            return R.success("关机成功");
        }
        return R.error("关机失败");
    }


    /**
     * 接收从机请求, 从机给出的每个参数都不应为 null
     */
    @CheckLogin
    @PostMapping("/OnSlaverPower")
    public R<String> slaveRequest(Long roomId, Integer setTemp, Integer curTemp, String mode) {
        log.info("从机请求参数: {}, {}, {}, {}", roomId, setTemp, curTemp, mode);
        if (roomId == null || setTemp == null || curTemp == null || mode == null)
            return R.error("参数错误");
        var request = new Request();
        request.setRoomId(roomId);
        request.setStopTemp(setTemp);
        request.setStartTemp(curTemp);
        request.setFanSpeed(mode);
        // 收到从机的新请求时, 记得也要更新一下从机能量和费用
        var list = masterService.slaveRequest(request);
        if (list != null) {
            slaveStatusService.updateSlaveEnergyAndFee(roomId, list.get(0), list.get(1));
            return R.success(null);
        } else {
            log.error("添加请求失败");
            return R.error(null);
        }
    }

    /**
     * 从机暂停送风
     */
    @CheckLogin
    @PostMapping("/OffSlaverPower")
    public R<Boolean> slavePowerOff(Long roomId) {
        log.info("从机请求关闭: {}", roomId);
        var list = masterService.slavePowerOff(roomId);
        if (list != null) {
            slaveStatusService.updateSlaveEnergyAndFee(roomId, list.get(0), list.get(1));
            slaveStatusService.updateId(roomId);
            return R.success(true);
        }
        return R.success(false);
    }


    /**
     * 检查从机 roomId 是否在可送风集合中
     *
     * @param roomId 从机的 roomId
     * @return 如果在返回 true, 如果不在返回 false
     */
    @PostMapping("/sendAir")
    public R<Boolean> sendAir(Long roomId, Integer setTemp, Integer curTemp, String mode) {
        if (roomId == null)
            return R.success(false);
        if (roomId == null || setTemp == null || curTemp == null || mode == null)
            return R.error("参数错误");
        slaveStatusService.updateId(roomId);
        slaveStatusService.updateSlaveStatus(roomId, curTemp, setTemp, "正常", mode);
        var r = masterService.contains(roomId);
        if (TEST)
            log.info("收到来自从机的查询: {}, 查询结果: {}", roomId, r);
        return R.success(r);
    }

    /**
     * 获取当前从机请求费用
     */
    @PostMapping("/slaveFee")
    public R<List<BigDecimal>> slaveFee(Long roomId) {
        if (roomId == null)
            return R.error("参数错误");
        // log.info("从机({})获取当前能量和费用", roomId);
        var current = masterService.getEnergyAndFee(roomId);
        var history = slaveStatusService.getEnergyAndFee(roomId);
        // 如果有历史和现在数据, 则将二者相加,
        // 如果只有历史数据则返回历史数据,
        // 如果只有现在数据则返回现在数据,
        // 如果两者都没有数据则返回 0
        if (current != null && history != null)
            return R.success(new ArrayList<>(List.of(current.get(0).add(history.get(0)), current.get(1).add(history.get(1)))));
        else
            return R.success(Objects.requireNonNullElseGet(current,
                    () -> Objects.requireNonNullElseGet(history,
                            () -> new ArrayList<>(List.of(new BigDecimal(0), new BigDecimal(0))))));
    }
}
