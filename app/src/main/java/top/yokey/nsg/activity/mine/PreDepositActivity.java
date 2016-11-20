package top.yokey.nsg.activity.mine;

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
import top.yokey.nsg.adapter.PreDepositBalListAdapter;
import top.yokey.nsg.adapter.PreDepositLogListAdapter;
import top.yokey.nsg.adapter.PreDepositRecListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class PreDepositActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView addImageView;

    private TextView moneyTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView[] mTextView;
    private RecyclerView[] mListView;
    private SwipeRefreshLayout[] mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>>[] mArrayList;

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
        setContentView(R.layout.activity_pre_deposit);
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

        moneyTextView = (TextView) findViewById(R.id.moneyTextView);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("预存款");
        addImageView.setImageResource(R.mipmap.ic_action_add);

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("账户余额");
        mTitleList.add("充值明细");
        mTitleList.add("提现记录");
        mTextView = new TextView[mTitleList.size()];
        mArrayList = new ArrayList[mTitleList.size()];
        mListView = new RecyclerView[mTitleList.size()];
        mSwipeRefreshLayout = new SwipeRefreshLayout[mTitleList.size()];
        for (int i = 0; i < mTitleList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            mTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.tipsTextView);
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mSwipeRefreshLayout[i] = (SwipeRefreshLayout) mViewList.get(i).findViewById(R.id.mainSwipeRefreshLayout);
            mListView[i].setLayoutManager(new LinearLayoutManager(this));
            ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout[i]);
            mArrayList[i] = new ArrayList<>();
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        getDeposit();
        getDepositLog();
        getDepositRec();
        getPdCashList();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isEmpty(mApplication.userHashMap.get("member_paypwd"))) {
                    DialogUtil.query(mActivity, "确认您的选择", "您尚未设置支付密码，是否设置?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, MineCenterActivity.class));
                        }
                    });
                } else {
                    mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, PreDepositCashActivity.class));
                }
            }
        });

        mTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTextView[0].getText().toString().equals("数据加载失败\n\n点击重试")) {
                    getDepositLog();
                }
            }
        });

        mTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTextView[1].getText().toString().equals("数据加载失败\n\n点击重试")) {
                    getDepositRec();
                }
            }
        });

        mTextView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTextView[2].getText().toString().equals("数据加载失败\n\n点击重试")) {
                    getPdCashList();
                }
            }
        });

        for (final SwipeRefreshLayout swipeRefreshLayout : mSwipeRefreshLayout) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 1000);
                }
            });
        }

    }

    private void getDeposit() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("my_asset");
        ajaxParams.put("fields", "predepoit");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            String temp = "余额 ￥ " + jsonObject.getString("predepoit");
                            moneyTextView.setText(temp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getDepositFailure();
                        }
                    } else {
                        getDepositFailure();
                    }
                } else {
                    getDepositFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getDepositFailure();
            }
        });

    }

    private void getDepositFailure() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "读取数据失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getDeposit();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void getDepositLog() {

        mTextView[0].setText("加载中...");
        mTextView[0].setVisibility(View.VISIBLE);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("predepositlog");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[0].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList[0].add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList[0].isEmpty()) {
                                mTextView[0].setText("暂无数据");
                                mTextView[0].setVisibility(View.VISIBLE);
                            } else {
                                mTextView[0].setVisibility(View.GONE);
                                mSwipeRefreshLayout[0].setRefreshing(false);
                                mListView[0].setAdapter(new PreDepositLogListAdapter(mActivity, mArrayList[0]));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getDepositLogFailure();
                        }
                    } else {
                        getDepositLogFailure();
                    }
                } else {
                    getDepositLogFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getDepositLogFailure();
            }
        });

    }

    private void getDepositLogFailure() {

        mTextView[0].setText("数据加载失败\n\n点击重试");
        mTextView[0].setVisibility(View.VISIBLE);

    }

    private void getDepositRec() {

        mTextView[1].setText("加载中...");
        mTextView[1].setVisibility(View.VISIBLE);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("pdrechargelist");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[1].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList[1].add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList[1].isEmpty()) {
                                mTextView[1].setText("暂无数据");
                                mTextView[1].setVisibility(View.VISIBLE);
                            } else {
                                mTextView[1].setVisibility(View.GONE);
                                mSwipeRefreshLayout[1].setRefreshing(false);
                                mListView[1].setAdapter(new PreDepositRecListAdapter(mActivity, mArrayList[1]));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getDepositRecFailure();
                        }
                    } else {
                        getDepositRecFailure();
                    }
                } else {
                    getDepositRecFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getDepositRecFailure();
            }
        });

    }

    private void getDepositRecFailure() {

        mTextView[1].setText("数据加载失败\n\n点击重试");
        mTextView[1].setVisibility(View.VISIBLE);

    }

    private void getPdCashList() {

        mTextView[2].setText("加载中...");
        mTextView[2].setVisibility(View.VISIBLE);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("pdcashlist");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[2].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList[2].add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList[2].isEmpty()) {
                                mTextView[2].setText("暂无数据");
                                mTextView[2].setVisibility(View.VISIBLE);
                            } else {
                                mTextView[2].setVisibility(View.GONE);
                                mSwipeRefreshLayout[2].setRefreshing(false);
                                mListView[2].setAdapter(new PreDepositBalListAdapter(mActivity, mArrayList[2]));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getPdCashListFailure();
                        }
                    } else {
                        getPdCashListFailure();
                    }
                } else {
                    getPdCashListFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getPdCashListFailure();
            }
        });

    }

    private void getPdCashListFailure() {

        mTextView[2].setText("数据加载失败\n\n点击重试");
        mTextView[2].setVisibility(View.VISIBLE);

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}