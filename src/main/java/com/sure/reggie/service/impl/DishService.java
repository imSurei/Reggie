package com.sure.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sure.reggie.dto.DishDto;
import com.sure.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    // 新增菜品，並新增此菜品對應的口味數據
    void saveWithFlavor(DishDto dishDto);

    // 更新菜品信息，及其口味信息
    void updateWithFlavor(DishDto dishDto);

    // 根據ID 查詢菜品及其口味信息
    DishDto getByIdWithFlavor(Long id);

    // 根據對應的套餐信息，停/起售菜品
    void updateStatusWithSetmeal(int status, List<Long> ids);

    // 根據菜品停售狀態，刪除菜品
    void deleteByIds(List<Long> ids);
}
