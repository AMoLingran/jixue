package com.study.molingran.qujixue.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.study.molingran.qujixue.NewVersionPopupWindow;
import com.study.molingran.qujixue.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MoLingran
 */
public class ArticleListFragment extends Fragment {
    private TextView tvNull;
    private SharedPreferences mUpdate;
    //    +
    private List<ArticleBean> datas = null;
    private ListView lvArticle;
    private SetAdapter mSetAdapter;

    public ArticleListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        datas = new ArrayList<>();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        lvArticle = (ListView) view.findViewById(R.id.lv_article);
        mUpdate = getContext().getSharedPreferences("update", 0);
        //登陆界面后检查更新
        if (mUpdate.getBoolean("needUpdate", false)) {
            new NewVersionPopupWindow(getActivity()).showAtBottom(tvNull);
        }
        /**
         * 后期修改写法
         */
        mSetAdapter = new SetAdapter(this.getContext(), datas);
        lvArticle.setAdapter(mSetAdapter);
        getDatasFromNetwork();
        return view;
    }


    class ArticleBean {
        private String title;
        private String article;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }
    }


    class SetAdapter extends BaseAdapter {
        private List<ArticleBean> dataList;
        private LayoutInflater mInflater;


        SetAdapter(Context mContext, List<ArticleBean> dataList) {
            this.dataList = dataList;
            mInflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ArticleListFragment.SetAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_article_item, null);
                holder = new ArticleListFragment.SetAdapter.ViewHolder();
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvArticle = convertView.findViewById(R.id.tv_article);
                convertView.setTag(holder);
            } else {
                holder = (ArticleListFragment.SetAdapter.ViewHolder) convertView.getTag();
            }
            //绑定数据到控件
            bindViewData(position, holder);
            return convertView;
        }

        class ViewHolder {
            private TextView tvTitle;
            private TextView tvArticle;
        }

        private void bindViewData(int position, ArticleListFragment.SetAdapter.ViewHolder holder) {
            holder.tvTitle.setText(dataList.get(position).getTitle());
            holder.tvArticle.setText(dataList.get(position).getArticle());
        }

        private void bindViewClickListener(int position, ArticleListFragment.SetAdapter.ViewHolder holder){

        }
    }

    /**
     * 后期服务器获取数据解析json
     */
    private List<ArticleBean> analyzeJson(String json) {
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<ArticleBean>>() {
        }.getType();
        return gson.fromJson(json, userListType);
    }

    /**
     * 用于测试数据，后期改为从服务器获取数据
     * 模仿   https://blog.csdn.net/amir_zt/article/details/70240519
     */
    private List<ArticleBean> getDatasFromNetwork() {
        final String PLACE[] = {
                "德克士(新世幻环球中心店)", "肯德基(九方餐厅)", "第18区海鲜拼盘", "邓家面馆",
                "锦水缘餐厅", "麦地里(中海店)", "可可豆汤", "临江门火锅店",
                "若水河鲜", "自贡鸿鹤鲜锅兔", "又见曾毛肚老火锅"
        };
        final String TIME[] = {
                "2017-04-08", "2017-04-09", "2017-04-10", "2017-04-11",
                "2017-04-12", "2017-04-13", "2017-04-14", "2017-04-15",
                "2017-04-16", "2017-04-17", "2017-04-18"
        };

        for (int i = 0; i < PLACE.length; i++) {
            ArticleBean restaurant = new ArticleBean();
            restaurant.setTitle(PLACE[i]);
            restaurant.setArticle(TIME[i]);
            datas.add(restaurant);
        }
        //刷新列表，如果是真实的网络数据，则放到请求回调函数当中使用。
        //注意如果是在异步线程中，应该怎么使用？它必须在主线程中执行！！
        mSetAdapter.notifyDataSetChanged();
        return datas;
    }
}
