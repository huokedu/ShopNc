package top.yokey.nsg.activity.seller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.GoodsOrderListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerOrderDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String order_id;
    private HashMap<String, String> mHashMap;
    private HashMap<String, String> commonHashMap;
    private HashMap<String, String> addressHashMap;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView orderSNTextView;
    private TextView paySNTextView;
    private TextView stateTextView;
    private TextView paymentTextView;
    private TextView addTimeTextView;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private EditText messageEditText;
    private EditText invoiceEditText;

    private RelativeLayout payTimeRelativeLayout;
    private TextView payTimeTextView;
    private RelativeLayout completeTimeRelativeLayout;
    private TextView completeTimeTextView;

    private TextView storeTextView;
    private RecyclerView mListView;
    private TextView infoTextView;
    private TextView[] bottomTextView;

    private ArrayList<HashMap<String, String>> goodsArrayList;

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
        setContentView(R.layout.activity_seller_order_detailed);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getJson();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        orderSNTextView = (TextView) findViewById(R.id.orderSNTextView);
        paySNTextView = (TextView) findViewById(R.id.paySNTextView);
        stateTextView = (TextView) findViewById(R.id.stateTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        addTimeTextView = (TextView) findViewById(R.id.addTimeTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        paymentTextView = (TextView) findViewById(R.id.paymentTextView);
        invoiceEditText = (EditText) findViewById(R.id.invoiceEditText);

        payTimeRelativeLayout = (RelativeLayout) findViewById(R.id.payTimeRelativeLayout);
        payTimeTextView = (TextView) findViewById(R.id.payTimeTextView);
        completeTimeRelativeLayout = (RelativeLayout) findViewById(R.id.completeTimeRelativeLayout);
        completeTimeTextView = (TextView) findViewById(R.id.completeTimeTextView);

        storeTextView = (TextView) findViewById(R.id.storeTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);

        bottomTextView = new TextView[4];
        bottomTextView[0] = (TextView) findViewById(R.id.bottom0TextView);
        bottomTextView[1] = (TextView) findViewById(R.id.bottom1TextView);
        bottomTextView[2] = (TextView) findViewById(R.id.bottom2TextView);
        bottomTextView[3] = (TextView) findViewById(R.id.bottom3TextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        order_id = mActivity.getIntent().getStringExtra("order_id");

        if (TextUtil.isEmpty(order_id)) {
            ToastUtil.show(mActivity, "传入的参数有误");
            return;
        }

        titleTextView.setText("订单详细");
        goodsArrayList = new ArrayList<>();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        bottomTextView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startChat(mActivity, mHashMap.get("buyer_id"));
            }
        });

        bottomTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startCall(mActivity, addressHashMap.get("mob_phone"));
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_detailed");
        ajaxParams.put("order_id", order_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            goodsArrayList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(data);
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("order_info")));
                            commonHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("extend_order_common")));
                            addressHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(commonHashMap.get("reciver_info")));
                            JSONArray jsonArray = new JSONArray(mHashMap.get("goods_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                hashMap.put("goods_image_url", hashMap.get("image_240_url"));
                                goodsArrayList.add(hashMap);
                            }
                            setValue();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getJsonFailure();
                        }
                    } else {
                        getJsonFailure();
                    }
                } else {
                    getJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getJsonFailure();
            }
        });

    }

    private void setValue() {

        orderSNTextView.setText(mHashMap.get("order_sn"));
        paySNTextView.setText(mHashMap.get("pay_sn"));
        stateTextView.setText(mHashMap.get("state_desc"));
        paymentTextView.setText(mHashMap.get("payment_name"));
        nameTextView.setText(commonHashMap.get("reciver_name"));

        if (!TextUtil.isEmpty(mHashMap.get("add_time")) && !mHashMap.get("add_time").equals("0")) {
            addTimeTextView.setText(TimeUtil.longToTime(mHashMap.get("add_time")));
        }

        if (!TextUtil.isEmpty(mHashMap.get("payment_time")) && !mHashMap.get("payment_time").equals("0")) {
            payTimeTextView.setText(TimeUtil.longToTime(mHashMap.get("payment_time")));
        } else {
            payTimeRelativeLayout.setVisibility(View.GONE);
        }

        if (!TextUtil.isEmpty(mHashMap.get("finnshed_time")) && !mHashMap.get("finnshed_time").equals("0")) {
            completeTimeTextView.setText(TimeUtil.longToTime(mHashMap.get("finnshed_time")));
        } else {
            completeTimeRelativeLayout.setVisibility(View.GONE);
        }

        if (!TextUtil.isEmpty(addressHashMap.get("mob_phone"))) {
            phoneTextView.setText(addressHashMap.get("mob_phone"));
        } else {
            phoneTextView.setText(addressHashMap.get("tel_phone"));
        }

        addressTextView.setText(addressHashMap.get("address"));

        if (TextUtil.isEmpty(commonHashMap.get("order_message"))) {
            messageEditText.setText("买家未留言");
        } else {
            messageEditText.setText(commonHashMap.get("order_message"));
        }

        if (TextUtil.isEmpty(commonHashMap.get("invoice_info")) || commonHashMap.get("invoice_info").equals("[]")) {
            invoiceEditText.setText("没有发票信息");
        } else {
            String invoice_info = commonHashMap.get("invoice_info");
            invoice_info = invoice_info.replace("{", "").replace("}", "");
            invoice_info = invoice_info.replace("\"", "");
            invoiceEditText.setText(invoice_info);
        }

        storeTextView.setText(mHashMap.get("store_name"));

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapter(new GoodsOrderListAdapter(mApplication, mActivity, goodsArrayList));

        String total = "共 <font color='#FF5001'>" + mHashMap.get("goods_count") + "</font> 件";
        total += "，共 <font color='#FF5001'>￥ " + mHashMap.get("order_amount") + "</font> 元";

        if (!mHashMap.get("shipping_fee").equals("0.00")) {
            total += "，运费 ￥ " + mHashMap.get("shipping_fee") + " 元";
        } else {
            total += "，免运费";
        }

        infoTextView.setText(Html.fromHtml(total));

        switch (mHashMap.get("order_state")) {
            case "0":
                bottomTextView[2].setVisibility(View.GONE);
                bottomTextView[3].setVisibility(View.GONE);
                break;
            case "10":
                bottomTextView[2].setText("取消订单");
                bottomTextView[3].setText("修改价格");
                bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderCancel(mHashMap.get("order_sn"));
                    }
                });
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderModify(mHashMap.get("order_sn"), mHashMap.get("goods_amount"), mHashMap.get("shipping_fee"));
                    }
                });
                break;
            case "20":
                bottomTextView[2].setVisibility(View.GONE);
                bottomTextView[3].setText("设置发货");
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDeliver();
                    }
                });
                break;
            case "30":
                bottomTextView[2].setVisibility(View.GONE);
                bottomTextView[3].setText("查看物流");
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startLogisticsSeller(mActivity, mHashMap.get("order_id"), mHashMap.get("buyer_id"));
                    }
                });
                break;
            case "40":
                bottomTextView[2].setVisibility(View.GONE);
                bottomTextView[3].setText("查看物流");
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startLogisticsSeller(mActivity, mHashMap.get("order_id"), mHashMap.get("buyer_id"));
                    }
                });
                break;
            default:
                break;
        }

    }

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试？",
                "读取数据失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getJson();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                    }
                });

    }

    private void orderModify(String order_sn, String order_price, String order_ship) {

        Intent intent = new Intent(mActivity, SellerOrderModifyActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_sn", order_sn);
        intent.putExtra("order_ship", order_ship);
        intent.putExtra("order_price", order_price);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_MODIFY);

    }

    private void orderCancel(String order_sn) {

        Intent intent = new Intent(mActivity, SellerOrderCancelActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_sn", order_sn);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_CANCEL);

    }

    private void orderDeliver() {
        Intent intent = new Intent(mActivity, SellerOrderDeliverActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_DELIVER);
    }

    private void returnActivity() {

        mActivity.setResult(RESULT_OK);
        mApplication.finishActivity(mActivity);

    }

}