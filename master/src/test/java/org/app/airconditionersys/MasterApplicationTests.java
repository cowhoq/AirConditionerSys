package org.app.airconditionersys;

import org.app.service.MasterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MasterApplicationTests {
    @Autowired
    MasterService masterService;

    @Test
    void testMasterService() {
        masterService.powerOn(null, null);
    }

}
