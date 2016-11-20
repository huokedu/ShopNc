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

public class PreDepositLogListAdapter extends RecyclerView.Adapter<PreDepositLogListAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<HashMap<String, String>> mArrayList;

    public PreDepositLogListAdapter(Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.contentTextView.setText(hashMap.get("lg_desc"));

        String money = hashMap.get("lg_av_amount");
        if (money.contains("-")) {
            money = "- " + money.substring(1, money.length());
        } else {
            money = "+ " + money;
        }
        holder.moneyTextView.setText(money);

        holder.timeTextView.setText(hashMap.get("lg_add_time_text"));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "账户余额",
                        hashMap.get("lg_desc"),
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
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_pre_deposit_log, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView contentTextView;
        private TextView moneyTextView;
        private TextView timeTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);

        }

    }

}