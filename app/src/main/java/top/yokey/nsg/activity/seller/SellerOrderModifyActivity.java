package top.yokey.nsg.activity.seller;

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
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerOrderModifyActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String order_id;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView idTextView;
    private EditText shipEditText;
    private EditText priceEditText;
    private TextView modifyTextView;

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
        setContentView(R.layout.activity_seller_order_modify);
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

        idTextView = (TextView) findViewById(R.id.idTextView);
        shipEditText = (EditText) findViewById(R.id.shipEditText);
        priceEditText = (EditText) findViewById(R.id.priceEditText);
        modifyTextView = (TextView) findViewById(R.id.modifyTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        order_id = mActivity.getIntent().getStringExtra("order_id");

        if (TextUtil.isEmpty(order_id)) {
            ToastUtil.show(mActivity, "传入的参数有误");
            return;
        }

        titleTextView.setText("订单修改价格");
        idTextView.setText(mActivity.getIntent().getStringExtra("order_sn"));

        String temp = "运费：" + mActivity.getIntent().getStringExtra("order_ship");
        shipEditText.setHint(temp);
        temp = "原价：" + mActivity.getIntent().getStringExtra("order_price");
        priceEditText.setHint(temp);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        modifyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.query(mActivity, "确认您的选择", "修改订单运费/价格,订单总额=订单价格+订单运费", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        modifyShip();
                    }
                });
            }
        });

    }

    private void modifyShip() {

        String price = priceEditText.getText().toString();
        String shipping_fee = shipEditText.getText().toString();

        if (TextUtil.isEmpty(price)) {
            ToastUtil.show(mActivity, "请输入金额");
            return;
        }

        if (TextUtil.isEmpty(shipping_fee)) {
            ToastUtil.show(mActivity, "请输入运费");
            return;
        }

        modifyTextView.setEnabled(false);
        modifyTextView.setText("修改运费");

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_ship_price");
        ajaxParams.put("order_id", order_id);
        ajaxParams.put("shipping_fee", shipping_fee);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (mApplication.getJsonSuccess(o.toString())) {
                    modifySpay();
                } else {
                    ToastUtil.showFailure(mActivity);
                    modifyTextView.setEnabled(true);
                    modifyTextView.setText("修 改");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                modifyTextView.setEnabled(true);
                modifyTextView.setText("修 改");
            }
        });

    }

    private void modifySpay() {

        String price = priceEditText.getText().toString();
        String shipping_fee = shipEditText.getText().toString();

        if (TextUtil.isEmpty(price)) {
            ToastUtil.show(mActivity, "请输入金额");
            return;
        }

        if (TextUtil.isEmpty(shipping_fee)) {
            ToastUtil.show(mActivity, "请输入运费");
            return;
        }

        modifyTextView.setEnabled(false);
        modifyTextView.setText("修改价格");

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_spay_price");
        ajaxParams.put("order_id", order_id);
        ajaxParams.put("goods_amount", price);
        ajaxParams.put("member_name", mApplication.sellerNameString);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (mApplication.getJsonSuccess(o.toString())) {
                    mActivity.setResult(RESULT_OK);
                    ToastUtil.showSuccess(mActivity);
                    mApplication.finishActivity(mActivity);
                } else {
                    ToastUtil.showFailure(mActivity);
                    modifyTextView.setEnabled(true);
                    modifyTextView.setText("修 改");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                modifyTextView.setEnabled(true);
                modifyTextView.setText("修 改");
            }
        });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}