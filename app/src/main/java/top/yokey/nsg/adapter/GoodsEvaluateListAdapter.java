package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.PhotoActivity;
import top.yokey.nsg.R;

public class GoodsEvaluateListAdapter extends RecyclerView.Adapter<GoodsEvaluateListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsEvaluateListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.imageLinearLayout.setVisibility(View.GONE);
        for (ImageView imageView : holder.mImageView) {
            imageView.setVisibility(View.GONE);
            imageView.setOnClickListener(null);
        }

        holder.headImageView.setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("member_avatar"), holder.headImageView);
        holder.usernameTextView.setText(hashMap.get("geval_frommembername"));
        holder.timeTextView.setText(hashMap.get("geval_addtime_date"));
        holder.contentTextView.setText(hashMap.get("geval_content"));

        final String geval_image_1024 = hashMap.get("geval_image_1024").replace("\"", "").replace("[", "").replace("]", "").replace("\\", "");
        String geval_image_240 = hashMap.get("geval_image_240");

        try {
            JSONArray jsonArray = new JSONArray(geval_image_240);
            if (jsonArray.length() != 0) {
                if (!jsonArray.toString().contains("default_goods_image_240")) {
                    holder.imageLinearLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i < 5) {
                            final int pos = i;
                            holder.mImageView[i].setVisibility(View.VISIBLE);
                            ImageLoader.getInstance().displayImage(jsonArray.getString(i), holder.mImageView[i]);
                            holder.mImageView[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mActivity, PhotoActivity.class);
                                    intent.putExtra("title", "评价图片");
                                    intent.putExtra("position", pos);
                                    intent.putExtra("image", geval_image_1024);
                                    mApplication.startActivity(mActivity, intent);
                                }
                            });
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (hashMap.get("geval_scores")) {
            case "1":
                holder.starImageView.setImageResource(R.mipmap.ic_star_one);
                break;
            case "2":
                holder.starImageView.setImageResource(R.mipmap.ic_star_two);
                break;
            case "3":
                holder.starImageView.setImageResource(R.mipmap.ic_star_thr);
                break;
            case "4":
                holder.starImageView.setImageResource(R.mipmap.ic_star_fou);
                break;
            case "5":
                holder.starImageView.setImageResource(R.mipmap.ic_star_fiv);
                break;
            default:
                holder.starImageView.setImageResource(R.mipmap.ic_star_fiv);
                break;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods_evaluate, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView headImageView;
        private TextView usernameTextView;
        private TextView timeTextView;
        private ImageView starImageView;
        private TextView contentTextView;
        private LinearLayout imageLinearLayout;
        private ImageView[] mImageView;

        private ViewHolder(View view) {
            super(view);

            headImageView = (ImageView) view.findViewById(R.id.headImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            starImageView = (ImageView) view.findViewById(R.id.starImageView);
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);

            imageLinearLayout = (LinearLayout) view.findViewById(R.id.imageLinearLayout);
            mImageView = new ImageView[5];
            mImageView[0] = (ImageView) view.findViewById(R.id.oneImageView);
            mImageView[1] = (ImageView) view.findViewById(R.id.twoImageView);
            mImageView[2] = (ImageView) view.findViewById(R.id.thrImageView);
            mImageView[3] = (ImageView) view.findViewById(R.id.fouImageView);
            mImageView[4] = (ImageView) view.findViewById(R.id.fivImageView);

        }

    }

}