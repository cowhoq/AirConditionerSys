package org.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 存储, 存储启动了的 slave 的 roomId, 和最新请求时间
     */
    private final Map<Long, LocalDateTime> registeredId = Collections.synchronizedMap(new HashMap<>());

    public void registerId(Long roomId) {
        registeredId.put(roomId, LocalDateTime.now());
    }

    public void unregisterId(Long roomId) {
        registeredId.remove(roomId);
    }

    /**
     * 更新注册状态
     */
    public void updateId(Long roomId) {
        if (this.isRegistered(roomId))
            registeredId.put(roomId, LocalDateTime.now());
    }

    public Boolean isRegistered(Long roomId) {
        return registeredId.containsKey(roomId);
    }

    /**
     * 检查从机的注册状态
     */
    @Scheduled(fixedRateString = "${master.pollingInterval}")
    public void checkRegisteredId() {
        var now = LocalDateTime.now();
        for (var entry : registeredId.entrySet()) {
            // 如果从机状态超时, 则认为从机离线, 删除其请求, 并将其房间使用状态设置为未在使用
            if (ChronoUnit.MINUTES.between(entry.getValue(), now) > EXPIRY_DURATION) {
                var roomId = entry.getKey();
                log.error("从机 {} 超时, 离线", roomId);
                masterService.slavePowerOff(roomId);
                var room = roomService.getById(roomId);
                room.setInuse(false);
                roomService.updateById(room);
                registeredId.remove(entry.getKey());
            }
        }
    }

}
