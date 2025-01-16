package com.kklive.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/17 4:32
 * @Version V1.0
 */
@SpringBootApplication(scanBasePackages = "com.kklive")
public class KKliveWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(KKliveWebApplication.class, args);
    }
}
