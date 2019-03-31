package com.study.molingran.qujixue.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.activity.SetAvatarActivity;

/**
 * @author MoLingran
 *  设置fragment
 *  即将弃用
 */
public class SettingFragment extends PreferenceFragment {

    private SharedPreferences mUserdata;

    private EditTextPreference etpNickname;
    private Preference pSetAvatar;
    private SwitchPreference spAutoLogin;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        getPreferenceManager().setSharedPreferencesName("userdata");

        etpNickname = (EditTextPreference) findPreference("nickname");
        pSetAvatar = findPreference("setAvatar");
        spAutoLogin = (SwitchPreference)findPreference("autoLogin");

        mUserdata = getPreferenceManager().getSharedPreferences();

        spAutoLogin.setChecked(mUserdata.getBoolean("autoLogin",false));
        etpNickname.setText(mUserdata.getString("nickname", ""));
        etpNickname.setSummary("当前昵称："+mUserdata.getString("nickname", ""));
        pSetAvatar.setSummary("*尚未完成*");

        etpNickname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!newValue.toString().isEmpty()){
                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    return true;
                }else {
                    Toast.makeText(getActivity(), "不能为空", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });

        pSetAvatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), SetAvatarActivity.class));
                return false;
            }
        });

    }
}
