package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.mine.LoginActivity;
import top.yokey.nsg.activity.order.BuySetup1Activity;
import top.yokey.nsg.adapter.CartListAdapter;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CartActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView scanImageView;
    private EditText titleEditText;
    private ImageView messageImageView;

    private TextView buyTextView;
    private TextView calcTextView;
    private LinearLayout bottomLinearLayout;

    private CheckBox allCheckBox;
    private TextView tipsTextView;
    private RecyclerView mListView;
    private CartListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cart);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
            if (!mApplication.userHashMap.isEmpty()) {
                getJson();
            }
        } else {
            tipsTextView.setText("请登录...");
            tipsTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {

        //实例化 toolbar
        scanImageView = (ImageView) findViewById(R.id.scanImageView);
        titleEditText = (EditText) findViewById(R.id.keywordEditText);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        calcTextView = (TextView) findViewById(R.id.calcTextView);
        buyTextView = (TextView) findViewById(R.id.buyTextView);
        bottomLinearLayout = (LinearLayout) findViewById(R.id.bottomLinearLayout);

        allCheckBox = (CheckBox) findViewById(R.id.allCheckBox);
        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        messageImageView.setImageResource(R.mipmap.ic_action_message);

        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
            tipsTextView.setText("加载中...");
        } else {
            tipsTextView.setText("请登录...");
        }

        mArrayList = new ArrayList<>();
        mAdapter = new CartListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

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

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtil.isEmpty(mApplication.userKeyString)) {
                    MainActivity.mTabHost.setCurrentTab(0);
                } else {
                    mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                }
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

        allCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allCheckBox.isChecked()) {
                    for (int i = 0; i < mArrayList.size(); i++) {
                        mArrayList.get(i).put("click", "1");
                    }
                } else {
                    for (int i = 0; i < mArrayList.size(); i++) {
                        mArrayList.get(i).put("click", "0");
                    }
                }
                mAdapter.notifyDataSetChanged();
                updateInfo();
            }
        });

        buyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cart_id = "", ifcart = "1";

                for (int i = 0; i < mArrayList.size(); i++) {
                    if (mArrayList.get(i).get("click").equals("1")) {
                        cart_id += mArrayList.get(i).get("data") + ",";
                    }
                }

                if (TextUtil.isEmpty(cart_id)) {
                    ToastUtil.show(mActivity, "请至少选择一件商品");
                    return;
                }

                cart_id = cart_id.substring(0, cart_id.length() - 1);

                Intent intent = new Intent(mActivity, BuySetup1Activity.class);
                intent.putExtra("ifcart", ifcart);
                intent.putExtra("cart_id", cart_id);
                mApplication.startActivityLoginSuccess(mActivity, intent);

            }
        });

        mAdapter.setOnTextWatcherListener(new CartListAdapter.onTextWatcherListener() {
            @Override
            public void onTextWatcher() {
                updateInfo();
            }
        });

        mAdapter.setOnDelClickListener(new CartListAdapter.onDelClickListener() {
            @Override
            public void onDelClick() {
                getJson();
            }
        });

        mAdapter.setOnCheckClickListener(new CartListAdapter.onCheckClickListener() {
            @Override
            public void onCheckClick() {
                for (int i = 0; i < mArrayList.size(); i++) {
                    if (mArrayList.get(i).get("click").equals("0")) {
                        allCheckBox.setChecked(false);
                        updateInfo();
                        return;
                    }
                }
                allCheckBox.setChecked(true);
                updateInfo();
            }
        });

    }

    private void getJson() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.put("act", "member_cart");
        ajaxParams.put("op", "cart_list");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList.clear();
                            allCheckBox.setChecked(false);
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("cart_list"));
                            if (jsonArray.length() == 0) {
                                tipsTextView.setVisibility(View.VISIBLE);
                                tipsTextView.setText("购物车为空\n\n去逛逛吧!");
                                bottomLinearLayout.setVisibility(View.GONE);
                                return;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                JSONArray goodsJsonArray = new JSONArray(hashMap.get("goods"));
                                for (int j = 0; j < goodsJsonArray.length(); j++) {
                                    HashMap<String, String> goodsHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(goodsJsonArray.get(j).toString()));
                                    goodsHashMap.put("data", goodsHashMap.get("cart_id") + "|" + goodsHashMap.get("goods_num"));
                                    goodsHashMap.put("store_id", hashMap.get("store_id"));
                                    goodsHashMap.put("store_name", hashMap.get("store_name"));
                                    if (j == 0) {
                                        goodsHashMap.put("show_store", "1");
                                    } else {
                                        goodsHashMap.put("show_store", "0");
                                    }
                                    if (j == (goodsJsonArray.length() - 1)) {
                                        goodsHashMap.put("show_line", "1");
                                    } else {
                                        goodsHashMap.put("show_line", "0");
                                    }
                                    goodsHashMap.put("click", "0");
                                    mArrayList.add(goodsHashMap);
                                }
                            }
                            if (mArrayList.isEmpty()) {
                                tipsTextView.setVisibility(View.VISIBLE);
                                tipsTextView.setText("购物车为空\n\n去逛逛吧!");
                                bottomLinearLayout.setVisibility(View.GONE);
                            } else {
                                tipsTextView.setVisibility(View.GONE);
                                bottomLinearLayout.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
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
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                super.onFinish();
            }
        }.start();

    }

    public void updateInfo() {

        Double goods_total = 0.0;
        int goods_number = 0;

        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).get("click").equals("1")) {
                int number = Integer.parseInt(mArrayList.get(i).get("goods_num"));
                Double price = Double.parseDouble(mArrayList.get(i).get("goods_price"));
                goods_total += (number * price);
                goods_number += number;
            }
        }

        if (goods_number == 0) {
            buyTextView.setEnabled(false);
            calcTextView.setText("没有选中商品");
        } else {
            buyTextView.setEnabled(true);
            String total = "共 <font color='#FF5001'>" + goods_number + "</font> 件，";
            total += "共 <font color='#FF5001'>" + goods_total + "</font> 元";
            calcTextView.setText(Html.fromHtml(total));
        }

    }

}