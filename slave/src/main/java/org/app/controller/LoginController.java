package org.app.controller;

import org.app.common.R;
import org.app.service.SlaveService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zfq
 */
@RestController
public class LoginController {
    @PostMapping("/login")
    public R<String> login(Long roomId, String user, String password) {
        var restTemplate = new RestTemplate();
        var requestEntity = getRequestEntity(roomId, user, password);
        var response = restTemplate.exchange(SlaveService.BASE_URL + "/SlaveLogin",
                HttpMethod.POST, requestEntity, R.class);
        var r = response.getBody();
        if (r != null && r.getCode() == 1) {
            SlaveService.setROOM_ID(roomId);
            return R.success("登录成功");
        }
        return R.error("登录失败");
    }

    private HttpEntity<Map<String, String>> getRequestEntity(Long roomId, String user, String password) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var requestBody = new HashMap<String, String>();
        requestBody.put("roomId", String.valueOf(roomId));
        requestBody.put("user", user);
        requestBody.put("password", password);
        return new HttpEntity<>(requestBody, headers);
    }
}
