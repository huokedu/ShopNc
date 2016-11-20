package top.yokey.nsg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;

public class RedPacketListAdapter extends RecyclerView.Adapter<RedPacketListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> mArrayList;

    public RedPacketListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.mArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        final HashMap<String, String> hashMap = mArrayList.get(position);

        String temp = "红包活动：" + hashMap.get("packet_name");
        holder.nameTextView.setText(temp);
        temp = "红包金额：" + hashMap.get("packet_price") + " 元";
        holder.moneyTextView.setText(temp);
        holder.stateTextView.setText("红包状态：已领取");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_red_packet, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView moneyTextView;
        private TextView stateTextView;

        private ViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            stateTextView = (TextView) view.findViewById(R.id.stateTextView);

        }

    }

}