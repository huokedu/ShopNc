package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
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

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerGoodsOfflineListAdapter extends RecyclerView.Adapter<SellerGoodsOfflineListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public SellerGoodsOfflineListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image"), holder.mImageView);
        holder.nameTextView.setText(hashMap.get("goods_name"));
        String temp = "￥ " + hashMap.get("goods_price");
        holder.pricePromotionTextView.setText(temp);
        temp = "库存 " + hashMap.get("goods_storage_sum") + " 件";
        holder.storageTextView.setText(temp);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.show(mActivity, "开发中");
            }
        });

        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(mActivity, "确认您的选择", "上架这个商品", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
                        ajaxParams.putAct("seller_goods");
                        ajaxParams.putOp("goods_show");
                        ajaxParams.put("commonids", hashMap.get("goods_commonid"));
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (mApplication.getJsonSuccess(o.toString())) {
                                    mArrayList.remove(holder.getAdapterPosition());
                                    ToastUtil.showSuccess(mActivity);
                                    notifyDataSetChanged();
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
                });
            }
        });

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(mActivity, "确认您的选择", "删除这个商品", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
                        ajaxParams.putAct("seller_goods");
                        ajaxParams.putOp("goods_drop");
                        ajaxParams.put("commonids", hashMap.get("goods_commonid"));
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (mApplication.getJsonSuccess(o.toString())) {
                                    mArrayList.remove(holder.getAdapterPosition());
                                    ToastUtil.showSuccess(mActivity);
                                    notifyDataSetChanged();
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
                });
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_seller_goods_offline, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView nameTextView;
        private TextView pricePromotionTextView;
        private TextView storageTextView;
        private FloatingActionButton editButton;
        private FloatingActionButton upButton;
        private FloatingActionButton delButton;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            pricePromotionTextView = (TextView) view.findViewById(R.id.pricePromotionTextView);
            storageTextView = (TextView) view.findViewById(R.id.storageTextView);
            editButton = (FloatingActionButton) view.findViewById(R.id.editButton);
            upButton = (FloatingActionButton) view.findViewById(R.id.upButton);
            delButton = (FloatingActionButton) view.findViewById(R.id.delButton);

        }

    }

}