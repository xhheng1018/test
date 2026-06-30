package com.xzcit.duanfengheng.config;

import com.xzcit.duanfengheng.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    // 页面跳转视图映射
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/backend/page/login.html").setViewName("backend/page/login");
        registry.addViewController("/backend/page/index.html").setViewName("backend/page/index");
        registry.addViewController("/backend/page/category.html").setViewName("backend/page/category");
        registry.addViewController("/backend/page/category-edit.html").setViewName("backend/page/category-edit");
        registry.addViewController("/backend/page/dish.html").setViewName("backend/page/dish");
        registry.addViewController("/backend/page/dish-add.html").setViewName("backend/page/dish-add");
        registry.addViewController("/backend/page/dish-edit.html").setViewName("backend/page/dish-edit");

        // 套餐页面映射
        registry.addViewController("/backend/page/setmeal.html").setViewName("backend/page/setmeal");
        registry.addViewController("/backend/page/setmeal-add.html").setViewName("backend/page/setmeal-add");
        registry.addViewController("/backend/page/setmeal-edit.html").setViewName("backend/page/setmeal-edit");
    }

    // 登录拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录页面、登录登出接口
                        "/backend/page/login.html",
                        "/employee/login",
                        "/employee/logout",
                        // 静态资源
                        "/static/**",
                        // 分类、菜品查询接口（套餐弹窗专用）
                        "/dish/list",
                        "/category/list",
                        // 文件上传、图片下载
                        "/upload",
                        "/common/download",
                        // 套餐相关全部接口
                        "/setmeal/page",
                        "/setmeal",
                        "/setmeal/**"
                );
    }
}