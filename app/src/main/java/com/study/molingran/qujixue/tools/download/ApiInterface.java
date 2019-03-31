package com.study.molingran.qujixue.tools.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author Molingran
 * @create 2019/02/16 23:48
 */
public interface ApiInterface {
    /**
     * 下载视频
     *
     * @param fileUrl 文件地址
     * @return
     * 大文件时要加不然会OOM
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
