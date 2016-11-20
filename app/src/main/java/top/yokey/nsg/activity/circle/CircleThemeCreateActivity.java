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

public class CircleThemeCreateActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText nameEditText;
    private EditText contentEditText;

    private TextView createTextView;

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
        setContentView(R.layout.activity_circle_theme_create);
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

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

        createTextView = (TextView) findViewById(R.id.createTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("创建主题");

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });

    }

    private void create() {

        String theme_name = nameEditText.getText().toString();
        String theme_content = contentEditText.getText().toString();

        if (TextUtil.isEmpty(theme_name) || TextUtil.isEmpty(theme_content)) {
            ToastUtil.show(mActivity, "内容未填写完整");
            return;
        }

        if (theme_name.length() < 6) {
            ToastUtil.show(mActivity, "主题名称至少六个字符");
            return;
        }

        if (theme_content.length() < 10) {
            ToastUtil.show(mActivity, "主题内容至少十个字符");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("theme_create");
        ajaxParams.put("theme_name", theme_name);
        ajaxParams.put("theme_content", theme_content);
        ajaxParams.put("circle_id", CircleDetailedActivity.circle_id);
        ajaxParams.put("circle_name", CircleDetailedActivity.circle_name);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
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
                DialogUtil.cancel();
            }
        });

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消发布主题",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

}