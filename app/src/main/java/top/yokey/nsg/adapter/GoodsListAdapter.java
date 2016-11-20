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
import top.yokey.nsg.utility.TextUtil;

public class GoodsListAdapter extends RecyclerView.Adapter<GoodsListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.horLinearLayout.setVisibility(View.GONE);
        holder.verRelativeLayout.setVisibility(View.GONE);

        if (hashMap.get("model").equals("hor")) {
            holder.horLinearLayout.setVisibility(View.VISIBLE);
            holder.horImageView[0].setImageResource(R.mipmap.ic_launcher);
            ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url_1"), holder.horImageView[0]);
            holder.horNameTextView[0].setText(hashMap.get("goods_name_1"));
            String temp = "￥ " + hashMap.get("goods_promotion_price_1");
            holder.horPricePromotionTextView[0].setText(temp);
            temp = "￥ " + hashMap.get("goods_price_1") + " ";
            holder.horPriceTextView[0].setText(temp);
            holder.horPriceTextView[0].getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.horBorderImageView[0].setVisibility(View.VISIBLE);
            if (hashMap.get("group_flag_1").equals("false")) {
                if (hashMap.get("xianshi_flag_1").equals("false")) {
                    holder.horBorderImageView[0].setVisibility(View.GONE);
                } else {
                    holder.horBorderImageView[0].setImageResource(R.mipmap.ic_goods_border_rob);
                }
            } else {
                holder.horBorderImageView[0].setImageResource(R.mipmap.ic_goods_border_group);
            }
            holder.horRelativeLayout[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApplication.startGoods(mActivity, hashMap.get("goods_id_1"));
                }
            });
            if (TextUtil.isEmpty(hashMap.get("goods_id_2"))) {
                holder.horRelativeLayout[1].setVisibility(View.INVISIBLE);
            } else {
                holder.horRelativeLayout[1].setVisibility(View.VISIBLE);
                holder.horImageView[1].setImageResource(R.mipmap.ic_launcher);
                ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url_2"), holder.horImageView[1]);
                holder.horNameTextView[1].setText(hashMap.get("goods_name_2"));
                temp = "￥ " + hashMap.get("goods_promotion_price_2");
                holder.horPricePromotionTextView[1].setText(temp);
                temp = "￥ " + hashMap.get("goods_price_2") + " ";
                holder.horPriceTextView[1].setText(temp);
                holder.horPriceTextView[1].getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.horBorderImageView[1].setVisibility(View.VISIBLE);
                if (hashMap.get("group_flag_2").equals("false")) {
                    if (hashMap.get("xianshi_flag_2").equals("false")) {
                        holder.horBorderImageView[1].setVisibility(View.GONE);
                    } else {
                        holder.horBorderImageView[1].setImageResource(R.mipmap.ic_goods_border_rob);
                    }
                } else {
                    holder.horBorderImageView[1].setImageResource(R.mipmap.ic_goods_border_group);
                }
                holder.horRelativeLayout[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.startGoods(mActivity, hashMap.get("goods_id_2"));
                    }
                });
            }
        } else {
            holder.verRelativeLayout.setVisibility(View.VISIBLE);
            holder.verImageView.setImageResource(R.mipmap.ic_launcher);
            ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url"), holder.verImageView);
            holder.verNameTextView.setText(hashMap.get("goods_name"));
            String temp = "￥ " + hashMap.get("goods_promotion_price");
            holder.verPricePromotionTextView.setText(temp);
            temp = "￥ " + hashMap.get("goods_price") + " ";
            holder.verPriceTextView.setText(temp);
            holder.verPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.verBorderImageView.setVisibility(View.VISIBLE);
            if (hashMap.get("group_flag").equals("false")) {
                if (hashMap.get("xianshi_flag").equals("false")) {
                    holder.verBorderImageView.setVisibility(View.GONE);
                } else {
                    holder.verBorderImageView.setImageResource(R.mipmap.ic_goods_border_rob);
                }
            } else {
                holder.verBorderImageView.setImageResource(R.mipmap.ic_goods_border_group);
            }
            holder.verRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApplication.startGoods(mActivity, hashMap.get("goods_id"));
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout horLinearLayout;
        private RelativeLayout[] horRelativeLayout;
        private ImageView[] horImageView;
        private ImageView[] horBorderImageView;
        private TextView[] horNameTextView;
        private TextView[] horPricePromotionTextView;
        private TextView[] horPriceTextView;

        private RelativeLayout verRelativeLayout;
        private ImageView verImageView;
        private ImageView verBorderImageView;
        private TextView verNameTextView;
        private TextView verPricePromotionTextView;
        private TextView verPriceTextView;

        private ViewHolder(View view) {
            super(view);

            horLinearLayout = (LinearLayout) view.findViewById(R.id.horLinearLayout);
            horRelativeLayout = new RelativeLayout[2];
            horRelativeLayout[0] = (RelativeLayout) view.findViewById(R.id.hor1RelativeLayout);
            horRelativeLayout[1] = (RelativeLayout) view.findViewById(R.id.hor2RelativeLayout);
            horImageView = new ImageView[2];
            horImageView[0] = (ImageView) view.findViewById(R.id.hor1ImageView);
            horImageView[1] = (ImageView) view.findViewById(R.id.hor2ImageView);
            horBorderImageView = new ImageView[2];
            horBorderImageView[0] = (ImageView) view.findViewById(R.id.hor1BorderImageView);
            horBorderImageView[1] = (ImageView) view.findViewById(R.id.hor2BorderImageView);
            horNameTextView = new TextView[2];
            horNameTextView[0] = (TextView) view.findViewById(R.id.hor1NameTextView);
            horNameTextView[1] = (TextView) view.findViewById(R.id.hor2NameTextView);
            horPricePromotionTextView = new TextView[2];
            horPricePromotionTextView[0] = (TextView) view.findViewById(R.id.hor1PricePromotionTextView);
            horPricePromotionTextView[1] = (TextView) view.findViewById(R.id.hor2PricePromotionTextView);
            horPriceTextView = new TextView[2];
            horPriceTextView[0] = (TextView) view.findViewById(R.id.hor1PriceTextView);
            horPriceTextView[1] = (TextView) view.findViewById(R.id.hor2PriceTextView);

            verRelativeLayout = (RelativeLayout) view.findViewById(R.id.verRelativeLayout);
            verImageView = (ImageView) view.findViewById(R.id.verImageView);
            verBorderImageView = (ImageView) view.findViewById(R.id.verBorderImageView);
            verNameTextView = (TextView) view.findViewById(R.id.verNameTextView);
            verPricePromotionTextView = (TextView) view.findViewById(R.id.verPricePromotionTextView);
            verPriceTextView = (TextView) view.findViewById(R.id.verPriceTextView);

        }

    }

}