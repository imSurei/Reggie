package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.common.CustomException;
import com.sure.reggie.entity.Category;
import com.sure.reggie.entity.Dish;
import com.sure.reggie.entity.Setmeal;
import com.sure.reggie.mapper.CategoryMapper;
import com.sure.reggie.service.impl.CategoryService;
import com.sure.reggie.service.impl.DishService;
import com.sure.reggie.service.impl.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除分类，删除前进行是否该ID关联了菜品、套餐信息的判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException("該分類已關聯菜品，無法刪除");
        }

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int SetmealCount = setmealService.count(setmealQueryWrapper);
        if (SetmealCount > 0) {
            throw new CustomException("該分類已關聯菜品，無法刪除");
        }

        super.removeById(id);
    }
}
