package com.study.molingran.qujixue.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import cn.bmob.v3.BmobUser;
import com.study.molingran.qujixue.QRCodePopupWindow;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.activity.AboutActivity;
import com.study.molingran.qujixue.activity.LoginActivity;
import com.study.molingran.qujixue.activity.SettingActivity;
import com.study.molingran.qujixue.tools.update.AppCheckUpdate;


/**
 * @author MoLingran
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "MineFragment";

    private int counter = 0;

    private SharedPreferences mSetting;
    private Context mContext;

    private Window window;

    private TextView tvUsername;
    private ImageButton imgBtnSetting;
    private ImageButton imgBtnScan;
    private ImageView ivUpdateReminder;
    private LinearLayout llAccount;
    private LinearLayout llBinding;
    private LinearLayout llCollection;
    private LinearLayout llFeedback;
    private LinearLayout llAbout;
    private LinearLayout llShare;
    private LinearLayout llLogout;


    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * 在活动停止时设置回状态栏颜色
     */
    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getActivity().getColor(R.color.colorBackground));
        }
    }

    /**
     * 在活动启动时设置状态栏颜色
     */
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getActivity().getColor(R.color.white));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        tvUsername = view.findViewById(R.id.tv_username);
        imgBtnScan = view.findViewById(R.id.imgbtn_scan);
        imgBtnSetting = view.findViewById(R.id.imgbtn_setting);
        ivUpdateReminder = view.findViewById(R.id.iv_update_reminder);
        llAccount = view.findViewById(R.id.ll_account);
        llBinding = view.findViewById(R.id.ll_binding);
        llCollection = view.findViewById(R.id.ll_collection);
        llFeedback = view.findViewById(R.id.ll_feedback);
        llAbout = view.findViewById(R.id.ll_about);
        llShare = view.findViewById(R.id.ll_share);
        llLogout = view.findViewById(R.id.ll_logout);

        mContext = getContext();
        mSetting = (mContext).getSharedPreferences("userdata", 0);
        window = getActivity().getWindow();

        tvUsername.setText(mSetting.getString("nickname", "用户名"));

        imgBtnScan.setOnClickListener(this);
        imgBtnSetting.setOnClickListener(this);
        llAccount.setOnClickListener(this);
        llBinding.setOnClickListener(this);
        llCollection.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llLogout.setOnClickListener(this);

        //更新小红点
        if (AppCheckUpdate.getShare(mContext).getBoolean("needUpdate", false)) {
            ivUpdateReminder.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == imgBtnScan) {
            Toast.makeText(mContext, "扫码以后再做（咕咕咕）", Toast.LENGTH_SHORT).show();
        }
        if (v == imgBtnSetting) {
            startActivity(new Intent(mContext, SettingActivity.class));
        }
        if (v == llAccount) {
            Toast.makeText(mContext, "暂时放在右上角的设置里", Toast.LENGTH_SHORT).show();
        }
        if (v == llBinding) {
            Toast.makeText(mContext, "尚未完成", Toast.LENGTH_SHORT).show();
        }
        if (v == llCollection) {
            Toast.makeText(mContext, "尚未完成", Toast.LENGTH_SHORT).show();
        }
        if (v == llFeedback) {
            Toast.makeText(mContext, "尚未完成", Toast.LENGTH_SHORT).show();
        }
        if (v == llAbout) {
            startActivity(new Intent(mContext, AboutActivity.class));
        }
        if (v == llShare) {
            new QRCodePopupWindow(mContext).showAtBottom(llShare);
        }
        if (v == llLogout) {
            logOut();
        }

    }

    /**
     * 退出登陆方法
     */
    private void logOut() {
        counter++;
        if (counter == 2) {
            BmobUser.logOut();   //清除缓存用户对象
            startActivity(new Intent(mContext, LoginActivity.class));
            mContext.getSharedPreferences("userdata",0).edit().putBoolean("autoLogin", false).apply();
            getActivity().finish();
        } else {
            Toast.makeText(mContext, "再点一次退出", Toast.LENGTH_SHORT).show();
        }
    }

}
