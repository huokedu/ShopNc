package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.NetworkUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SettingActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView clearCacheTextView;
    private TextView checkNetworkTextView;
    private TextView useHelpTextView;
    private TextView aboutTextView;

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
        setContentView(R.layout.activity_setting);
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

        clearCacheTextView = (TextView) findViewById(R.id.clearCacheTextView);
        checkNetworkTextView = (TextView) findViewById(R.id.checkNetworkTextView);
        useHelpTextView = (TextView) findViewById(R.id.useHelpTextView);
        aboutTextView = (TextView) findViewById(R.id.aboutTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("系统设置");

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        clearCacheTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "清除缓存？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                                ImageLoader.getInstance().clearDiskCache();
                                ImageLoader.getInstance().clearMemoryCache();
                            }
                        });
            }
        });

        checkNetworkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(mActivity,
                        "确认您的选择",
                        "即将进行网络检测？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                                checkNetworkTextView.setEnabled(false);
                                checkNetworkTextView.setText("检测中...");
                                mApplication.mFinalHttp.get(mApplication.apiUrlString, new AjaxCallBack<Object>() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        super.onSuccess(o);

                                        checkNetworkTextView.setEnabled(true);
                                        checkNetworkTextView.setText("网络检测");

                                        String message = "网络状态：正常" + "\n网络类型：";

                                        if (NetworkUtil.isWifiActivity(mActivity)) {
                                            message += "WIFI";
                                        } else {
                                            message += "GPRS";
                                        }

                                        DialogUtil.query(
                                                mActivity,
                                                "检测结果",
                                                message,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        DialogUtil.cancel();
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                                        super.onFailure(t, errorNo, strMsg);
                                        checkNetworkTextView.setEnabled(true);
                                        checkNetworkTextView.setText("网络检测");
                                        ToastUtil.showFailureNetwork(mActivity);
                                    }
                                });
                            }
                        });
            }
        });

        useHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("link", mApplication.helpUrlString);
                intent.putExtra("model", "normal");
                mApplication.startActivity(mActivity, intent);
            }
        });

        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("link", mApplication.aboutUrlString);
                intent.putExtra("model", "normal");
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}