package org.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.User;
import org.app.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author zfq
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    public Boolean login(String name, String password) {
        var lqw = new LambdaQueryWrapper<User>();
        lqw.eq(User::getName, name).eq(User::getPassword, password);
        return this.getOne(lqw) != null;
    }
}
