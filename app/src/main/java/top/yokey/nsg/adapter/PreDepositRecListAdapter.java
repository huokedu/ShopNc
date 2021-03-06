package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.utility.DialogUtil;

public class PreDepositRecListAdapter extends RecyclerView.Adapter<PreDepositRecListAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<HashMap<String, String>> mArrayList;

    public PreDepositRecListAdapter(Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        String status = hashMap.get("pdr_payment_state");
        if (status.equals("1")) {
            String temp = hashMap.get("pdr_payment_name") + "：" + hashMap.get("pdr_payment_state_text");
            holder.statusTextView.setText(temp);
        } else {
            holder.statusTextView.setText(hashMap.get("pdr_payment_state_text"));
        }

        String money = "￥ " + hashMap.get("pdr_amount");
        holder.moneyTextView.setText(money);

        final String sn = "编号：" + hashMap.get("pdr_sn");
        holder.snTextView.setText(sn);

        holder.timeTextView.setText(hashMap.get("pdr_add_time_text"));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "充值明细",
                        sn,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                            }
                        }
                );
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_pre_deposit_rec, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView statusTextView;
        private TextView moneyTextView;
        private TextView snTextView;
        private TextView timeTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            statusTextView = (TextView) view.findViewById(R.id.statusTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            snTextView = (TextView) view.findViewById(R.id.snTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);

        }

    }

}