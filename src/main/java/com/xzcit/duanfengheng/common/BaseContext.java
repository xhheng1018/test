package com.xzcit.duanfengheng.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 设置当前登录员工ID
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    // 获取ID
    public static Long getCurrentId() {
        return threadLocal.get();
    }
    // 清空（过滤器里必须执行）
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}