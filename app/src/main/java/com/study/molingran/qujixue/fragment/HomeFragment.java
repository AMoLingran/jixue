package com.study.molingran.qujixue.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.study.molingran.qujixue.R;


/**
 * @author MoLingran
 */
public class HomeFragment extends Fragment {
    private PagerSlidingTabStrip pstTitle;
    private ViewPager vpPager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        pstTitle = view.findViewById(R.id.pst_title);
        vpPager = view.findViewById(R.id.vp_pager);
        setTitle();
        return view;
    }

    private void setTitle() {
        FragmentManager fragmentManager = getChildFragmentManager();
        vpPager.setAdapter(new Adapter(fragmentManager));
        vpPager.setCurrentItem(0);
        // 设置 PagerSlidingTabStrip （我觉得这个pagerView的开源包并不算好用）
        // 貌似这个更加好用 https://www.jianshu.com/p/2865812fed41
        pstTitle.setViewPager(vpPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pstTitle.setUnderlineColor((getActivity()).getColor(R.color.transparent));
            pstTitle.setTextColor(getActivity().getColor(R.color.black87));
            pstTitle.setIndicatorColor(getActivity().getColor(R.color.colorTheme));
            pstTitle.setDividerColor((getActivity()).getColor(R.color.transparent));
        }else {
            pstTitle.setUnderlineColor(getResources().getColor(R.color.transparent));
            pstTitle.setTextColor(getResources().getColor(R.color.black87));
            pstTitle.setIndicatorColor(getResources().getColor(R.color.colorTheme));
            pstTitle.setDividerColor((getResources()).getColor(R.color.transparent));
        }
        pstTitle.setShouldExpand(true);
        pstTitle.setIndicatorHeight(5);
        pstTitle.setTextSize(50);
    }

    /**
     * FragmentPagerAdapter 设置
     */
    class Adapter extends FragmentPagerAdapter {
        String[] title = {"文章", "视频"};

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new ArticleListFragment();
                case 1:
                    return new VideoFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return title.length;
        }
    }
}

