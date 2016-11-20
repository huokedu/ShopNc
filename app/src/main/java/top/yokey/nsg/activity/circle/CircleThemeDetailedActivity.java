package top.yokey.nsg.activity.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.scrollablelayout.ScrollableLayout;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.CircleThemeReplyListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CircleThemeDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    public static String theme_id;
    public static String theme_name;
    public static String theme_content;
    public static String theme_author;
    public static String theme_browser;
    public static String theme_like;
    public static String theme_comment;
    public static String member_id;
    public static String member_name;
    public static String member_avatar;

    private ImageView backImageView;
    private TextView titleTextView;

    private ScrollableLayout mScrollableLayout;
    private FloatingActionButton createButton;
    private RelativeLayout topRelativeLayout;
    private ImageView mImageView;
    private TextView nameTextView;
    private TextView descTextView;
    private TextView authorTextView;
    private TextView browserTextView;
    private TextView commentTextView;
    private TextView likeTextView;

    private RecyclerView mListView;
    private CircleThemeReplyListAdapter mAdapter;
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
        setContentView(R.layout.activity_circle_theme_detailed);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getDetailed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        mScrollableLayout = (ScrollableLayout) findViewById(R.id.mainScrollableLayout);
        topRelativeLayout = (RelativeLayout) findViewById(R.id.topRelativeLayout);
        createButton = (FloatingActionButton) findViewById(R.id.createButton);
        mImageView = (ImageView) findViewById(R.id.mainImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        descTextView = (TextView) findViewById(R.id.descTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        browserTextView = (TextView) findViewById(R.id.browserTextView);
        likeTextView = (TextView) findViewById(R.id.likeTextView);
        commentTextView = (TextView) findViewById(R.id.commentTextView);

        mListView = (RecyclerView) findViewById(R.id.mainListView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        theme_id = mActivity.getIntent().getStringExtra("theme_id");
        theme_name = mActivity.getIntent().getStringExtra("theme_name");
        theme_content = mActivity.getIntent().getStringExtra("theme_content");
        theme_author = mActivity.getIntent().getStringExtra("theme_author");
        theme_browser = mActivity.getIntent().getStringExtra("theme_browser");
        theme_like = mActivity.getIntent().getStringExtra("theme_like");
        theme_comment = mActivity.getIntent().getStringExtra("theme_comment");
        member_id = mActivity.getIntent().getStringExtra("member_id");
        member_name = mActivity.getIntent().getStringExtra("member_name");
        member_avatar = mActivity.getIntent().getStringExtra("member_avatar");

        if (TextUtil.isEmpty(theme_id)) {
            ToastUtil.show(mActivity, "传入数据有误!");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("主题详细");
        nameTextView.setText(theme_name);
        descTextView.setText(theme_content);
        authorTextView.setText(theme_author);
        browserTextView.setText(theme_browser);
        likeTextView.setText(theme_like);
        commentTextView.setText(theme_comment);

        mImageView.setImageResource(R.mipmap.ic_default_circle);
        ImageLoader.getInstance().displayImage(member_avatar, mImageView);
        mScrollableLayout.getHelper().setCurrentScrollableContainer(mListView);

        mArrayList = new ArrayList<>();
        mAdapter = new CircleThemeReplyListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapter(mAdapter);

        getDetailed();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        topRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startChat(mActivity, member_id);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CircleDetailedActivity.applyBoolean) {
                    DialogUtil.query(mActivity, "加入圈子?", "您尚未加入圈子", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            Intent intent = new Intent(mActivity, CircleApplyActivity.class);
                            mApplication.startActivityLoginSuccess(mActivity, intent);
                        }
                    });
                } else {
                    Intent intent = new Intent(mActivity, CircleThemeReplyActivity.class);
                    mApplication.startActivityLoginSuccess(mActivity, intent);
                }
            }
        });

    }

    private void getDetailed() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "circle");
        ajaxParams.put("op", "theme_detailed");
        ajaxParams.put("theme_id", theme_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    if (TextUtil.isEmpty(mApplication.getJsonError(o.toString()))) {
                        try {
                            mArrayList.clear();
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("reply_info"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            if (mArrayList.isEmpty()) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                if (!mApplication.userHashMap.isEmpty()) {
                                    hashMap.put("member_id", mApplication.userHashMap.get("member_id"));
                                    hashMap.put("member_name", mApplication.userHashMap.get("member_name"));
                                    hashMap.put("member_avatar", mApplication.userHashMap.get("avator"));
                                } else {
                                    hashMap.put("member_id", "0");
                                    hashMap.put("member_name", "游客");
                                    hashMap.put("member_avatar", "");
                                }
                                hashMap.put("cm_levelname", "初级粉丝");
                                hashMap.put("reply_id", "1");
                                hashMap.put("reply_addtime", "");
                                hashMap.put("reply_content", "暂无回复，快快回复一个吧!");
                                mArrayList.add(hashMap);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getDetailedFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getDetailedFailure();
                    }
                } else {
                    getDetailedFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getDetailedFailure();
            }
        });

    }

    private void getDetailedFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试?",
                "读取数据失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getDetailed();
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

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}