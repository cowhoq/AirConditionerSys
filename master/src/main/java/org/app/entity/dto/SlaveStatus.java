package org.app.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SlaveStatus {
    Long roomId;

    Integer curTemp;

    Integer setTemp;

    /**
     * 从机与主机的状态, 取值: "正常", "离线", "关机"
     */
    String status;

    String mode;

    LocalDateTime registeredTime;

    public SlaveStatus(Long roomId, LocalDateTime registeredTime) {
        this.roomId = roomId;
        this.registeredTime = registeredTime;
    }
}
