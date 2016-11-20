package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.NotifyListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class NotifyActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private boolean firstBoolean;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView tipsTextView;
    private RecyclerView mListView;
    private NotifyListAdapter mAdapter;
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_recycler);
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

        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        firstBoolean = true;
        titleTextView.setText("消息通知");

        mArrayList = new ArrayList<>();
        mAdapter = new NotifyListAdapter(mArrayList);
        mListView.setBackgroundResource(R.mipmap.bg_login);
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

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView.getText().toString().equals("读取通知数据失败\n\n点击重试")) {
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
                        getJson();
                    }
                }, 1000);
            }
        });

    }

    private void getJson() {

        if (firstBoolean) {
            firstBoolean = false;
            tipsTextView.setText("读取中...");
            tipsTextView.setVisibility(View.VISIBLE);
        }

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("system_msg");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        if (TextUtil.isEmpty(data)) {
                            tipsTextView.setText("暂无通知\n\n稍后再来看看吧!");
                            tipsTextView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                            return;
                        }
                        try {
                            mArrayList.clear();
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList.isEmpty()) {
                                tipsTextView.setText("暂无通知\n\n稍后再来看看吧!");
                                tipsTextView.setVisibility(View.VISIBLE);
                            } else {
                                tipsTextView.setVisibility(View.GONE);
                            }
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
            }
        });

    }

    private void getJsonFailure() {

        tipsTextView.setText("读取通知数据失败\n\n点击重试");
        tipsTextView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}