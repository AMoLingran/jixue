package com.study.molingran.qujixue.tools.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.google.gson.Gson;
import com.study.molingran.qujixue.tools.GetAppInfo;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Molingran
 * @create 2019/02/13 20:12
 */
public class AppCheckUpdate {
    public static final String TAG = "AppCheckUpdate";
    private static final int NO_NET = -1;
    private static final int LINK_ERROR = -2;
    private static final int SUCCESS = 200;
    private static final int FIRST_SUCCESS = 2001;

    private Context mContext;
    private SharedPreferences mUpdate;

    public int version;


    /**
     * 利用网络检查更新信息
     *
     * @param toast    是否弹出提示框
     * @param mContext 上下文
     */
    public AppCheckUpdate(Context mContext, Boolean toast) {
        mUpdate = mContext.getSharedPreferences("update", 0);
        this.mContext = mContext;
        getUpdate(toast);
        addReminder();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_NET:
                    Toast.makeText(mContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case LINK_ERROR:
                    Toast.makeText(mContext, "服务器异常，请联系开发者", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Toast.makeText(mContext, "检查更新成功", Toast.LENGTH_SHORT).show();
                case FIRST_SUCCESS:
                    addShared(String.valueOf(msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    public static SharedPreferences getShare(Context context) {
        return context.getSharedPreferences("update", 0);
    }

    private void getUpdate(final boolean toast) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        CacheControl cacheControl = new CacheControl.Builder()
                .build();
        String url = "https://www.yeek.top/qujixue/getApp/version.txt";
        Request request = new Request.Builder()
                .get()
                .cacheControl(cacheControl)
                .url(url)
                .build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            Message message = mHandler.obtainMessage();

            @Override
            public void onFailure(Call call, IOException e) {
                message.what = NO_NET;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == SUCCESS) {
                    assert response.body() != null;
                    message.obj = response.body().string();
                    if (toast) {
                        message.what = SUCCESS;
                    } else {
                        message.what = FIRST_SUCCESS;
                    }
                } else {
                    message.what = LINK_ERROR;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    private void addShared(String s) {
        AppUpdatesBean appUpdatesBean = new Gson().fromJson(s, AppUpdatesBean.class);
        List<AppUpdatesBean.FutureBean> future = appUpdatesBean.getFuture();
        SharedPreferences.Editor editor = mUpdate.edit();
        editor.putInt("versionCode", Integer.valueOf(appUpdatesBean.getVersionCode()));
        editor.putString("version", appUpdatesBean.getVersion());
        editor.putString("content", appUpdatesBean.getContent());
        editor.putString("date", appUpdatesBean.getDate());
        editor.putString("link", appUpdatesBean.getLink());
        if (appUpdatesBean.getFuture() != null) {
            StringBuilder futureString = new StringBuilder();
            for (AppUpdatesBean.FutureBean string : future) {
                futureString.append(string.getVersion()).append("：").append(string.getContent()).append("\n");
            }
            editor.putString("future", futureString.substring(0, futureString.length()));
        }

        editor.apply();
    }

    private void addReminder() {
        int nowVersion = GetAppInfo.getVersionCode(mContext);
        int newVersion = mUpdate.getInt("versionCode", 0);
        int ignoreVersion = mUpdate.getInt("ignoreVersion", 0);
        if (newVersion > ignoreVersion & newVersion - nowVersion > 0) {
            mUpdate.edit().putBoolean("needUpdate", true).apply();
        } else {
            mUpdate.edit().putBoolean("needUpdate", false).apply();
        }
    }
}
