package com.study.molingran.qujixue.tools.download;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.util.concurrent.TimeUnit;

/**
 * @author Molingran
 * @create 2019/02/16 23:47
 */
class ApiHelper {

    private final int CONN_TIMEOUT = 30;
    private final int READ_TIMEOUT = 30;
    private final int WRITE_TIMEOUT = 30;

    private static ApiHelper mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mHttpClient;

    /**
     * 构造http对象
     */
    private ApiHelper() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                //读取超时
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                //写入超时
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        //对象引用
        mHttpClient = builder.build();
    }

    /**
     * 对象单例
     */
    static ApiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ApiHelper();
        }
        return mInstance;
    }

    /**
     * 待填坑
     */
    ApiHelper buildRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://yeek.com/")
                .client(mHttpClient)
                .build();
        return this;
    }

    /**
     * 待填坑
     */
    <T> T createService(Class<T> serviceClass) {
        return mRetrofit.create(serviceClass);
    }
}
