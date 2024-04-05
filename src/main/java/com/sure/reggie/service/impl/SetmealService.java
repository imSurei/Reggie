package com.sure.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sure.reggie.dto.SetmealDto;
import com.sure.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，同時保存套餐與菜品的關聯關係
    void saveWithDish(SetmealDto setmealDto);

    // 刪除套餐，同時刪除其關聯的菜品
    void removeWithDish(List<Long> ids);
}
