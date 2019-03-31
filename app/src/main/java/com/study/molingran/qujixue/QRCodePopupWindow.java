package com.study.molingran.qujixue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Molingran
 * @create 2019/01/31 0:48
 *
 */
public class QRCodePopupWindow extends PopupWindow {
    private static final String TAG = "QRCodePopupWindow";

    private static final int SUCCESS = 200;
    private static final int NO_NET = -1;
    private static final int LINK_ERROR = -2;

    private Context mContext;

    private ImageView ivQRCode;
    private ProgressBar pbLoading;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    //将byte数组类型的转换为bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //设置到bitmap里面去，加载网络获取的图片
                    ivQRCode.setImageBitmap(bitmap);
                    break;
                case NO_NET:
                    Toast.makeText(mContext, "网络异常，更新二维码失败", Toast.LENGTH_SHORT).show();
                    ivQRCode.setImageResource(R.drawable.qrcode_local);
                    break;
                case LINK_ERROR:
                    Toast.makeText(mContext, "错误码" + msg.obj + "链接丢失，请到一客首页更新APP", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            pbLoading.setVisibility(View.GONE);
        }
    };


    public QRCodePopupWindow(Context context) {
        super(context);
        this.mContext = context;
        setBackgroundDrawable(null);
        Log.e(TAG, "QRCodePopupWindow: 开始初始化" );
        initialize();
    }

    /**
     * 初始化布局
     */
    private void initialize() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.ppw_qr, null);
        pbLoading = view.findViewById(R.id.pb_loading);
        ivQRCode = view.findViewById(R.id.iv_qrcode);

        setContentView(view);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        getImg();
    }

    /**
     * 联网请求二维码
     */
    private void getImg() {
        //缓存文件夹
        File cacheFile = new File(mContext.getExternalCacheDir(), "cache");
        //缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        //创建缓存对象
        Cache cache = new Cache(cacheFile, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(60, TimeUnit.SECONDS)
                .build();

        String url = "https://yeek.top/qujixue/getApp/qrcode_cloud.png";
        //创建请求
        Request request = new Request.Builder()
                .get()
                .cacheControl(cacheControl)
                .url(url)
                .build();
        //使用异步加载，判断连接网络是否成功//
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
                    //获取成功
                    message.obj = response.body().bytes();
                    message.what = SUCCESS;
                } else {
                    //获取失败
                    message.obj = response.code();
                    message.what = LINK_ERROR;
                }
                mHandler.sendMessage(message);

            }
        });

    }

    /**
     *  弹窗位置设置
     */
    public void showAtBottom(View view) {

        showAtLocation(view, Gravity.CENTER, 0, 0);
//        showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
//        showAtLocation();

    }


}
