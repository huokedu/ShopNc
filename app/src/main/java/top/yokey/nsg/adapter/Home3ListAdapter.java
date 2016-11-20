package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.BrowserActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;

public class Home3ListAdapter extends RecyclerView.Adapter<Home3ListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public Home3ListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.mImageView[1].setVisibility(View.VISIBLE);

        holder.mImageView[0].setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("image_1"), holder.mImageView[0]);
        holder.mImageView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (hashMap.get("type_1")) {
                    case "keyword":
                        mApplication.startKeyword(mActivity, hashMap.get("data_1"));
                        break;
                    case "special":
                        mApplication.startSpecial(mActivity, hashMap.get("data_1"));
                        break;
                    case "goods":
                        mApplication.startGoods(mActivity, hashMap.get("data_1"));
                        break;
                    case "url":
                        startUrl(hashMap.get("data_1"));
                        break;
                    default:
                        mApplication.startKeyword(mActivity, "");
                        break;
                }
            }
        });

        if (!TextUtil.isEmpty(hashMap.get("image_2"))) {
            holder.mImageView[1].setImageResource(R.mipmap.ic_launcher);
            ImageLoader.getInstance().displayImage(hashMap.get("image_2"), holder.mImageView[1]);
            holder.mImageView[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (hashMap.get("type_2")) {
                        case "keyword":
                            mApplication.startKeyword(mActivity, hashMap.get("data_2"));
                            break;
                        case "special":
                            mApplication.startSpecial(mActivity, hashMap.get("data_2"));
                            break;
                        case "goods":
                            mApplication.startGoods(mActivity, hashMap.get("data_2"));
                            break;
                        case "url":
                            startUrl(hashMap.get("data_2"));
                            break;
                        default:
                            mApplication.startKeyword(mActivity, "");
                            break;
                    }
                }
            });
        } else {
            holder.mImageView[1].setVisibility(View.INVISIBLE);
            holder.mImageView[1].setOnClickListener(null);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_home3, group, false);
        return new ViewHolder(view);
    }

    private void startUrl(String link) {

        try {
            link = URLDecoder.decode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (link.contains("gc_id")) {
            String gc_id = link.substring(link.lastIndexOf("=") + 1, link.length());
            mApplication.startCategory(mActivity, gc_id);
            return;
        }

        if (link.contains("goods_id")) {
            String goods_id = link.substring(link.lastIndexOf("=") + 1, link.length());
            mApplication.startGoods(mActivity, goods_id);
            return;
        }

        if (link.contains("product_list.html") && !link.contains("?")) {
            mApplication.startKeyword(mActivity, "");
            return;
        }

        if (link.contains("product_list.html") && link.contains("keyword")) {
            mApplication.startKeyword(mActivity, link.substring(link.lastIndexOf("=") + 1, link.length()));
            return;
        }

        Intent intent = new Intent(mActivity, BrowserActivity.class);
        intent.putExtra("model", "normal");
        intent.putExtra("link", link);
        mApplication.startActivity(mActivity, intent);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView[] mImageView;

        private ViewHolder(View view) {
            super(view);

            mImageView = new ImageView[2];
            mImageView[0] = (ImageView) view.findViewById(R.id.oneImageView);
            mImageView[1] = (ImageView) view.findViewById(R.id.twoImageView);

        }

    }

}