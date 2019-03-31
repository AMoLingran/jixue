package com.study.molingran.qujixue.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.tools.GetAppInfo;
import com.study.molingran.qujixue.tools.update.AppCheckUpdate;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.util.List;

/**
 * @author MoLingran
 * 启动活动：检查APP授权
 */
public class LaunchActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "LaunchActivity";

    private final int LOADING_TIME = 450;
    private final int REQUEST_NUMBER = 3;
    private int counter = 0;
    private String[] perms = {
            //读写
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //手机信息
            Manifest.permission.READ_PHONE_STATE};

    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        tvVersion = findViewById(R.id.tv_article);

        tvVersion.setText("Version " + GetAppInfo.getVersionName(this));

        new AppCheckUpdate(this, false);

    }

    /**
     * 从其他页面回来后再次检查权限
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        //如果请求达到 3 次则直接退出
        if (counter == REQUEST_NUMBER) {
            finish();
        }
        counter++;
        checkPermission();
    }

    /**
     * 启动登陆活动方法
     */
    private void startActivity() {
        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 执行耗时任务后启动登陆活动
     */
    private void task() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity();
                    }
                });
            }
        }).start();
    }

    /**
     * 等待 LOADING_TIME 秒后启动登陆活动
     */
    private void timing() {
        //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity();
            }
        }, LOADING_TIME);
    }

    /**
     * 检查权限方法
     */
    private void checkPermission() {

        if (EasyPermissions.hasPermissions(this, perms)) {
            //如果有权限则直接启动
            timing();
        } else {
            //没有权限就用 EasyPermissions 的 requestPermissions 申请权限
            //因为简单的请求对话框不能对取消点击做出反应，所以手动设置请求对话框
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 1, perms)
                    .setRationale("这是软件运行的必须权限")
                    .setPositiveButtonText("好的")
                    .setNegativeButtonText("")
                    .build());
            //EasyPermissions.requestPermissions(this, "这是软件运行的必须权限", 1, perms);
        }

    }

    /**
     * 重写 onRequestPermissionsResult 方法处理请求结果
     * 这里用 EasyPermissions 来处理请求结果
     *
     * @param requestCode  请求业务代码，返回 requestPermissions 中的代码
     * @param permissions  请求权限的字符串数组
     * @param grantResults 请求结果 0授予 -1不授予
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 重新 onPermissionsGranted 方法处理请求成功
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        checkPermission();
    }

    /**
     * 重新 onPermissionsDenied 方法处理请求失败
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //对于被勾选“不再提醒”后拒绝的权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("APP无法正常启动")
                    .setRationale("您禁止了某些权限，让我帮您传送至设置页面开启吧。")
                    .setPositiveButton("好的")
                    .setNegativeButton("不用了")
                    .build()
                    .show();

        }
        //一般被拒绝的则重新检查（获取）
        checkPermission();
    }
}
