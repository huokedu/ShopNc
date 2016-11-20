package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
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

import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;

public class Category1ListAdapter extends RecyclerView.Adapter<Category1ListAdapter.ViewHolder> {

    private Activity mActivity;
    private onItemClickListener itemClickListener;
    private ArrayList<HashMap<String, String>> mArrayList;

    public Category1ListAdapter(Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.itemClickListener = null;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final HashMap<String, String> hashMap = mArrayList.get(position);

        holder.mTextView.setText(hashMap.get("gc_name"));

        if (hashMap.get("click").equals("0")) {
            holder.mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.greyAdd));
            if (TextUtil.isEmpty(hashMap.get("image"))) {
                holder.mImageView.setImageResource(R.mipmap.ic_normal_class);
            } else {
                holder.mImageView.setImageResource(R.mipmap.ic_launcher);
                ImageLoader.getInstance().displayImage(hashMap.get("image"), holder.mImageView);
            }
        } else {
            holder.mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.main));
            if (TextUtil.isEmpty(hashMap.get("image"))) {
                holder.mImageView.setImageResource(R.mipmap.ic_normal_class_press);
            } else {
                holder.mImageView.setImageResource(R.mipmap.ic_launcher);
                ImageLoader.getInstance().displayImage(hashMap.get("image"), holder.mImageView);
            }
        }

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(hashMap.get("gc_id"), hashMap.get("gc_name"));
                }
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_category1, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView mTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            mTextView = (TextView) view.findViewById(R.id.mainTextView);

        }

    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(String id, String value);
    }

}