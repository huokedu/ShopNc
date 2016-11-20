package top.yokey.nsg.activity.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import top.yokey.nsg.activity.home.ChatListActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ScanActivity;
import top.yokey.nsg.activity.home.SearchActivity;
import top.yokey.nsg.adapter.CircleListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class CircleActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView scanImageView;
    private EditText titleEditText;
    private ImageView messageImageView;
    private FloatingActionButton createButton;

    private TextView tipsTextView;
    private TextView stateTextView;
    private RecyclerView mListView;
    private CircleListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_circle);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        scanImageView = (ImageView) findViewById(R.id.scanImageView);
        titleEditText = (EditText) findViewById(R.id.keywordEditText);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);
        createButton = (FloatingActionButton) findViewById(R.id.createButton);

        stateTextView = (TextView) findViewById(R.id.stateTextView);
        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        messageImageView.setImageResource(R.mipmap.ic_action_message);
        stateTextView.setVisibility(View.GONE);

        mArrayList = new ArrayList<>();
        mAdapter = new CircleListAdapter(mApplication, mActivity, mArrayList);
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

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, CircleCreateActivity.class));
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

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView.getText().toString().equals("读取圈子数据失败\n\n点击重试")) {
                    getJson();
                }
                if (tipsTextView.getText().toString().equals("暂无圈子\n\n点击创建一个吧")) {
                    mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, CircleCreateActivity.class));
                }
            }
        });

    }

    private void getJson() {

        tipsTextView.setText("加载中...");
        tipsTextView.setVisibility(View.VISIBLE);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + "act=circle", new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            mArrayList.clear();
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("circle_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList.isEmpty()) {
                                tipsTextView.setText("暂无圈子\n\n点击创建一个吧");
                                tipsTextView.setVisibility(View.VISIBLE);
                            } else {
                                tipsTextView.setVisibility(View.GONE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
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

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();

        tipsTextView.setText("读取圈子数据失败\n\n点击重试");
        tipsTextView.setVisibility(View.VISIBLE);

    }

}