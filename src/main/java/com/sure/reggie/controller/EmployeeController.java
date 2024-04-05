package com.sure.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sure.reggie.common.Result;
import com.sure.reggie.entity.Employee;
import com.sure.reggie.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 員工登陸管理端
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 將前端頁面提交的用戶密碼進行 MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根據頁面提交的 userName 查詢數據庫
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); // 表中字段 userName 有唯一索引

        // 返回 userName 查詢結果
        if (emp == null) return Result.error("用戶名不存在，登陸失敗");

        // 校驗 password
        if (!emp.getPassword().equals(password)) return Result.error("密碼錯誤，登陸失敗");

        // 查詢員工狀態 是否可登陸系統
        if (emp.getStatus() == 0) return Result.error("此帳號已禁用");

        // 登陸成功，將 員工id 存入session，並返回登陸成功結果
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 員工退出管理端
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增員工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Employee employee) {
        log.info("add employee -> {}", employee.toString());
        // 統一給新增員工 設置初始值
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return Result.success("新增員工成功");
    }

    /**
     * 員工信息分頁查詢
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        // 分頁構造器
        Page pageInfo = new Page(page, pageSize);

        // 條件構造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name); // 過濾條件
        queryWrapper.orderByDesc(Employee::getUpdateTime); // 排序條件

        // 執行查詢
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根據ID修改員工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return Result.success("員工信息修改成功");
    }

    /**
     * 根據ID查詢員工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) return Result.success(employee);
        return Result.error("沒有查找到該名員工信息");
    }


}
