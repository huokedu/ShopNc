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
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;

public class StoreListListAdapter extends RecyclerView.Adapter<StoreListListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public StoreListListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
        ImageLoader.getInstance().displayImage(hashMap.get("store_avatar_url"), holder.mImageView);
        holder.nameTextView.setText(hashMap.get("store_name"));
        holder.timeTextView.setText(TimeUtil.decode(hashMap.get("fav_time_text")));
        if (TextUtil.isEmpty(hashMap.get("store_collect"))) {
            holder.collectionTextView.setText("0");
        } else {
            holder.collectionTextView.setText(hashMap.get("store_collect"));
        }
        if (TextUtil.isEmpty(hashMap.get("goods_count"))) {
            holder.goodsTextView.setText("0");
        } else {
            holder.goodsTextView.setText(hashMap.get("goods_count"));
        }

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startStore(mActivity, hashMap.get("store_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_store, group, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRelativeLayout;
        public ImageView mImageView;
        public TextView timeTextView;
        public TextView nameTextView;
        public TextView goodsTextView;
        public TextView collectionTextView;

        public ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            goodsTextView = (TextView) view.findViewById(R.id.goodsTextView);
            collectionTextView = (TextView) view.findViewById(R.id.collectionTextView);

        }

    }

}