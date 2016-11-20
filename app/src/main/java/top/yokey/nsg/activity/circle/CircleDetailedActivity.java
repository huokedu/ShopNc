package top.yokey.nsg.activity.circle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.scrollablelayout.ScrollableLayout;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.CircleFriendListAdapter;
import top.yokey.nsg.adapter.CircleThemeListAdapter;
import top.yokey.nsg.adapter.GoodsCircleListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CircleDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private List<View> mViewList;
    public static String circle_id;
    public static String circle_name;
    public static String circle_info;
    public static String circle_desc;
    public static boolean applyBoolean;
    public static String circle_masterid;
    public static String circle_joinaudit;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView applyImageView;

    private ScrollableLayout mScrollableLayout;
    private FloatingActionButton createButton;
    private ImageView mImageView;
    private TextView nameTextView;
    private TextView infoTextView;
    private TextView descTextView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private CircleThemeListAdapter themeAdapter;
    private GoodsCircleListAdapter goodsAdapter;
    private CircleFriendListAdapter friendAdapter;
    private ArrayList<HashMap<String, String>>[] mArrayList;

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
        setContentView(R.layout.activity_circle_detailed);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getThemeJson();
        if (applyBoolean) {
            applyImageView.setImageResource(R.mipmap.ic_action_quit);
        } else {
            applyImageView.setImageResource(R.mipmap.ic_action_add);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        applyImageView = (ImageView) findViewById(R.id.moreImageView);

        mScrollableLayout = (ScrollableLayout) findViewById(R.id.mainScrollableLayout);
        createButton = (FloatingActionButton) findViewById(R.id.createButton);
        mImageView = (ImageView) findViewById(R.id.mainImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        descTextView = (TextView) findViewById(R.id.descTextView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        applyBoolean = false;
        circle_id = mActivity.getIntent().getStringExtra("circle_id");
        circle_name = mActivity.getIntent().getStringExtra("circle_name");
        circle_info = mActivity.getIntent().getStringExtra("circle_info");
        circle_desc = mActivity.getIntent().getStringExtra("circle_desc");
        circle_masterid = mActivity.getIntent().getStringExtra("circle_masterid");
        circle_joinaudit = mActivity.getIntent().getStringExtra("circle_joinaudit");

        if (TextUtil.isEmpty(circle_id)) {
            ToastUtil.show(mActivity, "传入数据有误!");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("圈子详细");
        nameTextView.setText(circle_name);
        infoTextView.setText(circle_info);
        descTextView.setText(circle_desc);

        mImageView.setImageResource(R.mipmap.ic_default_circle);
        String image = mApplication.circlePicUrlString + circle_id + ".jpg";
        ImageLoader.getInstance().displayImage(image, mImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                mImageView.setImageResource(R.mipmap.ic_default_circle);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                mImageView.setImageResource(R.mipmap.ic_default_circle);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                mImageView.setImageResource(R.mipmap.ic_default_circle);
            }
        });

        mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));

        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("主题");
        mTitleList.add("圈友");
        mTitleList.add("商品");

        mArrayList = new ArrayList[3];
        RecyclerView[] mListView = new RecyclerView[3];

        for (int i = 0; i < mTitleList.size(); i++) {
            mArrayList[i] = new ArrayList<>();
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
            mListView[i].setLayoutManager(new LinearLayoutManager(mActivity));
        }

        themeAdapter = new CircleThemeListAdapter(mApplication, mActivity, mArrayList[0]);
        friendAdapter = new CircleFriendListAdapter(mApplication, mActivity, mArrayList[1]);
        goodsAdapter = new GoodsCircleListAdapter(mApplication, mActivity, mArrayList[2]);
        mListView[0].setAdapter(themeAdapter);
        mListView[1].setAdapter(friendAdapter);
        mListView[2].setAdapter(goodsAdapter);

        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mScrollableLayout.getHelper().setCurrentScrollableContainer(mViewList.get(0));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        getThemeJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        applyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!applyBoolean) {
                    Intent intent = new Intent(mActivity, CircleApplyActivity.class);
                    mApplication.startActivityLoginSuccess(mActivity, intent);
                } else {
                    DialogUtil.query(
                            mActivity,
                            "确认您的选择",
                            "退出这个圈子？",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogUtil.cancel();
                                    quitCircle();
                                }
                            }
                    );
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!applyBoolean) {
                    DialogUtil.query(mActivity, "加入圈子?", "您尚未加入圈子", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            Intent intent = new Intent(mActivity, CircleApplyActivity.class);
                            mApplication.startActivityLoginSuccess(mActivity, intent);
                        }
                    });
                } else {
                    Intent intent = new Intent(mActivity, CircleThemeCreateActivity.class);
                    mApplication.startActivityLoginSuccess(mActivity, intent);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mScrollableLayout.getHelper().setCurrentScrollableContainer(mViewList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void quitCircle() {

        if (circle_masterid.equals(mApplication.userHashMap.get("member_id"))) {
            ToastUtil.show(mActivity, "创始人不能退出");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("quit");
        ajaxParams.put("circle_id", circle_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    applyImageView.setImageResource(R.mipmap.ic_action_add);
                    ToastUtil.showSuccess(mActivity);
                    applyBoolean = false;
                    getThemeJson();
                } else {
                    ToastUtil.showFailure(mActivity);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                DialogUtil.cancel();
            }
        });

    }

    private void getThemeJson() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "circle");
        ajaxParams.put("op", "theme");
        ajaxParams.put("circle_id", circle_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    if (TextUtil.isEmpty(mApplication.getJsonError(o.toString()))) {
                        try {
                            mArrayList[0].clear();
                            JSONObject jsonObject = new JSONObject(mApplication.getJsonData(o.toString()));
                            if (!jsonObject.getString("theme_list").equals("[]")) {
                                jsonObject = new JSONObject(jsonObject.getString("theme_list"));
                                Iterator iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next().toString();
                                    mArrayList[0].add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString(key))));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                themeAdapter.notifyDataSetChanged();
                getFriendJson();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                themeAdapter.notifyDataSetChanged();
                getFriendJson();
            }
        });

    }

    private void getFriendJson() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "circle");
        ajaxParams.put("op", "friend");
        ajaxParams.put("circle_id", circle_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    if (TextUtil.isEmpty(mApplication.getJsonError(o.toString()))) {
                        try {
                            mArrayList[1].clear();
                            JSONObject jsonObject = new JSONObject(mApplication.getJsonData(o.toString()));
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("friend_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                hashMap.put("circle_masterid", circle_masterid);
                                if (!mApplication.userHashMap.isEmpty()) {
                                    if (hashMap.get("member_id").equals(mApplication.userHashMap.get("member_id"))) {
                                        applyBoolean = true;
                                    }
                                }
                                mArrayList[1].add(hashMap);
                            }
                            if (applyBoolean) {
                                applyImageView.setImageResource(R.mipmap.ic_action_quit);
                            } else {
                                applyImageView.setImageResource(R.mipmap.ic_action_add);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                friendAdapter.notifyDataSetChanged();
                getGoodsJson();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                friendAdapter.notifyDataSetChanged();
                getGoodsJson();
            }
        });

    }

    private void getGoodsJson() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "circle");
        ajaxParams.put("op", "goods");
        ajaxParams.put("circle_id", circle_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    if (TextUtil.isEmpty(mApplication.getJsonError(o.toString()))) {
                        try {
                            mArrayList[2].clear();
                            JSONObject jsonObject = new JSONObject(mApplication.getJsonData(o.toString()));
                            if (!jsonObject.getString("goods_list").equals("[]")) {
                                jsonObject = new JSONObject(jsonObject.getString("goods_list"));
                                Iterator iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String key1 = iterator.next().toString();
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString(key1));
                                    hashMap.put("goods_id_1", jsonObject1.getString("snsgoods_goodsid"));
                                    hashMap.put("goods_name_1", jsonObject1.getString("snsgoods_goodsname"));
                                    hashMap.put("goods_price_1", jsonObject1.getString("snsgoods_goodsprice"));
                                    hashMap.put("goods_image_url_1", jsonObject1.getString("goods_image_url"));
                                    hashMap.put("member_id_1", jsonObject1.getString("share_memberid"));
                                    hashMap.put("member_name_1", jsonObject1.getString("share_membername"));
                                    hashMap.put("member_avatar_1", jsonObject1.getString("member_avatar"));
                                    hashMap.put("is_like_1", jsonObject1.getString("share_islike"));
                                    hashMap.put("is_share_1", jsonObject1.getString("share_isshare"));
                                    hashMap.put("share_time_1", jsonObject1.getString("share_addtime"));
                                    hashMap.put("like_time_1", jsonObject1.getString("share_likeaddtime"));
                                    hashMap.put("share_content_1", jsonObject1.getString("share_content"));
                                    if (iterator.hasNext()) {
                                        String key2 = iterator.next().toString();
                                        JSONObject jsonObject2 = new JSONObject(jsonObject.getString(key2));
                                        hashMap.put("goods_id_2", jsonObject2.getString("snsgoods_goodsid"));
                                        hashMap.put("goods_name_2", jsonObject2.getString("snsgoods_goodsname"));
                                        hashMap.put("goods_price_2", jsonObject2.getString("snsgoods_goodsprice"));
                                        hashMap.put("goods_image_url_2", jsonObject2.getString("goods_image_url"));
                                        hashMap.put("member_id_2", jsonObject2.getString("share_memberid"));
                                        hashMap.put("member_name_2", jsonObject2.getString("share_membername"));
                                        hashMap.put("member_avatar_2", jsonObject2.getString("member_avatar"));
                                        hashMap.put("is_like_2", jsonObject2.getString("share_islike"));
                                        hashMap.put("is_share_2", jsonObject2.getString("share_isshare"));
                                        hashMap.put("share_time_2", jsonObject2.getString("share_addtime"));
                                        hashMap.put("like_time_2", jsonObject2.getString("share_likeaddtime"));
                                        hashMap.put("share_content_2", jsonObject2.getString("share_content"));
                                    } else {
                                        hashMap.put("goods_id_2", "");
                                    }
                                    mArrayList[2].add(hashMap);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                goodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                goodsAdapter.notifyDataSetChanged();
            }
        });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}