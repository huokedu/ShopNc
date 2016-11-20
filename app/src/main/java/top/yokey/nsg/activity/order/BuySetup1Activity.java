package top.yokey.nsg.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.mine.AddressActivity;
import top.yokey.nsg.activity.mine.InvoiceActivity;
import top.yokey.nsg.adapter.BuyListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class BuySetup1Activity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String ifcart;
    private String cart_id;
    private HashMap<String, String> mHashMap;
    private HashMap<String, String> messageHashMap;

    private String address_id;
    private String vat_hash;
    private String offpay_hash;
    private String offpay_hash_batch;
    private String pay_name;
    private String invoice_id;
    private String voucher;
    private String pd_pay;
    private String password;
    private String fcode;
    private String rcb_pay;
    private String rpt;
    private String pay_message;

    private ImageView backImageView;
    private TextView titleTextView;

    private RecyclerView mListView;
    private BuyListAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mArrayList;

    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView addressTitleTextView;
    private RelativeLayout addressRelativeLayout;

    private TextView invoiceTitleTextView;
    private TextView invoiceNoTextView;
    private TextView invoiceYesTextView;

    private TextView confirmTextView;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_CHOOSE_ADDRESS:
                    address_id = data.getStringExtra("address_id");
                    nameTextView.setText(data.getStringExtra("true_name"));
                    phoneTextView.setText(data.getStringExtra("tel_phone"));
                    addressTextView.setText(data.getStringExtra("area_info"));
                    addressTextView.append(" ");
                    addressTextView.append(data.getStringExtra("address"));
                    changeAddress(data);
                    break;
                case NcApplication.CODE_CHOOSE_INVOICE:
                    invoice_id = data.getStringExtra("inv_id");
                    invoiceTitleTextView.setText("发票信息 ( ");
                    invoiceTitleTextView.append(data.getStringExtra("inv_title") + " : ");
                    invoiceTitleTextView.append(data.getStringExtra("inv_content") + " )");
                    invoiceYesTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    invoiceYesTextView.setBackgroundResource(R.drawable.border_text_view_goods_activity);
                    invoiceNoTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
                    invoiceNoTextView.setBackgroundResource(R.drawable.border_text_view_goods_normal);
                    break;
            }
        } else {
            switch (req) {
                case NcApplication.CODE_CHOOSE_ADDRESS:
                    DialogUtil.query(
                            mActivity,
                            "确认您的选择",
                            "添加收货地址？",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogUtil.cancel();
                                    Intent intent = new Intent(mActivity, AddressActivity.class);
                                    intent.putExtra("model", "choose");
                                    mApplication.startActivity(mActivity, intent, NcApplication.CODE_CHOOSE_ADDRESS);
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogUtil.cancel();
                                    mApplication.finishActivity(mActivity);
                                }
                            }
                    );
                    break;
                default:
                    break;
            }
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
        setContentView(R.layout.activity_buy_setup1);
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

        mListView = (RecyclerView) findViewById(R.id.mainListView);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        addressTitleTextView = (TextView) findViewById(R.id.addressTitleTextView);
        addressRelativeLayout = (RelativeLayout) findViewById(R.id.addressRelativeLayout);

        invoiceTitleTextView = (TextView) findViewById(R.id.invoiceTitleTextView);
        invoiceYesTextView = (TextView) findViewById(R.id.invoiceYesTextView);
        invoiceNoTextView = (TextView) findViewById(R.id.invoiceNoTextView);

        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        ifcart = mActivity.getIntent().getStringExtra("ifcart");
        cart_id = mActivity.getIntent().getStringExtra("cart_id");
        address_id = "";
        vat_hash = "";
        offpay_hash = "";
        offpay_hash_batch = "";
        pay_name = "";
        invoice_id = "";
        voucher = "";
        pd_pay = "";
        password = "";
        fcode = "";
        rcb_pay = "";
        rpt = "";
        pay_message = "";

        if (TextUtil.isEmpty(ifcart) || TextUtil.isEmpty(cart_id) || TextUtil.isEmpty(cart_id)) {
            ToastUtil.show(mActivity, "数据错误");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("确认订单信息");

        mArrayList = new ArrayList<>();
        mAdapter = new BuyListAdapter(mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        addressTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddressActivity.class);
                intent.putExtra("model", "choose");
                mApplication.startActivity(mActivity, intent, NcApplication.CODE_CHOOSE_ADDRESS);
            }
        });

        addressRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddressActivity.class);
                intent.putExtra("model", "choose");
                mApplication.startActivity(mActivity, intent, NcApplication.CODE_CHOOSE_ADDRESS);
            }
        });

        invoiceNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoice_id = "0";
                invoiceTitleTextView.setText("发票信息");
                invoiceNoTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                invoiceNoTextView.setBackgroundResource(R.drawable.border_text_view_goods_activity);
                invoiceYesTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
                invoiceYesTextView.setBackgroundResource(R.drawable.border_text_view_goods_normal);
            }
        });

        invoiceYesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, InvoiceActivity.class);
                intent.putExtra("model", "choose");
                mApplication.startActivity(mActivity, intent, NcApplication.CODE_CHOOSE_INVOICE);
            }
        });

        mAdapter.setOnTextWatcherListener(new BuyListAdapter.onTextWatcherListener() {
            @Override
            public void onTextWatcher(String id, String content) {
                messageHashMap.put(id, content);
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrderInfo();
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_buy");
        ajaxParams.putOp("buy_step1");
        ajaxParams.put("cart_id", cart_id);
        ajaxParams.put("ifcart", ifcart);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        messageHashMap = new HashMap<>();
                        String data = mApplication.getJsonData(o.toString());
                        mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(data));
                        parseInfo();
                        parseCartList();
                        parseAddress();
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

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "数据加载失败，是否重试？",
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
                        DialogUtil.cancel();
                    }
                }
        );

    }

    private void parseInfo() {

        vat_hash = mHashMap.get("vat_hash");

        try {
            JSONObject jsonObject = new JSONObject(mHashMap.get("address_api"));
            offpay_hash = jsonObject.getString("offpay_hash");
            offpay_hash_batch = jsonObject.getString("offpay_hash_batch");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String temp = "订单支付：" + mHashMap.get("order_amount") + " 元（含运费）";
        confirmTextView.setText(temp);
        pay_name = "online";
        invoice_id = "0";
        voucher = "";
        pd_pay = "0";
        password = "";
        fcode = "";
        rcb_pay = "";
        rpt = "";

    }

    private void parseCartList() {

        try {

            mArrayList.clear();
            JSONObject jsonObject = new JSONObject(mHashMap.get("store_cart_list"));

            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString(key))));
            }

            mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseAddress() {

        try {
            JSONObject jsonObject = new JSONObject(mHashMap.get("address_info"));
            address_id = jsonObject.getString("address_id");
            nameTextView.setText(jsonObject.getString("true_name"));
            if (TextUtil.isEmpty(jsonObject.getString("tel_phone"))) {
                phoneTextView.setText(jsonObject.getString("mob_phone"));
            } else {
                phoneTextView.setText(jsonObject.getString("tel_phone"));
            }
            addressTextView.setText(jsonObject.getString("area_info"));
            addressTextView.append(" ");
            addressTextView.append(jsonObject.getString("address"));
        } catch (JSONException e) {
            DialogUtil.query(
                    mActivity,
                    "确认您的选择",
                    "添加收货地址？",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            Intent intent = new Intent(mActivity, AddressActivity.class);
                            intent.putExtra("model", "choose");
                            mApplication.startActivity(mActivity, intent, NcApplication.CODE_CHOOSE_ADDRESS);
                        }
                    },
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

    private void changeAddress(final Intent intent) {

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_buy");
        ajaxParams.putOp("change_address");
        ajaxParams.put("freight_hash", mHashMap.get("freight_hash"));
        ajaxParams.put("city_id", intent.getStringExtra("city_id"));
        ajaxParams.put("area_id", intent.getStringExtra("area_id"));
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
                            JSONObject jsonObject = new JSONObject(data);
                            offpay_hash = jsonObject.getString("offpay_hash");
                            offpay_hash_batch = jsonObject.getString("offpay_hash_batch");
                        } catch (JSONException e) {
                            changeAddressFailure(intent);
                            e.printStackTrace();
                        }
                    } else {
                        changeAddressFailure(intent);
                    }
                } else {
                    changeAddressFailure(intent);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                changeAddressFailure(intent);
                DialogUtil.cancel();
            }
        });

    }

    private void changeAddressFailure(final Intent intent) {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "数据加载失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        changeAddress(intent);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                        DialogUtil.cancel();
                    }
                }
        );

    }

    private void confirmOrderInfo() {

        pay_message = "";

        Iterator iterator = messageHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = messageHashMap.get(key);
            pay_message += key + "|" + value + ",";
        }

        if (pay_message.length() != 0) {
            pay_message = pay_message.substring(0, pay_message.length() - 1);
        }

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_buy");
        ajaxParams.putOp("buy_step2");
        ajaxParams.put("ifcart", ifcart);
        ajaxParams.put("cart_id", cart_id);
        ajaxParams.put("address_id", address_id);
        ajaxParams.put("vat_hash", vat_hash);
        ajaxParams.put("offpay_hash", offpay_hash);
        ajaxParams.put("offpay_hash_batch", offpay_hash_batch);
        ajaxParams.put("pay_name", pay_name);
        ajaxParams.put("invoice_id", invoice_id);
        ajaxParams.put("voucher", voucher);
        ajaxParams.put("pd_pay", pd_pay);
        ajaxParams.put("password", password);
        ajaxParams.put("fcode", fcode);
        ajaxParams.put("rcb_pay", rcb_pay);
        ajaxParams.put("rpt", rpt);
        ajaxParams.put("pay_message", pay_message);

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
                            JSONObject jsonObject = new JSONObject(data);
                            Intent intent = new Intent(mActivity, BuySetup2Activity.class);
                            intent.putExtra("pay_sn", jsonObject.getString("pay_sn"));
                            intent.putExtra("payment_code", jsonObject.getString("payment_code"));
                            mApplication.startActivity(mActivity, intent);
                            mApplication.finishActivity(mActivity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.show(mActivity, error);
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

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消订单？",
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