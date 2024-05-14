package org.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zfq
 */
@Data
public class Request {
    private Long id;

    private Long roomId;

    private LocalDateTime startTime;

    private LocalDateTime stopTime;

    @TableField(value = "start_temperature")
    private Integer startTemp;

    @TableField(value = "stop_temperature")
    private Integer stopTemp;

    private String fanSpeed;

    private BigDecimal totalFee;
}
