package com.example.retrofitdemo.utils;

/**
 * 数据回调接口
 * 用于在子线程处理完数据后，在主线程回调结果
 * @param <T> 数据类型
 */
public interface DataCallback<T> {
    /**
     * 成功回调（在主线程执行）
     * @param data 处理后的数据
     */
    void onSuccess(T data);
    
    /**
     * 失败回调（在主线程执行）
     * @param error 错误信息
     */
    void onError(String error);
}

