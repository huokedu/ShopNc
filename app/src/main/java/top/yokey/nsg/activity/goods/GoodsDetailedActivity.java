package top.yokey.nsg.activity.goods;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.home.CartMyActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.PhotoActivity;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.activity.store.StoreVoucherActivity;
import top.yokey.nsg.activity.mine.LoginActivity;
import top.yokey.nsg.activity.order.BuySetup1Activity;
import top.yokey.nsg.adapter.GoodsDetailedListAdapter;
import top.yokey.nsg.adapter.SpecListAdapter;
import top.yokey.nsg.adapter.ViewPagerAdapter;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class GoodsDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String goods_id;
    private String store_id;
    private String member_id;
    private String goods_image;
    private int goods_storage;
    private String goods_name;
    private String goods_jingle;
    private String[] specString;
    private String evaluation_count;
    private boolean collectionBoolean;
    private HashMap<String, String> mHashMap;
    private ArrayList<HashMap<String, String>> specNameArrayList;
    private ArrayList<HashMap<String, String>> specValueArrayList;
    private ArrayList<HashMap<String, String>> goodsSpecArrayList;
    private ArrayList<HashMap<String, String>> specListArrayList;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView collectionImageView;
    private ImageView shareImageView;

    private ViewPager goodsViewPager;
    private TextView nameTextView;
    private TextView jingleTextView;
    private TextView pricePromotionTextView;
    private TextView priceTextView;
    private TextView saleNumTextView;
    private TextView vouchersTextView;
    private LinearLayout activityLinearLayout;
    private TextView activityNameTextView;
    private TextView activityTitleTextView;
    private TextView activityContentTextView;
    private TextView hairTitleTextView;
    private TextView hairContentTextView;
    private LinearLayout specLinearLayout;
    private TextView[] specTextView;
    private LinearLayout evaluateLinearLayout;
    private TextView evaluatePerTextView;
    private TextView evaluateNumTextView;
    private TextView introduceTextView;
    private TextView storeTextView;
    private TextView storeInTextView;
    private TextView storeGoodsTextView;
    private TextView storeDescTextView;
    private TextView storeServerTextView;
    private TextView storeDeliverTextView;

    private TextView backgroundTextView;
    private RelativeLayout specRelativeLayout;
    private ImageView specImageView;
    private TextView specNameTextView;
    private TextView specPriceTextView;
    private TextView specStockTextView;
    private TextView[] specValueTextView;
    private View[] specLineView;
    private RecyclerView[] specValueListView;
    private TextView specAddTextView;
    private View preSellLineView;
    private TextView preSellTextView;
    private TextView preSellTimeTextView;
    private EditText specNumberEditText;
    private TextView specSubTextView;

    private TextView[] bottomTextView;

    private RecyclerView goodsListView;
    private GoodsDetailedListAdapter goodsAdapter;
    private ArrayList<HashMap<String, String>> goodsArrayList;

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
        setContentView(R.layout.activity_goods_detailed);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (mHashMap != null) {
            String temp = mHashMap.get("goods_info");
            HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(temp));
            //是否收藏
            if (!TextUtil.isEmpty(mApplication.userKeyString)) {
                if (hashMap.get("goods_collect").equals("1")) {
                    collectionBoolean = true;
                    collectionImageView.setImageResource(R.mipmap.ic_action_collection_press);
                } else {
                    collectionBoolean = false;
                    collectionImageView.setImageResource(R.mipmap.ic_action_collection);
                }
            }
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
        collectionImageView = (ImageView) findViewById(R.id.collectionImageView);
        shareImageView = (ImageView) findViewById(R.id.shareImageView);

        goodsViewPager = (ViewPager) findViewById(R.id.goodsViewPager);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        jingleTextView = (TextView) findViewById(R.id.jingleTextView);
        pricePromotionTextView = (TextView) findViewById(R.id.pricePromotionTextView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        saleNumTextView = (TextView) findViewById(R.id.saleNumTextView);
        vouchersTextView = (TextView) findViewById(R.id.vouchersTextView);
        activityLinearLayout = (LinearLayout) findViewById(R.id.activityLinearLayout);
        activityNameTextView = (TextView) findViewById(R.id.activityNameTextView);
        activityTitleTextView = (TextView) findViewById(R.id.activityTitleTextView);
        activityContentTextView = (TextView) findViewById(R.id.activityContentTextView);
        hairTitleTextView = (TextView) findViewById(R.id.hairTitleTextView);
        hairContentTextView = (TextView) findViewById(R.id.hairContentTextView);
        specLinearLayout = (LinearLayout) findViewById(R.id.specLinearLayout);
        specTextView = new TextView[2];
        specTextView[0] = (TextView) findViewById(R.id.spec1TextView);
        specTextView[1] = (TextView) findViewById(R.id.spec2TextView);
        evaluateLinearLayout = (LinearLayout) findViewById(R.id.evaluateLinearLayout);
        evaluatePerTextView = (TextView) findViewById(R.id.evaluatePerTextView);
        evaluateNumTextView = (TextView) findViewById(R.id.evaluateNumTextView);
        introduceTextView = (TextView) findViewById(R.id.introduceTextView);
        storeTextView = (TextView) findViewById(R.id.storeTextView);
        storeInTextView = (TextView) findViewById(R.id.storeInTextView);
        storeGoodsTextView = (TextView) findViewById(R.id.storeGoodsTextView);
        storeDescTextView = (TextView) findViewById(R.id.storeDescTextView);
        storeServerTextView = (TextView) findViewById(R.id.storeServerTextView);
        storeDeliverTextView = (TextView) findViewById(R.id.storeDeliverTextView);
        goodsListView = (RecyclerView) findViewById(R.id.goodsListView);

        backgroundTextView = (TextView) findViewById(R.id.backgroundTextView);
        specRelativeLayout = (RelativeLayout) findViewById(R.id.specRelativeLayout);
        specImageView = (ImageView) findViewById(R.id.specImageView);
        specNameTextView = (TextView) findViewById(R.id.specNameTextView);
        specPriceTextView = (TextView) findViewById(R.id.specPriceTextView);
        specStockTextView = (TextView) findViewById(R.id.specStockTextView);
        specValueTextView = new TextView[2];
        specValueTextView[0] = (TextView) findViewById(R.id.specValue1TextView);
        specValueTextView[1] = (TextView) findViewById(R.id.specValue2TextView);
        specLineView = new View[2];
        specLineView[0] = findViewById(R.id.specLine1View);
        specLineView[1] = findViewById(R.id.specLine2View);
        specValueListView = new RecyclerView[2];
        specValueListView[0] = (RecyclerView) findViewById(R.id.specValue1ListView);
        specValueListView[1] = (RecyclerView) findViewById(R.id.specValue2ListView);
        preSellLineView = findViewById(R.id.specLine4View);
        preSellTextView = (TextView) findViewById(R.id.preSellTextView);
        preSellTimeTextView = (TextView) findViewById(R.id.preSellTimeTextView);
        specAddTextView = (TextView) findViewById(R.id.specAddTextView);
        specNumberEditText = (EditText) findViewById(R.id.specNumberEditText);
        specSubTextView = (TextView) findViewById(R.id.specSubTextView);

        bottomTextView = new TextView[4];
        bottomTextView[0] = (TextView) findViewById(R.id.bottom0TextView);
        bottomTextView[1] = (TextView) findViewById(R.id.bottom1TextView);
        bottomTextView[2] = (TextView) findViewById(R.id.bottom2TextView);
        bottomTextView[3] = (TextView) findViewById(R.id.bottom3TextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        store_id = "0";
        goods_name = "";
        goods_image = "";
        goods_jingle = "";
        evaluation_count = "0";
        collectionBoolean = false;
        specString = new String[]{"", ""};
        goods_id = mActivity.getIntent().getStringExtra("goods_id");

        if (TextUtil.isEmpty(goods_id)) {
            mApplication.finishActivity(mActivity);
            ToastUtil.show(mActivity, "参数异常");
        }

        titleTextView.setText("商品详细");

        specRelativeLayout.setVisibility(View.GONE);
        specValueListView[0].setVisibility(View.GONE);
        specValueListView[1].setVisibility(View.GONE);
        specValueTextView[0].setVisibility(View.GONE);
        specValueTextView[1].setVisibility(View.GONE);
        specTextView[0].setVisibility(View.GONE);
        specTextView[1].setVisibility(View.GONE);
        specLineView[0].setVisibility(View.GONE);
        specLineView[1].setVisibility(View.GONE);
        specAddTextView.setText("+");
        specNumberEditText.setText("1");
        specSubTextView.setText("-");

        goodsArrayList = new ArrayList<>();
        goodsAdapter = new GoodsDetailedListAdapter(mApplication, mActivity, goodsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        goodsListView.setLayoutManager(linearLayoutManager);
        goodsListView.setAdapter(goodsAdapter);

        //动态改变 mViewPager 高度
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) goodsViewPager.getLayoutParams();
        layoutParams.height = DisplayUtil.getWidth(mActivity);
        goodsViewPager.setLayoutParams(layoutParams);

        getJson();

    }

    private void initEven() {

        //返回按钮
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "商品分享");
                intent.putExtra("name", goods_name);
                intent.putExtra("jingle", goods_jingle);
                intent.putExtra("image", goods_image);
                intent.putExtra("link", mApplication.goodsUrlString + goods_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        collectionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmpty(mApplication.userKeyString)) {
                    mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                } else {
                    if (collectionBoolean) {
                        DialogUtil.query(
                                mActivity,
                                "确认您的选择",
                                "取消收藏?",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DialogUtil.cancel();
                                        DialogUtil.progress(mActivity);
                                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                                        ajaxParams.putAct("member_favorites");
                                        ajaxParams.putOp("favorites_del");
                                        ajaxParams.put("fav_id", goods_id);
                                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                super.onSuccess(o);
                                                DialogUtil.cancel();
                                                if (mApplication.getJsonSuccess(o.toString())) {
                                                    collectionImageView.setImageResource(R.mipmap.ic_action_collection);
                                                    ToastUtil.show(mActivity, "取消收藏成功");
                                                    collectionBoolean = false;
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
                                }
                        );
                    } else {
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_favorites");
                        ajaxParams.putOp("favorites_add");
                        ajaxParams.put("goods_id", goods_id);
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (!TextUtil.isEmpty(o.toString())) {
                                    if (mApplication.getJsonSuccess(o.toString())) {
                                        collectionImageView.setImageResource(R.mipmap.ic_action_collection_press);
                                        ToastUtil.show(mActivity, "收藏成功");
                                        collectionBoolean = true;
                                    } else {
                                        ToastUtil.showFailure(mActivity);
                                    }
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
                }
            }
        });

        vouchersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, StoreVoucherActivity.class);
                intent.putExtra("store_id", store_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        specLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (specRelativeLayout.getVisibility() == View.GONE) {
                    showSpecLayout();
                }
            }
        });

        specTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (specRelativeLayout.getVisibility() == View.GONE) {
                    showSpecLayout();
                }
            }
        });

        specTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (specRelativeLayout.getVisibility() == View.GONE) {
                    showSpecLayout();
                }
            }
        });

        //商品评价
        evaluateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GoodsEvaluateActivity.class);
                intent.putExtra("goods_id", goods_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        //商品介绍
        introduceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("model", "goods_introduce");
                intent.putExtra("link", mApplication.apiUrlString + "act=goods&op=goods_body&goods_id=" + goods_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        //进入店铺
        storeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!store_id.equals("0")) {
                    mApplication.startStore(mActivity, store_id);
                }
            }
        });

        //进入店铺
        storeInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!store_id.equals("0")) {
                    mApplication.startStore(mActivity, store_id);
                }
            }
        });

        //客服
        bottomTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (member_id.equals(mApplication.userHashMap.get("member_id"))) {
                    ToastUtil.show(mActivity, "您不能跟自己聊天");
                    return;
                }
                mApplication.startChat(mActivity, member_id);
            }
        });

        //购物车
        bottomTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, CartMyActivity.class));
            }
        });

        //加入购物车
        bottomTextView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (member_id.equals(mApplication.userHashMap.get("member_id"))) {
                    ToastUtil.show(mActivity, "您不能购买自己店铺的商品");
                    return;
                }
                if (specRelativeLayout.getVisibility() == View.VISIBLE) {
                    if (TextUtil.isEmpty(mApplication.userKeyString)) {
                        mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                    } else {
                        int number = Integer.parseInt(specNumberEditText.getText().toString());
                        if (number < 0 || number > goods_storage) {
                            ToastUtil.show(mActivity, "购买数量有误");
                        } else {
                            KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                            ajaxParams.putAct("member_cart");
                            ajaxParams.putOp("cart_add");
                            ajaxParams.put("goods_id", goods_id);
                            ajaxParams.put("quantity", number + "");
                            mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                                @Override
                                public void onSuccess(Object o) {
                                    super.onSuccess(o);
                                    if (TextUtil.isJson(o.toString())) {
                                        String error = mApplication.getJsonError(o.toString());
                                        if (TextUtil.isEmpty(error)) {
                                            String data = mApplication.getJsonData(o.toString());
                                            if (data.equals("1")) {
                                                ToastUtil.showSuccess(mActivity);
                                                showSpecLayout();
                                            } else {
                                                ToastUtil.showFailure(mActivity);
                                            }
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
                                    } else {
                                        ToastUtil.showFailure(mActivity);
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t, int errorNo, String strMsg) {
                                    super.onFailure(t, errorNo, strMsg);
                                    ToastUtil.showFailure(mActivity);
                                }
                            });
                        }
                    }
                } else {
                    showSpecLayout();
                }
            }
        });

        //购买
        bottomTextView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (member_id.equals(mApplication.userHashMap.get("member_id"))) {
                    ToastUtil.show(mActivity, "您不能购买自己店铺的商品");
                    return;
                }
                if (specRelativeLayout.getVisibility() == View.VISIBLE) {
                    if (TextUtil.isEmpty(mApplication.userKeyString)) {
                        mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                    } else {
                        int number = Integer.parseInt(specNumberEditText.getText().toString());
                        if (number < 0 || number > goods_storage) {
                            ToastUtil.show(mActivity, "购买数量有误");
                        } else {
                            Intent intent = new Intent(mActivity, BuySetup1Activity.class);
                            intent.putExtra("ifcart", "0");
                            intent.putExtra("cart_id", goods_id + "|" + number);
                            mApplication.startActivityLoginSuccess(mActivity, intent);
                            showSpecLayout();
                        }
                    }
                } else {
                    showSpecLayout();
                }
            }
        });

        backgroundTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (specRelativeLayout.getVisibility() == View.VISIBLE) {
                    showSpecLayout();
                }
            }
        });

        specNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    specNumberEditText.setSelection(specNumberEditText.getText().length());
                }
            }
        });

        specAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(specNumberEditText.getText().toString());
                if (number < goods_storage) {
                    String numberString = number + 1 + "";
                    specNumberEditText.setText(numberString);
                    specNumberEditText.setSelection(numberString.length());
                }
            }
        });

        specSubTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(specNumberEditText.getText().toString());
                if (number > 1) {
                    String numberString = number - 1 + "";
                    specNumberEditText.setText(numberString);
                    specNumberEditText.setSelection(numberString.length());
                }
            }
        });

        specRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不需要任何处理
            }
        });

        specNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!specNumberEditText.getText().toString().isEmpty()) {
                    int number = Integer.parseInt(specNumberEditText.getText().toString());
                    if (number < 1) {
                        specNumberEditText.setText("1");
                    }
                    if (number > goods_storage) {
                        String temp = goods_storage + "";
                        specNumberEditText.setText(temp);
                    }
                    specNumberEditText.setSelection(specNumberEditText.getText().length());
                } else {
                    specNumberEditText.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);
        String url = mApplication.apiUrlString + "act=goods&op=goods_detail&goods_id=" + goods_id + "&key=" + mApplication.userKeyString;
        mApplication.mFinalHttp.get(url, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(data));
                        parseGoodsInfo();
                        parseManSongInfo();
                        parseGiftArray();
                        parseCommendList();
                        parseStoreInfo();
                        parseSpecList();
                        parseGoodsImage();
                        parseVoucher();
                        parseEvalList();
                        parseEvalInfo();
                        parseHairInfo();
                    } else {
                        ToastUtil.show(mActivity, error);
                    }
                } else {
                    getJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        DialogUtil.query(mActivity, "是否重试?", "读取数据失败", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.cancel();
                getJson();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.finishActivity(mActivity);
                DialogUtil.cancel();
            }
        });

    }

    private void parseGoodsInfo() {

        //读取 goods_info 并转换成 hashMap
        String temp = mHashMap.get("goods_info");
        HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(temp));
        //评论人数
        evaluation_count = hashMap.get("evaluation_count");
        //是否收藏
        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
            if (mHashMap.get("is_favorate").equals("true")) {
                collectionBoolean = true;
                collectionImageView.setImageResource(R.mipmap.ic_action_collection_press);
            } else {
                collectionBoolean = false;
                collectionImageView.setImageResource(R.mipmap.ic_action_collection);
            }
        } else {
            collectionBoolean = false;
            collectionImageView.setImageResource(R.mipmap.ic_action_collection);
        }
        //名字简介
        goods_name = hashMap.get("goods_name");
        goods_jingle = hashMap.get("goods_jingle");
        nameTextView.setText(hashMap.get("goods_name"));
        specNameTextView.setText(hashMap.get("goods_name"));
        if (!TextUtil.isEmpty(hashMap.get("goods_jingle"))) {
            jingleTextView.setText(hashMap.get("goods_jingle"));
        } else {
            jingleTextView.setVisibility(View.GONE);
        }
        //库存
        goods_storage = Integer.parseInt(hashMap.get("goods_storage"));
        temp = "库存：" + goods_storage + " 件";
        specStockTextView.setText(temp);
        //价格
        temp = "￥ " + hashMap.get("goods_promotion_price");
        pricePromotionTextView.setText(temp);
        specPriceTextView.setText(temp);
        //价格
        temp = "￥ " + hashMap.get("goods_price");
        priceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        priceTextView.setText(temp);
        //销量
        temp = "销量：<font color='#FF0000'>" + hashMap.get("goods_salenum") + "</font> 件";
        saleNumTextView.setText(Html.fromHtml(temp));
        //活动
        temp = hashMap.get("goods_promotion_type");
        if (!temp.equals("0")) {
            if (hashMap.get("promotion_type") != null) {
                try {
                    temp = hashMap.get("promotion_type");
                    if (temp.equals("xianshi")) {
                        activityNameTextView.setText("限时");
                        temp = "直降 " + hashMap.get("down_price") + " 元，最低 " + hashMap.get("lower_limit") + " 件起";
                        activityContentTextView.setText(temp);
                    }
                    if (temp.equals("groupbuy")) {
                        activityNameTextView.setText("团购");
                        temp = "直降 " + hashMap.get("down_price") + " 元，限购 " + hashMap.get("upper_limit") + " 件";
                        activityContentTextView.setText(temp);
                    }
                    activityTitleTextView.setText(hashMap.get("title"));
                } catch (Exception e) {
                    activityLinearLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            } else {
                activityLinearLayout.setVisibility(View.GONE);
            }
        } else {
            activityLinearLayout.setVisibility(View.GONE);
        }
        //spec_name
        temp = hashMap.get("spec_name");
        specNameArrayList = new ArrayList<>();
        if (!TextUtil.isEmpty(temp)) {
            try {
                JSONObject jsonObject = new JSONObject(temp);
                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    String key = iterator.next().toString();
                    String value = jsonObject.getString(key);
                    hashMap1.put("id", key);
                    hashMap1.put("value", value);
                    specNameArrayList.add(hashMap1);
                }
                for (int i = 0; i < specNameArrayList.size(); i++) {
                    if (i < 2) {
                        specLineView[i].setVisibility(View.VISIBLE);
                        specValueListView[i].setVisibility(View.VISIBLE);
                        specValueTextView[i].setVisibility(View.VISIBLE);
                        specValueTextView[i].setText(specNameArrayList.get(i).get("value"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            specTextView[0].setText("默认");
            specTextView[0].setVisibility(View.VISIBLE);
        }
        //spec_value
        temp = hashMap.get("spec_value");
        specValueArrayList = new ArrayList<>();
        if (!TextUtil.isEmpty(temp)) {
            try {
                JSONObject jsonObject = new JSONObject(temp);
                if (specNameArrayList.size() != 0) {
                    for (int i = 0; i < specNameArrayList.size(); i++) {
                        String id = specNameArrayList.get(i).get("id");
                        String value = specNameArrayList.get(i).get("value");
                        JSONObject object = new JSONObject(jsonObject.getString(id));
                        Iterator iterator = object.keys();
                        while (iterator.hasNext()) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            String key = iterator.next().toString();
                            hashMap1.put("value", object.getString(key));
                            hashMap1.put("parent_value", value);
                            hashMap1.put("parent_id", id);
                            hashMap1.put("id", key);
                            specValueArrayList.add(hashMap1);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //goods_spec
        temp = hashMap.get("goods_spec");
        goodsSpecArrayList = new ArrayList<>();
        if (!TextUtil.isEmpty(temp)) {
            try {
                JSONObject jsonObject = new JSONObject(temp);
                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    String key = iterator.next().toString();
                    String value = jsonObject.getString(key);
                    for (int i = 0; i < specValueArrayList.size(); i++) {
                        String id = specValueArrayList.get(i).get("id");
                        if (key.equals(id)) {
                            String parent_value = specValueArrayList.get(i).get("parent_value");
                            hashMap1.put("key", key);
                            hashMap1.put("value", value);
                            hashMap1.put("content", parent_value + "：" + value);
                        }
                    }
                    goodsSpecArrayList.add(hashMap1);
                }
                for (int i = 0; i < goodsSpecArrayList.size(); i++) {
                    if (i < 2) {
                        specTextView[i].setVisibility(View.VISIBLE);
                        specTextView[i].setText(goodsSpecArrayList.get(i).get("content"));
                        specString[i] = goodsSpecArrayList.get(i).get("key");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //预售处理
        if (hashMap.get("is_presell").equals("1")) {
            bottomTextView[2].setVisibility(View.GONE);
            preSellLineView.setVisibility(View.VISIBLE);
            preSellTextView.setVisibility(View.VISIBLE);
            preSellTimeTextView.setVisibility(View.VISIBLE);
            temp = TimeUtil.longToTime(hashMap.get("presell_deliverdate")) + " 发货";
            preSellTimeTextView.setText(temp);
        } else {
            bottomTextView[2].setVisibility(View.VISIBLE);
            preSellLineView.setVisibility(View.GONE);
            preSellTextView.setVisibility(View.GONE);
            preSellTimeTextView.setVisibility(View.GONE);
        }

    }

    private void parseManSongInfo() {

    }

    private void parseGiftArray() {

    }

    private void parseCommendList() {

        String temp = mHashMap.get("goods_commend_list");
        try {
            JSONArray jsonArray = new JSONArray(temp);
            for (int i = 0; i < jsonArray.length(); i++) {
                goodsArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
            }
            goodsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseStoreInfo() {

        String temp = mHashMap.get("store_info");
        HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(temp));
        //店铺ID
        store_id = hashMap.get("store_id");
        member_id = hashMap.get("member_id");
        //店铺名字
        storeTextView.setText(hashMap.get("store_name"));
        //商品总数
        temp = "商品总数：<font color='#FF0000'>" + hashMap.get("goods_count") + " </font>件";
        storeGoodsTextView.setText(Html.fromHtml(temp));
        //店铺评分
        try {
            JSONObject jsonObject = new JSONObject(hashMap.get("store_credit"));
            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("store_desccredit"));
            temp = jsonObject1.getString("text") + "：" + jsonObject1.getString("credit") + " 分";
            storeDescTextView.setText(temp);
            jsonObject1 = new JSONObject(jsonObject.getString("store_servicecredit"));
            temp = jsonObject1.getString("text") + "：" + jsonObject1.getString("credit") + " 分";
            storeServerTextView.setText(temp);
            jsonObject1 = new JSONObject(jsonObject.getString("store_deliverycredit"));
            temp = jsonObject1.getString("text") + "：" + jsonObject1.getString("credit") + " 分";
            storeDeliverTextView.setText(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseSpecList() {

        ArrayList<HashMap<String, String>>[] specArrayList = new ArrayList[2];
        SpecListAdapter[] specAdapter = new SpecListAdapter[2];

        String temp = mHashMap.get("spec_list");
        specListArrayList = new ArrayList<>(TextUtil.jsonObjectToArrayList(temp));

        for (int i = 0; i < specNameArrayList.size(); i++) {
            if (i < 2) {
                String value = specNameArrayList.get(i).get("value");
                specArrayList[i] = new ArrayList<>();
                for (int j = 0; j < specValueArrayList.size(); j++) {
                    if (value.equals(specValueArrayList.get(j).get("parent_value"))) {
                        HashMap<String, String> hashMap = new HashMap<>(specValueArrayList.get(j));
                        hashMap.put("default", "0");
                        for (int k = 0; k < goodsSpecArrayList.size(); k++) {
                            if (goodsSpecArrayList.get(k).get("value").equals(hashMap.get("value"))) {
                                hashMap.put("default", "1");
                                break;
                            }
                        }
                        specArrayList[i].add(hashMap);
                    }
                }
            }
        }

        for (int i = 0; i < specArrayList.length; i++) {
            if (i < 2) {
                final int position = i;
                specAdapter[i] = new SpecListAdapter(specArrayList[i]);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                specValueListView[i].setLayoutManager(linearLayoutManager);
                specValueListView[i].setAdapter(specAdapter[i]);
                specAdapter[i].setOnItemClickListener(new SpecListAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(String id, String value) {
                        specString[position] = id;
                        refreshSpecData();
                    }
                });
            }
        }

    }

    private void parseGoodsImage() {

        final String temp = mHashMap.get("goods_image");
        Vector<String> vector = new Vector<>(TextUtil.encodeImage(temp));
        if (vector.size() != 0) {
            goods_image = vector.get(0);
            List<View> list = new ArrayList<>();
            ImageView[] imageView = new ImageView[vector.size()];
            ImageLoader.getInstance().displayImage(vector.get(0), specImageView);
            for (int i = 0; i < vector.size(); i++) {
                final int position = i;
                list.add(mActivity.getLayoutInflater().inflate(R.layout.include_image_view, null));
                imageView[i] = (ImageView) list.get(i).findViewById(R.id.mainImageView);
                ImageLoader.getInstance().displayImage(vector.get(i), imageView[i]);
                imageView[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity, PhotoActivity.class);
                        intent.putExtra("title", "商品图片");
                        intent.putExtra("position", position);
                        intent.putExtra("image", temp);
                        mApplication.startActivity(mActivity, intent);
                    }
                });
            }
            goodsViewPager.setAdapter(new ViewPagerAdapter(list));
        }

    }

    private void parseVoucher() {

        if (mHashMap.get("voucher") == null) {
            vouchersTextView.setVisibility(View.GONE);
        } else {
            vouchersTextView.setVisibility(View.VISIBLE);
        }

    }

    private void parseEvalList() {

    }

    private void parseEvalInfo() {

        String temp = mHashMap.get("goods_evaluate_info");
        HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(temp));
        temp = hashMap.get("good_percent") + "% 好评";
        evaluatePerTextView.setText(temp);
        temp = evaluation_count + " 人评价";
        evaluateNumTextView.setText(temp);

    }

    private void parseHairInfo() {

        String temp = mHashMap.get("goods_hair_info");
        HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(temp));
        hairTitleTextView.setText(hashMap.get("area_name"));
        temp = hashMap.get("if_store_cn") + "，" + hashMap.get("content");
        hairContentTextView.setText(temp);

    }

    private void showSpecLayout() {

        if (specRelativeLayout.getVisibility() == View.GONE) {
            specRelativeLayout.startAnimation(mApplication.upTranslateAnimation);
            specRelativeLayout.setVisibility(View.VISIBLE);
            backgroundTextView.startAnimation(mApplication.showAlphaAnimation);
            backgroundTextView.setVisibility(View.VISIBLE);
        } else {
            specRelativeLayout.startAnimation(mApplication.downTranslateAnimation);
            specRelativeLayout.setVisibility(View.GONE);
            backgroundTextView.startAnimation(mApplication.goneAlphaAnimation);
            backgroundTextView.setVisibility(View.GONE);
        }

    }

    private void refreshSpecData() {

        for (int i = 0; i < specListArrayList.size(); i++) {
            String key = specListArrayList.get(i).get("key");
            if (key.contains(specString[0]) && key.contains(specString[1])) {
                goods_id = specListArrayList.get(i).get("value");
                break;
            }
        }

        getJson();

    }

    private void returnActivity() {

        if (specRelativeLayout.getVisibility() == View.VISIBLE) {
            showSpecLayout();
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}