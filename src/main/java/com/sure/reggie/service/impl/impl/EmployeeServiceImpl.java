package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.entity.Employee;
import com.sure.reggie.mapper.EmployeeMapper;
import com.sure.reggie.service.impl.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
