package com.example.retrofitdemo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * 主线程Handler工具类
 * 用于在子线程中切换到主线程执行代码
 */
public class MainThreadHandler {
    
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    
    /**
     * 在主线程执行Runnable
     * @param runnable 要执行的代码
     */
    public static void post(Runnable runnable) {
        mainHandler.post(runnable);
    }
    
    /**
     * 延迟在主线程执行Runnable
     * @param runnable 要执行的代码
     * @param delayMillis 延迟时间（毫秒）
     */
    public static void postDelayed(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }
    
    /**
     * 判断当前是否在主线程
     * @return true表示在主线程，false表示在子线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}

