package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.circle.CircleDetailedActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;

public class CircleListAdapter extends RecyclerView.Adapter<CircleListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public CircleListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.nameTextView.setText(hashMap.get("circle_name"));
        final String info = "话题 ( " + hashMap.get("circle_thcount") + " )  |  组员 ( " + hashMap.get("circle_mcount") + " )";
        holder.infoTextView.setText(info);
        holder.descTextView.setText(hashMap.get("circle_desc"));

        //圈子图片处理
        holder.mImageView.setImageResource(R.mipmap.ic_default_circle);
        String image = mApplication.circlePicUrlString + hashMap.get("circle_id") + ".jpg";
        ImageLoader.getInstance().displayImage(image, holder.mImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                holder.mImageView.setImageResource(R.mipmap.ic_default_circle);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                holder.mImageView.setImageResource(R.mipmap.ic_default_circle);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                holder.mImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                holder.mImageView.setImageResource(R.mipmap.ic_default_circle);
            }
        });

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CircleDetailedActivity.class);
                intent.putExtra("circle_id", hashMap.get("circle_id"));
                intent.putExtra("circle_name", hashMap.get("circle_name"));
                intent.putExtra("circle_masterid", hashMap.get("circle_masterid"));
                intent.putExtra("circle_joinaudit", hashMap.get("circle_joinaudit"));
                intent.putExtra("circle_info", holder.infoTextView.getText().toString());
                intent.putExtra("circle_desc", holder.descTextView.getText().toString());
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_circle, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView nameTextView;
        private TextView infoTextView;
        private TextView descTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);
            descTextView = (TextView) view.findViewById(R.id.descTextView);

        }

    }

}