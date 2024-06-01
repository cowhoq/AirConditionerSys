package org.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.app.common.R;
import org.app.service.SlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author zfq
 */
@Slf4j
@RestController
public class LoginController {
    @Autowired
    SlaveService slaveService;

    /**
     * 登录，需要房间 号！
     */
    @PostMapping("/login")
    public R login(Long roomId, String name, String password) {
        var restTemplate = new RestTemplate();
        log.info(roomId + name + password);
        var requestEntity = getRequestEntity(roomId, name, password);
        var response = restTemplate.exchange(SlaveService.BASE_URL + "/slave-login",
                HttpMethod.POST, requestEntity, R.class);
        var r = response.getBody();
        log.info(String.valueOf(r));
        if (r != null && r.getCode() == 1) {
            SlaveService.setROOM_ID(roomId);
            slaveService.setSetTemp((Integer) r.getData());
            return r;
        }
        if (r != null)
            return r;
        return R.error("登录失败");
    }

    /**
     * 登出，只需要房间号！
     */
    @PostMapping("logout")
    public R logout(Long roomId) {
        var restTemplate = new RestTemplate();
        var requestEntity = getRequestEntity(roomId, null, null);
        var response = restTemplate.exchange(SlaveService.BASE_URL + "/slave-logout",
                HttpMethod.POST, requestEntity, R.class);
        var r = response.getBody();
        if (r != null && r.getCode() == 1) {
            SlaveService.setROOM_ID(null);
            return r;
        }
        return r;
    }

    private HttpEntity<MultiValueMap<String, String>> getRequestEntity(Long roomId, String name, String password) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("roomId", String.valueOf(roomId));
        if (name != null) requestBody.add("name", name);
        if (password != null) requestBody.add("password", password);
        return new HttpEntity<>(requestBody, headers);
    }
}
