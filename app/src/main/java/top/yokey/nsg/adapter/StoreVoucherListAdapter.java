package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class StoreVoucherListAdapter extends RecyclerView.Adapter<StoreVoucherListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public StoreVoucherListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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
        ImageLoader.getInstance().displayImage(hashMap.get("voucher_t_customimg"), holder.mImageView);
        String temp = hashMap.get("voucher_t_title") + "，<font color='#FF0000'>￥ " + hashMap.get("voucher_t_price") + " 元</font>";
        holder.mTextView.setText(Html.fromHtml(temp));

        temp = "需消费 " + hashMap.get("voucher_t_limit") + " 元使用";
        holder.infoTextView.setText(temp);

        temp = hashMap.get("voucher_t_end_date_text") + " 前可用";
        holder.timeTextView.setText(temp);

        holder.getTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                ajaxParams.putAct("member_voucher");
                ajaxParams.putOp("voucher_freeex");
                ajaxParams.put("tid", hashMap.get("voucher_t_id"));
                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (!TextUtil.isEmpty(o.toString())) {
                            String data = mApplication.getJsonData(o.toString());
                            if (data.contains("error")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    ToastUtil.show(mActivity, jsonObject.getString("error"));
                                } catch (JSONException e) {
                                    ToastUtil.show(mActivity, data);
                                    e.printStackTrace();
                                }
                            } else {
                                ToastUtil.show(mActivity, data);
                            }
                        } else {
                            ToastUtil.showFailure(mActivity);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ToastUtil.showFailure(mActivity);
                    }
                });
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_store_voucher, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;
        private TextView infoTextView;
        private TextView timeTextView;
        private TextView getTextView;

        private ViewHolder(View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            mTextView = (TextView) view.findViewById(R.id.mainTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            getTextView = (TextView) view.findViewById(R.id.getTextView);

        }

    }

}