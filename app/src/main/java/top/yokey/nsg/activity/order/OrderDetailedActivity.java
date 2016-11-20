package top.yokey.nsg.activity.order;

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
import top.yokey.nsg.adapter.GoodsOrderDetailedListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class OrderDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String order_id;
    private String lock_state;
    private HashMap<String, String> mHashMap;
    private HashMap<String, String> storeHashMap;
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

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            getJson();
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
        setContentView(R.layout.activity_order_detailed);
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
        lock_state = mActivity.getIntent().getStringExtra("lock_state");

        if (TextUtil.isEmpty(order_id) || TextUtil.isEmpty(lock_state)) {
            ToastUtil.show(mActivity, "传入的参数有误");
            return;
        }

        titleTextView.setText("订单详细");

        getJson();

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
                mApplication.startChat(mActivity, storeHashMap.get("member_id"));
            }
        });

        bottomTextView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtil.isEmpty(storeHashMap.get("store_phone"))) {
                    mApplication.startCall(mActivity, storeHashMap.get("store_phone"));
                } else {
                    ToastUtil.show(mActivity, "未填写联系电话");
                }
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_order");
        ajaxParams.putOp("order_info");
        ajaxParams.put("order_id", order_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
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
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("order_info")));
                            storeHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("extend_store")));
                            commonHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("extend_order_common")));
                            addressHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(commonHashMap.get("reciver_info")));
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

        if (!TextUtil.isEmpty(mHashMap.get("add_time"))) {
            addTimeTextView.setText(mHashMap.get("add_time"));
        }

        if (!TextUtil.isEmpty(mHashMap.get("payment_time"))) {
            payTimeTextView.setText(mHashMap.get("payment_time"));
        } else {
            payTimeRelativeLayout.setVisibility(View.GONE);
        }

        if (!TextUtil.isEmpty(mHashMap.get("finnshed_time"))) {
            completeTimeTextView.setText(mHashMap.get("finnshed_time"));
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

        if (TextUtil.isEmpty(mHashMap.get("invoice"))) {
            invoiceEditText.setText("没有发票信息");
        } else {
            invoiceEditText.setText(mHashMap.get("invoice"));
        }

        storeTextView.setText(mHashMap.get("store_name"));

        try {

            ArrayList<HashMap<String, String>> goodsArrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(mHashMap.get("goods_list"));
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                hashMap.put("order_state", mHashMap.get("order_state"));
                hashMap.put("delete_state", mHashMap.get("delete_state"));
                goodsArrayList.add(hashMap);
            }

            mListView.setLayoutManager(new LinearLayoutManager(mActivity));
            mListView.setAdapter(new GoodsOrderDetailedListAdapter(mApplication, mActivity, goodsArrayList));

            String total = "共 <font color='#FF5001'>" + mHashMap.get("goods_count") + "</font> 件";
            total += "，共 <font color='#FF5001'>￥ " + mHashMap.get("real_pay_amount") + "</font> 元";

            if (!mHashMap.get("shipping_fee").equals("0.00")) {
                total += "，运费 ￥ " + mHashMap.get("shipping_fee") + " 元";
            } else {
                total += "，免运费";
            }

            infoTextView.setText(Html.fromHtml(total));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bottomTextView[3].setVisibility(View.GONE);

        switch (mHashMap.get("order_state")) {
            case "0":
                if (mHashMap.get("delete_state").equals("0")) {
                    bottomTextView[2].setText("删除订单");
                    bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDelete();
                        }
                    });
                }
                if (mHashMap.get("delete_state").equals("1")) {
                    bottomTextView[2].setText("彻底删除");
                    bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDrop();
                        }
                    });
                    bottomTextView[3].setVisibility(View.VISIBLE);
                    bottomTextView[3].setText("恢复订单");
                    bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderRestore();
                        }
                    });
                }
                break;
            case "10":
                bottomTextView[2].setText("取消订单");
                bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderCancel();
                    }
                });
                bottomTextView[3].setVisibility(View.VISIBLE);
                bottomTextView[3].setText("订单支付");
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, BuySetup2Activity.class);
                        intent.putExtra("pay_sn", mHashMap.get("pay_sn"));
                        intent.putExtra("payment_code", "online");
                        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_PAY);
                    }
                });
                break;
            case "20":
                if (lock_state.equals("1")) {
                    bottomTextView[2].setText("订单退款中...");
                    bottomTextView[2].setOnClickListener(null);
                } else {
                    bottomTextView[2].setText("订单退款");
                    bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderRefund();
                        }
                    });
                }
                break;
            case "30":
                bottomTextView[2].setText("查看物流");
                bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApplication.startLogistics(mActivity, mHashMap.get("order_id"));
                    }
                });
                bottomTextView[3].setVisibility(View.VISIBLE);
                bottomTextView[3].setText("确认收货");
                bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderReceive();
                    }
                });
                break;
            case "40":
                if (mHashMap.get("delete_state").equals("0")) {
                    if (mHashMap.get("evaluation_state").equals("0")) {
                        bottomTextView[2].setText("删除订单");
                        bottomTextView[3].setText("订单评价");
                        bottomTextView[3].setVisibility(View.VISIBLE);
                        bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderDelete();
                            }
                        });
                        bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderEvaluate();
                            }
                        });
                    } else {
                        bottomTextView[2].setText("删除订单");
                        bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderDelete();
                            }
                        });
                    }
                } else {
                    bottomTextView[2].setText("彻底删除");
                    bottomTextView[2].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDrop();
                        }
                    });
                    bottomTextView[3].setVisibility(View.VISIBLE);
                    bottomTextView[3].setText("恢复订单");
                    bottomTextView[3].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderRestore();
                        }
                    });
                }
                break;
            default:
                break;
        }

    }

    private void orderEvaluate() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法评价");
            return;
        }

        Intent intent = new Intent(mActivity, OrderEvaluateActivity.class);
        intent.putExtra("order_id", mHashMap.get("order_id"));
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_EVALUATE);

    }

    private void orderReceive() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法确认收货");
            return;
        }

        DialogUtil.query(
                mActivity,
                "确认收货？",
                "请确认您已经收到货品，确认收货，货款会支付给卖家。",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_order");
                        ajaxParams.putOp("order_receive");
                        ajaxParams.put("order_id", mHashMap.get("order_id"));
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            ToastUtil.showSuccess(mActivity);
                                            getJson();
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
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
                    }
                }
        );

    }

    private void orderRefund() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款...");
            return;
        }

        Intent intent = new Intent(mActivity, OrderRefundAllActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_REFUND);

    }

    private void orderCancel() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法取消");
            return;
        }

        DialogUtil.query(
                mActivity,
                "取消订单？",
                "取消这个订单以及所有关联订单。",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_order");
                        ajaxParams.putOp("order_cancel");
                        ajaxParams.put("order_id", mHashMap.get("order_id"));
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            ToastUtil.show(mActivity, "取消订单成功，已支付金额已原路退回。");
                                            getJson();
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
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
                    }
                }
        );

    }

    private void orderDelete() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法删除");
            return;
        }

        DialogUtil.query(
                mActivity,
                "删除订单？",
                "删除这个订单以及所有关联订单。",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_order");
                        ajaxParams.putOp("change_state");
                        ajaxParams.put("state_type", "order_delete");
                        ajaxParams.put("order_id", mHashMap.get("order_id"));
                        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            ToastUtil.show(mActivity, "订单已删除，您可以在回收站中找回");
                                            getJson();
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
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
                    }
                }
        );

    }

    private void orderRestore() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法恢复");
            return;
        }

        DialogUtil.query(
                mActivity,
                "恢复订单？",
                "恢复这个订单以及所有关联订单。",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_order");
                        ajaxParams.putOp("change_state");
                        ajaxParams.put("state_type", "order_restore");
                        ajaxParams.put("order_id", mHashMap.get("order_id"));
                        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            ToastUtil.show(mActivity, "订单已恢复");
                                            getJson();
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
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
                    }
                }
        );

    }

    private void orderDrop() {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法彻底删除");
            return;
        }

        DialogUtil.query(
                mActivity,
                "彻底删除订单？",
                "彻底删除这个订单以及所有关联订单。",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_order");
                        ajaxParams.putOp("change_state");
                        ajaxParams.put("state_type", "order_drop");
                        ajaxParams.put("order_id", mHashMap.get("order_id"));
                        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            ToastUtil.show(mActivity, "订单已彻底删除");
                                            returnActivity();
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
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
                    }
                }
        );

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

    private void returnActivity() {

        mActivity.setResult(RESULT_OK);
        mApplication.finishActivity(mActivity);

    }

}