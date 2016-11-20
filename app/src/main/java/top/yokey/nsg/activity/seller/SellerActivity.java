package top.yokey.nsg.activity.seller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.ChatListActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class SellerActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView editImageView;

    private RelativeLayout storeRelativeLayout;
    private ImageView qrCodeImageView;
    private ImageView messageImageView;

    private ImageView avatarImageView;
    private TextView nameTextView;
    private TextView[] mTextView;

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
        setContentView(R.layout.activity_seller);
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
        editImageView = (ImageView) findViewById(R.id.moreImageView);

        storeRelativeLayout = (RelativeLayout) findViewById(R.id.storeRelativeLayout);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        mTextView = new TextView[6];
        mTextView[0] = (TextView) findViewById(R.id.orderTextView);
        mTextView[1] = (TextView) findViewById(R.id.refundTextView);
        mTextView[2] = (TextView) findViewById(R.id.returnTextView);
        mTextView[3] = (TextView) findViewById(R.id.goodsTextView);
        mTextView[4] = (TextView) findViewById(R.id.countTextView);
        mTextView[5] = (TextView) findViewById(R.id.settlementTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("卖家中心");
        editImageView.setImageResource(R.mipmap.ic_action_edit);

        getInfo();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivitySellerLoginSuccess(mActivity, new Intent(mActivity, SellerEditActivity.class));
            }
        });


        messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, ChatListActivity.class));
            }
        });

        storeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "店铺分享");
                intent.putExtra("type", "store");
                intent.putExtra("value", mApplication.storeHashMap.get("store_id"));
                intent.putExtra("name", mApplication.storeHashMap.get("store_name"));
                intent.putExtra("jingle", mApplication.storeHashMap.get("store_name"));
                intent.putExtra("image", mApplication.storeHashMap.get("store_avatar"));
                intent.putExtra("link", mApplication.storeUrlString + mApplication.storeHashMap.get("store_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        qrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "店铺分享");
                intent.putExtra("type", "store");
                intent.putExtra("value", mApplication.storeHashMap.get("store_id"));
                intent.putExtra("name", mApplication.storeHashMap.get("store_name"));
                intent.putExtra("jingle", mApplication.storeHashMap.get("store_name"));
                intent.putExtra("image", mApplication.storeHashMap.get("store_avatar"));
                intent.putExtra("link", mApplication.storeUrlString + mApplication.storeHashMap.get("store_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        mTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivitySellerLoginSuccess(mActivity, new Intent(mActivity, SellerOrderActivity.class));
            }
        });

        mTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivitySellerLoginSuccess(mActivity, new Intent(mActivity, SellerRefundActivity.class));
            }
        });

        mTextView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivitySellerLoginSuccess(mActivity, new Intent(mActivity, SellerReturnActivity.class));
            }
        });

        mTextView[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivitySellerLoginSuccess(mActivity, new Intent(mActivity, SellerGoodsActivity.class));
            }
        });

    }

    private void getInfo() {

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_store");
        ajaxParams.putOp("store_info");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String data = mApplication.getJsonData(o.toString());
                    if (data.contains("store_info")) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            mApplication.storeHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("store_info")));
                            nameTextView.setText(mApplication.storeHashMap.get("store_name"));
                            ImageLoader.getInstance().displayImage(mApplication.storeHashMap.get("store_avatar"), avatarImageView);
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

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}