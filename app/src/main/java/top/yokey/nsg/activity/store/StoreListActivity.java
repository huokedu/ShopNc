package top.yokey.nsg.activity.store;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import top.yokey.nsg.adapter.StoreListListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class StoreListActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private int curpage;
    private String keyword;
    private boolean hasmore;

    private Spinner mSpinner;
    private ImageView backImageView;
    private EditText keywordEditText;
    private ImageView searchImageView;

    private TextView tipsTextView;
    private TextView stateTextView;
    private RecyclerView mListView;
    private StoreListListAdapter mAdapter;
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
        setContentView(R.layout.activity_store_list);
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

        stateTextView = (TextView) findViewById(R.id.stateTextView);
        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        curpage = 1;
        hasmore = true;
        keyword = mActivity.getIntent().getStringExtra("keyword");

        mSpinner.setVisibility(View.GONE);

        mArrayList = new ArrayList<>();
        mAdapter = new StoreListListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getJson();

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
                keyword = keywordEditText.getText().toString();
                AndroidUtil.hideKeyboard(view);
                keywordEditText.setText("");
                hasmore = true;
                curpage = 1;
                getJson();
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
                if (tipsTextView.getText().toString().equals("读取店铺数据失败\n\n点击重试")) {
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
        ajaxParams.put("act", "shop");
        ajaxParams.put("op", "shop_list");
        ajaxParams.put("page", "10");
        ajaxParams.put("curpage", curpage + "");
        ajaxParams.put("keyword", keyword);

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
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("store_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                mArrayList.add(hashMap);
                            }
                            if (mArrayList.isEmpty()) {
                                if (curpage == 1) {
                                    tipsTextView.setText("暂无店铺\n\n试试别的关键字吧");
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
            tipsTextView.setText("读取店铺数据失败\n\n点击重试");
            tipsTextView.setVisibility(View.VISIBLE);
        } else {
            stateTextView.setText("读取失败...");
            stateTextView.setVisibility(View.VISIBLE);
            new MyCountTime(500, 250) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    stateTextView.setVisibility(View.GONE);
                }
            }.start();
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