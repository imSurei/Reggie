package com.sure.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sure.reggie.dto.DishDto;
import com.sure.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    // 新增菜品，並新增此菜品對應的口味數據
    void saveWithFlavor(DishDto dishDto);

    // 更新菜品信息，及其口味信息
    void updateWithFlavor(DishDto dishDto);

    // 根據ID 查詢菜品及其口味信息
    DishDto getByIdWithFlavor(Long id);
}
