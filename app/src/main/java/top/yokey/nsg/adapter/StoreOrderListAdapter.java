package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.order.OrderActivity;
import top.yokey.nsg.activity.order.OrderDetailedActivity;
import top.yokey.nsg.activity.order.OrderEvaluateActivity;
import top.yokey.nsg.activity.order.OrderRefundAllActivity;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class StoreOrderListAdapter extends RecyclerView.Adapter<StoreOrderListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public StoreOrderListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mApplication = application;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final HashMap<String, String> hashMap = mArrayList.get(position);

        int goods_num = 0;

        try {
            ArrayList<HashMap<String, String>> goodsArrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(hashMap.get("extend_order_goods"));
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> hashMap1 = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                goods_num += Integer.parseInt(hashMap1.get("goods_num"));
                goodsArrayList.add(hashMap1);
            }
            holder.mListView.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.mListView.setAdapter(new GoodsOrderListAdapter(mApplication, mActivity, goodsArrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.storeTextView.setText(hashMap.get("store_name"));
        if (hashMap.get("lock_state").equals("1")) {
            holder.stateTextView.setText("退货/款中...");
        } else {
            holder.stateTextView.setText(hashMap.get("state_desc"));
        }

        String total = "共 <font color='#FF5001'>" + goods_num + "</font> 件";
        total += "，共 <font color='#FF5001'>￥ " + hashMap.get("order_amount") + "</font> 元";

        if (!hashMap.get("shipping_fee").equals("0.00")) {
            total += "，运费 ￥ " + hashMap.get("shipping_fee") + " 元";
        } else {
            total += "，免运费";
        }

        holder.infoTextView.setText(Html.fromHtml(total));

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDetailed(hashMap.get("order_id"), hashMap.get("lock_state"));
            }
        });

        holder.operaTextView.setOnClickListener(null);
        holder.optionTextView.setOnClickListener(null);

        switch (hashMap.get("order_state")) {
            case "0":
                if (hashMap.get("delete_state").equals("0")) {
                    holder.operaTextView.setText("删除订单");
                    holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDelete(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                    holder.optionTextView.setText("订单详细");
                    holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDetailed(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                }
                if (hashMap.get("delete_state").equals("1")) {
                    holder.operaTextView.setVisibility(View.VISIBLE);
                    holder.operaTextView.setText("彻底删除");
                    holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDrop(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                    holder.optionTextView.setText("恢复订单");
                    holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderRestore(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                }
                break;
            case "10":
                holder.operaTextView.setText("订单详细");
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderDetailed(hashMap.get("order_id"), hashMap.get("lock_state"));
                    }
                });
                holder.optionTextView.setText("取消订单");
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderCancel(hashMap.get("order_id"), hashMap.get("lock_state"));
                    }
                });
                break;
            case "20":
                holder.operaTextView.setText("订单详细");
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderDetailed(hashMap.get("order_id"), hashMap.get("lock_state"));
                    }
                });
                holder.optionTextView.setText("订单退款");
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderRefund(hashMap.get("order_id"), hashMap.get("lock_state"));
                    }
                });
                break;
            case "30":
                holder.operaTextView.setText("查看物流");
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApplication.startLogistics(mActivity, hashMap.get("order_id"));
                    }
                });
                holder.optionTextView.setText("确认收货");
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderReceive(hashMap.get("order_id"), hashMap.get("lock_state"));
                    }
                });
                break;
            case "40":
                if (hashMap.get("delete_state").equals("0")) {
                    if (hashMap.get("evaluation_state").equals("0")) {
                        holder.operaTextView.setText("删除订单");
                        holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderDelete(hashMap.get("order_id"), hashMap.get("lock_state"));
                            }
                        });
                        holder.optionTextView.setText("订单评价");
                        holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderEvaluate(hashMap.get("order_id"), hashMap.get("lock_state"));
                            }
                        });
                    } else {
                        holder.operaTextView.setText("删除订单");
                        holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderDelete(hashMap.get("order_id"), hashMap.get("lock_state"));
                            }
                        });
                        holder.optionTextView.setText("投诉维权");
                        holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                orderDetailed(hashMap.get("order_id"), hashMap.get("lock_state"));
                            }
                        });
                    }
                } else {
                    holder.operaTextView.setVisibility(View.VISIBLE);
                    holder.operaTextView.setText("彻底删除");
                    holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDrop(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                    holder.optionTextView.setText("恢复订单");
                    holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderRestore(hashMap.get("order_id"), hashMap.get("lock_state"));
                        }
                    });
                }
                break;
            default:
                break;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_store_order, group, false);
        return new ViewHolder(view);
    }

    private void orderDetailed(final String order_id, String lock_state) {

        Intent intent = new Intent(mActivity, OrderDetailedActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("lock_state", lock_state);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_DETAILED);

    }

    private void orderRefund(final String order_id, String lock_state) {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款...");
            return;
        }

        Intent intent = new Intent(mActivity, OrderRefundAllActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_REFUND);

    }

    private void orderEvaluate(final String order_id, String lock_state) {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法评价");
            return;
        }

        Intent intent = new Intent(mActivity, OrderEvaluateActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_EVALUATE);

    }

    private void orderReceive(final String order_id, String lock_state) {

        if (lock_state.equals("1")) {
            ToastUtil.show(mActivity, "订单正在退货/款，暂时无法收货");
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
                                        if (data.equals("1")) {
                                            OrderActivity.getJson();
                                            ToastUtil.showSuccess(mActivity);
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

    private void orderCancel(final String order_id, String lock_state) {

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
                                        if (data.equals("1")) {
                                            OrderActivity.getJson();
                                            ToastUtil.show(mActivity, "取消订单成功，已支付金额已原路退回。");
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

    private void orderDelete(final String order_id, String lock_state) {

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
                                        if (data.equals("1")) {
                                            OrderActivity.getJson();
                                            ToastUtil.show(mActivity, "订单已删除，您可以在回收站中找回");
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

    private void orderRestore(final String order_id, String lock_state) {

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
                                        if (data.equals("1")) {
                                            OrderActivity.getJson();
                                            ToastUtil.show(mActivity, "订单已恢复");
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

    private void orderDrop(final String order_id, String lock_state) {

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
                                        if (data.equals("1")) {
                                            OrderActivity.getJson();
                                            ToastUtil.show(mActivity, "订单已彻底删除");
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private TextView storeTextView;
        private TextView stateTextView;
        private RecyclerView mListView;
        private TextView infoTextView;
        private TextView operaTextView;
        private TextView optionTextView;

        private ViewHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view.findViewById(R.id.mainLinearLayout);
            storeTextView = (TextView) view.findViewById(R.id.storeTextView);
            stateTextView = (TextView) view.findViewById(R.id.stateTextView);
            mListView = (RecyclerView) view.findViewById(R.id.mainListView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);
            operaTextView = (TextView) view.findViewById(R.id.operaTextView);
            optionTextView = (TextView) view.findViewById(R.id.optionTextView);

        }

    }

}