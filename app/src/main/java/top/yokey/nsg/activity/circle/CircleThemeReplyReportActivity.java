package top.yokey.nsg.activity.circle;

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

public class CircleThemeReplyReportActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String reply_id;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText contentEditText;
    private TextView reportTextView;

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
        setContentView(R.layout.activity_circle_theme_reply_report);
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
        reportTextView = (TextView) findViewById(R.id.reportTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        reply_id = mActivity.getIntent().getStringExtra("reply_id");

        if (TextUtil.isEmpty(reply_id)) {
            ToastUtil.show(mActivity, "传入参数有误");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("举报回复");

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });

    }

    private void report() {

        String content = contentEditText.getText().toString();

        if (TextUtil.isEmpty(content)) {
            ToastUtil.show(mActivity, "举报内容不能为空");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("theme_reply_report");
        ajaxParams.put("circle_id", CircleDetailedActivity.circle_id);
        ajaxParams.put("theme_id", CircleThemeDetailedActivity.theme_id);
        ajaxParams.put("reply_id", reply_id);
        ajaxParams.put("content", content);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    if (mApplication.getJsonSuccess(o.toString())) {
                        ToastUtil.showSuccess(mActivity);
                        mApplication.finishActivity(mActivity);
                    } else {
                        ToastUtil.show(mActivity, mApplication.getJsonError(o.toString()));
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

    }

    private void returnActivity() {

        DialogUtil.query(mActivity, "确认您的选择", "取消回复主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.cancel();
                mApplication.finishActivity(mActivity);
            }
        });

    }

}