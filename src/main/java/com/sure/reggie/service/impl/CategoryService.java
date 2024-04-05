package com.sure.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sure.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
