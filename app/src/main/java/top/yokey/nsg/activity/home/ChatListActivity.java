package top.yokey.nsg.activity.home;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.yokey.nsg.adapter.ChatListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class ChatListActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView tipsTextView;
    private RecyclerView mListView;
    private ChatListAdapter mAdapter;
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

        titleTextView.setText("消息列表");

        mArrayList = new ArrayList<>();
        mAdapter = new ChatListAdapter(mApplication, mActivity, mArrayList);
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

        tipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsTextView.getText().toString().equals("读取消息列表失败\n\n点击重试")) {
                    getJson();
                }
            }
        });

    }

    private void getJson() {

        tipsTextView.setText("加载中...");
        tipsTextView.setVisibility(View.VISIBLE);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_chat");
        ajaxParams.putOp("get_user_list");

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
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getString("list").equals("[]")) {
                                tipsTextView.setText("暂无聊天记录\n\n快去聊聊吧!");
                                tipsTextView.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                            jsonObject = new JSONObject(jsonObject.getString("list"));
                            Iterator<String> iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString(iterator.next()))));
                            }
                            if (mArrayList.isEmpty()) {
                                tipsTextView.setText("暂无聊天记录\n\n快去聊聊吧!");
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

        tipsTextView.setVisibility(View.VISIBLE);
        tipsTextView.setText("读取消息列表失败\n\n点击重试");
        mSwipeRefreshLayout.setRefreshing(false);

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}