package top.yokey.nsg.activity.seller;

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

import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerLoginActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView loginTextView;

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
        setContentView(R.layout.activity_seller_login);
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
        loginTextView = (TextView) findViewById(R.id.loginTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        usernameEditText.setText(mApplication.sellerNameString);
        usernameEditText.setSelection(mApplication.sellerNameString.length());

        //设置全屏
        DisplayUtil.setFullScreen(mActivity);

    }

    private void initEven() {

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {

        //对登录数据进行验证
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (TextUtil.isEmpty(username)) {
            ToastUtil.show(mActivity, "用户名不能为空");
            return;
        }

        if (TextUtil.isEmpty(password)) {
            ToastUtil.show(mActivity, "密码不能为空");
            return;
        }

        //设置登录标签
        loginTextView.setEnabled(false);
        loginTextView.setText("登录中...");

        // POST 数据提交
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "seller_login");
        ajaxParams.put("seller_name", username);
        ajaxParams.put("password", password);
        ajaxParams.put("client", "android");

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
                            mApplication.sellerNameString = username;
                            mApplication.sellerKeyString = jsonObject.getString("key");
                            mApplication.mSharedPreferencesEditor.putString("seller_name", username);
                            mApplication.mSharedPreferencesEditor.putString("seller_key", mApplication.sellerKeyString);
                            mApplication.mSharedPreferencesEditor.apply();
                            mApplication.startActivity(mActivity, new Intent(mActivity, SellerActivity.class));
                            mApplication.finishActivity(mActivity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginFailure();
                        }
                    } else {
                        loginTextView.setEnabled(true);
                        ToastUtil.show(mActivity, error);
                        loginTextView.setText("登 录");
                    }
                } else {
                    loginFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                loginFailure();
            }
        });

    }

    private void loginFailure() {

        loginTextView.setEnabled(true);
        ToastUtil.showFailure(mActivity);
        loginTextView.setText("登 录");

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消登录？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

}