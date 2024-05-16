package org.app.common;

import org.app.entity.WorkMode;
import org.app.service.MasterService;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.*;

@Aspect
@Component
public class WorkModeAspect {
    @Autowired
    MasterService masterService;

    @Pointcut("@annotation(org.app.common.CheckWorkMode)")
    public void cut() {
    }

    @Before("cut()")
    public void auth(JoinPoint joinPoint) {
        var wordMode = masterService.getWorkMode();
        if (wordMode.equals(WorkMode.OFF))
            throw new IllegalStateException("主机未启动");
    }
}
