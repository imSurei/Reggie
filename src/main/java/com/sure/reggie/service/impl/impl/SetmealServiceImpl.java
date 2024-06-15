package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.common.CustomException;
import com.sure.reggie.dto.SetmealDto;
import com.sure.reggie.entity.Setmeal;
import com.sure.reggie.entity.SetmealDish;
import com.sure.reggie.mapper.SetmealMapper;
import com.sure.reggie.service.impl.SetmealDishService;
import com.sure.reggie.service.impl.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同時保存套餐與菜品的關聯關係
     *
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息
        this.save(setmealDto);

        // 保存套餐和菜品的關聯關係
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 刪除套餐，同時刪除其關聯的菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 判讀該套餐是否已經停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = (int) this.count(queryWrapper);
        if (count > 0) throw new CustomException("套餐正在售賣中，無法刪除");

        // 刪除套餐表數據
        this.removeByIds(ids);

        // 刪除該套餐關聯的菜品數據
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 單個或批量更新套餐停/启售状态
     *
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(int status, List<Long> ids) {

        if (status == 0) {
            for (Long id : ids) {
                LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
                Setmeal setmeal = this.getById(id);
                setmeal.setStatus(0);
                queryWrapper.eq(Setmeal::getId, id);
                this.update(setmeal, queryWrapper);
            }
        } else if (status == 1) {
            for (Long id : ids) {
                LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
                Setmeal setmeal = this.getById(id);
                setmeal.setStatus(1);
                queryWrapper.eq(Setmeal::getId, id);
                this.update(setmeal, queryWrapper);
            }
        } else new CustomException("套餐更新状态失败");
    }

    /**
     * 根據ID查詢套餐信息和其對應的菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        // 更新Set-meal基本信息
        this.updateById(setmealDto);

        // 刪除該套餐中的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        // 添加當前提交的菜品
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId()); // ⚠️和line129 remove操作一致
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }
}
