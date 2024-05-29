package org.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;


import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Rain
 * 处理无效路由
 */
@Controller
public class MyBasicErrorController extends BasicErrorController {

    @Autowired
    public MyBasicErrorController(ErrorAttributes errorAttributes,
                                  ServerProperties serverProperties,
                                  List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        var status = getStatus(request);
        // 获取 Spring Boot 默认提供的错误信息，然后添加一个自定义的错误信息
        Map<String, Object> model = Collections
                .unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
        return new ModelAndView("index.html", model, status);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        var status = getStatus(request);
        // 获取 Spring Boot 默认提供的错误信息，然后添加一个自定义的错误信息
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        return new ResponseEntity<>(body, status);
    }
}
