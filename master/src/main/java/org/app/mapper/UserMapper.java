package org.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.app.entity.User;

/**
 * @author zfq
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
