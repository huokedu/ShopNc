package top.yokey.nsg.adapter;

import android.app.Activity;
import android.graphics.Paint;
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
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;

public class GoodsCircleListAdapter extends RecyclerView.Adapter<GoodsCircleListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsCircleListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.horImageView[0].setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url_1"), holder.horImageView[0]);
        holder.horNameTextView[0].setText(hashMap.get("goods_name_1"));
        String temp = "￥ " + hashMap.get("goods_price_1");
        holder.horPricePromotionTextView[0].setText(temp);
        temp = "￥ " + hashMap.get("goods_price_1") + " ";
        holder.horPriceTextView[0].setText(temp);
        holder.horPriceTextView[0].getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ImageLoader.getInstance().displayImage(hashMap.get("member_avatar_1"), holder.horAvatarImageView[0]);
        holder.horUsernameTextView[0].setText(hashMap.get("member_name_1"));
        if (hashMap.get("is_share_1").equals("1")) {
            temp = TimeUtil.decode(TimeUtil.longToTime(hashMap.get("share_time_1"))) + " 分享";
        } else {
            temp = TimeUtil.decode(TimeUtil.longToTime(hashMap.get("like_time_1"))) + " 喜欢";
        }
        holder.horTimeTextView[0].setText(temp);
        if (TextUtil.isEmpty(hashMap.get("share_content_1"))) {
            holder.horContentTextView[0].setText("暂无内容");
        } else {
            holder.horContentTextView[0].setText(hashMap.get("share_content_1"));
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
            temp = "￥ " + hashMap.get("goods_price_2");
            holder.horPricePromotionTextView[1].setText(temp);
            temp = "￥ " + hashMap.get("goods_price_2") + " ";
            holder.horPriceTextView[1].setText(temp);
            holder.horPriceTextView[1].getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ImageLoader.getInstance().displayImage(hashMap.get("member_avatar_2"), holder.horAvatarImageView[1]);
            holder.horUsernameTextView[1].setText(hashMap.get("member_name_2"));
            if (hashMap.get("is_share_2").equals("1")) {
                temp = TimeUtil.decode(TimeUtil.longToTime(hashMap.get("share_time_2"))) + " 分享";
            } else {
                temp = TimeUtil.decode(TimeUtil.longToTime(hashMap.get("like_time_2"))) + " 喜欢";
            }
            holder.horTimeTextView[1].setText(temp);
            if (TextUtil.isEmpty(hashMap.get("share_content_2"))) {
                holder.horContentTextView[1].setText("暂无内容");
            } else {
                holder.horContentTextView[1].setText(hashMap.get("share_content_2"));
            }
            holder.horRelativeLayout[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mApplication.startGoods(mActivity, hashMap.get("goods_id_2"));
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods_circle, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout[] horRelativeLayout;
        private ImageView[] horImageView;
        private TextView[] horNameTextView;
        private TextView[] horPricePromotionTextView;
        private TextView[] horPriceTextView;
        private ImageView[] horAvatarImageView;
        private TextView[] horUsernameTextView;
        private TextView[] horTimeTextView;
        private TextView[] horContentTextView;

        private ViewHolder(View view) {
            super(view);

            horRelativeLayout = new RelativeLayout[2];
            horRelativeLayout[0] = (RelativeLayout) view.findViewById(R.id.hor1RelativeLayout);
            horRelativeLayout[1] = (RelativeLayout) view.findViewById(R.id.hor2RelativeLayout);
            horImageView = new ImageView[2];
            horImageView[0] = (ImageView) view.findViewById(R.id.hor1ImageView);
            horImageView[1] = (ImageView) view.findViewById(R.id.hor2ImageView);
            horNameTextView = new TextView[2];
            horNameTextView[0] = (TextView) view.findViewById(R.id.hor1NameTextView);
            horNameTextView[1] = (TextView) view.findViewById(R.id.hor2NameTextView);
            horPricePromotionTextView = new TextView[2];
            horPricePromotionTextView[0] = (TextView) view.findViewById(R.id.hor1PricePromotionTextView);
            horPricePromotionTextView[1] = (TextView) view.findViewById(R.id.hor2PricePromotionTextView);
            horPriceTextView = new TextView[2];
            horPriceTextView[0] = (TextView) view.findViewById(R.id.hor1PriceTextView);
            horPriceTextView[1] = (TextView) view.findViewById(R.id.hor2PriceTextView);
            horAvatarImageView = new ImageView[2];
            horAvatarImageView[0] = (ImageView) view.findViewById(R.id.hor1AvatarImageView);
            horAvatarImageView[1] = (ImageView) view.findViewById(R.id.hor2AvatarImageView);
            horUsernameTextView = new TextView[2];
            horUsernameTextView[0] = (TextView) view.findViewById(R.id.hor1UsernameTextView);
            horUsernameTextView[1] = (TextView) view.findViewById(R.id.hor2UsernameTextView);
            horTimeTextView = new TextView[2];
            horTimeTextView[0] = (TextView) view.findViewById(R.id.hor1TimeTextView);
            horTimeTextView[1] = (TextView) view.findViewById(R.id.hor2TimeTextView);
            horContentTextView = new TextView[2];
            horContentTextView[0] = (TextView) view.findViewById(R.id.hor1ContentTextView);
            horContentTextView[1] = (TextView) view.findViewById(R.id.hor2ContentTextView);

        }

    }

}