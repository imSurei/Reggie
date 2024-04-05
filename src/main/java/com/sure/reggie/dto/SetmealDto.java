package com.sure.reggie.dto;


import com.sure.reggie.entity.Setmeal;
import com.sure.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
