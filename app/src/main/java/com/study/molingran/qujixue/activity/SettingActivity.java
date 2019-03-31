package com.study.molingran.qujixue.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.fragment.SettingFragment;
/**
 * @author MoLingran
 * 承载设置页面活动
 * 即将弃用
 */
public class SettingActivity extends AppCompatActivity {
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        imgBtnBack = findViewById(R.id.imgbtn_back);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.setting_fragment, new SettingFragment())
                .commit();

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
