package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.entity.Room;
import org.app.entity.User;
import org.app.service.RoomService;
import org.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zfq
 */
@Slf4j
@RestController()
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    /**
     * 新用户注册
     *
     * @param user 新用户注册填写的信息
     * @return 注册成功返回用户 id, 以便后续其他功能使用
     */
    @PostMapping("/register")
    public R<Long> register(@RequestBody User user) {
        log.info("新用户注册: {}", user);
        // TODO: 用户注册时可能存在用户名重复的情况, 以后需要增加异常处理
        userService.save(user);
        return R.success(user.getId());
    }

    /**
     * 用户添加房间
     *
     * @param user 用户信息 (只用到了其中的 userId)
     * @return 新添加的房间的 id
     */
    @PostMapping("/room")
    public R<Long> addRoom(@RequestBody User user) {
        // roomId 使用 MybatisPlus 自动生成的 id
        var room = new Room();
        room.setUserId(user.getId());
        roomService.save(room);
        return R.success(room.getRoomId());
    }
}
