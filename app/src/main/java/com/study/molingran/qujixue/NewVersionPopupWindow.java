package com.study.molingran.qujixue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.study.molingran.qujixue.tools.GetAppInfo;
import com.study.molingran.qujixue.tools.update.AppCheckUpdate;
import com.study.molingran.qujixue.tools.download.DownloadListener;
import com.study.molingran.qujixue.tools.download.DownloadUtil;

/**
 * @author Molingran
 * @create 2019/01/31 0:48
 * <p>
 * 现在暂时用弹窗下载，以后就用通知栏显示下载
 */
public class NewVersionPopupWindow extends PopupWindow {
    private static final String TAG = "NewVersionPopupWindow";

    private final int COMPLETED = 100;
    private final int PROGRESS = 1;
    private final int PRESENCE = 0;
    private final int FAILURE = -1;
    private String mUrl;

    private Activity mActivity;
    private SharedPreferences mUpdate;
    private DownloadUtil mDownloadUtil;

    private ScrollView sv;
    private TextView tvVersion;
    private TextView tvContent;
    private TextView tvFuture;
    private TextView tvIgnore;
    private Button btnNowUpdate;
    private ImageButton imgBtnClose;
    private ProgressBar pbProgressBar;
    private TextView tvSpeed;
    private ConstraintLayout cl;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    pbProgressBar.setProgress(msg.arg1);
                    tvSpeed.setText("已下载" + msg.arg1 + "%");
                    break;
                case COMPLETED:
                    dialog("下载完成", "遇到问题了？");
                    break;
                case PRESENCE:
                    dialog("安装文件已存在", "文件路径：\n" + msg.obj.toString());
                    break;
                case FAILURE:
                    dialog("下载失败", (String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    public NewVersionPopupWindow(Activity mActivity) {
        super(mActivity);
        this.mActivity = mActivity;
        setBackgroundDrawable(null);
        mUpdate = mActivity.getSharedPreferences("update", 0);
        initialize();
    }


    private void initialize() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.ppw_new_version, null);

        sv = view.findViewById(R.id.sv);
        cl = view.findViewById(R.id.Constraint);
        tvVersion = view.findViewById(R.id.tv_versioncode);
        tvContent = view.findViewById(R.id.tv_content);
        tvFuture = view.findViewById(R.id.tv_future);
        tvSpeed = view.findViewById(R.id.tv_speed);
        tvIgnore = view.findViewById(R.id.tv_ignore_up);
        btnNowUpdate = view.findViewById(R.id.btn_now_update);
        imgBtnClose = view.findViewById(R.id.imgbtn_close);
        pbProgressBar = view.findViewById(R.id.pb_progressBar);

        mUrl = AppCheckUpdate.getShare(mActivity).getString("link", "");

        setComponent();
        setContentView(view);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();

    }


    public void showAtBottom(View view) {

        showAtLocation(view, Gravity.CENTER, 0, 0);
//        showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
//        showAtLocation();

    }

    private void setComponent() {
        tvVersion.setText(GetAppInfo.getVersionName(mActivity) + " → " + mUpdate.getString("version", ""));
        tvContent.setText(mUpdate.getString("content", ""));
        tvFuture.setText("更新预告：\n" + mUpdate.getString("future", ""));
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnNowUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAPK();
                cl.setMinHeight(50);
                showAtBottom(tvVersion);
                pbProgressBar.setVisibility(View.VISIBLE);
                sv.setVisibility(View.GONE);
                tvIgnore.setVisibility(View.GONE);
            }
        });
        tvIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdate.edit().putBoolean("needUpdate", false).apply();
                mUpdate.edit().putInt("ignoreVersion", mUpdate.getInt("versionCode", 0)).apply();
                Toast.makeText(mActivity, "已忽略版本代码为" + mUpdate.getInt("versionCode", 0) + "的更新", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }


    private void dialog(String title, final String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if ("安装文件已存在".equals(title)) {
            dialog.setPositiveButton("删除并重新下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadUtil(mActivity, mUrl).dFile();
                    downloadAPK();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setNeutralButton("安装", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadUtil(mActivity, mUrl).openAPK();
                }
            });
        }
        if ("下载完成".equals(title)) {
            dialog.setPositiveButton("重启安装", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadUtil(mActivity, mUrl).openAPK();
                }
            });
            dialog.setNeutralButton("删除并重新下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadUtil(mActivity, mUrl).dFile();
                    downloadAPK();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        dialog.show();
    }

    private void downloadAPK() {
        //String url = AppCheckUpdate.getShare(mActivity).getString("link","https://www.yeek.top/qujixue/getApp/趣积学.apk")
        mDownloadUtil = new DownloadUtil(mActivity, mUrl);
        mDownloadUtil.downloadFile(new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                Message message = mHandler.obtainMessage();
                message.what = PROGRESS;
                message.arg1 = currentLength;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFinish(String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                Message message = mHandler.obtainMessage();
                message.what = COMPLETED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onPresence(String localPath) {
                Log.e(TAG, "onPresence: " + localPath);
                Message message = mHandler.obtainMessage();
                message.what = PRESENCE;
                message.obj = localPath;
                mHandler.sendMessage(message);
            }


            @Override
            public void onFailure(final String erroInfo) {
                Log.e(TAG, "onFailure: " + erroInfo);
                Message message = mHandler.obtainMessage();
                message.what = FAILURE;
                message.obj = erroInfo;
                mHandler.sendMessage(message);
            }
        });
    }

}
