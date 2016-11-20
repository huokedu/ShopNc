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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.adapter.ChatOnlyListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class ChatOnlyActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String u_id;
    private String u_name;
    private String avatar;

    private boolean bottomBoolean;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView tipsTextView;
    private RecyclerView mListView;
    private ChatOnlyListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;

    private EditText contentEditText;
    private TextView sendTextView;

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
        setContentView(R.layout.activity_chat_only);
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

        contentEditText = (EditText) findViewById(R.id.contentEditText);
        sendTextView = (TextView) findViewById(R.id.sendTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        bottomBoolean = true;
        u_id = mActivity.getIntent().getStringExtra("u_id");

        if (u_id.equals(mApplication.userHashMap.get("member_id"))) {
            ToastUtil.show(mActivity, "您不能跟您自己聊天");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("...");

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getInfo();

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
                if (tipsTextView.getText().toString().equals("读取聊天记录失败\n\n点击重试")) {
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

        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentEditText.getText().toString();
                if (TextUtil.isEmpty(content)) {
                    return;
                }
                sendTextView.setEnabled(false);
                contentEditText.setEnabled(false);
                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                ajaxParams.putAct("member_chat");
                ajaxParams.putOp("send_msg");
                ajaxParams.put("t_id", u_id);
                ajaxParams.put("t_name", u_name);
                ajaxParams.put("t_msg", content);
                ajaxParams.put("chat_goods_id", "");
                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        contentEditText.setText("");
                        sendTextView.setEnabled(true);
                        contentEditText.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ToastUtil.showFailure(mActivity);
                        contentEditText.setEnabled(true);
                        sendTextView.setEnabled(true);
                    }
                });
            }
        });

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bottomBoolean = false;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        bottomBoolean = true;
                    }
                }
            }
        });

    }

    private void getInfo() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_chat");
        ajaxParams.putOp("get_node_info");
        ajaxParams.put("u_id", u_id);
        ajaxParams.put("chat_goods_id", "");

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
                            jsonObject = new JSONObject(jsonObject.getString("user_info"));
                            if (TextUtil.isEmpty(jsonObject.getString("store_name"))) {
                                titleTextView.setText(jsonObject.getString("member_name"));
                            } else {
                                titleTextView.setText(jsonObject.getString("store_name"));
                            }
                            if (TextUtil.isEmpty(jsonObject.getString("store_avatar"))) {
                                avatar = jsonObject.getString("member_avatar");
                            } else {
                                avatar = jsonObject.getString("store_avatar");
                            }
                            u_name = jsonObject.getString("member_name");
                            u_id = jsonObject.getString("member_id");
                            mArrayList = new ArrayList<>();
                            mAdapter = new ChatOnlyListAdapter(mApplication, mArrayList, avatar);
                            mListView.setLayoutManager(new LinearLayoutManager(mActivity));
                            mListView.setAdapter(mAdapter);
                            getJson();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getInfoFailure();
                        }
                    } else {
                        getInfoFailure();
                    }
                } else {
                    getInfoFailure();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getInfoFailure();
            }
        });

    }

    private void getInfoFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取信息失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getInfo();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void getJson() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "member_chat");
        ajaxParams.put("op", "get_chat_log");
        ajaxParams.put("page", "50");
        String link = ajaxParams.toString();
        ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.put("t_id", u_id);
        ajaxParams.put("t", "30");

        mApplication.mFinalHttp.post(mApplication.apiUrlString + link, ajaxParams, new AjaxCallBack<Object>() {
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
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList.isEmpty()) {
                                tipsTextView.setText("暂无聊天记录\n\n快去聊聊吧!");
                                tipsTextView.setVisibility(View.VISIBLE);
                            } else {
                                tipsTextView.setVisibility(View.GONE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                            if (bottomBoolean) {
                                mListView.smoothScrollToPosition(mArrayList.size());
                            }
                            new MyCountTime(2000, 1000) {
                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    getJson();
                                }
                            }.start();
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
        tipsTextView.setText("读取聊天记录失败\n\n点击重试");
        mSwipeRefreshLayout.setRefreshing(false);

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}