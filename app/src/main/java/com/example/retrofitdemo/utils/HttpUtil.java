package com.example.retrofitdemo.utils;

import com.example.retrofitdemo.base.WanAndroidService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求工具类
 * 基于单例模式-使用饿汉式
 * 网络请求库:Retrofit
 * 服务文件：WanAndroidService.java
 * app中所有网络请求的baseURL:https://www.wanandroid.com/
 */
public class HttpUtil {

    String TAG = "HttpUtil";
    private static HttpUtil httpUtil = new HttpUtil();
    private static WanAndroidService wanAndroidService;
    private static Retrofit retrofit;

    private Gson gson = new GsonBuilder()
            //配置你的Gson
            .setDateFormat("yyyy-MM-dd hh:mm:ss")

            .create();

    private HttpUtil() {
        //使用Log验证是否单例生效,app使用过程中HttpUtil只初始化一次即可");
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        wanAndroidService = retrofit.create(WanAndroidService.class);
    }

    public static HttpUtil getInstance() {
        return httpUtil;
    }

    //对外提供 wanAndroidService服务的 get方法 HttpUtil.getWanAndroidService()即可
    public static WanAndroidService getWanAndroidService() {
        return wanAndroidService;
    }
}


