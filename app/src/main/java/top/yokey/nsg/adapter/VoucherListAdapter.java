package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;

public class VoucherListAdapter extends RecyclerView.Adapter<VoucherListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public VoucherListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("voucher_t_customimg"), holder.mImageView);

        holder.storeTextView.setText(hashMap.get("store_name"));
        holder.stateTextView.setText(hashMap.get("voucher_state_text"));

        String temp = "有效期至 " + hashMap.get("voucher_end_date_text");
        holder.timeTextView.setText(temp);

        temp = "消费满 " + hashMap.get("voucher_limit") + " 元可用";
        holder.limitTextView.setText(temp);

        temp = "￥ " + hashMap.get("voucher_price");
        holder.moneyTextView.setText(temp);

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startStore(mActivity, hashMap.get("voucher_store_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_voucher, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView storeTextView;
        private TextView timeTextView;
        private TextView limitTextView;
        private TextView moneyTextView;
        private TextView stateTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            storeTextView = (TextView) view.findViewById(R.id.storeTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            limitTextView = (TextView) view.findViewById(R.id.limitTextView);
            moneyTextView = (TextView) view.findViewById(R.id.moneyTextView);
            stateTextView = (TextView) view.findViewById(R.id.stateTextView);

        }

    }

}