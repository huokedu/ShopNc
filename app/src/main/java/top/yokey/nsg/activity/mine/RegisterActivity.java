package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class RegisterActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText emailEditText;
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
        setContentView(R.layout.activity_register);
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

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfirmEditText = (EditText) findViewById(R.id.passwordConfirmEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        registerTextView = (TextView) findViewById(R.id.registerTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        //设置全屏
        DisplayUtil.setFullScreen(mActivity);

    }

    private void initEven() {

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {

        //获取注册信息
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        String email = emailEditText.getText().toString();

        //对注册信息进行验证
        if (TextUtil.isEmpty(username)) {
            ToastUtil.show(mActivity, "用户名不能为空");
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

        if (TextUtil.isEmpty(email)) {
            ToastUtil.show(mActivity, "邮箱不能为空");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            ToastUtil.show(mActivity, "两次输入的密码不一样");
            return;
        }

        if (!TextUtil.isEmailAddress(email)) {
            ToastUtil.show(mActivity, "邮箱格式不对");
            return;
        }

        registerTextView.setEnabled(false);
        registerTextView.setText("注册中");

        //POST数据初始化&提交
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "login");
        ajaxParams.put("op", "register");
        ajaxParams.put("username", username);
        ajaxParams.put("password", password);
        ajaxParams.put("password_confirm", passwordConfirm);
        ajaxParams.put("email", email);
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
                        intent.putExtra("username", usernameEditText.getText().toString());
                        mActivity.setResult(RESULT_OK, intent);
                        mApplication.finishActivity(mActivity);
                    } else {
                        ToastUtil.show(mActivity, error);
                        registerTextView.setEnabled(true);
                        registerTextView.setText("注 册");
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