package top.yokey.nsg.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;

public class GoodsBuyListAdapter extends RecyclerView.Adapter<GoodsBuyListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsBuyListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.mArrayList = arrayList;
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
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url"), holder.mImageView);
        holder.nameTextView.setText(hashMap.get("goods_name"));
        String info = "￥ <font color='#FF5001'>" + goods_price + "</font><br>";
        info = info + "x <font color='#FF5001'>" + goods_num + "</font><br>";
        info = info + "共 <font color='#FF5001'>" + (goods_price * goods_num) + "</font>";
        holder.infoTextView.setText(Html.fromHtml(info));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods_buy, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView nameTextView;
        private TextView infoTextView;

        public ViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);

        }

    }

}