package com.study.molingran.qujixue.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.tools.bomb.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 易嘉庆 MoLingran
 * 注册活动：注册账号
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private final int SUCCESS = 0;
    private final int FAILURE = -1;
    private final int PHONE_LENGTH = 11;
    private final int CODE_LENGTH = 6;
    private String phone;
    private boolean phoneOK = false;

    private Activity mActivity;

    private EditText etUsername;
    private EditText etPassword;
    private EditText etRePassword;
    private EditText etCode;
    private Button btnRegisterOK;
    private Button btnGetCode;
    private ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
        etCode = findViewById(R.id.et_code);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_repassword);
        btnRegisterOK = findViewById(R.id.btn_Register_OK);
        btnGetCode = findViewById(R.id.btn_getCode);
        imgBtnBack = findViewById(R.id.imgbtn_back);

        mActivity = RegisterActivity.this;

        //注册点击监听器
        imgBtnBack.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);
        btnRegisterOK.setOnClickListener(this);

        //注册输入监听器
        inputTextListener(etUsername);
        inputTextListener(etPassword);
        inputTextListener(etCode);
        inputTextListener(etRePassword);
    }


    /**
     * 获取输入框文本
     *
     * @param view 输入框
     * @return 输入框的文本
     */
    private String getInput(EditText view) {
        return view.getText().toString().trim();
    }


    /**
     * 点击监听器
     * （个人写法，怎么舒服怎么来）
     */
    @Override
    public void onClick(View v) {
        if (v == imgBtnBack) {
            finish();
        }
        if (v == btnGetCode) {
            if (checkAccount()) {
                BmobSMS.requestSMSCode(getInput(etUsername), "default", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer smsId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(mActivity, "短信发送成功", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "done: 发送验证码成功，短信ID：" + smsId);
                            Log.e(TAG, "done: 电话号码" + getInput(etUsername));
                        } else {
                            Toast.makeText(mActivity, "发送验证码失败"+"" +
                                    "\ncode:"+e.getErrorCode()+
                                    "\nmsg:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "done: 发送验证码失败，+" + e.getErrorCode() + "-" + e.getMessage());
                        }
                    }
                });
                phone = getInput(etUsername);
                //计时开始
                downTimer.start();
                //防止重复发送
                btnGetCode.setEnabled(false);
                //记录获取验证码时的账号，之后进行验证
                //获取完验证码转到下一个输入框
                etCode.requestFocus();
            } else {
                Toast.makeText(RegisterActivity.this, "请检查你的手机号", Toast.LENGTH_SHORT).show();
            }

        }

        if (v == btnRegisterOK) {
            //验证码和注册检查双重验证
            if (checkAccountData() & phoneOK) {
                // bmob 验证通过开始注册
                BmobUser bu = new BmobUser();
                bu.setUsername(phone);
                bu.setPassword(getInput(etPassword));
                bu.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User s, BmobException e) {
                        if (e == null) {
                            Log.e(TAG, "done: 注册成功");
                            //注册成功记录用户资料
                            SharedPreferences.Editor editor = getSharedPreferences("userdata", 0).edit();
                            editor.putString("username", getInput(etUsername));
                            editor.putString("nickname", getInput(etUsername));
                            editor.putString("password", getInput(etPassword));
                            editor.putBoolean("autoLogin", true);
                            editor.apply();
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                            //注册完后关闭登录界面 加个if避免空对象崩溃。
                            if (LoginActivity.pInstance != null) {
                                //在LoginActivity加个变量可以在其他Activity关闭LoginActivity
                                LoginActivity.pInstance.finish();
                            }
                        } else {
                            Log.e(TAG, "done: 注册失败-" + e);
                        }
                    }
                });
                //写入用户资料

            } else {
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 注册输入文本监听器
     * (个人写法，怎么舒服怎么来)
     */
    private void inputTextListener(final EditText view) {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //每次输入都检查注册按钮是否可用
                changedButton();

                //对于账号框
                if (view == etUsername) {
                    //如果长度为11位则可以点击 获取验证码 按钮
                    if (getInput(etUsername).length() == PHONE_LENGTH) {
                        btnGetCode.setEnabled(true);
                    } else {
                        btnGetCode.setEnabled(false);
                    }
                }
                //对于验证码框
                if (view == etCode) {
                    //输入6位数字后
                    if (s.length() == CODE_LENGTH) {
                        //验证验证码
                        checkPhone();
                        //同时焦点移到下一个输入框
                        etPassword.requestFocus();
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 改变按钮状态
     */
    private void changedButton() {
        //任意一个输入框为空则注册按钮不可用
        if (!(getInput(etUsername).isEmpty() ||
                getInput(etCode).isEmpty() ||
                getInput(etPassword).isEmpty() ||
                getInput(etRePassword).isEmpty())) {
            btnRegisterOK.setEnabled(true);
        } else {
            btnRegisterOK.setEnabled(false);
        }
    }

    /**
     * 检查账号名是否正确
     *
     * @return 账号名正确 返回 true
     */
    private boolean checkAccount() {
        //正则表达式
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        String account = getInput(etUsername);
        Matcher matcher = Pattern
                .compile(regex)
                .matcher(account);
        //返回查找结果
        return matcher.find();
    }

    /**
     * 验证码重新获取
     * millisInFuture 毫秒数
     * countDownInterval 返回时间间隔
     */
    private CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {

        //定期返回剩余值
        @Override
        public void onTick(long l) {
            btnGetCode.setText("已发送(" + (l / 1000) + "s)");
        }

        //完成时进行的方法
        @Override
        public void onFinish() {
            btnGetCode.setText("重新获取");
            btnGetCode.setEnabled(true);
        }
    };

    /**
     * 处理验证码结果
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    phoneOK = true;
                    Toast.makeText(mActivity, "验证码验证成功", Toast.LENGTH_SHORT).show();
                    break;
                case FAILURE:
                    phoneOK = false;
                    Toast.makeText(mActivity, "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * bmob 检查验证码
     */
    private void checkPhone() {
        final Message message = mHandler.obtainMessage();
        Log.e(TAG, "checkPhone: " + getInput(etUsername));
        BmobSMS.verifySmsCode(getInput(etUsername), getInput(etCode), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    message.what = SUCCESS;
                    mHandler.sendMessage(message);
                } else {
                    message.what = FAILURE;
                    mHandler.sendMessage(message);
                    Toast.makeText(mActivity, "验证码错误", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "done: 验证码错误：" + e.getErrorCode() + "-" + e.getMessage());
                }
            }
        });
    }

    /**
     * 检查注册的账号资料
     *
     * @return 如果通过检查 返回 true
     */
    private boolean checkAccountData() {
        boolean cancel = false;
        View focusView = null;

        //检查密码输入是否正确
        if (!getInput(etPassword).equals(getInput(etRePassword))) {
            focusView = etRePassword;
            etRePassword.setError("你的两次密码不一样！");
            cancel = true;
        }


        if (cancel) {
            // 如果出现错误；不尝试注册，并将焦点放到第一个错误字段上，返回 FALSE。
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }


}
