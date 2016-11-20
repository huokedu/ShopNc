package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.seller.SellerRefundDetailedActivity;
import top.yokey.nsg.activity.seller.SellerRefundHandlerActivity;

public class SellerRefundListAdapter extends RecyclerView.Adapter<SellerRefundListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public SellerRefundListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mApplication = application;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        holder.usernameTextView.setText("用户：");
        holder.usernameTextView.append(hashMap.get("buyer_name"));

        holder.handlerTextView.setVisibility(View.GONE);
        if (hashMap.get("seller_state").equals("1")) {
            holder.stateTextView.setText("商家：待处理");
            holder.handlerTextView.setVisibility(View.VISIBLE);
        } else if (hashMap.get("seller_state").equals("2")) {
            holder.stateTextView.setText("商家：已同意");
            if (!hashMap.get("admin_time").equals("0")) {
                holder.stateTextView.setText("平台：已确认");
            }
        } else if (hashMap.get("seller_state").equals("3")) {
            holder.stateTextView.setText("商家：已拒绝");
        }

        holder.orderSNTextView.setText("订单编号：");
        holder.orderSNTextView.append(hashMap.get("order_sn"));
        holder.refundSNTextView.setText("退款编号：");
        holder.refundSNTextView.append(hashMap.get("refund_sn"));
        holder.moneyTextView.setText("￥ ");
        holder.moneyTextView.append(hashMap.get("refund_amount"));
        holder.moneyTextView.append("元");

        holder.handlerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SellerRefundHandlerActivity.class);
                intent.putExtra("refund_id", hashMap.get("refund_id"));
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        holder.detailedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SellerRefundDetailedActivity.class);
                intent.putExtra("refund_id", hashMap.get("refund_id"));
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SellerRefundDetailedActivity.class);
                intent.putExtra("refund_id", hashMap.get("refund_id"));
                mApplication.startActivityLoginSuccess(mActivity, intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_seller_refund, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private TextView usernameTextView;
        private TextView stateTextView;
        private TextView orderSNTextView;
        private TextView refundSNTextView;
        private TextView moneyTextView;
        private TextView handlerTextView;
        private TextView detailedTextView;

        private ViewHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view.findViewById(R.id.mainLinearLayout);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            stateTextView = (TextView) view.findViewById(R.id.stateTextView);
            orderSNTextView = (TextView) view.findViewById(R.id.orderSNTextView);
            refundSNTextView = (TextView) view.findViewById(R.id.refundSNTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            handlerTextView = (TextView) view.findViewById(R.id.handlerTextView);
            detailedTextView = (TextView) view.findViewById(R.id.detailedTextView);

        }

    }

}