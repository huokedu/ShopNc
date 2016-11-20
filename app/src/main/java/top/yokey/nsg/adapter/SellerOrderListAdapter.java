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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.seller.SellerOrderCancelActivity;
import top.yokey.nsg.activity.seller.SellerOrderDeliverActivity;
import top.yokey.nsg.activity.seller.SellerOrderDetailedActivity;
import top.yokey.nsg.activity.seller.SellerOrderModifyActivity;
import top.yokey.nsg.utility.TextUtil;

public class SellerOrderListAdapter extends RecyclerView.Adapter<SellerOrderListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public SellerOrderListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
            JSONArray jsonArray = new JSONArray(hashMap.get("goods_list"));
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> hashMap1 = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                hashMap1.put("goods_image_url", hashMap1.get("image_240_url"));
                goods_num += Integer.parseInt(hashMap1.get("goods_num"));
                goodsArrayList.add(hashMap1);
            }
            holder.mListView.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.mListView.setAdapter(new GoodsOrderListAdapter(mApplication, mActivity, goodsArrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.storeTextView.setText(hashMap.get("store_name"));
        holder.stateTextView.setText(hashMap.get("state_desc"));

        String total = "共 <font color='#FF5001'>" + goods_num + "</font> 件";
        total += "，共 <font color='#FF5001'>￥ " + hashMap.get("order_amount") + "</font> 元";

        if (!hashMap.get("shipping_fee").equals("0.00")) {
            total += "，运费 ￥ " + hashMap.get("shipping_fee") + " 元";
        } else {
            total += "，免运费";
        }

        holder.infoTextView.setText(Html.fromHtml(total));
        holder.operaTextView.setVisibility(View.GONE);
        holder.optionTextView.setVisibility(View.GONE);

        switch (hashMap.get("order_state")) {
            case "0":
                holder.optionTextView.setText("订单详细");
                holder.optionTextView.setVisibility(View.VISIBLE);
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDetailed(hashMap.get("order_id"));
                    }
                });
                break;
            case "10":
                holder.operaTextView.setText("取消订单");
                holder.optionTextView.setText("修改价格");
                holder.operaTextView.setVisibility(View.VISIBLE);
                holder.optionTextView.setVisibility(View.VISIBLE);
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderCancel(hashMap.get("order_id"), hashMap.get("order_sn"));
                    }
                });
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderModify(hashMap.get("order_id"), hashMap.get("order_sn"), hashMap.get("order_amount"), hashMap.get("shipping_fee"));
                    }
                });
                break;
            case "20":
                holder.operaTextView.setText("订单详细");
                holder.optionTextView.setText("设置发货");
                holder.operaTextView.setVisibility(View.VISIBLE);
                holder.optionTextView.setVisibility(View.VISIBLE);
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDetailed(hashMap.get("order_id"));
                    }
                });
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDeliver(hashMap.get("order_id"));
                    }
                });
                break;
            case "30":
                holder.operaTextView.setText("订单详细");
                holder.optionTextView.setText("查看物流");
                holder.operaTextView.setVisibility(View.VISIBLE);
                holder.optionTextView.setVisibility(View.VISIBLE);
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDetailed(hashMap.get("order_id"));
                    }
                });
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startLogisticsSeller(mActivity, hashMap.get("order_id"), hashMap.get("buyer_id"));
                    }
                });
                break;
            case "40":
                holder.operaTextView.setText("订单详细");
                holder.optionTextView.setText("查看物流");
                holder.operaTextView.setVisibility(View.VISIBLE);
                holder.optionTextView.setVisibility(View.VISIBLE);
                holder.operaTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDetailed(hashMap.get("order_id"));
                    }
                });
                holder.optionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startLogisticsSeller(mActivity, hashMap.get("order_id"), hashMap.get("buyer_id"));
                    }
                });
                break;
            default:
                break;
        }

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDetailed(hashMap.get("order_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_seller_order, group, false);
        return new ViewHolder(view);
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

    //订单修改价格
    private void orderModify(String order_id, String order_sn, String order_price, String order_ship) {

        Intent intent = new Intent(mActivity, SellerOrderModifyActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_sn", order_sn);
        intent.putExtra("order_ship", order_ship);
        intent.putExtra("order_price", order_price);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_MODIFY);

    }

    //订单取消
    private void orderCancel(String order_id, String order_sn) {

        Intent intent = new Intent(mActivity, SellerOrderCancelActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_sn", order_sn);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_CANCEL);

    }

    //订单详细
    private void orderDetailed(String order_id) {
        Intent intent = new Intent(mActivity, SellerOrderDetailedActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_DETAILED);
    }

    //订单发货
    private void orderDeliver(String order_id) {
        Intent intent = new Intent(mActivity, SellerOrderDeliverActivity.class);
        intent.putExtra("order_id", order_id);
        mApplication.startActivity(mActivity, intent, NcApplication.CODE_SELLER_ORDER_DELIVER);
    }

}