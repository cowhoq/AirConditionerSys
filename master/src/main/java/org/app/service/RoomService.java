package org.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.Room;
import org.app.mapper.RoomMapper;
import org.springframework.stereotype.Service;

/**
 * @author zfq
 */
@Service
public class RoomService extends ServiceImpl<RoomMapper, Room> {
}
