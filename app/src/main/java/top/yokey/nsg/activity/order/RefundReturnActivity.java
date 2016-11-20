package top.yokey.nsg.activity.order;

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
import top.yokey.nsg.adapter.RefundListAdapter;
import top.yokey.nsg.adapter.ReturnListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class RefundReturnActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private int refundCurPageInt;
    private boolean refundHasmore;
    private RecyclerView refundListView;
    private TextView refundTipsTextView;
    private TextView refundStateTextView;
    private RefundListAdapter refundAdapter;
    private SwipeRefreshLayout refundSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> refundArrayList;

    private int returnCurPageInt;
    private boolean returnHasmore;
    private RecyclerView returnListView;
    private TextView returnTipsTextView;
    private TextView returnStateTextView;
    private ReturnListAdapter returnAdapter;
    private SwipeRefreshLayout returnSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> returnArrayList;

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
        setContentView(R.layout.activity_viewpager);
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

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("退款/退货");

        List<View> mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view, null));

        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("退款");
        mTitleList.add("退货");

        refundHasmore = true;
        refundCurPageInt = 1;
        refundArrayList = new ArrayList<>();
        refundAdapter = new RefundListAdapter(mApplication, mActivity, refundArrayList);
        refundListView = (RecyclerView) mViewList.get(0).findViewById(R.id.mainListView);
        refundTipsTextView = (TextView) mViewList.get(0).findViewById(R.id.tipsTextView);
        refundStateTextView = (TextView) mViewList.get(0).findViewById(R.id.stateTextView);
        refundSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(0).findViewById(R.id.mainSwipeRefreshLayout);
        refundListView.setLayoutManager(new LinearLayoutManager(mActivity));
        ControlUtil.setSwipeRefreshLayout(refundSwipeRefreshLayout);
        refundListView.setAdapter(refundAdapter);

        returnHasmore = true;
        returnCurPageInt = 1;
        returnArrayList = new ArrayList<>();
        returnAdapter = new ReturnListAdapter(mApplication, mActivity, returnArrayList);
        returnListView = (RecyclerView) mViewList.get(1).findViewById(R.id.mainListView);
        returnTipsTextView = (TextView) mViewList.get(1).findViewById(R.id.tipsTextView);
        returnStateTextView = (TextView) mViewList.get(1).findViewById(R.id.stateTextView);
        returnSwipeRefreshLayout = (SwipeRefreshLayout) mViewList.get(1).findViewById(R.id.mainSwipeRefreshLayout);
        returnListView.setLayoutManager(new LinearLayoutManager(mActivity));
        ControlUtil.setSwipeRefreshLayout(refundSwipeRefreshLayout);
        returnListView.setAdapter(returnAdapter);

        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        getRefundJson();
        getReturnJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnActivity();
            }
        });

        refundTipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (refundTipsTextView.getText().toString().contains("点击重试!")) {
                    refundHasmore = true;
                    refundCurPageInt = 1;
                    refundArrayList.clear();
                    refundAdapter.notifyDataSetChanged();
                    getRefundJson();
                }
            }
        });

        refundSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refundHasmore = true;
                        refundCurPageInt = 1;
                        refundArrayList.clear();
                        refundAdapter.notifyDataSetChanged();
                        getRefundJson();
                    }
                }, 1000);
            }
        });

        refundListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        getRefundJson();
                    }
                }
            }
        });

        returnTipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (returnTipsTextView.getText().toString().contains("点击重试!")) {
                    returnHasmore = true;
                    returnCurPageInt = 1;
                    returnArrayList.clear();
                    returnAdapter.notifyDataSetChanged();
                    getReturnJson();
                }
            }
        });

        returnSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        returnHasmore = true;
                        returnCurPageInt = 1;
                        returnArrayList.clear();
                        returnAdapter.notifyDataSetChanged();
                        getReturnJson();
                    }
                }, 1000);
            }
        });

        returnListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        getReturnJson();
                    }
                }
            }
        });

    }

    private void getRefundJson() {

        if (!refundHasmore) {
            return;
        }

        //读取时的处理
        if (refundCurPageInt == 1) {
            refundTipsTextView.setText("加载中...");
            refundTipsTextView.setVisibility(View.VISIBLE);
        } else {
            refundStateTextView.setText("加载中...");
            refundStateTextView.setVisibility(View.VISIBLE);
        }

        //初始化POST数据
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.put("curpage", refundCurPageInt + "");
        ajaxParams.putOp("get_refund_list");
        ajaxParams.putAct("member_refund");
        ajaxParams.put("page", "10");

        //POST提交
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            if (refundCurPageInt == 1) {
                                refundArrayList.clear();
                            }
                            String data = mApplication.getJsonData(o.toString());
                            if (data.equals("null") || data.equals("{\"refund_list\":[]}")) {
                                if (refundCurPageInt == 1) {
                                    refundStateTextView.setVisibility(View.GONE);
                                    refundTipsTextView.setVisibility(View.VISIBLE);
                                    refundTipsTextView.setText("暂无退款记录\n\n一会再来看看吧!");
                                } else {
                                    refundTipsTextView.setVisibility(View.GONE);
                                    refundStateTextView.setVisibility(View.VISIBLE);
                                    refundStateTextView.setText("没有更多了");
                                    new MyCountTime(1000, 500) {
                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            refundStateTextView.setVisibility(View.GONE);
                                            refundStateTextView.startAnimation(mApplication.goneAlphaAnimation);
                                        }
                                    }.start();
                                }
                            } else {
                                JSONObject jsonObject = new JSONObject(data);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("refund_list"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    refundArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                                }
                                refundStateTextView.setVisibility(View.GONE);
                                refundTipsTextView.setVisibility(View.GONE);
                                refundCurPageInt++;
                            }
                            refundHasmore = mApplication.getJsonHasMore(o.toString());
                            refundSwipeRefreshLayout.setRefreshing(false);
                            refundAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getRefundJsonFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getRefundJsonFailure();
                    }
                } else {
                    getRefundJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRefundJsonFailure();
                DialogUtil.cancel();
            }
        });

    }

    private void getRefundJsonFailure() {

        refundSwipeRefreshLayout.setRefreshing(false);
        refundAdapter.notifyDataSetChanged();

        if (refundCurPageInt == 1) {
            refundStateTextView.setVisibility(View.GONE);
            refundTipsTextView.setVisibility(View.VISIBLE);
            refundTipsTextView.setText("读取数据失败\n\n点击重试!");
        } else {
            refundTipsTextView.setVisibility(View.GONE);
            refundStateTextView.setVisibility(View.VISIBLE);
            refundStateTextView.setText("加载数据失败...");
            refundStateTextView.startAnimation(mApplication.goneAlphaAnimation);
            new MyCountTime(1000, 500) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    refundStateTextView.setVisibility(View.GONE);
                }
            }.start();
        }

    }

    private void getReturnJson() {

        if (!returnHasmore) {
            return;
        }

        //读取时的处理
        if (returnCurPageInt == 1) {
            returnTipsTextView.setText("加载中...");
            returnTipsTextView.setVisibility(View.VISIBLE);
        } else {
            returnStateTextView.setText("加载中...");
            returnStateTextView.setVisibility(View.VISIBLE);
        }

        //初始化POST数据
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.put("curpage", returnCurPageInt + "");
        ajaxParams.putOp("get_return_list");
        ajaxParams.putAct("member_return");
        ajaxParams.put("page", "10");

        //POST提交
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            if (returnCurPageInt == 1) {
                                returnArrayList.clear();
                            }
                            String data = mApplication.getJsonData(o.toString());
                            if (data.equals("null") || data.equals("{\"return_list\":[]}")) {
                                if (returnCurPageInt == 1) {
                                    returnStateTextView.setVisibility(View.GONE);
                                    returnTipsTextView.setVisibility(View.VISIBLE);
                                    returnTipsTextView.setText("暂无退款记录\n\n一会再来看看吧!");
                                } else {
                                    returnTipsTextView.setVisibility(View.GONE);
                                    returnStateTextView.setVisibility(View.VISIBLE);
                                    returnStateTextView.setText("没有更多了");
                                    new MyCountTime(1000, 500) {
                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            returnStateTextView.setVisibility(View.GONE);
                                            returnStateTextView.startAnimation(mApplication.goneAlphaAnimation);
                                        }
                                    }.start();
                                }
                            } else {
                                JSONObject jsonObject = new JSONObject(data);
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("return_list"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    returnArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                                }
                                returnStateTextView.setVisibility(View.GONE);
                                returnTipsTextView.setVisibility(View.GONE);
                                returnCurPageInt++;
                            }
                            returnHasmore = mApplication.getJsonHasMore(o.toString());
                            returnSwipeRefreshLayout.setRefreshing(false);
                            returnAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getReturnJsonFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getReturnJsonFailure();
                    }
                } else {
                    getReturnJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getReturnJsonFailure();
                DialogUtil.cancel();
            }
        });

    }

    private void getReturnJsonFailure() {

        returnSwipeRefreshLayout.setRefreshing(false);
        returnAdapter.notifyDataSetChanged();

        if (returnCurPageInt == 1) {
            returnStateTextView.setVisibility(View.GONE);
            returnTipsTextView.setVisibility(View.VISIBLE);
            returnTipsTextView.setText("读取数据失败\n\n点击重试!");
        } else {
            returnTipsTextView.setVisibility(View.GONE);
            returnStateTextView.setVisibility(View.VISIBLE);
            returnStateTextView.setText("加载数据失败...");
            returnStateTextView.startAnimation(mApplication.goneAlphaAnimation);
            new MyCountTime(1000, 500) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    returnStateTextView.setVisibility(View.GONE);
                }
            }.start();
        }

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}