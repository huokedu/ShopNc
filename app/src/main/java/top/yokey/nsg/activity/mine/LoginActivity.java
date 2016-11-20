package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String qqString;
    private UserInfo qqUserInfo;
    private IUiListener qqIUiListener;
    private IUiListener qqUserIUiListener;
    private CountDownTimer mCountDownTimer;
    private HashMap<String, String> qqHashMap;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView loginTextView;

    private TextView registerTextView;
    private TextView findTextView;

    private TextView wxTextView;
    private TextView qqTextView;
    private TextView wbTextView;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_REGISTER:
                    usernameEditText.setText(data.getStringExtra("username"));
                    usernameEditText.setSelection(data.getStringExtra("username").length());
                    break;
                case NcApplication.CODE_LOGIN:
                    mApplication.userKeyString = data.getStringExtra("key");
                    mApplication.mSharedPreferencesEditor.putString("user_key", mApplication.userKeyString);
                    mApplication.mSharedPreferencesEditor.apply();
                    getInfo();
                default:
                    Tencent.onActivityResultData(req, res, data, qqIUiListener);
                    break;
            }
        } else {
            switch (req) {
                case NcApplication.CODE_LOGIN:
                    break;
                case NcApplication.CODE_REGISTER:
                    break;
                default:
                    Tencent.onActivityResultData(req, res, data, qqIUiListener);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
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

        registerTextView = (TextView) findViewById(R.id.registerTextView);
        findTextView = (TextView) findViewById(R.id.findTextView);

        wxTextView = (TextView) findViewById(R.id.wxTextView);
        qqTextView = (TextView) findViewById(R.id.qqTextView);
        wbTextView = (TextView) findViewById(R.id.wbTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        //设置全屏
        mCountDownTimer = null;
        DisplayUtil.setFullScreen(mActivity);
        usernameEditText.setText(mApplication.userUsernameString);
        usernameEditText.setSelection(mApplication.userUsernameString.length());

        qqIUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    JSONObject jsonObject = new JSONObject(o.toString());
                    mApplication.mTencent.setOpenId(jsonObject.getString("openid"));
                    mApplication.mTencent.setAccessToken(jsonObject.getString("access_token"), jsonObject.getString("expires_in"));
                    qqUserInfo = new UserInfo(mActivity, mApplication.mTencent.getQQToken());
                    qqUserInfo.getUserInfo(qqUserIUiListener);
                } catch (JSONException e) {
                    ToastUtil.show(mActivity, "QQ登陆失败");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.show(mActivity, "QQ登陆失败");
            }

            @Override
            public void onCancel() {
                ToastUtil.show(mActivity, "QQ登陆已取消");
            }
        };

        qqUserIUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    qqString = o.toString();
                    qqHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(o.toString()));
                    loginQQ();
                } catch (Exception e) {
                    ToastUtil.show(mActivity, "QQ登陆失败");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.show(mActivity, "QQ登陆失败");
            }

            @Override
            public void onCancel() {
                ToastUtil.show(mActivity, "QQ登陆已取消");
            }
        };

    }

    private void initEven() {

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("请选择")
                        .setMessage("注册方式")
                        .setPositiveButton("手机注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mApplication.startActivity(mActivity, new Intent(mActivity, RegisterPhoneActivity.class), NcApplication.CODE_REGISTER);
                            }
                        })
                        .setNegativeButton("用户名", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mApplication.startActivity(mActivity, new Intent(mActivity, RegisterActivity.class), NcApplication.CODE_REGISTER);
                            }
                        })
                        .show();
            }
        });

        findTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, BrowserActivity.class);
                intent.putExtra("model", "normal");
                intent.putExtra("link", mApplication.findPassUrlString);
                mApplication.startActivity(mActivity, intent);
            }
        });

        wxTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "carjob_wx_login";
                mApplication.mIwxapi.sendReq(req);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                mCountDownTimer = new CountDownTimer(9999999, 1000) {
                    @Override
                    public void onTick(long l) {
                        if (!TextUtil.isEmpty(mApplication.userKeyString)) {
                            mCountDownTimer.cancel();
                            mCountDownTimer = null;
                            getInfo();
                        }
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        });

        qqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.mTencent.login(mActivity, "all", qqIUiListener);
            }
        });

        wbTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, LoginWebActivity.class);
                intent.putExtra("model", "login");
                intent.putExtra("link", mApplication.loginWBUrlString);
                mApplication.startActivity(mActivity, intent, NcApplication.CODE_LOGIN);
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
        ajaxParams.put("act", "login");
        ajaxParams.put("username", username);
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
                            mApplication.userUsernameString = username;
                            mApplication.userKeyString = jsonObject.getString("key");
                            mApplication.mSharedPreferencesEditor.putString("user_username", username);
                            mApplication.mSharedPreferencesEditor.putString("user_key", mApplication.userKeyString);
                            mApplication.mSharedPreferencesEditor.apply();
                            getInfo();
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

    private void loginQQ() {

        DialogUtil.progress(mActivity);

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "connect_app");
        ajaxParams.put("op", "login_qq");
        ajaxParams.put("info", qqString);
        ajaxParams.put("client", "android");
        ajaxParams.put("nickname", qqHashMap.get("nickname"));
        ajaxParams.put("openid", mApplication.mTencent.getOpenId());

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        mApplication.userKeyString = mApplication.getJsonData(o.toString());
                        mApplication.mSharedPreferencesEditor.putString("user_key", mApplication.userKeyString);
                        mApplication.mSharedPreferencesEditor.apply();
                        getInfo();
                    } else {
                        ToastUtil.show(mActivity, "QQ登陆失败");
                    }
                } else {
                    ToastUtil.show(mActivity, "QQ登陆失败");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.show(mActivity, "QQ登陆失败");
                DialogUtil.cancel();
            }
        });

    }

    private void getInfo() {

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            ToastUtil.show(mActivity, "登录成功");
                            JSONObject jsonObject = new JSONObject(data);
                            mApplication.userHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("member_info")));
                            mApplication.mSharedPreferencesEditor.putString("user_username", mApplication.userHashMap.get("member_name"));
                            mApplication.mSharedPreferencesEditor.apply();
                            mApplication.finishActivity(mActivity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getInfoFailure();
                        }
                    } else {
                        ToastUtil.show(mActivity, error);
                        mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                    }
                } else {
                    getInfoFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getInfoFailure();
            }
        });

    }

    private void getInfoFailure() {

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                super.onFinish();
                getInfoFailure();
            }
        }.start();

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