package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.order.BuySetup2Activity;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.TextUtil;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public OrderListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        try {

            JSONArray jsonArray = new JSONArray(hashMap.get("order_list"));
            ArrayList<HashMap<String, String>> storeArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                storeArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
            }

            holder.mListView.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.mListView.setAdapter(new StoreOrderListAdapter(mApplication, mActivity, storeArrayList));
            holder.payTextView.setText("去支付 ￥ ");

            if (!TextUtil.isEmpty(hashMap.get("pay_amount"))) {
                holder.payTextView.setVisibility(View.VISIBLE);
                holder.payTextView.append(hashMap.get("pay_amount"));
                holder.payTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, BuySetup2Activity.class);
                        intent.putExtra("pay_sn", hashMap.get("pay_sn"));
                        intent.putExtra("payment_code", "online");
                        mApplication.startActivity(mActivity, intent, NcApplication.CODE_ORDER_PAY);
                    }
                });
            } else {
                holder.payTextView.setVisibility(View.GONE);
                holder.payTextView.setOnClickListener(null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_order, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView mListView;
        private TextView payTextView;

        private ViewHolder(View view) {
            super(view);

            mListView = (RecyclerView) view.findViewById(R.id.mainListView);
            payTextView = (TextView) view.findViewById(R.id.payTextView);

        }

    }

}