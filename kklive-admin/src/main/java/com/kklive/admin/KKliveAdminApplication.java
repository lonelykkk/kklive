package com.kklive.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/17 4:32
 * @Version V1.0
 */
@SpringBootApplication(scanBasePackages = {"com.kklive"})
@MapperScan(basePackages = {"com.kklive.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class KKliveAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(KKliveAdminApplication.class, args);
    }
}
