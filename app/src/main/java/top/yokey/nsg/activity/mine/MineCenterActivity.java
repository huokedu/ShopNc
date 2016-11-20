package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.QRCodeUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class MineCenterActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String member_paypwd;
    private String member_email_bind;
    private String member_mobile_bind;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView modifyImageView;

    private RelativeLayout userRelativeLayout;
    private ImageView avatarImageView;
    private TextView usernameTextView;
    private TextView levelTextView;
    private TextView loginTextView;
    private TextView lastLoginTextView;
    private ImageView qrCodeImageView;

    private TextView passwordTextView;
    private TextView emailTextView;
    private TextView mobileTextView;
    private TextView payPassTextView;

    private TextView nameTextView;
    private TextView sexTextView;
    private TextView qqTextView;
    private TextView wwTextView;
    private TextView birthdayTextView;
    private TextView areaTextView;

    private TextView feedbackTextView;
    private TextView logoutTextView;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            setValue();
        }
    }

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
        setContentView(R.layout.activity_mine_center);
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
        modifyImageView = (ImageView) findViewById(R.id.moreImageView);

        userRelativeLayout = (RelativeLayout) findViewById(R.id.userRelativeLayout);
        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        levelTextView = (TextView) findViewById(R.id.levelTextView);
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        lastLoginTextView = (TextView) findViewById(R.id.lastLoginTextView);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);

        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        mobileTextView = (TextView) findViewById(R.id.mobileTextView);
        payPassTextView = (TextView) findViewById(R.id.payPassTextView);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        sexTextView = (TextView) findViewById(R.id.sexTextView);
        qqTextView = (TextView) findViewById(R.id.qqTextView);
        wwTextView = (TextView) findViewById(R.id.wwTextView);
        birthdayTextView = (TextView) findViewById(R.id.birthdayTextView);
        areaTextView = (TextView) findViewById(R.id.areaTextView);

        feedbackTextView = (TextView) findViewById(R.id.feedbackTextView);
        logoutTextView = (TextView) findViewById(R.id.logoutTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("个人中心");
        modifyImageView.setImageResource(R.mipmap.ic_action_edit);

        setValue();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        modifyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivity(mActivity, new Intent(mActivity, MineEditActivity.class), NcApplication.CODE_MINE_MODIFY);
            }
        });

        userRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "[uid:" + mApplication.userHashMap.get("member_id") + "]";
                DialogUtil.qrCode(mActivity, "扫描二维码跟我聊", QRCodeUtil.create(content, 512, 512));
            }
        });

        qrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "[uid:" + mApplication.userHashMap.get("member_id") + "]";
                DialogUtil.qrCode(mActivity, "扫描二维码跟我聊", QRCodeUtil.create(content, 512, 512));
            }
        });

        passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (member_mobile_bind.equals("1")) {
                    mApplication.startActivity(mActivity, new Intent(mActivity, MinePassActivity.class));
                } else {
                    mApplication.startActivity(mActivity, new Intent(mActivity, BindMobileActivity.class), NcApplication.CODE_BIND_EMAIL);
                }
            }
        });

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!member_email_bind.equals("1")) {
                    mApplication.startActivity(mActivity, new Intent(mActivity, BindEmailActivity.class), NcApplication.CODE_BIND_EMAIL);
                } else {
                    ToastUtil.show(mActivity, "您已绑定邮箱，如需解绑请访问PC端操作");
                }
            }
        });

        mobileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!member_mobile_bind.equals("1")) {
                    mApplication.startActivity(mActivity, new Intent(mActivity, BindMobileActivity.class), NcApplication.CODE_BIND_MOBILE);
                } else {
                    ToastUtil.show(mActivity, "您已绑定手机，如需解绑请访问PC端操作");
                }
            }
        });

        payPassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isEmpty(member_paypwd)) {
                    if (member_mobile_bind.equals("1")) {
                        mApplication.startActivity(mActivity, new Intent(mActivity, MinePayPassActivity.class), NcApplication.CODE_BIND_PAY_PASS);
                    } else {
                        mApplication.startActivity(mActivity, new Intent(mActivity, BindMobileActivity.class), NcApplication.CODE_BIND_MOBILE);
                    }
                } else {
                    ToastUtil.show(mActivity, "您已设置支付密码，如需操作，请访问PC端");
                }
            }
        });

        feedbackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivity(mActivity, new Intent(mActivity, FeedbackActivity.class));
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "注销登录？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                                logout();
                            }
                        }
                );
            }
        });

    }

    private void setValue() {

        member_paypwd = mApplication.userHashMap.get("member_paypwd");
        member_email_bind = mApplication.userHashMap.get("member_email_bind");
        member_mobile_bind = mApplication.userHashMap.get("member_mobile_bind");

        ImageLoader.getInstance().displayImage(mApplication.userHashMap.get("avator"), avatarImageView);

        if (TextUtil.isEmpty(mApplication.userHashMap.get("member_truename"))) {
            usernameTextView.setText(mApplication.userHashMap.get("member_name"));
        } else {
            usernameTextView.setText(mApplication.userHashMap.get("member_truename"));
        }

        levelTextView.setText(mApplication.userHashMap.get("level_name"));

        if (member_email_bind.equals("1")) {
            emailTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_email_bind), null, null);
            emailTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.main));
        } else {
            emailTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_email), null, null);
            emailTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
        }

        if (member_mobile_bind.equals("1")) {
            mobileTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_mobile_bind), null, null);
            mobileTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.main));
        } else {
            mobileTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_mobile), null, null);
            mobileTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
        }

        if (!TextUtil.isEmpty(member_paypwd)) {
            payPassTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_pay_pass_bind), null, null);
            payPassTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.main));
        } else {
            payPassTextView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.mipmap.ic_user_center_pay_pass), null, null);
            payPassTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
        }

        String ip = "http://ip.taobao.com/service/getIpInfo.php?ip=";
        String loginIp = ip + mApplication.userHashMap.get("member_login_ip");
        String lastLoginIp = ip + mApplication.userHashMap.get("member_old_login_ip");

        if (!loginTextView.getText().toString().contains("本次登录")) {
            loginTextView.setText("...");
            mApplication.mFinalHttp.get(loginIp, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    if (!TextUtil.isEmpty(o.toString())) {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());
                            jsonObject = new JSONObject(jsonObject.getString("data"));
                            loginTextView.setText("本次登录：");
                            loginTextView.append(jsonObject.getString("city"));
                            loginTextView.append(" ");
                            loginTextView.append(jsonObject.getString("isp"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        if (!lastLoginTextView.getText().toString().contains("上次登录")) {
            lastLoginTextView.setText("...");
            mApplication.mFinalHttp.get(lastLoginIp, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    if (!TextUtil.isEmpty(o.toString())) {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());
                            jsonObject = new JSONObject(jsonObject.getString("data"));
                            lastLoginTextView.setText("上次登录：");
                            lastLoginTextView.append(jsonObject.getString("city"));
                            lastLoginTextView.append(" ");
                            lastLoginTextView.append(jsonObject.getString("isp"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        String member_truename = mApplication.userHashMap.get("member_truename");
        String member_sex = mApplication.userHashMap.get("member_sex");
        String member_qq = mApplication.userHashMap.get("member_qq");
        String member_ww = mApplication.userHashMap.get("member_ww");
        String member_birthday = mApplication.userHashMap.get("member_birthday");
        String member_areainfo = mApplication.userHashMap.get("member_areainfo");

        if (TextUtil.isEmpty(member_truename)) {
            member_truename = "未填写";
        }

        if (TextUtil.isEmpty(member_qq)) {
            member_qq = "未填写";
        }

        if (TextUtil.isEmpty(member_ww)) {
            member_ww = "未填写";
        }

        if (TextUtil.isEmpty(member_birthday) || member_birthday.contains("0000")) {
            member_birthday = "未填写";
        }

        if (TextUtil.isEmpty(member_areainfo)) {
            member_areainfo = "未填写";
        }

        qqTextView.setText(member_qq);
        wwTextView.setText(member_ww);
        areaTextView.setText(member_areainfo);
        nameTextView.setText(member_truename);
        birthdayTextView.setText(member_birthday);

        switch (member_sex) {
            case "1":
                sexTextView.setText("男");
                break;
            case "2":
                sexTextView.setText("女");
                break;
            case "3":
                sexTextView.setText("保密");
                break;
            default:
                sexTextView.setText("未填写");
                break;
        }

    }

    private void logout() {

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("logout");
        ajaxParams.put("username", mApplication.userHashMap.get("member_name"));
        ajaxParams.put("client", "android");
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                ToastUtil.showSuccess(mActivity);
                mApplication.userKeyString = "";
                mApplication.userHashMap.clear();
                mApplication.redPackArrayList.clear();
                mApplication.voucherArrayList.clear();
                for (ArrayList arrayList : mApplication.orderArrayList) {
                    arrayList.clear();
                }
                mApplication.mSharedPreferencesEditor.putString("user_key", "");
                mApplication.mSharedPreferencesEditor.putString("seller_key", "");
                mApplication.mSharedPreferencesEditor.putString("seller_name", "");
                mApplication.mSharedPreferencesEditor.apply();
                mApplication.finishActivity(mActivity);
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

        mApplication.finishActivity(mActivity);

    }

}