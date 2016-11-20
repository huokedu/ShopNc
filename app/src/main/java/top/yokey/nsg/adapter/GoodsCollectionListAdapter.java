package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import top.yokey.nsg.utility.ToastUtil;

public class GoodsCollectionListAdapter extends RecyclerView.Adapter<GoodsCollectionListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private onItemChange mOnItemChange;
    private ArrayList<HashMap<String, String>> mArrayList;

    public GoodsCollectionListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mApplication = application;
        this.mOnItemChange = null;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final HashMap<String, String> hashMap = mArrayList.get(position);

        holder.horLinearLayout.setVisibility(View.VISIBLE);
        holder.verRelativeLayout.setVisibility(View.GONE);

        holder.horImageView[0].setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url_1"), holder.horImageView[0]);
        holder.horBorderImageView[0].setVisibility(View.GONE);
        holder.horNameTextView[0].setText(hashMap.get("goods_name_1"));
        holder.horPriceTextView[0].setText(hashMap.get("goods_price_1"));

        if (!TextUtil.isEmpty(hashMap.get("fav_id_2"))) {
            holder.horRelativeLayout[1].setVisibility(View.VISIBLE);
            holder.horImageView[1].setImageResource(R.mipmap.ic_launcher);
            ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url_2"), holder.horImageView[1]);
            holder.horBorderImageView[1].setVisibility(View.GONE);
            holder.horNameTextView[1].setText(hashMap.get("goods_name_2"));
            holder.horPriceTextView[1].setText(hashMap.get("goods_price_2"));
        } else {
            holder.horRelativeLayout[1].setVisibility(View.INVISIBLE);
        }

        holder.horRelativeLayout[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startGoods(mActivity, hashMap.get("goods_id_1"));
            }
        });

        holder.horRelativeLayout[0].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                delGoods(hashMap.get("fav_id_1"));
                return false;
            }
        });

        holder.horRelativeLayout[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startGoods(mActivity, hashMap.get("goods_id_2"));
            }
        });

        holder.horRelativeLayout[1].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                delGoods(hashMap.get("fav_id_2"));
                return false;
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout horLinearLayout;
        private RelativeLayout[] horRelativeLayout;
        private ImageView[] horImageView;
        private ImageView[] horBorderImageView;
        private TextView[] horNameTextView;
        private TextView[] horPriceTextView;
        private RelativeLayout verRelativeLayout;

        private ViewHolder(View view) {
            super(view);

            horLinearLayout = (LinearLayout) view.findViewById(R.id.horLinearLayout);
            verRelativeLayout = (RelativeLayout) view.findViewById(R.id.verRelativeLayout);

            horRelativeLayout = new RelativeLayout[2];
            horRelativeLayout[0] = (RelativeLayout) view.findViewById(R.id.hor1RelativeLayout);
            horRelativeLayout[1] = (RelativeLayout) view.findViewById(R.id.hor2RelativeLayout);
            horImageView = new ImageView[2];
            horImageView[0] = (ImageView) view.findViewById(R.id.hor1ImageView);
            horImageView[1] = (ImageView) view.findViewById(R.id.hor2ImageView);
            horBorderImageView = new ImageView[2];
            horBorderImageView[0] = (ImageView) view.findViewById(R.id.hor1BorderImageView);
            horBorderImageView[1] = (ImageView) view.findViewById(R.id.hor2BorderImageView);
            horNameTextView = new TextView[2];
            horNameTextView[0] = (TextView) view.findViewById(R.id.hor1NameTextView);
            horNameTextView[1] = (TextView) view.findViewById(R.id.hor2NameTextView);
            horPriceTextView = new TextView[2];
            horPriceTextView[0] = (TextView) view.findViewById(R.id.hor1PricePromotionTextView);
            horPriceTextView[1] = (TextView) view.findViewById(R.id.hor2PricePromotionTextView);

        }

    }

    public void setOnItemChange(onItemChange itemChange) {
        this.mOnItemChange = itemChange;
    }

    private void delGoods(final String fav_id) {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "删除这个收藏",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_favorites");
                        ajaxParams.putOp("favorites_del");
                        ajaxParams.put("fav_id", fav_id);
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                if (TextUtil.isJson(o.toString())) {
                                    String error = mApplication.getJsonError(o.toString());
                                    if (TextUtil.isEmpty(error)) {
                                        String data = mApplication.getJsonData(o.toString());
                                        if (data.equals("1")) {
                                            mApplication.userHashMap.put("favorites_goods", Integer.parseInt(mApplication.userHashMap.get("favorites_goods")) - 1 + "");
                                            ToastUtil.showSuccess(mActivity);
                                            if (mOnItemChange != null) {
                                                mOnItemChange.onChange();
                                            }
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

    }

    public interface onItemChange {
        void onChange();
    }

}