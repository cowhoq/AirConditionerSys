package org.app.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private BigDecimal energy = new BigDecimal(0);

    private BigDecimal fee = new BigDecimal(0);

    private LocalDateTime registeredTime;

    public SlaveStatus(Long roomId, String status, LocalDateTime registeredTime) {
        this.roomId = roomId;
        this.status = status;
        this.registeredTime = registeredTime;
    }

    public void addEnergy(BigDecimal energy) {
        this.energy = this.energy.add(energy);
    }

    public void addFee(BigDecimal fee) {
        this.fee = this.fee.add(fee);
    }

    public void zero() {
        this.energy = new BigDecimal(0);
        this.fee = new BigDecimal(0);
    }
}
