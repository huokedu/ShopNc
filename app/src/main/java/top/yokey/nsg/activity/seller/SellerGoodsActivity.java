package top.yokey.nsg.activity.seller;

import android.app.Activity;
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
import top.yokey.nsg.adapter.SellerGoodsOfflineListAdapter;
import top.yokey.nsg.adapter.SellerGoodsOnlineListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class SellerGoodsActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView addImageView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView[] tipsTextView;
    private SwipeRefreshLayout[] mSwipeRefreshLayout;

    private SellerGoodsOnlineListAdapter onlineAdapter;
    private ArrayList<HashMap<String, String>> onlineArrayList;

    private SellerGoodsOfflineListAdapter offlineAdapter;
    private ArrayList<HashMap<String, String>> offlineArrayList;

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
        setContentView(R.layout.activity_seller_goods);
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
        addImageView = (ImageView) findViewById(R.id.moreImageView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("商品");
        addImageView.setImageResource(R.mipmap.ic_action_add);

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("出售中");
        mTitleList.add("已下架");
        tipsTextView = new TextView[2];
        RecyclerView[] mListView = new RecyclerView[2];
        mSwipeRefreshLayout = new SwipeRefreshLayout[2];
        for (int i = 0; i < mTitleList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            tipsTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.tipsTextView);
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mSwipeRefreshLayout[i] = (SwipeRefreshLayout) mViewList.get(i).findViewById(R.id.mainSwipeRefreshLayout);
            ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout[i]);
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        //上线中
        onlineArrayList = new ArrayList<>();
        onlineAdapter = new SellerGoodsOnlineListAdapter(mApplication, mActivity, onlineArrayList);
        mListView[0].setLayoutManager(new LinearLayoutManager(this));
        mListView[0].setAdapter(onlineAdapter);

        //已下架
        offlineArrayList = new ArrayList<>();
        offlineAdapter = new SellerGoodsOfflineListAdapter(mApplication, mActivity, offlineArrayList);
        mListView[1].setLayoutManager(new LinearLayoutManager(this));
        mListView[1].setAdapter(offlineAdapter);

        //读取数据
        getOnlineJson();
        getOfflineJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        mSwipeRefreshLayout[0].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOnlineJson();
                    }
                }, 1000);
            }
        });

        tipsTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView[0].getText().toString().equals("读取商品数据失败\n\n点击重试")) {
                    getOnlineJson();
                }
            }
        });

        mSwipeRefreshLayout[1].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOfflineJson();
                    }
                }, 1000);
            }
        });

        tipsTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView[1].getText().toString().equals("读取商品数据失败\n\n点击重试")) {
                    getOfflineJson();
                }
            }
        });

    }

    private void getOnlineJson() {

        if (onlineArrayList.isEmpty()) {
            tipsTextView[0].setText("加载中...");
            tipsTextView[0].setVisibility(View.VISIBLE);
        }

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_goods");
        ajaxParams.putOp("goods_list");
        ajaxParams.put("goods_type", "online");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            if (data.contains("[]")) {
                                tipsTextView[0].setText("暂无商品\n\n一会再来看看吧！");
                                tipsTextView[0].setVisibility(View.VISIBLE);
                            } else {
                                onlineArrayList.clear();
                                JSONObject jsonObject = new JSONObject(data);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    onlineArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                                }
                                if (onlineArrayList.isEmpty()) {
                                    tipsTextView[0].setText("暂无商品\n\n一会再来看看吧！");
                                    tipsTextView[0].setVisibility(View.VISIBLE);
                                } else {
                                    tipsTextView[0].setVisibility(View.GONE);
                                }
                            }
                            mSwipeRefreshLayout[0].setRefreshing(false);
                            onlineAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getOnlineJsonFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getOnlineJsonFailure();
                    }
                } else {
                    getOnlineJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getOnlineJsonFailure();
            }
        });

    }

    private void getOnlineJsonFailure() {

        tipsTextView[0].setText("读取商品数据失败\n\n点击重试");
        tipsTextView[0].setVisibility(View.VISIBLE);
        mSwipeRefreshLayout[0].setRefreshing(false);
        onlineAdapter.notifyDataSetChanged();

    }

    private void getOfflineJson() {

        if (offlineArrayList.isEmpty()) {
            tipsTextView[1].setText("加载中...");
            tipsTextView[1].setVisibility(View.VISIBLE);
        }

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_goods");
        ajaxParams.putOp("goods_list");
        ajaxParams.put("goods_type", "offline");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            if (data.contains("[]")) {
                                tipsTextView[1].setText("暂无商品\n\n一会再来看看吧！");
                                tipsTextView[1].setVisibility(View.VISIBLE);
                            } else {
                                offlineArrayList.clear();
                                JSONObject jsonObject = new JSONObject(data);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    offlineArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                                }
                                if (offlineArrayList.isEmpty()) {
                                    tipsTextView[1].setText("暂无商品\n\n一会再来看看吧！");
                                    tipsTextView[1].setVisibility(View.VISIBLE);
                                } else {
                                    tipsTextView[1].setVisibility(View.GONE);
                                }
                            }
                            mSwipeRefreshLayout[1].setRefreshing(false);
                            offlineAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getOfflineJsonFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getOfflineJsonFailure();
                    }
                } else {
                    getOfflineJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getOfflineJsonFailure();
            }
        });

    }

    private void getOfflineJsonFailure() {

        tipsTextView[1].setText("读取商品数据失败\n\n点击重试");
        tipsTextView[1].setVisibility(View.VISIBLE);
        mSwipeRefreshLayout[1].setRefreshing(false);
        offlineAdapter.notifyDataSetChanged();

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}