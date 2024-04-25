package org.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.User;
import org.app.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author zfq
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
