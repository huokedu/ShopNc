package top.yokey.nsg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import top.yokey.nsg.activity.order.BuySetup2Activity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.ToastUtil;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private NcApplication mApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    ToastUtil.show(this, "微信支付成功");
                    BuySetup2Activity.mActivity.setResult(RESULT_OK);
                    BuySetup2Activity.mApplication.finishActivity(BuySetup2Activity.mActivity);
                    break;
                case -1:
                    ToastUtil.show(this, "微信支付失败");
                    break;
                case -2:
                    ToastUtil.show(this, "微信取消支付");
                    break;
            }
            finish();
        }
    }

}