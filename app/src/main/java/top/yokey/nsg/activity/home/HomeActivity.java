package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.yokey.nsg.adapter.HomeListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class HomeActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView scanImageView;
    private EditText titleEditText;
    private ImageView messageImageView;

    private TextView tipsTextView;
    private RecyclerView mListView;
    private HomeListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_home);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        scanImageView = (ImageView) findViewById(R.id.scanImageView);
        titleEditText = (EditText) findViewById(R.id.keywordEditText);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleEditText.setFocusable(false);

        mArrayList = new ArrayList<>();
        mAdapter = new HomeListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getJson();

    }

    private void initEven() {

        scanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, ScanActivity.class));
            }
        });

        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, SearchActivity.class));
            }
        });

        messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, ChatListActivity.class));
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getJson();
                    }
                }, 1000);
            }
        });

    }

    private void getJson() {

        //读取数据
        mApplication.mFinalHttp.get(mApplication.apiUrlString, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList.clear();
                            JSONArray jsonArray = new JSONArray(data);
                            if (jsonArray.length() == 0) {
                                tipsTextView.setVisibility(View.VISIBLE);
                                tipsTextView.setText("没有数据");
                                return;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                Iterator iterator = jsonObject.keys();
                                String keys = iterator.next().toString();
                                String value = jsonObject.getString(keys);
                                hashMap.put("keys", keys);
                                hashMap.put("value", value);
                                mArrayList.add(hashMap);
                                //添加导航
                                if (i == 0) {
                                    hashMap = new HashMap<>();
                                    hashMap.put("keys", "nav");
                                    mArrayList.add(hashMap);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            tipsTextView.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getJsonFailure();
                        }
                    } else {
                        getJsonFailure();
                    }
                } else {
                    getJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                super.onFinish();
                getJson();
            }
        }.start();

    }

}