package com.xzcit.duanfengheng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzcit.duanfengheng.common.R;
import com.xzcit.duanfengheng.entity.Employee;
import com.xzcit.duanfengheng.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     * 请求地址：POST /employee/login
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1. 获取前端密码，进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据用户名查询数据库员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 账号不存在 或 密码错误
        if (emp == null || !emp.getPassword().equals(password)) {
            return R.error("用户名或密码错误!");
        }

        // 4. 账号已禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 【修复】存入完整员工对象，和过滤器读取逻辑匹配
        request.getSession().setAttribute("employee", emp);
        return R.success(emp);
    }

    /**
     * 员工退出
     * 请求地址：POST /employee/logout
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 销毁整个Session，彻底清除登录信息
        request.getSession().invalidate();
        return R.success("退出成功");
    }
}