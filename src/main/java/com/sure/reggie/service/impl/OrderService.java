package com.sure.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sure.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
