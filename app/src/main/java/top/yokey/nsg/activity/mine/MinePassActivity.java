package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class MinePassActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String mobile;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText mobileEditText;
    private TextView getTextView;
    private EditText verifyEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private TextView confirmTextView;

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
        setContentView(R.layout.activity_mine_pass);
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

        mobileEditText = (EditText) findViewById(R.id.mobileEditText);
        getTextView = (TextView) findViewById(R.id.getTextView);
        verifyEditText = (EditText) findViewById(R.id.verifyEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfirmEditText = (EditText) findViewById(R.id.passwordConfirmEditText);
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        mobile = mApplication.userHashMap.get("member_mobile");

        titleTextView.setText("修改账户密码");

        mobileEditText.setText(mobile);
        mobileEditText.setSelection(mobile.length());

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        getTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMobileCode();
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });

    }

    private void getMobileCode() {

        if (!TextUtil.isMobileNumber(mobile)) {
            ToastUtil.show(mActivity, "手机号码不正确");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("modify_password_step2");
        ajaxParams.put("mobile", mobile);
        ajaxParams.put("captcha", "mobile");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String data = mApplication.getJsonData(o.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (data.contains("error")) {
                            ToastUtil.show(mActivity, jsonObject.getString("error"));
                            return;
                        }
                        getTextView.setEnabled(false);
                        ToastUtil.showSuccess(mActivity);
                        int time = jsonObject.getInt("sms_time");
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

        //验证数据
        String auth_code = verifyEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String password_confirm = passwordConfirmEditText.getText().toString();

        if (TextUtil.isEmpty(auth_code)) {
            ToastUtil.show(mActivity, "验证码不能为空");
            return;
        }

        if (TextUtil.isEmpty(password) || TextUtil.isEmpty(password_confirm)) {
            ToastUtil.show(mActivity, "密码不能为空");
            return;
        }

        if (!password.equals(password_confirm)) {
            ToastUtil.show(mActivity, "两次输入的密码不一样");
            return;
        }

        confirmTextView.setEnabled(false);
        confirmTextView.setText("验证短信验证码");

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("modify_password_step3");
        ajaxParams.put("auth_code", auth_code);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    checkPower();
                } else {
                    ToastUtil.showFailure(mActivity);
                    confirmTextView.setEnabled(true);
                    confirmTextView.setText("确认");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                confirmTextView.setEnabled(true);
                confirmTextView.setText("确认");
            }
        });

    }

    private void checkPower() {

        confirmTextView.setEnabled(false);
        confirmTextView.setText("检查权限");

        String params = "act=member_account&op=modify_password_step4&key=" + mApplication.userKeyString;

        mApplication.mFinalHttp.get(mApplication.apiUrlString + params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    modifyPass();
                } else {
                    ToastUtil.showFailure(mActivity);
                    confirmTextView.setEnabled(true);
                    confirmTextView.setText("确认");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                confirmTextView.setEnabled(true);
                confirmTextView.setText("确认");
            }
        });

    }

    private void modifyPass() {

        confirmTextView.setEnabled(false);
        confirmTextView.setText("修改密码");

        String password = passwordEditText.getText().toString();
        String password_confirm = passwordConfirmEditText.getText().toString();

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("modify_password_step5");
        ajaxParams.put("password", password);
        ajaxParams.put("password1", password_confirm);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.showSuccess(mActivity);
                    mActivity.setResult(RESULT_OK);
                    mApplication.finishActivity(mActivity);
                } else {
                    ToastUtil.showFailure(mActivity);
                    confirmTextView.setEnabled(true);
                    confirmTextView.setText("确认");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                confirmTextView.setEnabled(true);
                confirmTextView.setText("确认");
            }
        });

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消修改密码?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

}