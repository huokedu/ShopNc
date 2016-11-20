package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.ChatListActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.order.OrderActivity;
import top.yokey.nsg.activity.order.RefundReturnActivity;
import top.yokey.nsg.activity.seller.SellerActivity;
import top.yokey.nsg.activity.seller.SellerLoginActivity;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.QRCodeUtil;
import top.yokey.nsg.utility.TextUtil;

public class MineActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView userAvatarImageView;
    private RelativeLayout userRelativeLayout;
    private TextView usernameTextView;
    private TextView userLevelTextView;
    private ImageView qrCodeImageView;
    private ImageView messageImageView;
    private ImageView notifyImageView;

    private TextView goodsTextView;
    private TextView storeTextView;
    private TextView footprintTextView;
    private TextView signTextView;

    private TextView orderTextView;
    private TextView[] orderNumberTextView;
    private RelativeLayout waitPaymentRelativeLayout;
    private RelativeLayout waitDeliverRelativeLayout;
    private RelativeLayout waitReceiptRelativeLayout;
    private RelativeLayout waitEvaluateRelativeLayout;
    private RelativeLayout waitRefundRelativeLayout;

    private TextView propertyTextView;
    private TextView preDepositTextView;
    private TextView rechargeCardTextView;
    private TextView vouchersTextView;
    private TextView redPacketsTextView;
    private TextView pointsTextView;

    private TextView sellerTextView;
    private TextView invoiceTextView;
    private TextView addressTextView;
    private TextView settingTextView;

    @Override
    protected void onResume() {
        super.onResume();
        setValue();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mine);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        userRelativeLayout = (RelativeLayout) findViewById(R.id.userRelativeLayout);
        userAvatarImageView = (ImageView) findViewById(R.id.userAvatarImageView);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        userLevelTextView = (TextView) findViewById(R.id.userLevelTextView);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);
        notifyImageView = (ImageView) findViewById(R.id.notifyImageView);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        goodsTextView = (TextView) findViewById(R.id.goodsTextView);
        storeTextView = (TextView) findViewById(R.id.storeTextView);
        footprintTextView = (TextView) findViewById(R.id.footprintTextView);
        signTextView = (TextView) findViewById(R.id.signTextView);

        orderTextView = (TextView) findViewById(R.id.orderTextView);
        orderNumberTextView = new TextView[5];
        orderNumberTextView[0] = (TextView) findViewById(R.id.orderNumberTextView);
        orderNumberTextView[1] = (TextView) findViewById(R.id.waitPaymentNumberTextView);
        orderNumberTextView[2] = (TextView) findViewById(R.id.waitDeliverNumberTextView);
        orderNumberTextView[3] = (TextView) findViewById(R.id.waitReceiptNumberTextView);
        orderNumberTextView[4] = (TextView) findViewById(R.id.waitEvaluateNumberTextView);
        waitPaymentRelativeLayout = (RelativeLayout) findViewById(R.id.waitPaymentRelativeLayout);
        waitDeliverRelativeLayout = (RelativeLayout) findViewById(R.id.waitDeliverRelativeLayout);
        waitReceiptRelativeLayout = (RelativeLayout) findViewById(R.id.waitReceiptRelativeLayout);
        waitEvaluateRelativeLayout = (RelativeLayout) findViewById(R.id.waitEvaluateRelativeLayout);
        waitRefundRelativeLayout = (RelativeLayout) findViewById(R.id.waitRefundRelativeLayout);

        propertyTextView = (TextView) findViewById(R.id.propertyTextView);
        preDepositTextView = (TextView) findViewById(R.id.preDepositTextView);
        rechargeCardTextView = (TextView) findViewById(R.id.rechargeCardTextView);
        vouchersTextView = (TextView) findViewById(R.id.vouchersTextView);
        redPacketsTextView = (TextView) findViewById(R.id.redPacketsTextView);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);

        sellerTextView = (TextView) findViewById(R.id.sellerTextView);
        invoiceTextView = (TextView) findViewById(R.id.invoiceTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        settingTextView = (TextView) findViewById(R.id.settingTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        if (TextUtil.isEmpty(mApplication.userKeyString)) {
            usernameTextView.setText("登录中");
        } else {
            usernameTextView.setText("请登录");
        }


    }

    private void initEven() {

        userRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, MineCenterActivity.class));
            }
        });

        userAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, MineCenterActivity.class));
            }
        });

        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, MineCenterActivity.class));
            }
        });

        messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, ChatListActivity.class));
            }
        });

        notifyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, NotifyActivity.class));
            }
        });

        qrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mApplication.userHashMap.isEmpty()) {
                    mApplication.startActivity(mActivity, new Intent(mActivity, LoginActivity.class));
                } else {
                    String content = "[uid:" + mApplication.userHashMap.get("member_id") + "]";
                    DialogUtil.qrCode(mActivity, "扫描二维码跟我聊", QRCodeUtil.create(content, 512, 512));
                }
            }
        });

        goodsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CollectionActivity.class);
                intent.putExtra("position", 0);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        storeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CollectionActivity.class);
                intent.putExtra("position", 1);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        footprintTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CollectionActivity.class);
                intent.putExtra("position", 2);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        signTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, SignActivity.class));
            }
        });

        orderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OrderActivity.class);
                intent.putExtra("position", 0);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        waitPaymentRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OrderActivity.class);
                intent.putExtra("position", 1);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        waitDeliverRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OrderActivity.class);
                intent.putExtra("position", 2);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        waitReceiptRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OrderActivity.class);
                intent.putExtra("position", 3);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        waitEvaluateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OrderActivity.class);
                intent.putExtra("position", 4);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        waitRefundRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RefundReturnActivity.class);
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        propertyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, PropertyActivity.class));
            }
        });

        preDepositTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, PreDepositActivity.class));
            }
        });

        rechargeCardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, RechargeCardActivity.class));
            }
        });

        vouchersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, VouchersActivity.class));
            }
        });

        redPacketsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, RedPacketActivity.class));
            }
        });

        pointsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, PointsActivity.class));
            }
        });

        sellerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtil.isEmpty(mApplication.userHashMap.get("store_id"))) {
                    if (mApplication.sellerNameString.isEmpty() || mApplication.sellerKeyString.isEmpty()) {
                        mApplication.startActivity(mActivity, new Intent(mActivity, SellerLoginActivity.class));
                    } else {
                        mApplication.startActivity(mActivity, new Intent(mActivity, SellerActivity.class));
                    }
                }
            }
        });

        invoiceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, InvoiceActivity.class);
                intent.putExtra("model", "normal");
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddressActivity.class);
                intent.putExtra("model", "normal");
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        settingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, SettingActivity.class));
            }
        });

    }

    private void setValue() {

        if (!mApplication.userHashMap.isEmpty()) {

            ImageLoader.getInstance().displayImage(mApplication.userHashMap.get("avator"), userAvatarImageView);

            if (TextUtil.isEmpty(mApplication.userHashMap.get("member_truename"))) {
                usernameTextView.setText(mApplication.userHashMap.get("member_name"));
            } else {
                usernameTextView.setText(mApplication.userHashMap.get("member_truename"));
            }

            userLevelTextView.setVisibility(View.VISIBLE);
            userLevelTextView.setText(mApplication.userHashMap.get("level_name"));

            String goods = "商品：" + mApplication.userHashMap.get("favorites_goods");
            goodsTextView.setText(goods);

            String store = "店铺：" + mApplication.userHashMap.get("favorites_store");
            storeTextView.setText(store);

            if (mApplication.orderArrayList != null) {
                String all_number = mApplication.orderArrayList[0].size() + mApplication.orderArrayList[5].size() + "";
                orderNumberTextView[0].setText(all_number);
                for (int i = 1; i < orderNumberTextView.length; i++) {
                    String number = mApplication.orderArrayList[i].size() + "";
                    if (number.equals("0")) {
                        orderNumberTextView[i].setVisibility(View.GONE);
                    } else {
                        orderNumberTextView[i].setVisibility(View.VISIBLE);
                        orderNumberTextView[i].setText(number);
                    }
                }
            }

            String preDeposit = "￥ " + mApplication.userHashMap.get("available_predeposit") + "\n预存款";
            preDepositTextView.setText(preDeposit);

            String rechargeCard = "￥ " + mApplication.userHashMap.get("available_rc_balance") + "\n充值卡";
            rechargeCardTextView.setText(rechargeCard);

            String voucher = mApplication.voucherArrayList.size() + "张\n代金券";
            vouchersTextView.setText(voucher);

            String redPack = mApplication.redPackArrayList.size() + "个\n红包";
            redPacketsTextView.setText(redPack);

            String points = mApplication.userHashMap.get("member_points") + "\n积分";
            pointsTextView.setText(points);

            if (TextUtil.isEmpty(mApplication.userHashMap.get("store_id"))) {
                sellerTextView.setVisibility(View.GONE);
            } else {
                sellerTextView.setVisibility(View.VISIBLE);
            }

        } else {

            if (TextUtil.isEmpty(mApplication.userKeyString)) {

                userLevelTextView.setVisibility(View.GONE);
                userAvatarImageView.setImageResource(R.mipmap.ic_avatar);
                usernameTextView.setText("注册登录");
                goodsTextView.setText("商品");
                storeTextView.setText("店铺");
                preDepositTextView.setText("预存款");
                rechargeCardTextView.setText("充值卡");
                vouchersTextView.setText("代金券");
                redPacketsTextView.setText("红包");
                pointsTextView.setText("积分");
                sellerTextView.setVisibility(View.GONE);
                for (TextView textView : orderNumberTextView) {
                    textView.setVisibility(View.GONE);
                }
                orderNumberTextView[0].setVisibility(View.VISIBLE);
                orderNumberTextView[0].setText("");

            }

        }

    }

}