package com.example.retrofitdemo.base;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * https://blog.csdn.net/qq_42391904/article/details/100533368
 */
public interface WanAndroidService {

    /**
     * 1.2首页banner
     * 原接口:https://www.wanandroid.com/banner/json
     * 方法：GET
     * 参数：无
     */
    @GET("banner/json")
    Call<ResponseBody> getBannerData();

    /**
     * 4.2 项目列表数据 某个项目类别下的文章
     * 原接口:https://www.wanandroid.com/project/list/1/json?cid=294
     * 方法：GET
     * 参数：
     * cid 分类的id，上面项目分类接口 int类型
     * 页码page：拼接在链接中，从1开始。 int类型
     */
    @GET("project/list/{page}/json")
    Call<ResponseBody> getProjectArticle(@Path("page") int page, @Query("cid") int cid);

    /**
     * method 表示请求的方法，区分大小写
     * path表示路径
     * hasBody表示是否有请求体
     */
    @HTTP(method = "GET", path = "project/list/{page}/json", hasBody = false)
    Call<ResponseBody> getBannerData(@Path("page") int page, @Query("cid") int cid);

    /**
     * 1.2首页banner 转换成BannerBean
     * 原接口:https://www.wanandroid.com/banner/json
     * 方法：GET
     * 参数：无
     */
    @GET("banner/json")
    Call<BannerBean> getBannerDataConvertData();



}
