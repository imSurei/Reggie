package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.entity.OrderDetail;
import com.sure.reggie.mapper.OrderDetailMapper;
import com.sure.reggie.service.impl.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
