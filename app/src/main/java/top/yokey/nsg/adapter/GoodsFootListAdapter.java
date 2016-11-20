package top.yokey.nsg.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
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

public class GoodsFootListAdapter extends RecyclerView.Adapter<GoodsFootListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsFootListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.horLinearLayout.setVisibility(View.GONE);
        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url"), holder.mImageView);
        holder.borderImageView.setVisibility(View.GONE);
        holder.nameTextView.setTextSize(16.0f);
        holder.nameTextView.setText(hashMap.get("goods_name"));
        holder.pricePromotionTextView.setText("￥ ");
        holder.pricePromotionTextView.append(hashMap.get("goods_promotion_price"));
        holder.priceTextView.setText("￥ ");
        holder.priceTextView.append(hashMap.get("goods_marketprice"));
        holder.priceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        holder.verRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startGoods(mActivity, hashMap.get("goods_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout horLinearLayout;
        private RelativeLayout verRelativeLayout;
        private ImageView mImageView;
        private ImageView borderImageView;
        private TextView nameTextView;
        private TextView pricePromotionTextView;
        private TextView priceTextView;

        private ViewHolder(View view) {
            super(view);

            horLinearLayout = (LinearLayout) view.findViewById(R.id.horLinearLayout);
            verRelativeLayout = (RelativeLayout) view.findViewById(R.id.verRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.verImageView);
            borderImageView = (ImageView) view.findViewById(R.id.verBorderImageView);
            nameTextView = (TextView) view.findViewById(R.id.verNameTextView);
            pricePromotionTextView = (TextView) view.findViewById(R.id.verPricePromotionTextView);
            priceTextView = (TextView) view.findViewById(R.id.verPriceTextView);

        }

    }

}