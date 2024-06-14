package org.app.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.app.entity.dto.SlaveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.app.MasterApplication.TEST;

/**
 * 从机状态管理
 */
@Slf4j
@Service
public class SlaveStatusService {
    @Autowired
    private MasterService masterService;

    @Autowired
    private RoomService roomService;

    @Value("${master.EXPIRY_DURATION}")
    private Long EXPIRY_DURATION;

    @Getter
    private final Map<Long, SlaveStatus> slaveStatusMap = new ConcurrentHashMap<>();

    public void register(Long roomId) {
        if (slaveStatusMap.containsKey(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            if (slaveStatus.getStatus().equals("离线") || slaveStatus.getStatus().equals("关机")) {
                slaveStatus.setStatus("正常");
                slaveStatus.setRegisteredTime(LocalDateTime.now());
                slaveStatusMap.put(roomId, slaveStatus);
            }
        } else {
            var slaveStatus = new SlaveStatus(roomId, "正常", LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public void unregister(Long roomId) {
        if (slaveStatusMap.containsKey(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setStatus("关机");
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            // 用户如果登出, 则需要重置费用和能量
            slaveStatus.zero();
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    /**
     * 更新注册状态
     */
    public void updateRegisteredTime(Long roomId) {
        if (this.isRegistered(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public Boolean isRegistered(Long roomId) {
        return slaveStatusMap.containsKey(roomId);
    }

    /**
     * 更新除去能量和费用以外的其他参数
     */
    public void updateSlaveStatus(Long roomId, Integer curTemp, Integer setTemp, String status, String mode) {
        if (this.isRegistered(roomId)) {
            // 如果存在则直接构造一个新的对象
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setCurTemp(curTemp);
            slaveStatus.setSetTemp(setTemp);
            slaveStatus.setStatus(status);
            slaveStatus.setMode(mode);
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    /**
     * 更新能量和费用
     */
    public void updateSlaveEnergyAndFee(Long roomId, BigDecimal energy, BigDecimal fee) {
        if (this.isRegistered(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.addEnergy(energy);
            slaveStatus.addFee(fee);
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            if (TEST)
                log.info("更新从机({}), 当前历史能量和费用为: {}, {}", roomId, slaveStatus.getEnergy(), slaveStatus.getFee());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public List<SlaveStatus> getSlaveStatusList() {
        return new ArrayList<>(slaveStatusMap.values());
    }

    public List<BigDecimal> getEnergyAndFee(Long roomId) {
        var slaveStatus = slaveStatusMap.get(roomId);
        return slaveStatus == null ? null : Arrays.asList(slaveStatus.getEnergy(), slaveStatus.getFee());
    }

    /**
     * 检查从机的注册状态
     */
    @Scheduled(fixedRateString = "${master.pollingInterval}")
    public void checkRegisteredId() {
        var now = LocalDateTime.now();
        // 在 GPT 的建议下改为使用迭代器
        var iterator = slaveStatusMap.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().getStatus().equals("离线"))
                continue;

            if (ChronoUnit.SECONDS.between(entry.getValue().getRegisteredTime(), now) > EXPIRY_DURATION) {
                var roomId = entry.getKey();
                if (entry.getValue().getStatus().equals("关机")) {
                    iterator.remove();
                    continue;
                }
                log.error("从机 {} 超时, 离线", roomId);
                entry.getValue().setStatus("离线");
                var list = masterService.slavePowerOff(roomId);
                if (list != null)
                    this.updateSlaveEnergyAndFee(roomId, list.get(0), list.get(1));

                var room = roomService.getById(roomId);
                room.setInuse(false);
                roomService.updateById(room);
            }
        }
    }
}
