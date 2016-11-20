package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class BindEmailActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText emailEditText;
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
        setContentView(R.layout.activity_bind_email);
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

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("绑定邮箱");

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString();
                if (TextUtil.isEmailAddress(email)) {
                    DialogUtil.progress(mActivity);
                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                    ajaxParams.putAct("member_index");
                    ajaxParams.putOp("bind_email");
                    ajaxParams.put("email", email);
                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            DialogUtil.cancel();
                            if (TextUtil.isJson(o.toString())) {
                                String error = mApplication.getJsonError(o.toString());
                                if (TextUtil.isEmpty(error)) {
                                    String data = mApplication.getJsonData(o.toString());
                                    ToastUtil.show(mActivity, data);
                                    mApplication.finishActivity(mActivity);
                                } else {
                                    ToastUtil.show(mActivity, error);
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
                    ToastUtil.show(mActivity, "邮箱地址不正确");
                }
            }
        });

    }

    private void returnActivity() {

        if (TextUtil.isEmpty(emailEditText.getText().toString())) {
            mApplication.finishActivity(mActivity);
        } else {
            DialogUtil.query(
                    mActivity,
                    "确认您的选择",
                    "取消绑定邮箱地址?",
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

}