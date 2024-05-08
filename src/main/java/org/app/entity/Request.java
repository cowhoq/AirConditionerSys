package org.app.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zfq
 */
@Data
public class Request {
    Long id;
    Long roomId;
    LocalDateTime startTime;
    LocalDateTime stopTime;
    Integer startTemperature;
    Integer stopTemperature;
    String fanSpeed;
    BigDecimal totalFee;
}
