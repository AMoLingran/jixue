package com.study.molingran.qujixue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.study.molingran.qujixue.NewVersionPopupWindow;
import com.study.molingran.qujixue.R;


/**
 * @author MoLingran
 */
public class StudyFragment extends Fragment {
    private Button btnTest;


    public StudyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        btnTest = view.findViewById(R.id.btn_test);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewVersionPopupWindow(getActivity()).showAtBottom(btnTest);
            }
        });

        return view;
    }



}

