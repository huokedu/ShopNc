package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class RegisterPhoneActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private EditText mobileEditText;
    private TextView getTextView;
    private EditText verifyEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private TextView registerTextView;

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
        setContentView(R.layout.activity_register_phone);
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

        mobileEditText = (EditText) findViewById(R.id.mobileEditText);
        getTextView = (TextView) findViewById(R.id.getTextView);
        verifyEditText = (EditText) findViewById(R.id.verifyEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfirmEditText = (EditText) findViewById(R.id.passwordConfirmEditText);
        registerTextView = (TextView) findViewById(R.id.registerTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        //设置全屏
        DisplayUtil.setFullScreen(mActivity);

    }

    private void initEven() {

        getTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerify();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });

    }

    private void getVerify() {

        String phoneString = mobileEditText.getText().toString();

        if (!TextUtil.isMobileNumber(phoneString)) {
            ToastUtil.show(mActivity, "手机号码格式不对");
            return;
        }

        DialogUtil.progress(mActivity);

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "connect");
        ajaxParams.put("op", "get_sms_captcha");
        ajaxParams.put("type", "1");
        ajaxParams.put("sec_val", "mobile");
        ajaxParams.put("sec_key", "mobile");
        ajaxParams.put("phone", phoneString);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String data = mApplication.getJsonData(o.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        ToastUtil.show(mActivity, jsonObject.getString("error"));
                        if (data.contains("sms_time")) {
                            getTextView.setEnabled(false);
                            int time = 60;
                            new CountDownTimer(time * 1000, 1000) {
                                int time = 60;

                                @Override
                                public void onTick(long l) {
                                    time--;
                                    String temp = time + " S";
                                    getTextView.setText(temp);
                                    getTextView.setEnabled(false);
                                }

                                @Override
                                public void onFinish() {
                                    getTextView.setText("获取");
                                    getTextView.setEnabled(true);
                                }
                            }.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.showFailure(mActivity);
                    }
                } else {
                    ToastUtil.showFailure(mActivity);
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

    private void verifyCode() {

        //获取注册信息
        String phone = mobileEditText.getText().toString();
        String verify = verifyEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        //对注册信息进行验证
        if (!TextUtil.isMobileNumber(phone)) {
            ToastUtil.show(mActivity, "手机号码格式不对");
            return;
        }

        if (TextUtil.isEmpty(verify)) {
            ToastUtil.show(mActivity, "短信验证码不能为空");
            return;
        }

        if (TextUtil.isEmpty(password)) {
            ToastUtil.show(mActivity, "密码不能为空");
            return;
        }

        if (TextUtil.isEmpty(passwordConfirm)) {
            ToastUtil.show(mActivity, "请再次输入密码");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            ToastUtil.show(mActivity, "两次输入的密码不一样");
            return;
        }

        registerTextView.setEnabled(false);
        registerTextView.setText("验证短信验证码");

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "connect");
        ajaxParams.put("op", "check_sms_captcha");
        ajaxParams.put("type", "1");
        ajaxParams.put("phone", phone);
        ajaxParams.put("captcha", verify);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        register();
                    } else {
                        registerFailure();
                    }
                } else {
                    registerFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                registerFailure();
            }
        });

    }

    private void register() {

        //获取注册信息
        String phone = mobileEditText.getText().toString();
        String verify = verifyEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        //对注册信息进行验证
        if (!TextUtil.isMobileNumber(phone)) {
            ToastUtil.show(mActivity, "手机号码格式不对");
            return;
        }

        if (TextUtil.isEmpty(verify)) {
            ToastUtil.show(mActivity, "短信验证码不能为空");
            return;
        }

        if (TextUtil.isEmpty(password)) {
            ToastUtil.show(mActivity, "密码不能为空");
            return;
        }

        if (TextUtil.isEmpty(passwordConfirm)) {
            ToastUtil.show(mActivity, "请再次输入密码");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            ToastUtil.show(mActivity, "两次输入的密码不一样");
            return;
        }

        registerTextView.setEnabled(false);
        registerTextView.setText("注册中");

        //POST数据初始化&提交
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "connect");
        ajaxParams.put("op", "sms_register");
        ajaxParams.put("phone", phone);
        ajaxParams.put("captcha", verify);
        ajaxParams.put("password", password);
        ajaxParams.put("client", "android");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        ToastUtil.show(mActivity, "注册成功");
                        Intent intent = new Intent();
                        intent.putExtra("username", mobileEditText.getText().toString());
                        mActivity.setResult(RESULT_OK, intent);
                        mApplication.finishActivity(mActivity);
                    } else {
                        registerFailure();
                    }
                } else {
                    registerFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                registerFailure();
            }
        });

    }

    private void registerFailure() {

        ToastUtil.showFailure(mActivity);
        registerTextView.setEnabled(true);
        registerTextView.setText("注 册");

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消注册？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

}