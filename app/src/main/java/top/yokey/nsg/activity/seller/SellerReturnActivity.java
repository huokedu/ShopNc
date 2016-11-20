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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.SellerReturnListAdapter;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class SellerReturnActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView refundTextView;
    private SellerReturnListAdapter refundAdapter;
    private SwipeRefreshLayout refundSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> refundArrayList;

    private TextView refundBackTextView;
    private SellerReturnListAdapter refundBackAdapter;
    private SwipeRefreshLayout refundBackSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> refundBackArrayList;


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
        setContentView(R.layout.activity_seller_return);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getRefundBack();
        getRefund();
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

        titleTextView.setText("退货");

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        RecyclerView[] mListView = new RecyclerView[2];
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("售前退货");
        mTitleList.add("售后退货");

        refundArrayList = new ArrayList<>();
        refundTextView = (TextView) mViewList.get(0).findViewById(R.id.tipsTextView);
        mListView[0] = (RecyclerView) mViewList.get(0).findViewById(R.id.mainListView);
        refundAdapter = new SellerReturnListAdapter(mApplication, mActivity, refundArrayList);
        refundSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(0).findViewById(R.id.mainSwipeRefreshLayout);
        mListView[0].setLayoutManager(new LinearLayoutManager(this));
        ControlUtil.setSwipeRefreshLayout(refundSwipeRefreshLayout);
        mListView[0].setAdapter(refundAdapter);

        refundBackArrayList = new ArrayList<>();
        refundBackTextView = (TextView) mViewList.get(1).findViewById(R.id.tipsTextView);
        mListView[1] = (RecyclerView) mViewList.get(1).findViewById(R.id.mainListView);
        refundBackAdapter = new SellerReturnListAdapter(mApplication, mActivity, refundBackArrayList);
        refundBackSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(1).findViewById(R.id.mainSwipeRefreshLayout);
        mListView[1].setLayoutManager(new LinearLayoutManager(this));
        ControlUtil.setSwipeRefreshLayout(refundBackSwipeRefreshLayout);
        mListView[1].setAdapter(refundBackAdapter);

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        refundTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refundTextView.getText().toString().contains("订单数据加载失败")) {
                    refundArrayList.clear();
                    refundAdapter.notifyDataSetChanged();
                    getRefund();
                }
            }
        });

        refundSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refundArrayList.clear();
                        refundAdapter.notifyDataSetChanged();
                        getRefund();
                    }
                }, 1000);
            }
        });

        refundBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refundBackTextView.getText().toString().contains("订单数据加载失败")) {
                    refundBackArrayList.clear();
                    refundBackAdapter.notifyDataSetChanged();
                    getRefundBack();
                }
            }
        });

        refundBackSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refundBackArrayList.clear();
                        refundBackAdapter.notifyDataSetChanged();
                        getRefundBack();
                    }
                }, 1000);
            }
        });

    }

    private void getRefund() {

        refundTextView.setText("加载中...");

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_return");
        ajaxParams.putOp("return_list");
        ajaxParams.put("lock", "2");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            refundArrayList.clear();
                            JSONArray jsonArray = new JSONArray(mApplication.getJsonData(o.toString()));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                refundArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.getString(i))));
                            }
                            if (refundArrayList.isEmpty()) {
                                refundTextView.setText("暂无售前退货订单\n\n一会再来看看吧");
                                refundTextView.setVisibility(View.VISIBLE);
                            } else {
                                refundTextView.setVisibility(View.GONE);
                            }
                            refundSwipeRefreshLayout.setRefreshing(false);
                            refundAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getRefundFailure();
                        }
                    } else {
                        getRefundFailure();
                    }
                } else {
                    getRefundFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRefundFailure();
            }
        });

    }

    private void getRefundFailure() {

        refundAdapter.notifyDataSetChanged();
        refundSwipeRefreshLayout.setRefreshing(false);
        refundTextView.setText("订单数据加载失败\n\n点击重试");

    }

    private void getRefundBack() {

        refundBackTextView.setText("加载中...");

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_return");
        ajaxParams.putOp("return_list");
        ajaxParams.put("lock", "1");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            refundBackArrayList.clear();
                            JSONArray jsonArray = new JSONArray(mApplication.getJsonData(o.toString()));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                refundBackArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.getString(i))));
                            }
                            if (refundBackArrayList.isEmpty()) {
                                refundBackTextView.setText("暂无售后退货订单\n\n一会再来看看吧");
                                refundBackTextView.setVisibility(View.VISIBLE);
                            } else {
                                refundBackTextView.setVisibility(View.GONE);
                            }
                            refundBackSwipeRefreshLayout.setRefreshing(false);
                            refundBackAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getRefundBackFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getRefundBackFailure();
                    }
                } else {
                    getRefundBackFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRefundBackFailure();
            }
        });

    }

    private void getRefundBackFailure() {

        refundBackAdapter.notifyDataSetChanged();
        refundBackSwipeRefreshLayout.setRefreshing(false);
        refundBackTextView.setText("订单数据加载失败\n\n点击重试");

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}