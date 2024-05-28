package org.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zfq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @TableId
    Long roomId;

    Long userId;

    Boolean inuse;
}
