package top.yokey.nsg.activity.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.mine.MineCenterActivity;
import top.yokey.nsg.payment.PayResult;
import top.yokey.nsg.payment.SignUtils;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class BuySetup2Activity extends AppCompatActivity {

    public static Activity mActivity;
    public static NcApplication mApplication;

    private String pay_sn;
    private boolean payBoolean;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView snTextView;

    private CheckBox depositCheckBox;
    private CheckBox rechargeableCheckBox;
    private EditText payPassEditText;

    private RadioButton aliPayRadioButton;
    private RadioButton wxPayRadioButton;

    private TextView payTextView;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        mActivity.setResult(RESULT_OK);
                        ToastUtil.show(mActivity, "支付成功");
                        mApplication.finishActivity(mActivity);
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.show(mActivity, "支付结果确认中");
                        } else {
                            ToastUtil.show(mActivity, "支付失败");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            new MyCountTime(1000, 500) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    payBoolean = false;
                }
            }.start();
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!payBoolean) {
                returnActivity();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_buy_setup2);
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

        depositCheckBox = (CheckBox) findViewById(R.id.depositCheckBox);
        rechargeableCheckBox = (CheckBox) findViewById(R.id.rechargeableCheckBox);
        payPassEditText = (EditText) findViewById(R.id.payPassEditText);

        aliPayRadioButton = (RadioButton) findViewById(R.id.aliPayRadioButton);
        wxPayRadioButton = (RadioButton) findViewById(R.id.wxPayRadioButton);

        payTextView = (TextView) findViewById(R.id.payTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        payBoolean = false;
        pay_sn = mActivity.getIntent().getStringExtra("pay_sn");

        if (!mActivity.getIntent().getStringExtra("payment_code").equals("online")) {
            ToastUtil.show(mActivity, "不支持的支付方式");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("订单支付");
        snTextView.append("：");
        snTextView.append(pay_sn);
        aliPayRadioButton.setVisibility(View.GONE);
        wxPayRadioButton.setVisibility(View.GONE);

        depositCheckBox.setText("预存款 ( 余额 : ￥ ");
        depositCheckBox.append(mApplication.userHashMap.get("available_predeposit"));
        depositCheckBox.append(" )");

        rechargeableCheckBox.setText("充值卡 ( 余额 : ￥ ");
        rechargeableCheckBox.append(mApplication.userHashMap.get("available_rc_balance"));
        rechargeableCheckBox.append(" )");

        getPayment();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!payBoolean) {
                    returnActivity();
                }
            }
        });

        depositCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtil.isEmpty(mApplication.userHashMap.get("member_paypwd"))) {
                    depositCheckBox.setChecked(false);
                    queryToPayPass();
                } else {
                    if (!mApplication.userHashMap.get("available_predeposit").equals("0.00")) {
                        payPassEditText.setVisibility(View.VISIBLE);
                        if (!isChecked) {
                            if (!rechargeableCheckBox.isChecked()) {
                                payPassEditText.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        ToastUtil.show(mActivity, "预存款余额不足");
                        depositCheckBox.setChecked(false);
                    }
                }
            }
        });

        rechargeableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtil.isEmpty(mApplication.userHashMap.get("member_paypwd"))) {
                    rechargeableCheckBox.setChecked(false);
                    queryToPayPass();
                } else {
                    if (!mApplication.userHashMap.get("available_rc_balance").equals("0.00")) {
                        payPassEditText.setVisibility(View.VISIBLE);
                        if (!isChecked) {
                            if (!depositCheckBox.isChecked()) {
                                payPassEditText.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        ToastUtil.show(mActivity, "充值卡余额不足");
                        rechargeableCheckBox.setChecked(false);
                    }
                }
            }
        });

        payTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });

    }

    private void pay() {

        if (depositCheckBox.isChecked() || rechargeableCheckBox.isChecked()) {

            DialogUtil.progress(mActivity, "验证支付密码");

            final String payPass = payPassEditText.getText().toString();

            KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
            ajaxParams.putAct("member_buy");
            ajaxParams.putOp("check_pd_pwd");
            ajaxParams.put("password", payPass);

            mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    DialogUtil.cancel();
                    if (TextUtil.isJson(o.toString())) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            if (data.contains("error")) {
                                JSONObject jsonObject = new JSONObject(data);
                                ToastUtil.show(mActivity, jsonObject.getString("error"));
                            } else {
                                KeyAjaxParams payAjaxParam = new KeyAjaxParams(mApplication);
                                payAjaxParam.putAct("member_payment");
                                payAjaxParam.putOp("pay_new");
                                payAjaxParam.put("pay_sn", pay_sn);
                                payAjaxParam.put("password", payPass);
                                if (depositCheckBox.isChecked()) {
                                    payAjaxParam.put("pd_pay", "1");
                                } else {
                                    payAjaxParam.put("pd_pay", "0");
                                }
                                if (rechargeableCheckBox.isChecked()) {
                                    payAjaxParam.put("rcb_pay", "1");
                                } else {
                                    payAjaxParam.put("rcb_pay", "0");
                                }
                                if (aliPayRadioButton.isChecked()) {
                                    payAjaxParam.put("payment_code", "alipay");
                                }
                                mApplication.mFinalHttp.get(mApplication.apiUrlString + payAjaxParam.toString(), null);
                                ToastUtil.show(mActivity, "支付成功，请查看订单状态");
                                mActivity.setResult(RESULT_OK);
                                mApplication.finishActivity(mActivity);
                            }
                        } catch (JSONException e) {
                            ToastUtil.showFailure(mActivity);
                            e.printStackTrace();
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

        } else {

            if (wxPayRadioButton.isChecked()) {

                DialogUtil.progress(mActivity);

                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                ajaxParams.put("payment_code", "wx_app_pay3");
                ajaxParams.put("act", "member_payment");
                ajaxParams.put("op", "wx_app_pay3");
                ajaxParams.put("pay_sn", pay_sn);

                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        DialogUtil.cancel();
                        try {
                            JSONObject json = new JSONObject(mApplication.getJsonData(o.toString()));
                            PayReq req = new PayReq();
                            req.appId = json.getString("appid");
                            req.partnerId = json.getString("partnerid");
                            req.prepayId = json.getString("prepayid");
                            req.nonceStr = json.getString("noncestr");
                            req.timeStamp = json.getString("timestamp");
                            req.packageValue = json.getString("package");
                            req.sign = json.getString("sign");
                            req.extData = "app data";
                            mApplication.mIwxapi.sendReq(req);
                        } catch (JSONException e) {
                            ToastUtil.showFailure(mActivity);
                            e.printStackTrace();
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

            if (aliPayRadioButton.isChecked()) {

                DialogUtil.progress(mActivity);

                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                ajaxParams.putAct("member_payment");
                ajaxParams.putOp("alipay_native_pay");
                ajaxParams.put("payment_code", "alipay");
                ajaxParams.put("pay_sn", pay_sn);

                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        DialogUtil.cancel();
                        try {
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            String args = jsonObject.getString("args");
                            String sign = getAliPaySign(args, jsonObject.getString("sign"));
                            try {
                                sign = URLEncoder.encode(sign, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            final String payInfo = args + "&sign=\"" + sign + "\"&" + getAliPaySignType();
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    payBoolean = true;
                                    PayTask alipay = new PayTask(mActivity);
                                    String result = alipay.pay(payInfo, true);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        } catch (JSONException e) {
                            ToastUtil.showFailure(mActivity);
                            e.printStackTrace();
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

    private void getPayment() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_payment");
        ajaxParams.putOp("payment_list");

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
                            String payment_list = jsonObject.getString("payment_list");
                            if (payment_list.contains("alipay")) {
                                aliPayRadioButton.setChecked(true);
                                aliPayRadioButton.setVisibility(View.VISIBLE);
                            }
                            if (payment_list.contains("wx")) {
                                wxPayRadioButton.setVisibility(View.VISIBLE);
                                if (aliPayRadioButton.getVisibility() == View.GONE) {
                                    wxPayRadioButton.setChecked(true);
                                }
                            }
                        } catch (JSONException e) {
                            getPaymentFailure();
                            e.printStackTrace();
                        }
                    } else {
                        getPaymentFailure();
                    }
                } else {
                    getPaymentFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getPaymentFailure();
            }
        });

    }

    private void getPaymentFailure() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "读取数据失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getPayment();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                        DialogUtil.cancel();
                    }
                }
        );

    }

    private void queryToPayPass() {

        DialogUtil.query(
                mActivity,
                "是否设置支付密码？",
                "系统检查到您尚未设置支付密码，是否设置？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, MineCenterActivity.class));
                        mApplication.finishActivity(mActivity);
                        DialogUtil.cancel();
                    }
                }
        );

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消支付？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private String getAliPaySign(String content, String rsa_private) {
        return SignUtils.sign(content, rsa_private);
    }

    private String getAliPaySignType() {
        return "sign_type=\"RSA\"";
    }

}