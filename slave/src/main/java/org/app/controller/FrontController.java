package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.common.Status;
import org.app.service.SlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zfq
 */
@Slf4j
@RestController
public class FrontController {
    @Autowired
    SlaveService slaveService;

    @GetMapping("/status")
    public R<Status> getStatus() {
        return R.success(SlaveService.getStatus());
    }

    /**
     * 修改设定温度，升高一度
     */
    @PostMapping(value = "/upSetTemp")
    public String upSetTemp() {
        //asyncService.setRequest(roomId, "up");
        //TODO: 需要将设定温度传给前端,且设定温度不能超过限制
        return "OK";
    }
}
