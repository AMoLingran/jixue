package com.study.molingran.qujixue.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.tools.GetAppInfo;
import com.study.molingran.qujixue.tools.bomb.LogInfo;
import com.study.molingran.qujixue.tools.bomb.User;

/**
 * @author MoLingran
 * 登陆活动：登陆账号，记录启动者信息
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "LoginActivity";
    
    public static LoginActivity pInstance;

    private final int PHONE_LENGTH = 11;
    private String mYeek = "<a href=\"http://m.yeek.top\">一客首页</a>";

    private SharedPreferences mUserData;
    private Activity mActivity;

    private TextView tvYeek;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserData = getSharedPreferences("userdata", 0);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnTest = findViewById(R.id.btn_test);
        tvYeek = findViewById(R.id.tv_yeek);

        //标记实例 可以在异地关闭 LoginActivity
        pInstance = this;
        mActivity = LoginActivity.this;

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnTest.setOnClickListener(this);

        tvYeek.setText(Html.fromHtml(mYeek));
        tvYeek.setMovementMethod(LinkMovementMethod.getInstance());

        etUsername.setText(mUserData.getString("username", ""));


        //bmob初始化
        Bmob.initialize(this, "9790406d627ca9bbd55161dcd82494c5");
        //登记启动信息
        LogInfo logInfo = new LogInfo();
        logInfo.setName(TAG);
        logInfo.setIMIE(GetAppInfo.getIMIE(mActivity));
        logInfo.setNumber(GetAppInfo.getTelNumber(mActivity));
        logInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

                if (e == null) {
                    Log.e(TAG, "done: 添加数据成功 \nIMIE:" + GetAppInfo.getIMIE(mActivity) + "\n手机号:" + GetAppInfo.getTelNumber(mActivity));
                } else {
                    Log.e(TAG, "done: 创建数据失败：" + e + "+" + s);
                }
            }
        });

        //用户管理初始化
        BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
        //如果有缓存且勾选了自动登陆则直接进入 MainActivity
        if (bmobUser != null & mUserData.getBoolean("autoLogin", false)) {
            startActivity(new Intent(mActivity, MainActivity.class));
            finish();
        }
        //检查是否有效输入（主要是让登录按钮变色）
        inputTextListener(etPassword);
        inputTextListener(etUsername);
    }


    /**
     * 获取输入框的文本
     *
     * @param editText 输入框
     * @return 输入框的文本
     */
    private String getInput(EditText editText) {
        return editText.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        if (v == btnTest) {
            startActivity(new Intent(mActivity, MainActivity.class));
            finish();
        }
        if (v == btnLogin) {
            BmobUser bu = new BmobUser();
            bu.setUsername(getInput(etUsername));
            bu.setPassword(getInput(etPassword));
            bu.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e == null) {
                        Toast.makeText(mActivity, "登录成功", Toast.LENGTH_SHORT).show();
                        mUserData.edit().putBoolean("autoLogin", true).apply();
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                    } else {
                        if ("errorCode:101,errorMsg:username or password incorrect.".equals(e.toString())) {
                            Toast.makeText(mActivity, "登录失败,账号或密码不正确", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "错误代码" + e, Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "done: " + e);
                    }
                }
            });
        }
        if (v == btnRegister) {
            startActivity(new Intent(mActivity, RegisterActivity.class));

        }
    }

    /**
     * 注册输入文本监听器
     * (个人写法，怎么舒服怎么来)
     */
    private void inputTextListener(EditText editText) {
        //https://www.cnblogs.com/Free-Thinker/p/6839276.html
        editText.addTextChangedListener(new TextWatcher() {
            //变化前的字符
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //变化的字符
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果输入框变化了，就改变按钮
                changedButton();
            }

            //变化后的字符
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //

    /**
     * 改变按钮状态
     */
    private void changedButton() {
        if ((getInput(etUsername).length() == PHONE_LENGTH && !getInput(etPassword).isEmpty())) {
            //都不为空则则可点击（已激活）
            btnLogin.setEnabled(true);
        } else {
            //其他情况都不可点击（未激活）
            btnLogin.setEnabled(false);
        }

    }


}
