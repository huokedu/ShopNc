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

public class BindMobileActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText mobileEditText;
    private TextView getTextView;
    private EditText verifyEditText;
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
        setContentView(R.layout.activity_bind_mobile);
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
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("绑定手机号码");

        getBind();

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
                bindMobilePhone();
            }
        });

    }

    private void getBind() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("get_mobile_info");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
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
                            if (jsonObject.getBoolean("state")) {
                                ToastUtil.show(mActivity, "您已经绑定过了");
                                mApplication.finishActivity(mActivity);
                            } else {
                                String mobile = jsonObject.getString("mobile");
                                if (!TextUtil.isEmpty(mobile)) {
                                    mobileEditText.setText(mobile);
                                    mobileEditText.setSelection(mobile.length());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getBindFailure();
                        }
                    } else {
                        getBindFailure();
                    }
                } else {
                    getBindFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getBindFailure();
            }
        });

    }

    private void getBindFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试",
                "读取数据失败?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getBind();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void getMobileCode() {

        String mobile = mobileEditText.getText().toString();

        if (!TextUtil.isMobileNumber(mobile)) {
            ToastUtil.show(mActivity, "手机号码不正确");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("bind_mobile_step1");
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

    private void bindMobilePhone() {

        String auth_code = verifyEditText.getText().toString();

        if (TextUtil.isEmpty(auth_code)) {
            ToastUtil.show(mActivity, "验证码不能为空");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_account");
        ajaxParams.putOp("bind_mobile_step2");
        ajaxParams.put("auth_code", auth_code);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.showSuccess(mActivity);
                    mApplication.userHashMap.put("member_mobile_bind", "1");
                    mActivity.setResult(RESULT_OK);
                    mApplication.finishActivity(mActivity);
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

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消绑定手机号码?",
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