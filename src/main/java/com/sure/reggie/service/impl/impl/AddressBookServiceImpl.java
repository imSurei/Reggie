package com.sure.reggie.service.impl.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sure.reggie.entity.AddressBook;
import com.sure.reggie.mapper.AddressBookMapper;
import com.sure.reggie.service.impl.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
