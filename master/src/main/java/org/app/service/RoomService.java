package org.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.Request;
import org.app.entity.Room;
import org.app.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zfq
 */
@Service
public class RoomService extends ServiceImpl<RoomMapper, Room> {
}
