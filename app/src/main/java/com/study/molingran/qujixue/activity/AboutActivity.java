package com.study.molingran.qujixue.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.molingran.qujixue.NewVersionPopupWindow;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.tools.GetAppInfo;
import com.study.molingran.qujixue.tools.update.AppCheckUpdate;

/**
 * @author MoLingran
 * 关于活动：当前版本 更新历史 检查更新 联系开发者
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private String mYeek = "<a href=\"http://m.yeek.top\">一客首页</a>";
    private String mUpdateLink = "<a href=\"http://yeek.top/qujixue/getApp/趣积学.apk\">获取应用</a>";
    private int nowVersion;
    private int newVersion;

    private SharedPreferences mUpdate;

    private ImageView imgBtnBack;
    private LinearLayout llHistory;
    private LinearLayout llCheckVersion;
    private LinearLayout llContact;
    private TextView tvVersion;
    private TextView tvNewVersion;
    private TextView tvContent;
    private TextView tvYeek;
    private TextView tvUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        imgBtnBack = findViewById(R.id.imgbtn_back);
        llHistory = findViewById(R.id.ll_history);
        llCheckVersion = findViewById(R.id.ll_check_version);
        llContact = findViewById(R.id.ll_contact);
        tvVersion = findViewById(R.id.tv_article);
        tvNewVersion = findViewById(R.id.tv_new_version);
        tvContent = findViewById(R.id.tv_contact);
        tvYeek = findViewById(R.id.tv_yeek);
        tvUpdate = findViewById(R.id.tv_update);

        mUpdate = this.getSharedPreferences("update", 0);
        nowVersion = GetAppInfo.getVersionCode(this);
        newVersion = mUpdate.getInt("versionCode", 0);

        tvVersion.setText("Version " + GetAppInfo.getVersionName(this));

        //设置超链接
        tvYeek.setText(Html.fromHtml(mYeek));
        tvYeek.setMovementMethod(LinkMovementMethod.getInstance());
        tvUpdate.setText(Html.fromHtml(mUpdateLink));
        tvUpdate.setMovementMethod(LinkMovementMethod.getInstance());

        imgBtnBack.setOnClickListener(this);
        llHistory.setOnClickListener(this);
        llCheckVersion.setOnClickListener(this);
        llContact.setOnClickListener(this);

        //如果有更新就提醒一下
        if (newVersion > nowVersion) {
            tvNewVersion.setText("新版本：" + mUpdate.getString("version", " "));
            tvNewVersion.setTextColor(this.getColor(R.color.red));
        }

    }

    @Override
    public void onClick(View v) {
        if (v == imgBtnBack) {
            finish();
        }
        if (v == llHistory) {
            startActivity(new Intent(this, HistoricalUpdateActivity.class));
        }
        if (v == llCheckVersion) {
            new AppCheckUpdate(this, true);
            if (newVersion > nowVersion) {
                new NewVersionPopupWindow(this).showAtBottom(imgBtnBack);
            }
        }
        if (v == llContact) {
            tvContent.setText("molingran@yeek.top");
        }
    }
}
