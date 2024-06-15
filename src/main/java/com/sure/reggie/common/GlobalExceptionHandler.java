package com.sure.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局異常處理
 * (@ControllerAdvice 使用AOP 將RestController和Controller類別的拋出異常攔截到)
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            return Result.error("用戶名 " + split[2] + " 已存在");
        }
        return Result.error("出現異常...");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e) {
        return Result.error(e.getMessage());
    }
}
