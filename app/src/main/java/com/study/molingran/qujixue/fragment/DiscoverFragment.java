package com.study.molingran.qujixue.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.study.molingran.qujixue.R;


/**
 * @author MoLingran
 */
public class DiscoverFragment extends Fragment{
    private static final String TAG = "DiscoverFragment";

    private Context mContext;
    private Intent mIntent;

    private ProgressBar pbProgressBar;
    private TextView tvSpeed;
    private Button btnGo;
    private Button btnTest;


    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        btnGo = view.findViewById(R.id.btn_go);
        pbProgressBar = view.findViewById(R.id.pb_progressBar);
        tvSpeed = view.findViewById(R.id.tv_speed);
        btnTest = view.findViewById(R.id.btn_test);

        this.mContext = getContext();

        return view;
    }




}
