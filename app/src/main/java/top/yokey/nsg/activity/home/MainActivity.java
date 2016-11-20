package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.man.ManActivity;
import top.yokey.nsg.activity.mine.LoginActivity;
import top.yokey.nsg.activity.mine.MineActivity;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class MainActivity extends CheckActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private int[][] mInt;
    private long firstTime = 0;
    private TextView[] mTextView;
    public static TabHost mTabHost;

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
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
            getInfo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        mTabHost = (TabHost) findViewById(R.id.mainTabHost);
        mTextView = new TextView[5];
        mTextView[0] = (TextView) findViewById(R.id.homeTextView);
        mTextView[1] = (TextView) findViewById(R.id.categoryTextView);
        mTextView[2] = (TextView) findViewById(R.id.manTextView);
        mTextView[3] = (TextView) findViewById(R.id.cartTextView);
        mTextView[4] = (TextView) findViewById(R.id.mineTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        mInt = new int[2][5];
        mInt[0][0] = R.mipmap.ic_nav_main_home;
        mInt[0][1] = R.mipmap.ic_nav_main_category;
        mInt[0][2] = R.mipmap.ic_nav_main_man;
        mInt[0][3] = R.mipmap.ic_nav_main_cart;
        mInt[0][4] = R.mipmap.ic_nav_main_mine;
        mInt[1][0] = R.mipmap.ic_nav_main_home_press;
        mInt[1][1] = R.mipmap.ic_nav_main_category_press;
        mInt[1][2] = R.mipmap.ic_nav_main_man_press;
        mInt[1][3] = R.mipmap.ic_nav_main_cart_press;
        mInt[1][4] = R.mipmap.ic_nav_main_mine_press;

        mTabHost.setup(this.getLocalActivityManager());
        mTabHost.addTab(mTabHost.newTabSpec("Home").setIndicator("Home").setContent(new Intent(mActivity, HomeActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Category").setIndicator("Category").setContent(new Intent(mActivity, CategoryActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Man").setIndicator("Man").setContent(new Intent(mActivity, ManActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Cart").setIndicator("Cart").setContent(new Intent(mActivity, CartActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("Mine").setIndicator("Mine").setContent(new Intent(mActivity, MineActivity.class)));
        mTabHost.setCurrentTab(4);
        mTabHost.setCurrentTab(3);
        mTabHost.setCurrentTab(2);
        mTabHost.setCurrentTab(1);
        mTabHost.setCurrentTab(0);

        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
            getInfo();
        }

    }

    private void initEven() {

        for (int i = 0; i < mTextView.length; i++) {
            final int pos = i;
            mTextView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTab(pos);
                }
            });
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "Home":
                        updateTab(0);
                        break;
                    case "Category":
                        updateTab(1);
                        break;
                    case "Circle":
                        updateTab(2);
                        break;
                    case "Cart":
                        updateTab(3);
                        break;
                    case "Mine":
                        updateTab(4);
                        break;
                }
            }
        });

    }

    private void updateTab(int i) {

        mTabHost.setCurrentTab(i);

        for (int j = 0; j < mTextView.length; j++) {
            mTextView[j].setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
            mTextView[j].setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, mInt[0][j]), null, null);
        }

        mTextView[i].setTextColor(ContextCompat.getColor(mActivity, R.color.main));
        mTextView[i].setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, mInt[1][i]), null, null);

    }

    private void getInfo() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
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
                            mApplication.userHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("member_info")));
                            mApplication.mSharedPreferencesEditor.putString("user_username", mApplication.userHashMap.get("member_name"));
                            mApplication.mSharedPreferencesEditor.putString("user_id", mApplication.userHashMap.get("member_id"));
                            mApplication.userIdString = mApplication.userHashMap.get("member_id");
                            mApplication.mSharedPreferencesEditor.apply();
                            getVoucherList();
                            getRedPackList();
                            getOrderList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getInfoFailure();
                        }
                    } else {
                        ToastUtil.show(mActivity, error);
                        mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                    }
                } else {
                    getInfoFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getInfoFailure();
            }
        });

    }

    private void getInfoFailure() {

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                super.onFinish();
                getInfoFailure();
            }
        }.start();

    }

    private void getOrderList() {

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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            getOrderListFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getOrderListFailure();
                    }
                } else {
                    getOrderListFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getOrderListFailure();
            }
        });

    }

    private void getOrderListFailure() {

        new MyCountTime(2000, 100) {
            @Override
            public void onFinish() {
                super.onFinish();
                getOrderList();
            }
        }.start();

    }

    private void getVoucherList() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_voucher");
        ajaxParams.putOp("voucher_list");
        ajaxParams.put("page", "999");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mApplication.voucherArrayList.clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("voucher_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mApplication.voucherArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                        } catch (JSONException e) {
                            getVoucherListFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getVoucherListFailure();
                    }
                } else {
                    getVoucherListFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getVoucherListFailure();
            }
        });

    }

    private void getVoucherListFailure() {

        new MyCountTime(2000, 100) {
            @Override
            public void onFinish() {
                super.onFinish();
                getVoucherList();
            }
        }.start();

    }

    private void getRedPackList() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_redpacket");
        ajaxParams.putOp("redpacket_list");
        ajaxParams.put("page", "999");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mApplication.redPackArrayList.clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mApplication.redPackArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                        } catch (JSONException e) {
                            getRedPackListFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getRedPackListFailure();
                    }
                } else {
                    getRedPackListFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRedPackListFailure();
            }
        });

    }

    private void getRedPackListFailure() {

        new MyCountTime(2000, 100) {
            @Override
            public void onFinish() {
                super.onFinish();
                getVoucherList();
            }
        }.start();

    }

    private void returnActivity() {

        if (mTabHost.getCurrentTab() != 0) {
            mTabHost.setCurrentTab(0);
            return;
        }

        long secondTime = System.currentTimeMillis();

        if (secondTime - firstTime > 2000) {
            ToastUtil.show(mActivity, "再按一次退出程序...");
            firstTime = secondTime;
        } else {
            mApplication.finishActivity(mActivity);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }

    }

}