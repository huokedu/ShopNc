package top.yokey.nsg.activity.man;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ChatListActivity;
import top.yokey.nsg.activity.home.ScanActivity;
import top.yokey.nsg.activity.home.SearchActivity;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.ManListAdapter;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.TextUtil;

public class ManActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView scanImageView;
    private EditText titleEditText;
    private ImageView messageImageView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private int posInt;
    private int[] curpageInt;
    private boolean[] hasmore;
    private String[] typeString;
    private TextView[] tipsTextView;
    private TextView[] stateTextView;
    private RecyclerView[] mListView;
    private ManListAdapter[] mAdapter;
    private SwipeRefreshLayout[] mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>>[] mArrayList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_man);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        scanImageView = (ImageView) findViewById(R.id.scanImageView);
        titleEditText = (EditText) findViewById(R.id.keywordEditText);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        messageImageView.setImageResource(R.mipmap.ic_action_message);

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));

        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("搭配");
        mTitleList.add("泡妞");
        mTitleList.add("发型");
        mTitleList.add("话题");

        curpageInt = new int[mTitleList.size()];
        hasmore = new boolean[mTitleList.size()];
        mArrayList = new ArrayList[mTitleList.size()];
        tipsTextView = new TextView[mTitleList.size()];
        stateTextView = new TextView[mTitleList.size()];
        mListView = new RecyclerView[mTitleList.size()];
        mAdapter = new ManListAdapter[mTitleList.size()];
        mSwipeRefreshLayout = new SwipeRefreshLayout[mTitleList.size()];
        for (int i = 0; i < mTitleList.size(); i++) {
            curpageInt[i] = 1;
            hasmore[i] = true;
            mArrayList[i] = new ArrayList<>();
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            tipsTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.tipsTextView);
            stateTextView[i] = (TextView) mViewList.get(i).findViewById(R.id.stateTextView);
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mSwipeRefreshLayout[i] = (SwipeRefreshLayout) mViewList.get(i).findViewById(R.id.mainSwipeRefreshLayout);
            mAdapter[i] = new ManListAdapter(mApplication, mActivity, mArrayList[i]);
            mListView[i].setLayoutManager(new LinearLayoutManager(this));
            ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout[i]);
            mListView[i].setAdapter(mAdapter[i]);
        }
        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        typeString = new String[]{"搭配", "泡妞", "发型", "话题"};
        posInt = 0;
        getJson();

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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                posInt = position;
                if (curpageInt[posInt] == 1) {
                    getJson();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (final TextView textView : tipsTextView) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textView.getText().toString().contains("点击重试")) {
                        curpageInt[posInt] = 1;
                        hasmore[posInt] = true;
                        getJson();
                    }
                }
            });
        }

        for (SwipeRefreshLayout swipeRefreshLayout : mSwipeRefreshLayout) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            curpageInt[posInt] = 1;
                            hasmore[posInt] = true;
                            getJson();
                        }
                    }, 1000);
                }
            });
        }

        for (RecyclerView recyclerView : mListView) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    }

    private void getJson() {

        if (!hasmore[posInt]) {
            return;
        }

        if (mArrayList[posInt].isEmpty()) {
            tipsTextView[posInt].setText("加载中...");
            tipsTextView[posInt].setVisibility(View.VISIBLE);
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "man");
        ajaxParams.put("op", "get_list");
        ajaxParams.put("type", typeString[posInt]);
        ajaxParams.put("page", "10");
        ajaxParams.put("curpage", curpageInt[posInt] + "");
        ajaxParams.put("member_id", mApplication.userIdString);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                String data = mApplication.getJsonData(o.toString());
                if (!TextUtil.isEmpty(data)) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            if (curpageInt[posInt] == 1) {
                                tipsTextView[posInt].setText("暂无内容\n\n一会再来看看吧");
                                stateTextView[posInt].setVisibility(View.GONE);
                                tipsTextView[posInt].setVisibility(View.VISIBLE);
                            } else {
                                stateTextView[posInt].setText("没有更多了");
                                tipsTextView[posInt].setVisibility(View.GONE);
                                if (stateTextView[posInt].getVisibility() == View.GONE) {
                                    stateTextView[posInt].setVisibility(View.VISIBLE);
                                }
                                new MyCountTime(1000, 500) {
                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        if (stateTextView[posInt].getVisibility() == View.VISIBLE) {
                                            stateTextView[posInt].setVisibility(View.GONE);
                                            stateTextView[posInt].startAnimation(mApplication.goneAlphaAnimation);
                                        }
                                    }
                                }.start();
                            }
                        } else {
                            if (curpageInt[posInt] == 1) {
                                mArrayList[posInt].clear();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList[posInt].add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.getString(i))));
                            }
                            stateTextView[posInt].setVisibility(View.GONE);
                            tipsTextView[posInt].setVisibility(View.GONE);
                            curpageInt[posInt]++;
                        }
                        hasmore[posInt] = mApplication.getJsonHasMore(o.toString());
                        mSwipeRefreshLayout[posInt].setRefreshing(false);
                        mAdapter[posInt].notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
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

        mAdapter[posInt].notifyDataSetChanged();
        mSwipeRefreshLayout[posInt].setRefreshing(false);

        if (mArrayList[posInt].isEmpty()) {
            stateTextView[posInt].setVisibility(View.GONE);
            tipsTextView[posInt].setVisibility(View.VISIBLE);
            tipsTextView[posInt].setText("数据加载失败\n\n点击重试");
        } else {
            tipsTextView[posInt].setVisibility(View.GONE);
            stateTextView[posInt].setVisibility(View.VISIBLE);
            stateTextView[posInt].setText("加载失败...");
            new MyCountTime(1000, 500) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    if (stateTextView[posInt].getVisibility() == View.VISIBLE) {
                        stateTextView[posInt].setVisibility(View.GONE);
                        stateTextView[posInt].startAnimation(mApplication.goneAlphaAnimation);
                    }
                }
            }.start();
        }

    }

}