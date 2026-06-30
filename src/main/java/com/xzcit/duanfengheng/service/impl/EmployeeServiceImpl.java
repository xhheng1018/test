package com.xzcit.duanfengheng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzcit.duanfengheng.entity.Employee;
import com.xzcit.duanfengheng.mapper.EmployeeMapper;
import com.xzcit.duanfengheng.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}