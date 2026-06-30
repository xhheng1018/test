package com.xzcit.duanfengheng.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${reggie.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /images/xxx 映射到 D:\\images\\
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
        // 页面模板
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
    }
}