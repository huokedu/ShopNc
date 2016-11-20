package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.activity.man.ManDetailedActivity;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class ManListAdapter extends RecyclerView.Adapter<ManListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public ManListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.titleTextView.setText(hashMap.get("man_title"));
        holder.clickTextView.setText(hashMap.get("man_click"));
        holder.praiseTextView.setText(hashMap.get("man_praise"));
        holder.shareTextView.setText(hashMap.get("man_share"));

        if (hashMap.get("is_praise").equals("0")) {
            holder.praiseTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.mipmap.ic_man_praise), null, null, null);
        } else {
            holder.praiseTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.mipmap.ic_man_praise_press), null, null, null);
        }

        ImageLoader.getInstance().displayImage(hashMap.get("man_image"), holder.mImageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.mImageView.setImageBitmap(loadedImage);
                holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        });

        holder.praiseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmpty(mApplication.userKeyString)) {
                    mApplication.startLogin(mActivity);
                } else {
                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                    ajaxParams.putAct("man_praise");
                    ajaxParams.putOp("praise");
                    ajaxParams.put("id", hashMap.get("man_id"));
                    ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                            if (mApplication.getJsonSuccess(o.toString())) {
                                int praise = Integer.parseInt(hashMap.get("man_praise")) + 1;
                                hashMap.put("man_praise", praise + "");
                                hashMap.put("is_praise", "1");
                                notifyDataSetChanged();
                            } else {
                                ToastUtil.show(mActivity, mApplication.getJsonError(o.toString()));
                            }
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            ToastUtil.showFailureNetwork(mActivity);
                        }
                    });
                }
            }
        });

        holder.shareTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "文章分享");
                intent.putExtra("name", hashMap.get("man_title"));
                intent.putExtra("jingle", "");
                intent.putExtra("image", hashMap.get("man_image"));
                intent.putExtra("link", mApplication.apiUrlString + "act=man&op=read_share&id=" + hashMap.get("man_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ManDetailedActivity.class);
                intent.putExtra("id", hashMap.get("man_id"));
                intent.putExtra("collection", hashMap.get("man_collection"));
                intent.putExtra("praise", hashMap.get("man_praise"));
                intent.putExtra("comment", hashMap.get("man_comment"));
                intent.putExtra("title", hashMap.get("man_title"));
                intent.putExtra("image", hashMap.get("man_image"));
                intent.putExtra("time", hashMap.get("man_time"));
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_man, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView titleTextView;
        private ImageView mImageView;
        private TextView clickTextView;
        private TextView praiseTextView;
        private TextView shareTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            clickTextView = (TextView) view.findViewById(R.id.clickTextView);
            praiseTextView = (TextView) view.findViewById(R.id.praiseTextView);
            shareTextView = (TextView) view.findViewById(R.id.shareTextView);

        }

    }

}