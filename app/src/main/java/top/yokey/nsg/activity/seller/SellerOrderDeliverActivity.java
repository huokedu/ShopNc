package top.yokey.nsg.activity.seller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.GoodsOrderListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerOrderDeliverActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String order_id;
    private boolean logisticsBoolean;
    private String shipping_express_id;
    private Vector<String> myExpressVector;
    private HashMap<String, String> mHashMap;
    private HashMap<String, String> commonHashMap;
    private HashMap<String, String> receiptHashMap;
    private HashMap<String, String> daddressHashMap;
    private ArrayList<HashMap<String, String>> goodsArrayList;
    private ArrayList<HashMap<String, String>> expressArrayList;

    private ImageView backImageView;
    private TextView titleTextView;

    private RecyclerView goodsListView;
    private TextView goodsTextView;
    private EditText messageEditText;
    private TextView receiptNameTextView;
    private TextView receiptPhoneTextView;
    private TextView receiptAddressTextView;
    private TextView deliverNameTextView;
    private TextView deliverPhoneTextView;
    private TextView deliverAddressTextView;
    private RadioButton logisticsNoRadioButton;
    private RadioButton logisticsYesRadioButton;
    private Spinner logisticsSpinner;
    private EditText logisticsEditText;
    private TextView deliverTextView;

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
        setContentView(R.layout.activity_seller_order_deliver);
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

        goodsListView = (RecyclerView) findViewById(R.id.goodsListView);
        goodsTextView = (TextView) findViewById(R.id.goodsTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        receiptNameTextView = (TextView) findViewById(R.id.receiptNameTextView);
        receiptPhoneTextView = (TextView) findViewById(R.id.receiptPhoneTextView);
        receiptAddressTextView = (TextView) findViewById(R.id.receiptAddressTextView);
        deliverNameTextView = (TextView) findViewById(R.id.deliverNameTextView);
        deliverPhoneTextView = (TextView) findViewById(R.id.deliverPhoneTextView);
        deliverAddressTextView = (TextView) findViewById(R.id.deliverAddressTextView);
        logisticsNoRadioButton = (RadioButton) findViewById(R.id.logisticsNoRadioButton);
        logisticsYesRadioButton = (RadioButton) findViewById(R.id.logisticsYesRadioButton);
        logisticsSpinner = (Spinner) findViewById(R.id.logisticsSpinner);
        logisticsEditText = (EditText) findViewById(R.id.logisticsEditText);
        deliverTextView = (TextView) findViewById(R.id.deliverTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        order_id = mActivity.getIntent().getStringExtra("order_id");

        if (TextUtil.isEmpty(order_id)) {
            ToastUtil.show(mActivity, "传入的参数有误");
            return;
        }

        logisticsBoolean = false;
        shipping_express_id = "";
        logisticsNoRadioButton.setChecked(true);

        mHashMap = new HashMap<>();
        commonHashMap = new HashMap<>();
        receiptHashMap = new HashMap<>();
        myExpressVector = new Vector<>();
        daddressHashMap = new HashMap<>();
        goodsArrayList = new ArrayList<>();
        expressArrayList = new ArrayList<>();
        titleTextView.setText("订单发货");

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        logisticsNoRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    logisticsBoolean = false;
                    logisticsSpinner.setVisibility(View.GONE);
                    logisticsEditText.setVisibility(View.GONE);
                }
            }
        });

        logisticsYesRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    logisticsBoolean = true;
                    logisticsSpinner.setVisibility(View.VISIBLE);
                    logisticsEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        logisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shipping_express_id = expressArrayList.get(position).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deliverTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliver();
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_deliver_info");
        ajaxParams.put("order_id", order_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (!TextUtil.isEmpty(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mApplication.getJsonData(o.toString())));
                            commonHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("extend_order_common")));
                            receiptHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(commonHashMap.get("reciver_info")));
                            daddressHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("daddress_info")));
                            //Goods
                            JSONArray jsonArray = new JSONArray(mHashMap.get("extend_order_goods"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                goodsArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            //myExpress
                            jsonArray = new JSONArray(mHashMap.get("my_express_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                myExpressVector.add(jsonArray.get(i).toString());
                            }
                            //Express
                            JSONObject jsonObject = new JSONObject(mHashMap.get("express_list"));
                            for (int i = 0; i < myExpressVector.size(); i++) {
                                expressArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString(myExpressVector.get(i)))));
                            }
                            //设置值
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

        //商品
        goodsListView.setLayoutManager(new LinearLayoutManager(mActivity));
        goodsListView.setAdapter(new GoodsOrderListAdapter(mApplication, mActivity, goodsArrayList));
        String total = "共 <font color='#FF5001'>" + goodsArrayList.size() + "</font> 件";
        total += "，共 <font color='#FF5001'>￥ " + mHashMap.get("order_amount") + "</font> 元";
        if (!mHashMap.get("shipping_fee").equals("0.00")) {
            total += "，运费 ￥ " + mHashMap.get("shipping_fee") + " 元";
        } else {
            total += "，免运费";
        }
        goodsTextView.setText(Html.fromHtml(total));
        //收货人
        receiptNameTextView.setText(commonHashMap.get("reciver_name"));
        if (TextUtil.isEmpty(receiptHashMap.get("tel_phone"))) {
            receiptPhoneTextView.setText(receiptHashMap.get("mob_phone"));
        } else {
            receiptPhoneTextView.setText(receiptHashMap.get("tel_phone"));
        }
        receiptAddressTextView.setText(receiptHashMap.get("address"));
        //发货人
        deliverNameTextView.setText(daddressHashMap.get("seller_name"));
        deliverPhoneTextView.setText(daddressHashMap.get("telphone"));
        deliverAddressTextView.setText(daddressHashMap.get("area_info"));
        deliverAddressTextView.append(" ");
        deliverAddressTextView.append(daddressHashMap.get("address"));
        //物流
        if (expressArrayList.isEmpty()) {
            logisticsYesRadioButton.setVisibility(View.GONE);
        } else {
            Vector<String> expressVector = new Vector<>();
            for (int i = 0; i < expressArrayList.size(); i++) {
                expressVector.add(expressArrayList.get(i).get("e_name"));
            }
            ArrayAdapter adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, expressVector);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            logisticsSpinner.setAdapter(adapter);
        }

    }

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试?",
                "读取数据失败?",
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
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                });

    }

    private void deliver() {

        if (logisticsBoolean) {
            if (logisticsEditText.getText().toString().isEmpty()) {
                ToastUtil.show(mActivity, "请输入快递单号！");
                return;
            }
        }

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_deliver_send");
        ajaxParams.put("order_id", order_id);
        ajaxParams.put("reciver_name", commonHashMap.get("reciver_name"));
        ajaxParams.put("reciver_area", receiptHashMap.get("area"));
        ajaxParams.put("reciver_street", receiptHashMap.get("street"));
        ajaxParams.put("reciver_mob_phone", receiptHashMap.get("mob_phone"));
        ajaxParams.put("reciver_tel_phone", receiptHashMap.get("tel_phone"));
        ajaxParams.put("reciver_dlyp", "");
        ajaxParams.put("deliver_explain", messageEditText.getText().toString());
        ajaxParams.put("daddress_id", commonHashMap.get("daddress_id"));

        if (logisticsBoolean) {
            ajaxParams.put("shipping_express_id", shipping_express_id);
            ajaxParams.put("shipping_code", logisticsEditText.getText().toString());
        } else {
            ajaxParams.put("shipping_express_id", "e1000");
        }

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    mActivity.setResult(RESULT_OK);
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

        mApplication.finishActivity(mActivity);

    }

}