package org.app.controller;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.app.service.MasterService;
import org.graalvm.collections.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 控制器类，管理从属设备的请求
 * @author czl
 * @date 2024-5-15 16:25
 */
@Controller
public class SlaverController {

    @Autowired
    private MasterService masterService;
    @Getter
    @Setter
    @NoArgsConstructor
    public class WorkStatus {
        private WorkMode workmode;
        private Pair<Integer, Integer> range;
    }
    /**
     * 返回主机的工作模式和工作温度
     * @return ResponseEntity<WorkStatus> 包含工作模式和温度的响应实体
     */
    @GetMapping(path = "/getMasterWork", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkStatus> getMasterWork() {
        WorkStatus status = new WorkStatus();
        status.workmode = masterService.getWorkMode();
        status.range = masterService.getRange();
        return ResponseEntity.ok(status);
    }
    @PostMapping("/OnSlaverPower")
    public ResponseEntity<Void> OnSlaverPower(@RequestParam Long roomId,
                                              @RequestParam double setTemp,
                                              @RequestParam double curTemp,
                                              @RequestParam String mode,
                                              @RequestParam String IpAddress
                                              ){
        Request request = new Request();
        request.setRoomId(roomId);
        request.setStopTemp(setTemp);
        request.setStartTemp(curTemp);
        request.setFanSpeed(mode);
        if(masterService.slavePowerOn(request,IpAddress)){
            return ResponseEntity.ok().build();
        }
        else return ResponseEntity.badRequest().build();
    }
}
