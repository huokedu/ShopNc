package top.yokey.nsg.activity.seller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerReturnHandlerActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String refund_id;
    private String seller_state;
    private String seller_message;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView snTextView;
    private TextView reasonTextView;
    private TextView numberTextView;
    private TextView moneyTextView;
    private TextView messageTextView;
    private ImageView[] mImageView;

    private RadioButton yesRadioButton;
    private RadioButton noRadioButton;
    private EditText storeMessageEditText;
    private TextView handlerTextView;

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
        setContentView(R.layout.activity_seller_return_handler);
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

        snTextView = (TextView) findViewById(R.id.snTextView);
        reasonTextView = (TextView) findViewById(R.id.reasonTextView);
        numberTextView = (TextView) findViewById(R.id.numberTextView);
        moneyTextView = (TextView) findViewById(R.id.moneyTextView);
        messageTextView = (TextView) findViewById(R.id.messageTextView);

        mImageView = new ImageView[3];
        mImageView[0] = (ImageView) findViewById(R.id.thrImageView);
        mImageView[1] = (ImageView) findViewById(R.id.twoImageView);
        mImageView[2] = (ImageView) findViewById(R.id.oneImageView);

        yesRadioButton = (RadioButton) findViewById(R.id.yesRadioButton);
        noRadioButton = (RadioButton) findViewById(R.id.noRadioButton);
        storeMessageEditText = (EditText) findViewById(R.id.storeMessageEditText);
        handlerTextView = (TextView) findViewById(R.id.handlerTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        seller_state = "2";
        seller_message = "";
        refund_id = mActivity.getIntent().getStringExtra("refund_id");
        if (TextUtil.isEmpty(refund_id)) {
            ToastUtil.show(mActivity, "参数错误");
            mApplication.finishActivity(mActivity);
            return;
        }

        titleTextView.setText("退货处理");
        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        yesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesRadioButton.isChecked()) {
                    seller_state = "2";
                    noRadioButton.setChecked(false);
                } else {
                    seller_state = "3";
                    noRadioButton.setChecked(true);
                }
            }
        });

        noRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noRadioButton.isChecked()) {
                    seller_state = "3";
                    yesRadioButton.setChecked(false);
                } else {
                    seller_state = "2";
                    yesRadioButton.setChecked(true);
                }
            }
        });

        handlerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerRefund();
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_return");
        ajaxParams.putOp("return_detailed");
        ajaxParams.put("return_id", refund_id);

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
                            JSONObject mJsonObject = new JSONObject(data);
                            snTextView.setText(mJsonObject.getString("refund_sn"));
                            reasonTextView.setText(mJsonObject.getString("reason_info"));
                            numberTextView.setText(mJsonObject.getString("goods_num"));
                            moneyTextView.setText(mJsonObject.getString("refund_amount"));
                            messageTextView.setText(mJsonObject.getString("buyer_message"));
                            //图片
                            JSONArray jsonArray = new JSONArray(mJsonObject.getString("pic_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (i < 3) {
                                    ImageLoader.getInstance().displayImage(jsonArray.get(i).toString(), mImageView[i]);
                                }
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
                ToastUtil.showFailureNetwork(mActivity);
                DialogUtil.cancel();
            }
        });

    }

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                " 是否重试?",
                "读取数据失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getJson();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                });

    }

    private void handlerRefund() {

        seller_message = storeMessageEditText.getText().toString();

        if (TextUtil.isEmpty(seller_message)) {
            ToastUtil.show(mActivity, "请输入备注");
            return;
        }

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_refund");
        ajaxParams.putOp("refund_handler");
        ajaxParams.put("refund_id", refund_id);
        ajaxParams.put("seller_state", seller_state);
        ajaxParams.put("seller_message", seller_message);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.showSuccess(mActivity);
                    mApplication.finishActivity(mActivity);
                } else {
                    ToastUtil.show(mActivity, mApplication.getJsonError(o.toString()));
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailureNetwork(mActivity);
                DialogUtil.cancel();
            }
        });

    }

    private void returnActivity() {

        if (storeMessageEditText.getText().toString().isEmpty()) {
            mApplication.finishActivity(mActivity);
        } else {
            DialogUtil.query(mActivity, "确认您的选择", "放弃处理？", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogUtil.cancel();
                    mApplication.finishActivity(mActivity);
                }
            });
        }

    }

}