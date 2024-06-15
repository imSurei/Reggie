package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.common.CustomException;
import com.sure.reggie.dto.DishDto;
import com.sure.reggie.entity.Dish;
import com.sure.reggie.entity.DishFlavor;
import com.sure.reggie.entity.SetmealDish;
import com.sure.reggie.mapper.DishMapper;
import com.sure.reggie.service.impl.DishFlavorService;
import com.sure.reggie.service.impl.DishService;
import com.sure.reggie.service.impl.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同時保存其對應的口味數據
     * 涉及到兩張表的操作，需加入事務控制
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 先保存菜品的基本信息 Dish物件
        this.save(dishDto);

        // 需要將 dishId 賦值給 flavors
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根據ID 查詢菜品及其口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息，及其口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新Dish基本信息
        this.updateById(dishDto);

        // 刪除該Dish對應的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加當前提交的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 將DishId 拼接到flavors
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根據對應的套餐信息，停/起售菜品
     *
     * @param status
     * @param ids
     */
    @Override
    public void updateStatusWithSetmeal(int status, List<Long> ids) {
        // 停售菜品
        if (status == 0) {
            // 確認要停售的菜品，沒有包含在套餐裡
            for (long id : ids) {
                LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SetmealDish::getDishId, id);
                int count = (int) setmealDishService.count(queryWrapper);
                if (count > 0) {
                    throw new CustomException("菜品（" + this.getById(id).getName() + "）關聯套餐，無法停售");
                } else { // 更新菜品狀態為0 停售菜品
                    Dish dish = this.getById(id);
                    dish.setStatus(0);
                    LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
                    dishQueryWrapper.eq(Dish::getId, id);
                    this.update(dish, dishQueryWrapper);
                }
            }
        }
        if (status == 1) {   // 起售菜品，不需確認套餐信息，直接將狀態更新為起售
            for (Long id : ids) {
                Dish dish = this.getById(id);
                dish.setStatus(1);
                LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
                dishQueryWrapper.eq(Dish::getId, id);
                this.update(dish, dishQueryWrapper);
            }
        } else new CustomException("無法更新菜品停/起售狀態");
    }

    /**
     * 根據菜品停售狀態，刪除菜品
     *
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = (int) this.count(queryWrapper);
        if (count > 0)
            throw new CustomException("菜品正在售賣中，無法刪除");
        this.removeByIds(ids);
    }
}
