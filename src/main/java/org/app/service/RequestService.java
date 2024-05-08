package org.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.Request;
import org.app.mapper.RequestMapper;
import org.springframework.stereotype.Service;

/**
 * @author zfq
 */
@Service
public class RequestService extends ServiceImpl<RequestMapper, Request> {
}
