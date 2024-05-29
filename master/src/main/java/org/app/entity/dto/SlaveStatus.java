package org.app.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SlaveStatus {
    private Long roomId;

    private Integer curTemp;

    private Integer setTemp;

    /**
     * 从机与主机的状态, 取值: "正常", "离线", "关机"
     */
    private String status;

    private String mode;

    private String wind;

    private LocalDateTime registeredTime;

    public SlaveStatus(Long roomId, LocalDateTime registeredTime) {
        this.roomId = roomId;
        this.registeredTime = registeredTime;
    }
}
