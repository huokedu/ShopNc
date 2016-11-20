package top.yokey.nsg.activity.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.List;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.adapter.BasePagerAdapter;
import top.yokey.nsg.adapter.GoodsListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class StoreActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String store_id;
    private List<View> mViewList;
    private HashMap<String, String> mHashMap;

    private ImageView backImageView;
    private EditText keywordEditText;
    private ImageView searchImageView;

    private ScrollableLayout mScrollableLayout;
    private RelativeLayout storeRelativeLayout;
    private ImageView avatarImageView;
    private TextView nameTextView;
    private TextView collectTextView;
    private TextView collectNumTextView;
    private TextView creditTextView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView[] mTextView;
    private Spinner mSpinner;

    private RecyclerView[] mListView;
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
        setContentView(R.layout.activity_store);
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
        keywordEditText = (EditText) findViewById(R.id.keywordEditText);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);

        mScrollableLayout = (ScrollableLayout) findViewById(R.id.mainScrollableLayout);
        storeRelativeLayout = (RelativeLayout) findViewById(R.id.storeRelativeLayout);
        mSpinner = (Spinner) findViewById(R.id.typeSpinner);
        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        collectTextView = (TextView) findViewById(R.id.collectTextView);
        collectNumTextView = (TextView) findViewById(R.id.collectNumTextView);
        creditTextView = (TextView) findViewById(R.id.creditTextView);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);

        mTextView = new TextView[3];
        mTextView[0] = (TextView) findViewById(R.id.infoTextView);
        mTextView[1] = (TextView) findViewById(R.id.vouchersTextView);
        mTextView[2] = (TextView) findViewById(R.id.keFuTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        mHashMap = new HashMap<>();

        store_id = mActivity.getIntent().getStringExtra("store_id");
        keywordEditText.setHint("搜索店内商品 ");
        mSpinner.setVisibility(View.GONE);

        mViewList = new ArrayList<>();
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));
        mViewList.add(mActivity.getLayoutInflater().inflate(R.layout.include_list_view_none, null));

        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("店铺推荐");
        mTitleList.add("全部商品");
        mTitleList.add("商品上新");
        mTitleList.add("店铺活动");

        mArrayList = new ArrayList[4];
        mListView = new RecyclerView[4];

        for (int i = 0; i < mTitleList.size(); i++) {
            mArrayList[i] = new ArrayList<>();
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));
            mListView[i] = (RecyclerView) mViewList.get(i).findViewById(R.id.mainListView);
        }

        ControlUtil.setTabLayout(mActivity, mTabLayout, new BasePagerAdapter(mViewList, mTitleList), mViewPager);
        mScrollableLayout.getHelper().setCurrentScrollableContainer(mViewList.get(0));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        getInfo();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        collectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtil.isEmpty(mApplication.userKeyString)) {
                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                    ajaxParams.putAct("member_favorites_store");
                    ajaxParams.put("store_id", store_id);
                    if (collectTextView.getText().toString().equals("已收藏")) {
                        ajaxParams.putOp("favorites_del");
                    } else {
                        ajaxParams.putOp("favorites_add");
                    }
                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            getInfo();
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            ToastUtil.showFailure(mActivity);
                        }
                    });
                } else {
                    mApplication.startLogin(mActivity);
                }
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordEditText.getText().toString();
                if (!TextUtil.isEmpty(keyword)) {
                    Intent intent = new Intent(mActivity, StoreGoodsActivity.class);
                    intent.putExtra("store_id", store_id);
                    intent.putExtra("keyword", keyword);
                    mApplication.startActivity(mActivity, intent);
                }
            }
        });

        storeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "店铺分享");
                intent.putExtra("name", mHashMap.get("store_name"));
                intent.putExtra("jingle", mHashMap.get("store_name"));
                intent.putExtra("image", mHashMap.get("store_avatar"));
                intent.putExtra("link", mApplication.storeUrlString + mHashMap.get("store_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        mTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StoreInfoActivity.class);
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_credit", creditTextView.getText().toString());
                mApplication.startActivity(mActivity, intent);
            }
        });

        mTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, StoreVoucherActivity.class);
                intent.putExtra("store_id", store_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        mTextView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startChat(mActivity, mHashMap.get("member_id"));
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

    private void getInfo() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("store");
        ajaxParams.putOp("store_info");
        ajaxParams.put("store_id", store_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("store_info")));
                            ImageLoader.getInstance().displayImage(mHashMap.get("store_avatar"), avatarImageView);
                            nameTextView.setText(mHashMap.get("store_name"));
                            collectNumTextView.setText("粉丝 ");
                            collectNumTextView.append(mHashMap.get("store_collect"));
                            creditTextView.setText(mHashMap.get("store_credit_text").replace(",", "\n").replace(" ", ""));
                            if (mHashMap.get("is_favorate").equals("true")) {
                                collectTextView.setText("已收藏");
                                collectTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
                            } else {
                                collectTextView.setText("收藏");
                                collectTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.pink));
                            }
                            getRank();
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
                getInfoFailure();
            }
        });

    }

    private void getInfoFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取店铺信息失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getInfo();
                    }
                }
        );

    }

    private void getRank() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "store");
        ajaxParams.put("op", "store_goods_rank");
        ajaxParams.put("store_id", store_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[0].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                            for (int i = 0; i < jsonArray.length(); i += 2) {
                                jsonObject = (JSONObject) jsonArray.get(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("model", "hor");
                                hashMap.put("goods_id_1", jsonObject.getString("goods_id"));
                                hashMap.put("goods_name_1", jsonObject.getString("goods_name"));
                                hashMap.put("goods_price_1", jsonObject.getString("goods_marketprice"));
                                hashMap.put("goods_promotion_price_1", jsonObject.getString("goods_price"));
                                hashMap.put("group_flag_1", jsonObject.getString("group_flag"));
                                hashMap.put("xianshi_flag_1", jsonObject.getString("xianshi_flag"));
                                hashMap.put("goods_image_url_1", jsonObject.getString("goods_image_url"));
                                if ((i + 1) < jsonArray.length()) {
                                    jsonObject = (JSONObject) jsonArray.get(i + 1);
                                    hashMap.put("goods_id_2", jsonObject.getString("goods_id"));
                                    hashMap.put("goods_name_2", jsonObject.getString("goods_name"));
                                    hashMap.put("goods_price_2", jsonObject.getString("goods_marketprice"));
                                    hashMap.put("goods_promotion_price_2", jsonObject.getString("goods_price"));
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
                                mArrayList[0].add(hashMap);
                            }
                            mListView[0].setLayoutManager(new LinearLayoutManager(mActivity));
                            mListView[0].setAdapter(new GoodsListAdapter(mApplication, mActivity, mArrayList[0]));
                            getAll();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getRankFailure();
                        }
                    } else {
                        getRankFailure();
                    }
                } else {
                    getRankFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getRankFailure();
            }
        });

    }

    private void getRankFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取店铺推荐失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getRank();
                    }
                }
        );

    }

    private void getAll() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "store");
        ajaxParams.put("op", "store_goods");
        ajaxParams.put("store_id", store_id);
        ajaxParams.put("page", "999");

        mApplication.mFinalHttp.post(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[1].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                hashMap.put("goods_promotion_price", hashMap.get("goods_price"));
                                hashMap.put("goods_price", hashMap.get("goods_marketprice"));
                                hashMap.put("model", "ver");
                                mArrayList[1].add(hashMap);
                            }
                            mListView[1].setLayoutManager(new LinearLayoutManager(mActivity));
                            mListView[1].setAdapter(new GoodsListAdapter(mApplication, mActivity, mArrayList[1]));
                            getNew();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getAllFailure();
                        }
                    } else {
                        getAllFailure();
                    }
                } else {
                    getAllFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getAllFailure();
            }
        });

    }

    private void getAllFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取店铺商品失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getAll();
                    }
                }
        );

    }

    private void getNew() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "store");
        ajaxParams.put("op", "store_new_goods");
        ajaxParams.put("store_id", store_id);
        ajaxParams.put("page", "999");

        mApplication.mFinalHttp.post(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            mArrayList[2].clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("goods_list"));
                            for (int i = 0; i < jsonArray.length(); i += 2) {
                                jsonObject = (JSONObject) jsonArray.get(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("model", "hor");
                                hashMap.put("goods_id_1", jsonObject.getString("goods_id"));
                                hashMap.put("goods_name_1", jsonObject.getString("goods_name"));
                                hashMap.put("goods_price_1", jsonObject.getString("goods_marketprice"));
                                hashMap.put("goods_promotion_price_1", jsonObject.getString("goods_price"));
                                hashMap.put("group_flag_1", jsonObject.getString("group_flag"));
                                hashMap.put("xianshi_flag_1", jsonObject.getString("xianshi_flag"));
                                hashMap.put("goods_image_url_1", jsonObject.getString("goods_image_url"));
                                if ((i + 1) < jsonArray.length()) {
                                    jsonObject = (JSONObject) jsonArray.get(i + 1);
                                    hashMap.put("goods_id_2", jsonObject.getString("goods_id"));
                                    hashMap.put("goods_name_2", jsonObject.getString("goods_name"));
                                    hashMap.put("goods_price_2", jsonObject.getString("goods_marketprice"));
                                    hashMap.put("goods_promotion_price_2", jsonObject.getString("goods_price"));
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
                                mArrayList[2].add(hashMap);
                            }
                            mListView[2].setLayoutManager(new LinearLayoutManager(mActivity));
                            mListView[2].setAdapter(new GoodsListAdapter(mApplication, mActivity, mArrayList[2]));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getNewFailure();
                        }
                    } else {
                        getNewFailure();
                    }
                } else {
                    getNewFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getNewFailure();
            }
        });

    }

    private void getNewFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取店铺新品失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getNew();
                    }
                }
        );

    }

    private void returnActivity() {

        if (keywordEditText.getText().length() != 0) {
            keywordEditText.setText("");
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}