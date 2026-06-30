package com.xzcit.duanfengheng.filter;

import com.alibaba.fastjson.JSON;
import com.xzcit.duanfengheng.common.BaseContext;
import com.xzcit.duanfengheng.common.R;
import com.xzcit.duanfengheng.entity.Employee;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录拦截过滤器
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        // 放行地址：只放行登录、静态资源，业务页面/category/dish全部拦截校验登录
        String[] passUrls = {
                "/login",
                "/employee/login",
                "/employee/logout",
                "/static/**",
                "/front/**",
                "/common/**"
        };

        // 匹配到放行路径，直接放行，不执行登录校验
        if (checkUrlMatch(passUrls, requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从session读取登录员工对象，key统一为 employee（和过滤器保持一致）
        Object empObj = request.getSession().getAttribute("employee");
        if (empObj != null) {
            Employee emp = (Employee) empObj;
            // 登录成功，存入当前线程ID
            BaseContext.setCurrentId(emp.getId());
            filterChain.doFilter(request, response);
            // 请求结束清空ThreadLocal，防止线程复用串数据
            BaseContext.removeCurrentId();
            return;
        }

        // 未登录，返回未登录标识
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        response.getWriter().flush();
        response.getWriter().close();
    }

    public boolean checkUrlMatch(String[] urls, String uri) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, uri)) {
                return true;
            }
        }
        return false;
    }
}