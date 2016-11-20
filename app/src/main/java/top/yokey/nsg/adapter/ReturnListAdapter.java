package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.order.ReturnDetailedActivity;

public class ReturnListAdapter extends RecyclerView.Adapter<ReturnListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public ReturnListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.storeTextView.setText(hashMap.get("store_name"));

        switch (hashMap.get("seller_state_v")) {
            case "1":
                holder.stateTextView.setText("等待商家确认");
                break;
            case "2":
                holder.stateTextView.setText("商家已确认");
                break;
            case "3":
                holder.stateTextView.setText("商家已拒绝");
                break;
        }

        if (!hashMap.get("admin_state").equals("无")) {
            holder.stateTextView.setText(hashMap.get("admin_state"));
        }

        ImageLoader.getInstance().displayImage(hashMap.get("goods_img_360"), holder.goodsImageView);
        holder.goodsTextView.setText(hashMap.get("goods_name"));

        String info = "退款金额<font color='#FF5001'> ￥ " + hashMap.get("refund_amount")
                + " </font> | 退货数量<font color='#FF5001'> x" + hashMap.get("goods_num") + " </font>";
        holder.infoTextView.setText(Html.fromHtml(info));

        holder.goodsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startGoods(mActivity, hashMap.get("goods_id"));
            }
        });

        holder.detailedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ReturnDetailedActivity.class);
                intent.putExtra("refund_id", hashMap.get("refund_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ReturnDetailedActivity.class);
                intent.putExtra("refund_id", hashMap.get("refund_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_return, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private RelativeLayout goodsRelativeLayout;
        private TextView storeTextView;
        private TextView stateTextView;
        private ImageView goodsImageView;
        private TextView goodsTextView;
        private TextView infoTextView;
        private TextView detailedTextView;

        private ViewHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view.findViewById(R.id.mainLinearLayout);
            goodsRelativeLayout = (RelativeLayout) view.findViewById(R.id.goodsRelativeLayout);
            storeTextView = (TextView) view.findViewById(R.id.storeTextView);
            stateTextView = (TextView) view.findViewById(R.id.stateTextView);
            goodsImageView = (ImageView) view.findViewById(R.id.goodsImageView);
            goodsTextView = (TextView) view.findViewById(R.id.goodsTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);
            detailedTextView = (TextView) view.findViewById(R.id.detailedTextView);

        }

    }

}