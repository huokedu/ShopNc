package top.yokey.nsg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;

public class RechargeCardListAdapter extends RecyclerView.Adapter<RechargeCardListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> mArrayList;

    public RechargeCardListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.mArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        String description = hashMap.get("description");

        holder.statusTextView.setText(description.substring(0, description.indexOf("，")));
        String temp = "SN" + description.substring(description.indexOf(":"), description.length());
        holder.snTextView.setText(temp);

        String money = "￥ " + hashMap.get("available_amount");
        holder.moneyTextView.setText(money);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_recharge_card, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView statusTextView;
        private TextView moneyTextView;
        private TextView snTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            statusTextView = (TextView) view.findViewById(R.id.statusTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            snTextView = (TextView) view.findViewById(R.id.snTextView);

        }

    }

}