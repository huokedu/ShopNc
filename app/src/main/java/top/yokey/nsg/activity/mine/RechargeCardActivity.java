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
import android.widget.EditText;
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
import top.yokey.nsg.adapter.RechargeCardListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class RechargeCardActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView moneyTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView mTextView;
    private RechargeCardListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    private EditText snEditText;
    private TextView confirmTextView;

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
        setContentView(R.layout.activity_recharge_card);
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

        moneyTextView = (TextView) findViewById(R.id.moneyTextView);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("充值卡");

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_recharge_card, null));
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("充值记录");
        mTitleList.add("充值卡充值");

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mArrayList = new ArrayList<>();
        mAdapter = new RechargeCardListAdapter(mArrayList);
        mTextView = (TextView) mViewList.get(0).findViewById(R.id.tipsTextView);
        RecyclerView mListView = (RecyclerView) mViewList.get(0).findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(0).findViewById(R.id.mainSwipeRefreshLayout);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        snEditText = (EditText) mViewList.get(1).findViewById(R.id.snEditText);
        confirmTextView = (TextView) mViewList.get(1).findViewById(R.id.confirmTextView);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getDeposit();
        getRcbLog();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTextView.getText().toString().equals("数据加载失败\n\n点击重试")) {
                    getRcbLog();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRcbLog();
                    }
                }, 1000);
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rechargeCard();
            }
        });

    }

    private void getDeposit() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("my_asset");
        ajaxParams.put("fields", "available_rc_balance");

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
                            String temp = "余额 ￥ " + jsonObject.getString("available_rc_balance");
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

    private void rechargeCard() {

        String rc_sn = snEditText.getText().toString();

        if (TextUtil.isEmpty(rc_sn)) {
            ToastUtil.show(mActivity, "充值卡号为空");
            return;
        }

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("rechargecard_add");
        ajaxParams.put("rc_sn", rc_sn);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    if (mApplication.getJsonSuccess(o.toString())) {
                        ToastUtil.showSuccess(mActivity);
                        snEditText.setText("");
                        getDeposit();
                        getRcbLog();
                    } else {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            ToastUtil.show(mActivity, jsonObject.getString("error"));
                        } catch (JSONException e) {
                            ToastUtil.showFailure(mActivity);
                            e.printStackTrace();
                        }
                    }
                } else {
                    ToastUtil.showFailure(mActivity);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
            }
        });

    }

    private void getRcbLog() {

        mTextView.setText("加载中...");
        mTextView.setVisibility(View.VISIBLE);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("rcblog");

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
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("log_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList.isEmpty()) {
                                mTextView.setText("暂无数据");
                                mTextView.setVisibility(View.VISIBLE);
                            } else {
                                mTextView.setVisibility(View.GONE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getRcbLogFailure();
                        }
                    } else {
                        getRcbLogFailure();
                    }
                } else {
                    getRcbLogFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRcbLogFailure();
            }
        });

    }

    private void getRcbLogFailure() {

        mTextView.setText("数据加载失败\n\n点击重试");
        mSwipeRefreshLayout.setRefreshing(false);
        mTextView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}