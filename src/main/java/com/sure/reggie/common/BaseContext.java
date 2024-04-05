package com.sure.reggie.common;

/**
 * 基於ThreadLocal封裝的工具類，用於保存和獲取當前用戶ID
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
