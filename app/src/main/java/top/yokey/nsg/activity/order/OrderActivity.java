package top.yokey.nsg.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.OrderListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class OrderActivity extends AppCompatActivity {

    public static Activity mActivity;
    public static NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static TextView[] mTextView;
    public static OrderListAdapter[] mAdapter;
    public static SwipeRefreshLayout[] mSwipeRefreshLayout;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            getJson();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_viewpager);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("我的订单");

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("全部");
        mTitleList.add("待支付");
        mTitleList.add("待发货");
        mTitleList.add("待收货");
        mTitleList.add("待评价");
        mTitleList.add("已删除");
        mTextView = new TextView[mTitleList.size()];
        mAdapter = new OrderListAdapter[mTitleList.size()];
        RecyclerView[] mListView = new RecyclerView[mTitleList.size()];
        mSwipeRefreshLayout = new SwipeRefreshLayout[mTitleList.size()];
        for (int i = 0; i < mTitleList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            mTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.tipsTextView);
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mSwipeRefreshLayout[i] = (SwipeRefreshLayout) mViewList.get(i).findViewById(R.id.mainSwipeRefreshLayout);
            mAdapter[i] = new OrderListAdapter(mApplication, mActivity, mApplication.orderArrayList[i]);
            mListView[i].setLayoutManager(new LinearLayoutManager(this));
            ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout[i]);
            mListView[i].setAdapter(mAdapter[i]);
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //根据传进来的值设置位置
        int position = mActivity.getIntent().getIntExtra("position", 0);
        mViewPager.setCurrentItem(position);
        setControl();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnActivity();
            }
        });

        for (SwipeRefreshLayout swipeRefreshLayout : mSwipeRefreshLayout) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        for (final TextView textView : mTextView) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textView.toString().equals("订单数据加载失败\n\n点击重试")) {
                        textView.setText("加载中...");
                        getJson();
                    }
                }
            });
        }

    }

    public static void getJson() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_order");
        ajaxParams.putOp("order_list");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("order_group_list"));
                            for (ArrayList arrayList : mApplication.orderArrayList) {
                                arrayList.clear();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                JSONArray order_list = new JSONArray(hashMap.get("order_list"));
                                jsonObject = (JSONObject) order_list.get(0);
                                if (jsonObject.getString("delete_state").equals("0")) {
                                    mApplication.orderArrayList[0].add(hashMap);
                                }
                                if (jsonObject.getString("delete_state").equals("1")) {
                                    mApplication.orderArrayList[5].add(hashMap);
                                }
                            }
                            for (int i = 0; i < mApplication.orderArrayList[0].size(); i++) {
                                try {
                                    JSONArray order_list = new JSONArray(mApplication.orderArrayList[0].get(i).get("order_list"));
                                    jsonObject = (JSONObject) order_list.get(0);
                                    if (jsonObject.getString("delete_state").equals("0")) {
                                        switch (jsonObject.getString("order_state")) {
                                            case "10":
                                                mApplication.orderArrayList[1].add(mApplication.orderArrayList[0].get(i));
                                                break;
                                            case "20":
                                                mApplication.orderArrayList[2].add(mApplication.orderArrayList[0].get(i));
                                                break;
                                            case "30":
                                                mApplication.orderArrayList[3].add(mApplication.orderArrayList[0].get(i));
                                                break;
                                            case "40":
                                                if (jsonObject.getString("evaluation_state").equals("0")) {
                                                    mApplication.orderArrayList[4].add(mApplication.orderArrayList[0].get(i));
                                                }
                                                break;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            setControl();
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

    public static void setControl() {

        for (int i = 0; i < mApplication.orderArrayList.length; i++) {
            if (mApplication.orderArrayList[i].isEmpty()) {
                mTextView[i].setVisibility(View.VISIBLE);
                mTextView[i].setText("暂无订单");
            } else {
                mTextView[i].setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < mSwipeRefreshLayout.length; i++) {
            mSwipeRefreshLayout[i].setRefreshing(false);
            mAdapter[i].notifyDataSetChanged();
        }

    }

    public static void getJsonFailure() {

        for (TextView textView : mTextView) {
            textView.setText("订单数据加载失败\n\n点击重试");
        }

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}