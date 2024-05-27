package org.app.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.app.entity.dto.SlaveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            var slaveStatus = new SlaveStatus(roomId, LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public void unregister(Long roomId) {
        if (slaveStatusMap.containsKey(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setMode("关机");
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    /**
     * 更新注册状态
     */
    public void updateId(Long roomId) {
        if (this.isRegistered(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setRegisteredTime(LocalDateTime.now());
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public Boolean isRegistered(Long roomId) {
        return slaveStatusMap.containsKey(roomId);
    }

    public void updateSlaveStatus(Long roomId, Integer curTemp, Integer setTemp, String status, String mode) {
        if (this.isRegistered(roomId)) {
            var slaveStatus = slaveStatusMap.get(roomId);
            slaveStatus.setCurTemp(curTemp);
            slaveStatus.setSetTemp(setTemp);
            slaveStatus.setStatus(status);
            slaveStatus.setMode(mode);
            slaveStatusMap.put(roomId, slaveStatus);
        }
    }

    public List<SlaveStatus> getSlaveStatusList() {
        return new ArrayList<>(slaveStatusMap.values());
    }

    /**
     * 检查从机的注册状态
     */
    @Scheduled(fixedRateString = "${master.pollingInterval}")
    public void checkRegisteredId() {
        var now = LocalDateTime.now();
        for (var entry : slaveStatusMap.entrySet()) {
            // 如果从机状态超时, 则认为从机离线, 删除其请求, 并将其房间使用状态设置为未在使用
            if (ChronoUnit.MINUTES.between(entry.getValue().getRegisteredTime(), now) > EXPIRY_DURATION) {
                var roomId = entry.getKey();
                if (entry.getValue().getStatus().equals("关机")) {
                    slaveStatusMap.remove(roomId);
                    continue;
                }
                log.error("从机 {} 超时, 离线", roomId);
                entry.getValue().setStatus("离线");
                masterService.slavePowerOff(roomId);
                var room = roomService.getById(roomId);
                room.setInuse(false);
                roomService.updateById(room);
            }
        }
    }

}
