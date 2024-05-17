package org.app.aop;

import org.app.service.SlaveStatusService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 从机发送请求时, 检查从机是否开机
 */
@Aspect
@Component
public class LoginAspect {
    @Autowired
    private SlaveStatusService slaveStatusService;

    @Pointcut("@annotation(org.app.aop.CheckLogin)")
    public void cut() {
    }

    @Before("cut()")
    public void checkLogin(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        var roomId = (Long) args[0];
        if (roomId == null)
            throw new IllegalArgumentException("请求参数错误, roomId 为 null");
        if (!slaveStatusService.isRegistered(roomId))
            throw new IllegalStateException("未登录");
    }
}
