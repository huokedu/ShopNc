package top.yokey.nsg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler {

    private Activity mActivity;
    private NcApplication mApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mApplication = (NcApplication) getApplication();
        mApplication.mIwxapi = WXAPIFactory.createWXAPI(this, "wx67bc79b681fee1b3");
        mApplication.mIwxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApplication.mIwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;
                DialogUtil.progress(mActivity);
                AjaxParams ajaxParams = new AjaxParams();
                ajaxParams.put("appid", "wx67bc79b681fee1b3");
                ajaxParams.put("secret", "dc4b9c7d7f6f6ccd0aca9432d43c9e6e");
                ajaxParams.put("code", code);
                ajaxParams.put("grant_type", "authorization_code");
                mApplication.mFinalHttp.get("https://api.weixin.qq.com/sns/oauth2/access_token?" + ajaxParams.toString(), new AjaxCallBack<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());
                            AjaxParams ajaxParams = new AjaxParams();
                            ajaxParams.put("appid", "wx67bc79b681fee1b3");
                            ajaxParams.put("access_token", jsonObject.getString("access_token"));
                            ajaxParams.put("openid", jsonObject.getString("openid"));
                            mApplication.mFinalHttp.get("https://api.weixin.qq.com/sns/userinfo?" + ajaxParams.toString(), new AjaxCallBack<Object>() {
                                @Override
                                public void onSuccess(Object o) {
                                    super.onSuccess(o);
                                    try {
                                        JSONObject jsonObject = new JSONObject(o.toString());
                                        AjaxParams ajaxParams = new AjaxParams();
                                        ajaxParams.put("act", "connect_app");
                                        ajaxParams.put("op", "login_wx");
                                        ajaxParams.put("info", o.toString());
                                        ajaxParams.put("client", "android");
                                        ajaxParams.put("nickname", jsonObject.getString("nickname"));
                                        ajaxParams.put("unionid", jsonObject.getString("unionid"));
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
                                                    } else {
                                                        ToastUtil.showFailure(mActivity);
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
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtil.showFailure(mActivity);
                                        DialogUtil.cancel();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t, int errorNo, String strMsg) {
                                    super.onFailure(t, errorNo, strMsg);
                                    ToastUtil.showFailure(mActivity);
                                    DialogUtil.cancel();
                                }
                            });
                        } catch (JSONException e) {
                            ToastUtil.showFailure(mActivity);
                            e.printStackTrace();
                            DialogUtil.cancel();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ToastUtil.showFailure(mActivity);
                        DialogUtil.cancel();
                    }
                });
                break;
            default:
                ToastUtil.show(mActivity, "授权失败");
                break;
        }
        finish();
    }

    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(iLaunchMyself);
    }

    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null && msg.mediaObject != null && (msg.mediaObject instanceof WXAppExtendObject)) {
            WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
            Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
        }
    }

}