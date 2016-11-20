package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.TextUtil;

public class GoodsOrderListAdapter extends RecyclerView.Adapter<GoodsOrderListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsOrderListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
        Double goods_price = Double.parseDouble(hashMap.get("goods_price"));
        int goods_num = Integer.parseInt(hashMap.get("goods_num"));
        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        if (TextUtil.isEmpty(hashMap.get("goods_image_url"))) {
            ImageLoader.getInstance().displayImage(hashMap.get("image_url"), holder.mImageView);
        } else {
            ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url"), holder.mImageView);
        }
        holder.nameTextView.setText(hashMap.get("goods_name"));
        String info = "￥ <font color='#FF5001'>" + goods_price + "</font><br>";
        info = info + "x <font color='#FF5001'>" + goods_num + "</font><br>";
        info = info + "共 <font color='#FF5001'>" + (goods_price * goods_num) + "</font>";
        holder.infoTextView.setText(Html.fromHtml(info));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startGoods(mActivity, hashMap.get("goods_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods_order, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView nameTextView;
        private TextView infoTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);

        }

    }

}

