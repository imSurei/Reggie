package com.sure.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sure.reggie.common.Result;
import com.sure.reggie.entity.User;
import com.sure.reggie.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 模擬手機發送驗證碼，並保存用作用戶登陸
     *
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phoneNum = user.getPhone();
        if (StringUtils.isNotEmpty(phoneNum)) {
            String code = "6677"; // 不開通第三方簡訊驗證服務，自定義驗證碼用於登陸
            session.setAttribute(phoneNum, code);
            return Result.success("簡訊發送成功");
        }
        return Result.error("簡訊發送失敗");
    }

    /**
     * 移動端用戶登陸
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        String phoneNum = (String) map.get("phone");
        String code = (String) map.get("code");
        Object codeInSession = session.getAttribute(phoneNum);

        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phoneNum);
            User user = userService.getOne(queryWrapper);
            if (user == null) { // 如果為新用戶，自動完成註冊
                user = new User();
                user.setPhone(phoneNum);
                userService.save(user);
            }
            // filter 需要setAttribute
            session.setAttribute("user",user.getId());
            return Result.success(user);
        }
        return Result.error("登陸失敗");
    }


}
