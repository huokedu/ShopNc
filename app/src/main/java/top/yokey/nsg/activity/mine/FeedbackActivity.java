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

public class FeedbackActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText contentEditText;
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
        setContentView(R.layout.activity_feedback);
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

        contentEditText = (EditText) findViewById(R.id.contentEditText);
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("用户反馈");
        contentEditText.setHint("请输入您在使用中遇到的问题");

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
                feedback();
            }
        });

    }

    private void feedback() {

        String content = contentEditText.getText().toString();

        if (TextUtil.isEmpty(content)) {
            ToastUtil.show(mActivity, "请先输入内容");
            return;
        }

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_feedback");
        ajaxParams.putOp("feedback_add");
        ajaxParams.put("feedback", content);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.showSuccess(mActivity);
                    mApplication.finishActivity(mActivity);
                } else {
                    ToastUtil.showFailure(mActivity);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
            }
        });

    }

    private void returnActivity() {

        if (contentEditText.getText().toString().length() == 0) {
            DialogUtil.query(
                    mActivity,
                    "确认您的选择",
                    "取消反馈？",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            mApplication.finishActivity(mActivity);
                        }
                    }
            );
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}