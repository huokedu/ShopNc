package top.yokey.nsg.activity.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class StoreInfoActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String store_id;
    private String store_name;
    private String store_avatar;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView shareImageView;

    private RelativeLayout storeRelativeLayout;
    private ImageView avatarImageView;
    private TextView nameTextView;
    private TextView collectTextView;
    private TextView collectNumTextView;
    private TextView creditTextView;

    private TextView storeNameTextView;
    private TextView storeAreaTextView;
    private TextView storeTimeTextView;
    private TextView storeZYSPTextView;
    private TextView storeGoodsTextView;

    private TextView storePhoneTextView;
    private TextView storeQQTextView;

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
        setContentView(R.layout.activity_store_info);
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
        shareImageView = (ImageView) findViewById(R.id.moreImageView);

        storeRelativeLayout = (RelativeLayout) findViewById(R.id.storeRelativeLayout);
        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        collectTextView = (TextView) findViewById(R.id.collectTextView);
        collectNumTextView = (TextView) findViewById(R.id.collectNumTextView);
        creditTextView = (TextView) findViewById(R.id.creditTextView);

        storeNameTextView = (TextView) findViewById(R.id.storeNameTextView);
        storeAreaTextView = (TextView) findViewById(R.id.storeAreaTextView);
        storeTimeTextView = (TextView) findViewById(R.id.storeTimeTextView);
        storeZYSPTextView = (TextView) findViewById(R.id.storeZYSPTextView);
        storeGoodsTextView = (TextView) findViewById(R.id.storeGoodsTextView);
        storePhoneTextView = (TextView) findViewById(R.id.storePhoneTextView);
        storeQQTextView = (TextView) findViewById(R.id.storeQQTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        store_name = "";
        store_avatar = "";
        titleTextView.setText("店铺介绍");
        store_id = mActivity.getIntent().getStringExtra("store_id");
        shareImageView.setImageResource(R.mipmap.ic_action_share);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "店铺分享");
                intent.putExtra("name", store_name);
                intent.putExtra("jingle", store_name);
                intent.putExtra("image", store_avatar);
                intent.putExtra("link", mApplication.storeUrlString + store_id);
                mApplication.startActivity(mActivity, intent);
            }
        });

        storeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "店铺分享");
                intent.putExtra("name", store_name);
                intent.putExtra("jingle", store_name);
                intent.putExtra("image", store_avatar);
                intent.putExtra("link", mApplication.storeUrlString + store_id);
                mApplication.startActivity(mActivity, intent);
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
                            getJson();
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

        storePhoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startCall(mActivity, storePhoneTextView.getText().toString());
            }
        });

        storeQQTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "http://wpa.qq.com/msgrd?v=3&uin=" + storeQQTextView.getText().toString() + "&site=qq&menu=yes";
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("model", "normal");
                intent.putExtra("link", link);
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("store");
        ajaxParams.putOp("store_intro");
        ajaxParams.put("store_id", store_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
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
                            jsonObject = new JSONObject(jsonObject.getString("store_info"));
                            //顶部的
                            ImageLoader.getInstance().displayImage(jsonObject.getString("store_avatar"), avatarImageView);
                            store_name = jsonObject.getString("store_name");
                            store_avatar = jsonObject.getString("store_avatar");
                            nameTextView.setText(jsonObject.getString("store_name"));
                            titleTextView.setText(jsonObject.getString("store_name"));
                            collectNumTextView.setText("粉丝 ");
                            collectNumTextView.append(jsonObject.getString("store_collect"));
                            creditTextView.setText(mActivity.getIntent().getStringExtra("store_credit"));
                            if (jsonObject.getBoolean("is_favorate")) {
                                collectTextView.setText("已收藏");
                                collectTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
                            } else {
                                collectTextView.setText("收藏");
                                collectTextView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.pink));
                            }
                            //基本信息
                            storeNameTextView.setText(jsonObject.getString("store_name"));
                            storeAreaTextView.setText(jsonObject.getString("area_info"));
                            storeTimeTextView.setText(jsonObject.getString("store_time_text"));
                            if (TextUtil.isEmpty(jsonObject.getString("store_zy"))) {
                                storeZYSPTextView.setText("未填写");
                            } else {
                                storeZYSPTextView.setText(jsonObject.getString("store_zy"));
                            }
                            storeGoodsTextView.setText(jsonObject.getString("goods_count"));
                            storeGoodsTextView.append(" 件");
                            if (TextUtil.isEmpty(jsonObject.getString("store_phone"))) {
                                storePhoneTextView.setText("未填写");
                            } else {
                                storePhoneTextView.setText(jsonObject.getString("store_phone"));
                            }
                            if (TextUtil.isEmpty(jsonObject.getString("store_qq"))) {
                                storeQQTextView.setText("未填写");
                            } else {
                                storeQQTextView.setText(jsonObject.getString("store_qq"));
                            }
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
                DialogUtil.cancel();
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取店铺信息失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getJson();
                    }
                }
        );

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }


}