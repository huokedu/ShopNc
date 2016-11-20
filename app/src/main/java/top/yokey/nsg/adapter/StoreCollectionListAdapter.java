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

import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class StoreCollectionListAdapter extends RecyclerView.Adapter<StoreCollectionListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public StoreCollectionListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
        holder.collectionTextView.setText(hashMap.get("store_collect"));
        holder.goodsTextView.setText(hashMap.get("goods_count"));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startStore(mActivity, hashMap.get("store_id"));
            }
        });

        holder.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "删除这个收藏",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                                ajaxParams.putAct("member_favorites_store");
                                ajaxParams.putOp("favorites_del");
                                ajaxParams.put("store_id", hashMap.get("store_id"));
                                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        super.onSuccess(o);
                                        if (TextUtil.isJson(o.toString())) {
                                            String error = mApplication.getJsonError(o.toString());
                                            if (TextUtil.isEmpty(error)) {
                                                String data = mApplication.getJsonData(o.toString());
                                                if (data.equals("1")) {
                                                    mApplication.userHashMap.put("favorites_store", Integer.parseInt(mApplication.userHashMap.get("favorites_store")) - 1 + "");
                                                    mArrayList.remove(holder.getAdapterPosition());
                                                    ToastUtil.showSuccess(mActivity);
                                                    notifyDataSetChanged();
                                                } else {
                                                    ToastUtil.showFailure(mActivity);
                                                }
                                            } else {
                                                ToastUtil.showFailure(mActivity);
                                            }
                                        } else {
                                            ToastUtil.showFailure(mActivity);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                                        super.onFailure(t, errorNo, strMsg);
                                        ToastUtil.showFailure(mActivity);
                                        DialogUtil.cancel();
                                    }
                                });
                            }
                        }
                );
                return false;
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_store, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView timeTextView;
        private TextView nameTextView;
        private TextView goodsTextView;
        private TextView collectionTextView;

        private ViewHolder(View view) {
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