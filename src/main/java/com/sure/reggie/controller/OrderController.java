package com.sure.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sure.reggie.common.BaseContext;
import com.sure.reggie.common.Result;
import com.sure.reggie.entity.Orders;
import com.sure.reggie.entity.User;
import com.sure.reggie.service.impl.OrderService;
import com.sure.reggie.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 訂單
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用戶下單
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return Result.success("下單成功");
    }

    /**
     * 订单信息分页查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        log.info("beginTIme:{},endTime:{}", beginTime, endTime);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        queryWrapper.ge(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .le(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 移動端用戶中心信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> userPage(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 更新订单派送状态
     *
     * @param order
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Orders order) {
        LambdaUpdateWrapper<Orders> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Orders::getId, order.getId());
        queryWrapper.set(Orders::getStatus, order.getStatus());
        orderService.update(queryWrapper);
        return Result.success("訂單派送狀態，更新成功");
    }
}
