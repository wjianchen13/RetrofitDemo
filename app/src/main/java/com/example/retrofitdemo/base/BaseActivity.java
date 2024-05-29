package com.example.retrofitdemo.base;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.retrofitdemo.R;
import com.example.retrofitdemo.utils.HttpUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Retrofit 入门
 * https://blog.csdn.net/qq_42391904/article/details/100533368
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        HttpUtil.getInstance();
    }

    /**
     * 同步请求
     * @param v
     */
    public void onExecute(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ResponseBody> call = HttpUtil.getWanAndroidService().getBannerData();
                    Response<ResponseBody> response = call.execute();
                    System.out.println("=========> response: " + response.body().toString());
                    String strBack = doJson(response.body());
                    System.out.println("请求成功, 返回的数据如下:\n" + doJson(response.body()));
                    JSONObject jsonObject = new JSONObject(strBack);
                    Gson gson = new Gson();
                    BannerBean bannerBean = gson.fromJson(jsonObject.toString(), BannerBean.class);
                    System.out.println("请求成功, 返回的数据如下:\n" + bannerBean.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ResponseBody 处理成 Json
     */
    private String doJson(ResponseBody responseBody) {
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }
        String result = "";
        if (contentLength != 0) {
            result = buffer.clone().readString(charset);
        }
        return result;
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 异步请求
     * @param v
     */
    public void onEnqueue(View v) {
        Call<ResponseBody> call = HttpUtil.getWanAndroidService().getBannerData();
        call.enqueue(new Callback<ResponseBody>() {
            //网络请求成功时调用
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println("=========> response: " + response.body().toString());
                    String strBack = doJson(response.body());
                    System.out.println("请求成功, 返回的数据如下:\n" + doJson(response.body()));
                    JSONObject jsonObject = new JSONObject(strBack);
                    Gson gson = new Gson();
                    BannerBean bannerBean = gson.fromJson(jsonObject.toString(), BannerBean.class);
                    System.out.println("请求成功, 返回的数据如下:\n" + bannerBean.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //网络请求失败时调用
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("=========> 请求失败");
            }
        });
    }

    /**
     * Http注解
     * @param v
     */
    public void onHttp(View v) {
        Call<ResponseBody> call = HttpUtil.getWanAndroidService().getBannerData(1, 294);
        call.enqueue(new Callback<ResponseBody>() {
            //网络请求成功时调用
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println("=========> response: " + response.body().toString());
                    System.out.println("请求成功, 返回的数据如下:\n" + doJson(response.body()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //网络请求失败时调用
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("=========> 请求失败");
            }
        });
    }

    /**
     * Retrofit 自动转换
     * @param v
     */
    public void onConvert(View v) {
        Call<BannerBean> call = HttpUtil.getWanAndroidService().getBannerDataConvertData();
        call.enqueue(new Callback<BannerBean>() {
            //网络请求成功时调用
            @Override
            public void onResponse(Call<BannerBean> call, Response<BannerBean> response) {
                try {
                    System.out.println("=========> response: " + response.body().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //网络请求失败时调用
            @Override
            public void onFailure(Call<BannerBean> call, Throwable t) {
                System.out.println("=========> 请求失败");
            }
        });
    }

}