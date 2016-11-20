package top.yokey.nsg.activity.seller;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.SellerOrderListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class SellerOrderActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView[] mTextView;
    private SellerOrderListAdapter[] mAdapter;
    private SwipeRefreshLayout[] mSwipeRefreshLayout;

    private ArrayList<HashMap<String, String>> allArrayList;
    private ArrayList<HashMap<String, String>>[] mArrayList;

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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_seller_order);
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

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("店铺订单");

        List<View> mViewList = new ArrayList<>();
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

        allArrayList = new ArrayList<>();
        mTextView = new TextView[mTitleList.size()];
        mArrayList = new ArrayList[mTitleList.size()];
        mAdapter = new SellerOrderListAdapter[mTitleList.size()];
        RecyclerView[] mListView = new RecyclerView[mTitleList.size()];
        mSwipeRefreshLayout = new SwipeRefreshLayout[mTitleList.size()];
        for (int i = 0; i < mTitleList.size(); i++) {
            mArrayList[i] = new ArrayList<>();
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            mTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.tipsTextView);
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mSwipeRefreshLayout[i] = (SwipeRefreshLayout) mViewList.get(i).findViewById(R.id.mainSwipeRefreshLayout);
            mAdapter[i] = new SellerOrderListAdapter(mApplication, mActivity, mArrayList[i]);
            mListView[i].setLayoutManager(new LinearLayoutManager(this));
            ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout[i]);
            mListView[i].setAdapter(mAdapter[i]);
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            allArrayList.clear();
                            for (ArrayList arrayList : mArrayList) {
                                arrayList.clear();
                            }
                            for (SellerOrderListAdapter adapter : mAdapter) {
                                adapter.notifyDataSetChanged();
                            }
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
                    if (textView.getText().toString().contains("订单数据加载失败")) {
                        allArrayList.clear();
                        for (ArrayList arrayList : mArrayList) {
                            arrayList.clear();
                        }
                        for (SellerOrderListAdapter adapter : mAdapter) {
                            adapter.notifyDataSetChanged();
                        }
                        getJson();
                    }
                }
            });
        }

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_list");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            allArrayList.clear();
                            for (ArrayList arrayList : mArrayList) {
                                arrayList.clear();
                            }
                            JSONObject jsonObject = new JSONObject(mApplication.getJsonData(o.toString()));
                            jsonObject = new JSONObject(jsonObject.getString("order_list"));
                            Iterator iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next().toString();
                                allArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString(key))));
                            }
                            if (allArrayList.isEmpty()) {
                                for (TextView textView : mTextView) {
                                    textView.setText("暂无订单");
                                    textView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                for (int i = 0; i < allArrayList.size(); i++) {
                                    mArrayList[0].add(allArrayList.get(i));
                                    if (allArrayList.get(i).get("delete_state").equals("0")) {
                                        switch (allArrayList.get(i).get("order_state")) {
                                            case "10":
                                                mArrayList[1].add(allArrayList.get(i));
                                                break;
                                            case "20":
                                                mArrayList[2].add(allArrayList.get(i));
                                                break;
                                            case "30":
                                                mArrayList[3].add(allArrayList.get(i));
                                                break;
                                            case "40":
                                                if (allArrayList.get(i).get("evaluation_state").equals("0")) {
                                                    mArrayList[4].add(allArrayList.get(i));
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < mArrayList.length; i++) {
                                if (mArrayList[i].isEmpty()) {
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
                DialogUtil.cancel();
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        for (int i = 0; i < mSwipeRefreshLayout.length; i++) {
            mSwipeRefreshLayout[i].setRefreshing(false);
            mAdapter[i].notifyDataSetChanged();
        }

        for (TextView textView : mTextView) {
            textView.setText("订单数据加载失败\n\n点击重试");
        }

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}