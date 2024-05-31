package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.entity.Request;
import org.app.entity.Room;
import org.app.entity.User;
import org.app.service.RequestService;
import org.app.service.RoomService;
import org.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台登录, 结账窗口
 *
 * @author zfq
 */
@Slf4j
@RestController()
@RequestMapping({"/api", ""})
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @Autowired
    RequestService requestService;

    /**
     * 新用户登记, 用户自己输入名称和房间 Id
     *
     * @return 注册成功返回用户 id, 以便后续其他功能使用
     */
    @PostMapping("/register")
    public R<String> register(Long userId, String name, Long roomId) {
        if (name == null || roomId == null)
            return R.error("参数错误");
        if (TEST)
            log.info("{}, {}", name, roomId);
        var room = roomService.getById(roomId);
        if (room != null)
            return R.error("房间已有人使用");

        var user = userService.getById(userId);
        if (user != null) { // 如果查询这个姓名的用户存在, 则直接添加房间
            room = new Room(roomId, userId, false);
            roomService.save(room);
        } else { // 如果用户不存在, 则需要在 user 表中添加用户
            user = new User(userId, name, "123456");
            userService.save(user);
            room = new Room(roomId, userId, false);
            roomService.save(room);
        }
        log.info("新用户注册: {}, 分配房间: {}", user, room);
        // TODO: 用户注册时可能存在用户名重复的情况, 以后需要增加异常处理
        return R.success("添加成功");
    }


    /**
     * 根据 roomId 获取用户的账单列表
     */
    @GetMapping("/bill")
    public R<List<Request>> getBillByRoomId(Long roomId) {
        return R.success(requestService.getRequestListByRoomId(roomId));
    }
}
