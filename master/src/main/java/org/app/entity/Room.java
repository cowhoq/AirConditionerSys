package org.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author zfq
 */
@Data
public class Room {
    @TableId
    Long roomId;

    Long userId;

    Boolean inuse;
}
