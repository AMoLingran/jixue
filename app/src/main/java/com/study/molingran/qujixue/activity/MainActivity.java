package com.study.molingran.qujixue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.study.molingran.qujixue.R;
import com.study.molingran.qujixue.fragment.DiscoverFragment;
import com.study.molingran.qujixue.fragment.HomeFragment;
import com.study.molingran.qujixue.fragment.MineFragment;
import com.study.molingran.qujixue.fragment.StudyFragment;


/**
 * @author MoLingran
 * 主活动：顾名思义
 */

public class MainActivity extends AppCompatActivity {

    /**
     * 单列模式必须重写的方法
     * 参照： https://www.cnblogs.com/622698abc/archive/2013/04/14/3020150.html
     */
    @Override
    protected void onNewIntent(Intent intent) {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                    return true;
                case R.id.navigation_discover:

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new DiscoverFragment())
                            .commit();
                    return true;
                case R.id.navigation_study:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new StudyFragment())
                            .commit();
                    return true;
                case R.id.navigation_mine:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new MineFragment())
                            .commit();
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
