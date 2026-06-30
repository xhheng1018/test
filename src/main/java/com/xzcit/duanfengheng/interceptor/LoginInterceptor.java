package com.xzcit.duanfengheng.interceptor;

import com.xzcit.duanfengheng.entity.Employee;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 登录、登出接口直接放行
        if ("/employee/login".equals(uri) || "/employee/logout".equals(uri)) {
            return true;
        }

        // 获取登录员工session
        Employee loginEmp = (Employee) request.getSession().getAttribute("employee");
        if (loginEmp == null) {
            // 判断是否为异步AJAX请求
            String xhr = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(xhr)) {
                // AJAX：返回未登录标识，不跳转页面
                response.getWriter().write("{\"code\":0,\"msg\":\"未登录，请先登录\"}");
            } else {
                // 页面跳转请求：重定向登录页
                response.sendRedirect("/backend/page/login.html");
            }
            return false;
        }
        // 已登录放行
        return true;
    }
}