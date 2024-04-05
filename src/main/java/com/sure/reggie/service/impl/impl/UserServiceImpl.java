package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.entity.User;
import com.sure.reggie.mapper.UserMapper;
import com.sure.reggie.service.impl.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
