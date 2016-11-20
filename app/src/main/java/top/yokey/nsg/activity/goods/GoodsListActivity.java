package top.yokey.nsg.activity.goods;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.GoodsListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class GoodsListActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String key;
    private int curpage;
    private String type;
    private String order;
    private String model;
    private String keyword;
    private String priceTo;
    private boolean hasmore;
    private String priceFrom;

    private Spinner mSpinner;
    private ImageView backImageView;
    private EditText keywordEditText;
    private ImageView searchImageView;

    private Spinner sortSpinner;
    private TextView orderTextView;
    private TextView screenTextView;
    private ImageView modelImageView;
    private RelativeLayout screenRelativeLayout;
    private EditText moneyStartEditText;
    private EditText moneyEndEditText;
    private TextView screenGoodsTextView;

    private TextView tipsTextView;
    private TextView stateTextView;
    private RecyclerView mListView;
    private GoodsListAdapter mAdapter;
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
        setContentView(R.layout.activity_goods_list);
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

        mSpinner = (Spinner) findViewById(R.id.typeSpinner);
        backImageView = (ImageView) findViewById(R.id.backImageView);
        keywordEditText = (EditText) findViewById(R.id.keywordEditText);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);

        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        orderTextView = (TextView) findViewById(R.id.orderTextView);
        screenTextView = (TextView) findViewById(R.id.screenTextView);
        modelImageView = (ImageView) findViewById(R.id.modelImageView);
        screenRelativeLayout = (RelativeLayout) findViewById(R.id.screenRelativeLayout);
        moneyStartEditText = (EditText) findViewById(R.id.moneyStartEditText);
        moneyEndEditText = (EditText) findViewById(R.id.moneyEndEditText);
        screenGoodsTextView = (TextView) findViewById(R.id.screenGoodsTextView);

        stateTextView = (TextView) findViewById(R.id.stateTextView);
        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        key = "0";
        order = "2";
        curpage = 1;
        model = "hor";
        priceTo = "";
        priceFrom = "";
        hasmore = true;
        type = mActivity.getIntent().getStringExtra("type");
        keyword = mActivity.getIntent().getStringExtra("keyword");

        mSpinner.setVisibility(View.GONE);

        mArrayList = new ArrayList<>();
        mAdapter = new GoodsListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"综合排序", "商品销量", "浏览数量", "价格排序"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);
        screenRelativeLayout.setVisibility(View.GONE);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = keywordEditText.getText().toString();
                if (TextUtil.isEmpty(temp)) {
                    ToastUtil.show(mActivity, "关键字不能为空!");
                } else {
                    AndroidUtil.hideKeyboard(view);
                    keywordEditText.setText("");
                    type = "keyword";
                    hasmore = true;
                    keyword = temp;
                    curpage = 1;
                    getJson();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hasmore = true;
                        curpage = 1;
                        getJson();
                    }
                }, 1000);
            }
        });

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView.getText().toString().equals("读取数据失败\n\n点击重试")) {
                    hasmore = true;
                    curpage = 1;
                    getJson();
                }
            }
        });

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        getJson();
                    }
                }
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(14.0f);
                if (i == 0) {
                    key = "";
                } else {
                    key = i + "";
                }
                hasmore = true;
                curpage = 1;
                getJson();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        orderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.equals("2")) {
                    order = "1";
                    orderTextView.setText("从低到高");
                } else {
                    order = "2";
                    orderTextView.setText("从高到低");
                }
                hasmore = true;
                curpage = 1;
                getJson();
            }
        });

        screenTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (screenRelativeLayout.getVisibility() == View.VISIBLE) {
                    screenRelativeLayout.setVisibility(View.GONE);
                } else {
                    screenRelativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        modelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.equals("ver")) {
                    modelImageView.setImageResource(R.mipmap.ic_goods_list_hor);
                    model = "hor";
                } else {
                    modelImageView.setImageResource(R.mipmap.ic_goods_list_ver);
                    model = "ver";
                }
                hasmore = true;
                curpage = 1;
                getJson();
            }
        });

        moneyStartEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceFrom = moneyStartEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        moneyEndEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceTo = moneyEndEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        screenGoodsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenRelativeLayout.setVisibility(View.GONE);
                hasmore = true;
                curpage = 1;
                getJson();
            }
        });

    }

    private void getJson() {

        if (!hasmore) {
            return;
        }

        if (curpage == 1) {
            DialogUtil.progress(mActivity);
        } else {
            stateTextView.setText("加载中...");
            stateTextView.setVisibility(View.VISIBLE);
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "goods");
        ajaxParams.put("op", "goods_list");
        ajaxParams.put("key", key);
        ajaxParams.put("order", order);
        ajaxParams.put("page", "10");
        ajaxParams.put("curpage", curpage + "");
        if (!TextUtil.isEmpty(priceFrom)) {
            ajaxParams.put("price_from", priceFrom);
        }
        if (!TextUtil.isEmpty(priceTo)) {
            ajaxParams.put("price_to", priceTo);
        }
        if (type.equals("category")) {
            ajaxParams.put("gc_id", keyword);
        } else {
            ajaxParams.put("keyword", keyword);
        }

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (curpage == 1) {
                    DialogUtil.cancel();
                }
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        if (curpage == 1) {
                            mArrayList.clear();
                        }
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                            if (model.equals("ver")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                    hashMap.put("model", model);
                                    mArrayList.add(hashMap);
                                }
                            } else {
                                for (int i = 0; i < jsonArray.length(); i += 2) {
                                    jsonObject = (JSONObject) jsonArray.get(i);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("model", model);
                                    hashMap.put("goods_id_1", jsonObject.getString("goods_id"));
                                    hashMap.put("goods_name_1", jsonObject.getString("goods_name"));
                                    hashMap.put("goods_price_1", jsonObject.getString("goods_price"));
                                    hashMap.put("goods_promotion_price_1", jsonObject.getString("goods_promotion_price"));
                                    hashMap.put("group_flag_1", jsonObject.getString("group_flag"));
                                    hashMap.put("xianshi_flag_1", jsonObject.getString("xianshi_flag"));
                                    hashMap.put("goods_image_url_1", jsonObject.getString("goods_image_url"));
                                    if ((i + 1) < jsonArray.length()) {
                                        jsonObject = (JSONObject) jsonArray.get(i + 1);
                                        hashMap.put("goods_id_2", jsonObject.getString("goods_id"));
                                        hashMap.put("goods_name_2", jsonObject.getString("goods_name"));
                                        hashMap.put("goods_price_2", jsonObject.getString("goods_price"));
                                        hashMap.put("goods_promotion_price_2", jsonObject.getString("goods_promotion_price"));
                                        hashMap.put("group_flag_2", jsonObject.getString("group_flag"));
                                        hashMap.put("xianshi_flag_2", jsonObject.getString("xianshi_flag"));
                                        hashMap.put("goods_image_url_2", jsonObject.getString("goods_image_url"));
                                    } else {
                                        hashMap.put("goods_id_2", "");
                                        hashMap.put("goods_name_2", "");
                                        hashMap.put("goods_price_2", "");
                                        hashMap.put("goods_promotion_price_2", "");
                                        hashMap.put("group_flag_2", "");
                                        hashMap.put("xianshi_flag_2", "");
                                        hashMap.put("goods_image_url_2", "");
                                    }
                                    mArrayList.add(hashMap);
                                }
                            }
                            if (mArrayList.isEmpty()) {
                                if (curpage == 1) {
                                    tipsTextView.setText("暂无商品\n\n试试别的关键字吧");
                                    tipsTextView.setVisibility(View.VISIBLE);
                                    stateTextView.setVisibility(View.GONE);
                                } else {
                                    stateTextView.setText("没有更多了...");
                                    stateTextView.setVisibility(View.VISIBLE);
                                    tipsTextView.setVisibility(View.GONE);
                                    new MyCountTime(1000, 500) {
                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            stateTextView.setVisibility(View.GONE);
                                        }
                                    }.start();
                                }
                            } else {
                                tipsTextView.setVisibility(View.GONE);
                                stateTextView.setVisibility(View.GONE);
                                curpage++;
                            }
                            hasmore = mApplication.getJsonHasMore(o.toString());
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
                if (curpage == 1) {
                    DialogUtil.cancel();
                }
            }
        });

    }

    private void getJsonFailure() {

        if (curpage == 1) {
            tipsTextView.setText("读取数据失败\n\n点击重试");
            tipsTextView.setVisibility(View.VISIBLE);
        }

    }

    private void returnActivity() {

        if (keywordEditText.getText().length() != 0) {
            keywordEditText.setText("");
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}