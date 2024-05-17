package org.app.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理
 *
 * @author zfq
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {
    /**
     * 请求时主机未开机, 状态错误
     */
    @ExceptionHandler(IllegalStateException.class)
    public R<String> exceptionHandler(IllegalStateException e) {
        var message = e.getMessage();
        log.error(e.getMessage());
        if (message.contains("主机未启动"))
            return R.error("主机未启动");
        else
            return R.error(message);
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<String> exceptionHandler(IllegalArgumentException e) {
        var message = e.getMessage();
        log.error(e.getMessage());
        return R.error(message);
    }
}
