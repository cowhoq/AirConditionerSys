package org.app.airconditionersys;

import lombok.extern.slf4j.Slf4j;
import org.app.service.MasterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MasterApplicationTests {
    @Autowired
    MasterService masterService;

    @Test
    void testMasterService() {
        try {
            masterService.getRange();
        } catch (IllegalStateException e) {
            log.error("主机尚未启动, 测试AOP成功");
        }
        masterService.powerOn(null, null);
        log.info("测试能否正确从主机中获取工作温度: {}", masterService.getRange());
    }

}
