package com.study.molingran.qujixue.tools.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;

/**
 * @author Molingran
 * @create 2019/02/16 23:50
 * 源代码
 * Retrofit2.0使用姊妹篇——带进度下载文件 https://blog.csdn.net/k_bb_666/article/details/79500623#t5
 */
public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static final String PATH = Environment.getExternalStorageDirectory() + "/Android/data/com.study.molingran.qujixue/Download";
    private ApiInterface mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Context context;
    private Thread mThread;
    private String url;
    private String mPath;

    public DownloadUtil(Context context, String url) {
        if (mApi == null) {
            //初始化网络请求接口
            mApi = ApiHelper.getInstance().buildRetrofit()
                    .createService(ApiInterface.class);
        }
        this.url = url;
        this.context = context;
        newFile();
    }

    /**
     * 创建文件/返回文件路径
     */
    private void newFile() {
        if (FileUtils.createOrExistsDir(PATH)) {
            String name = url;
            int i = name.lastIndexOf('/');
            if (i != -1) {
                name = name.substring(i);
                mPath = PATH + name;
            }
            mFile = new File(mPath);
            Log.e(TAG, "newFile: " + mFile.getPath());
        }
    }

    /**
     * 删除文件
     */
    public void dFile() {
        mFile.delete();
        Log.e(TAG, "dFile: 删掉了吧");
    }


    /**
     * 打开APK的一系列操作
     */
    public void openAPK() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(mPath);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    public void downloadFile(final DownloadListener downloadListener) {
        if (!FileUtils.isFileExists(mFile) && FileUtils.createOrExistsFile(mFile)) {
            if (mApi == null) {
                Log.e(TAG, "downloadVideo: 下载接口为空了");
                return;
            }
            mCall = mApi.downloadFile(url);
            Log.i(TAG, "downloadFile: " + mApi.toString());

            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    //下载文件放在子线程
                    Log.e(TAG, "onResponse: " + response.code());
                    //链接失效时直接返回错误
                    if (response.code() == 404) {
                        downloadListener.onFailure("链接失效");
                        return;
                    }
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };
                    mThread.start();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //下载失败
                    downloadListener.onFailure("网络错误！");

                }
            });
        } else {
            //文件已存在
            downloadListener.onPresence(mPath);
            Log.e(TAG, "downloadFile: 文件已存在");
        }
    }

    /**
     * 将下载的文件写入本地存储
     */
    private void writeFile2Disk(Response<ResponseBody> response, File file, DownloadListener downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;
//获取下载输入流
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();
        try {
            //输出流
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                Log.e(TAG, "当前进度: " + currentLength);
                //计算当前下载百分比，并经由回调传出
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
                //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                if ((int) (100 * currentLength / totalLength) == 100) {
                    //下载完成
                    downloadListener.onFinish(mPath);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
