package com.kklive.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/17 4:37
 * @Version V1.0
 */
@RestController
@RequestMapping
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "admin模块启动成功";
    }
}
