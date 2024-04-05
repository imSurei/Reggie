package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.entity.DishFlavor;
import com.sure.reggie.mapper.DishFlavorMapper;
import com.sure.reggie.service.impl.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
