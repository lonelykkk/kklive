package com.kklive.admin.intercepter;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/17 10:20
 * @Version V1.0
 */
public class WebAppConfigurer implements WebMvcConfigurer {
    @Resource
    private AppInterceptor appInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appInterceptor).addPathPatterns("/**");
    }
}
