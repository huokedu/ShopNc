package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.yokey.nsg.adapter.HomeListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SpecialActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String special_id;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView tipsTextView;
    private RecyclerView mListView;
    private HomeListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        special_id = mActivity.getIntent().getStringExtra("special_id");
        if (TextUtil.isEmpty(special_id)) {
            ToastUtil.show(mActivity, "传入的参数有误");
            mApplication.finishActivity(mActivity);
            return;
        }

        mArrayList = new ArrayList<>();
        mAdapter = new HomeListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
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
                if (tipsTextView.getText().toString().equals("读取专题数据失败\n\n点击重试")) {
                    getJson();
                }
            }
        });

    }

    private void getJson() {

        if (mArrayList.isEmpty()) {
            tipsTextView.setText("加载中...");
            tipsTextView.setVisibility(View.VISIBLE);
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "index");
        ajaxParams.put("op", "special");
        ajaxParams.put("special_id", special_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {

                            mArrayList.clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));

                            if (jsonArray.length() == 0) {
                                titleTextView.setText("专题");
                                tipsTextView.setText("暂无数据\n\n稍后再来看看吧");
                                tipsTextView.setVisibility(View.VISIBLE);
                                return;
                            }

                            titleTextView.setText(jsonObject.getString("special_desc"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                jsonObject = (JSONObject) jsonArray.get(i);
                                Iterator iterator = jsonObject.keys();
                                String keys = iterator.next().toString();
                                String value = jsonObject.getString(keys);
                                hashMap.put("keys", keys);
                                hashMap.put("value", value);
                                mArrayList.add(hashMap);
                            }

                            if (mArrayList.isEmpty()) {
                                tipsTextView.setText("暂无数据\n\n稍后再来看看吧");
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

        tipsTextView.setText("读取专题数据失败\n\n点击重试");
        tipsTextView.setVisibility(View.VISIBLE);

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}