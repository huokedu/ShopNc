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

public class CircleApplyActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText pursuerEditText;
    private EditText descEditText;

    private TextView applyTextView;

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
        setContentView(R.layout.activity_circle_apply);
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

        pursuerEditText = (EditText) findViewById(R.id.pursuerEditText);
        descEditText = (EditText) findViewById(R.id.descEditText);

        applyTextView = (TextView) findViewById(R.id.applyTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("加入圈子");
        String hint = "申请加入 " + CircleDetailedActivity.circle_name + " 的理由";
        pursuerEditText.setHint(hint);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply();
            }
        });

    }

    private void apply() {

        String circle_desc = descEditText.getText().toString();
        String circle_pursuer = pursuerEditText.getText().toString();

        if (TextUtil.isEmpty(circle_desc) || TextUtil.isEmpty(circle_pursuer)) {
            ToastUtil.show(mActivity, "内容未填写完整");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("apply");
        ajaxParams.put("circle_desc", circle_desc);
        ajaxParams.put("circle_pursuer", circle_pursuer);
        ajaxParams.put("circle_id", CircleDetailedActivity.circle_id);
        ajaxParams.put("circle_name", CircleDetailedActivity.circle_name);
        ajaxParams.put("circle_joinaudit", CircleDetailedActivity.circle_joinaudit);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    CircleDetailedActivity.applyBoolean = true;
                    mActivity.setResult(RESULT_OK);
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
                DialogUtil.cancel();
            }
        });

    }

    private void returnActivity() {

        DialogUtil.query(mActivity, "确认您的选择", "取消加入圈子", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.cancel();
                mApplication.finishActivity(mActivity);
            }
        });

    }

}