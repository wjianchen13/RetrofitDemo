package com.example.retrofitdemo.base;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.retrofitdemo.R;
import com.example.retrofitdemo.utils.DataCallback;
import com.example.retrofitdemo.utils.HttpUtil;
import com.example.retrofitdemo.utils.MainThreadHandler;
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

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
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
     * 优化版本：onResponse回调在子线程执行，处理完数据后切换到主线程
     * @param v
     */
    public void onEnqueue(View v) {
        Call<ResponseBody> call = HttpUtil.getWanAndroidService().getBannerData();
        call.enqueue(new Callback<ResponseBody>() {
            //网络请求成功时调用 - 现在在子线程中执行
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 当前在子线程，可以安全地进行耗时操作（数据解析）
                System.out.println("=========> onResponse回调线程: " + Thread.currentThread().getName());
                System.out.println("=========> isMainThread: " + isMainThread());
                
                try {
                    // 在子线程中进行数据解析（耗时操作）
                    String strBack = doJson(response.body());
                    System.out.println("=========> 子线程中解析JSON完成");
                    
                    JSONObject jsonObject = new JSONObject(strBack);
                    Gson gson = new Gson();
                    BannerBean bannerBean = gson.fromJson(jsonObject.toString(), BannerBean.class);
                    System.out.println("=========> 子线程中转换为BannerBean完成");
                    
                    // 数据解析完成后，切换到主线程进行UI更新
                    final BannerBean finalBannerBean = bannerBean;
                    MainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 现在在主线程，可以安全地更新UI
                            System.out.println("=========> 主线程回调线程: " + Thread.currentThread().getName());
                            System.out.println("=========> 主线程isMainThread: " + isMainThread());
                            System.out.println("=========> 请求成功, Banner数据: " + finalBannerBean.toString());
                            
                            // 这里可以更新UI，例如：
                            // textView.setText(finalBannerBean.getData().get(0).getTitle());
                            // recyclerView.setAdapter(new BannerAdapter(finalBannerBean.getData()));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // 错误处理也切换到主线程
                    final Exception finalException = e;
                    MainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("=========> 数据解析失败: " + finalException.getMessage());
                            // 可以在这里显示错误提示
                        }
                    });
                }
            }

            //网络请求失败时调用 - 也在子线程中执行
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("=========> onFailure回调线程: " + Thread.currentThread().getName());
                System.out.println("=========> 请求失败: " + t.getMessage());
                
                // 切换到主线程处理错误
                final Throwable finalThrowable = t;
                MainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("=========> 主线程处理错误");
                        // 可以在这里显示错误提示给用户
                        // Toast.makeText(BaseActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
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
     * 注意：由于配置了callbackExecutor，这个回调也在子线程执行
     * @param v
     */
    public void onConvert(View v) {
        Call<BannerBean> call = HttpUtil.getWanAndroidService().getBannerDataConvertData();
        call.enqueue(new Callback<BannerBean>() {
            //网络请求成功时调用 - 在子线程执行
            @Override
            public void onResponse(Call<BannerBean> call, Response<BannerBean> response) {
                System.out.println("=========> onConvert回调线程: " + Thread.currentThread().getName());
                try {
                    BannerBean bannerBean = response.body();
                    System.out.println("=========> 子线程中获取数据: " + bannerBean.toString());
                    
                    // 切换到主线程处理
                    final BannerBean finalBannerBean = bannerBean;
                    MainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("=========> 主线程处理数据: " + finalBannerBean.toString());
                            // 可以在这里更新UI
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //网络请求失败时调用 - 在子线程执行
            @Override
            public void onFailure(Call<BannerBean> call, Throwable t) {
                System.out.println("=========> 请求失败: " + t.getMessage());
                // 切换到主线程处理错误
                MainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("=========> 主线程处理错误");
                    }
                });
            }
        });
    }

    /**
     * 使用通用回调接口的示例方法（可选）
     * 这种方式可以让代码更优雅，统一处理数据回调
     */
    private void requestBannerDataWithCallback(DataCallback<BannerBean> callback) {
        Call<ResponseBody> call = HttpUtil.getWanAndroidService().getBannerData();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 在子线程中处理数据
                try {
                    String strBack = doJson(response.body());
                    JSONObject jsonObject = new JSONObject(strBack);
                    Gson gson = new Gson();
                    BannerBean bannerBean = gson.fromJson(jsonObject.toString(), BannerBean.class);
                    
                    // 切换到主线程回调
                    final BannerBean finalBannerBean = bannerBean;
                    MainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess(finalBannerBean);
                            }
                        }
                    });
                } catch (Exception e) {
                    final Exception finalException = e;
                    MainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(finalException.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                final Throwable finalThrowable = t;
                MainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(finalThrowable.getMessage());
                        }
                    }
                });
            }
        });
    }

}