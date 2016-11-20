package top.yokey.nsg.activity.mine;

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
import top.yokey.nsg.adapter.GoodsCollectionListAdapter;
import top.yokey.nsg.adapter.GoodsFootListAdapter;
import top.yokey.nsg.adapter.StoreCollectionListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CollectionActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView delImageView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView goodsTextView;
    private GoodsCollectionListAdapter goodsAdapter;
    private SwipeRefreshLayout goodsSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> goodsArrayList;

    private TextView storeTextView;
    private StoreCollectionListAdapter storeAdapter;
    private SwipeRefreshLayout storeSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> storeArrayList;

    private TextView footTextView;
    private GoodsFootListAdapter footAdapter;
    private SwipeRefreshLayout footSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> footArrayList;

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
        setContentView(R.layout.activity_viewpager);
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
        delImageView = (ImageView) findViewById(R.id.moreImageView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("我的收藏");

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("商品收藏");
        mTitleList.add("店铺收藏");
        mTitleList.add("我的足迹");
        for (int i = 0; i < mTitleList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        //商品
        goodsTextView = (TextView) mViewList.get(0).findViewById(R.id.tipsTextView);
        RecyclerView goodsListView = (RecyclerView) mViewList.get(0).findViewById(R.id.mainListView);
        goodsSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(0).findViewById(R.id.mainSwipeRefreshLayout);
        goodsArrayList = new ArrayList<>();
        goodsAdapter = new GoodsCollectionListAdapter(mApplication, mActivity, goodsArrayList);
        goodsListView.setLayoutManager(new LinearLayoutManager(this));
        goodsListView.setAdapter(goodsAdapter);
        //店铺
        storeTextView = (TextView) mViewList.get(1).findViewById(R.id.tipsTextView);
        RecyclerView storeListView = (RecyclerView) mViewList.get(1).findViewById(R.id.mainListView);
        storeSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(1).findViewById(R.id.mainSwipeRefreshLayout);
        storeArrayList = new ArrayList<>();
        storeAdapter = new StoreCollectionListAdapter(mApplication, mActivity, storeArrayList);
        storeListView.setLayoutManager(new LinearLayoutManager(this));
        storeListView.setAdapter(storeAdapter);
        //足迹
        footTextView = (TextView) mViewList.get(2).findViewById(R.id.tipsTextView);
        RecyclerView footListView = (RecyclerView) mViewList.get(2).findViewById(R.id.mainListView);
        footSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(2).findViewById(R.id.mainSwipeRefreshLayout);
        footArrayList = new ArrayList<>();
        footAdapter = new GoodsFootListAdapter(mApplication, mActivity, footArrayList);
        footListView.setLayoutManager(new LinearLayoutManager(this));
        footListView.setAdapter(footAdapter);
        //根据传进来的值设置位置
        int position = mActivity.getIntent().getIntExtra("position", 0);
        mViewPager.setCurrentItem(position);
        switch (position) {
            case 0:
                getGoods();
                getStore();
                getFootprint();
                break;
            case 1:
                getStore();
                getGoods();
                getFootprint();
                break;
            case 2:
                delImageView.setImageResource(R.mipmap.ic_action_del);
                getFootprint();
                getStore();
                getGoods();
                break;
            default:
                getGoods();
                getStore();
                getFootprint();
                break;
        }

        ControlUtil.setSwipeRefreshLayout(goodsSwipeRefreshLayout);
        ControlUtil.setSwipeRefreshLayout(storeSwipeRefreshLayout);
        ControlUtil.setSwipeRefreshLayout(footSwipeRefreshLayout);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnActivity();
            }
        });

        goodsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getGoods();
                    }
                }, 1000);
            }
        });

        goodsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goodsTextView.getText().toString().equals("数据加载失败\n\n点击重试")) {
                    goodsTextView.setText("加载中...");
                    getGoods();
                }
            }
        });

        goodsAdapter.setOnItemChange(new GoodsCollectionListAdapter.onItemChange() {
            @Override
            public void onChange() {
                getGoods();
            }
        });

        storeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStore();
                    }
                }, 1000);
            }
        });

        storeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storeTextView.getText().toString().equals("数据加载失败\n\n点击重试")) {
                    storeTextView.setText("加载中...");
                    getStore();
                }
            }
        });

        footSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFootprint();
                    }
                }, 1000);
            }
        });

        footTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (footTextView.getText().toString().equals("数据加载失败\n\n点击重试")) {
                    footTextView.setText("加载中...");
                    getFootprint();
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    delImageView.setImageResource(R.mipmap.ic_action_del);
                } else {
                    delImageView.setImageBitmap(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 2) {
                    DialogUtil.query(
                            mActivity,
                            "确认您的选择",
                            "清空足迹记录？",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DialogUtil.cancel();
                                    DialogUtil.progress(mActivity);
                                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                                    ajaxParams.putAct("member_goodsbrowse");
                                    ajaxParams.putOp("browse_clearall");
                                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            super.onSuccess(o);
                                            DialogUtil.cancel();
                                            if (TextUtil.isJson(o.toString())) {
                                                String error = mApplication.getJsonError(o.toString());
                                                if (TextUtil.isEmpty(error)) {
                                                    ToastUtil.showSuccess(mActivity);
                                                    getFootprint();
                                                } else {
                                                    ToastUtil.showFailure(mActivity);
                                                }
                                            } else {
                                                ToastUtil.showFailure(mActivity);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                                            super.onFailure(t, errorNo, strMsg);
                                            ToastUtil.showFailure(mActivity);
                                            DialogUtil.cancel();
                                        }
                                    });
                                }
                            }
                    );
                }
            }
        });

    }

    private void getGoods() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_favorites");
        ajaxParams.putOp("favorites_list");
        ajaxParams.put("page", "100");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            goodsArrayList.clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("favorites_list"));
                            for (int i = 0; i < jsonArray.length(); i += 2) {
                                jsonObject = (JSONObject) jsonArray.get(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("fav_id_1", jsonObject.getString("fav_id"));
                                hashMap.put("store_id_1", jsonObject.getString("store_id"));
                                hashMap.put("goods_id_1", jsonObject.getString("goods_id"));
                                hashMap.put("goods_name_1", jsonObject.getString("goods_name"));
                                hashMap.put("goods_price_1", "￥" + jsonObject.getString("goods_price"));
                                hashMap.put("goods_image_url_1", jsonObject.getString("goods_image_url"));
                                if ((i + 1) < jsonArray.length()) {
                                    jsonObject = (JSONObject) jsonArray.get(i + 1);
                                    hashMap.put("fav_id_2", jsonObject.getString("fav_id"));
                                    hashMap.put("store_id_2", jsonObject.getString("store_id"));
                                    hashMap.put("goods_id_2", jsonObject.getString("goods_id"));
                                    hashMap.put("goods_name_2", jsonObject.getString("goods_name"));
                                    hashMap.put("goods_price_2", "￥" + jsonObject.getString("goods_price"));
                                    hashMap.put("goods_image_url_2", jsonObject.getString("goods_image_url"));
                                } else {
                                    hashMap.put("fav_id_2", "");
                                    hashMap.put("store_id_2", "");
                                    hashMap.put("goods_id_2", "");
                                    hashMap.put("goods_name_2", "");
                                    hashMap.put("goods_price_2", "");
                                    hashMap.put("goods_image_url_2", "");
                                }
                                goodsArrayList.add(hashMap);
                            }
                            if (goodsArrayList.isEmpty()) {
                                goodsTextView.setVisibility(View.VISIBLE);
                                goodsTextView.setText("暂无数据!");
                            } else {
                                goodsTextView.setVisibility(View.GONE);
                            }
                            goodsSwipeRefreshLayout.setRefreshing(false);
                            goodsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            goodsTextView.setText("数据加载失败\n\n点击重试");
                            e.printStackTrace();
                        }
                    } else {
                        goodsTextView.setText("数据加载失败\n\n点击重试");
                    }
                } else {
                    goodsTextView.setText("数据加载失败\n\n点击重试");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                goodsTextView.setText("数据加载失败\n\n点击重试");
            }
        });

    }

    private void getStore() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_favorites_store");
        ajaxParams.putOp("favorites_list");
        ajaxParams.put("page", "100");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            storeArrayList.clear();
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("favorites_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                storeArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (storeArrayList.isEmpty()) {
                                storeTextView.setVisibility(View.VISIBLE);
                                storeTextView.setText("没有数据！");
                            } else {
                                storeTextView.setVisibility(View.GONE);
                            }
                            storeSwipeRefreshLayout.setRefreshing(false);
                            storeAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            storeTextView.setText("数据加载失败\n\n点击重试");
                            e.printStackTrace();
                        }
                    } else {
                        storeTextView.setText("数据加载失败\n\n点击重试");
                    }
                } else {
                    storeTextView.setText("数据加载失败\n\n点击重试");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                storeTextView.setText("数据加载失败\n\n点击重试");
            }
        });

    }

    private void getFootprint() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_goodsbrowse");
        ajaxParams.putOp("browse_list");
        ajaxParams.put("page", "100");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            footArrayList.clear();
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("goodsbrowse_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                footArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (footArrayList.isEmpty()) {
                                footTextView.setVisibility(View.VISIBLE);
                                footTextView.setText("没有数据");
                            } else {
                                footTextView.setVisibility(View.GONE);
                            }
                            footSwipeRefreshLayout.setRefreshing(false);
                            footAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            footTextView.setText("数据加载失败\n\n点击重试");
                            e.printStackTrace();
                        }
                    } else {
                        footTextView.setText("数据加载失败\n\n点击重试");
                    }
                } else {
                    footTextView.setText("数据加载失败\n\n点击重试");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                footTextView.setText("数据加载失败\n\n点击重试");
            }
        });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}