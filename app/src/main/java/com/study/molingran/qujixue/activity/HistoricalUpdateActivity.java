package com.study.molingran.qujixue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.study.molingran.qujixue.R;

import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MoLingran
 * 更新历史活动：展示历来更新（ListView练手）
 */
public class HistoricalUpdateActivity extends AppCompatActivity {
    public static final String TAG = "Historical";

    private final int SUCCESS = 200;
    private final int NO = -1;
    private final int OK = 0;

    private Context mContext;
    private ImageButton imgBtnBack;
    private ListView lvHistorical;
    private ProgressBar pb;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO:
                    Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
                case OK:

                    List<HistoricalBean> dataList = analyzeJson(String.valueOf(msg.obj));
                    lvHistorical.setAdapter(new SetAdapter(mContext, dataList));
                    pb.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_update);

        imgBtnBack = findViewById(R.id.imgbtn_back);
        lvHistorical = findViewById(R.id.lv_article);
        pb = findViewById(R.id.pb);

        mContext = HistoricalUpdateActivity.this;

        pb.setIndeterminate(true);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getList();
    }

    /**
     * OkHttp获取网络链接
     */
    private void getList() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        检查网络是否可用
        if (!(networkInfo == null)) {
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = "https://www.yeek.top/qujixue/getApp/old/oldVersion.txt";
            //String url = "https://www.yeek.top/qujixue/getApp/version.txt";
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            Call newCall = client.newCall(request);
            newCall.enqueue(new Callback() {
                Message message = mHandler.obtainMessage();

                @Override
                public void onFailure(Call call, IOException e) {
                    message.what = NO;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == SUCCESS) {
                        assert response.body() != null;
                        message.obj = response.body().string();
                        message.what = OK;
                    } else {
                        message.what = NO;
                    }

                    mHandler.sendMessage(message);
                }
            });
        } else {
            Toast.makeText(mContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用Gson解析Json字符串
     *
     * @param json Json字符串
     */
    private List<HistoricalBean> analyzeJson(String json) {
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<HistoricalBean>>() {
        }.getType();
        return gson.fromJson(json, userListType);
    }

    /**
     * 内部类：用于解析Json
     */
    class HistoricalBean {

        /**
         * version : 0.2.3.0213
         * content : 将检查更新扩充为“关于”界面
         */

        private String version;
        private String content;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * 内部类：列表适配器
     * 参照：https://blog.csdn.net/amir_zt/article/details/70240519
     */
    class SetAdapter extends BaseAdapter {
        private List<HistoricalBean> dataList;
        private LayoutInflater mInflater;


        SetAdapter(Context mContext, List<HistoricalBean> dataList) {
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

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_historical_update_item, null);
                holder = new ViewHolder();
                holder.tvContent = convertView.findViewById(R.id.tv_content);
                holder.tvVersion = convertView.findViewById(R.id.tv_article);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //绑定数据到控件
            bindViewData(position, holder);
            return convertView;
        }

        class ViewHolder {
            private TextView tvContent;
            private TextView tvVersion;
        }

        private void bindViewData(int position, ViewHolder holder) {
            holder.tvContent.setText(dataList.get(position).getContent());
            holder.tvVersion.setText(dataList.get(position).getVersion());
        }
    }
}
