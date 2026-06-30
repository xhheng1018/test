package com.xzcit.duanfengheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;

@Controller
public class PageController {

    /**
     * 登录页：templates/backend/page/login.html
     */
    @GetMapping("/login")
    public String loginPage() {
        return "backend/page/login";
    }

    /**
     * 后台首页：templates/backend/page/index.html
     * 带session登录校验，未登录自动跳登录页
     */
    @GetMapping("/index")
    public String index(HttpSession session) {
        Object loginEmployee = session.getAttribute("employee");
        if (loginEmployee == null) {
            // 未登录重定向到登录地址
            return "redirect:/login";
        }
        return "backend/page/index";
    }
}