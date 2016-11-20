package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.ToastUtil;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private onDelClickListener mDelClickListener;
    private onItemClickListener itemClickListener;
    private ArrayList<HashMap<String, String>> mArrayList;

    public InvoiceListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.itemClickListener = null;
        this.mApplication = application;
        this.mDelClickListener = null;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        holder.mTextView.setText(hashMap.get("inv_title"));
        holder.mTextView.append(" : ");
        holder.mTextView.append(hashMap.get("inv_content"));

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });

        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.query(mActivity, "确认您的选择", "删除这个发票信息", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        DialogUtil.progress(mActivity);
                        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                        ajaxParams.putAct("member_invoice");
                        ajaxParams.putOp("invoice_del");
                        ajaxParams.put("inv_id", hashMap.get("inv_id"));
                        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                super.onSuccess(o);
                                DialogUtil.cancel();
                                if (mApplication.getJsonSuccess(o.toString())) {
                                    mArrayList.remove(holder.getAdapterPosition());
                                    ToastUtil.showSuccess(mActivity);
                                    notifyDataSetChanged();
                                    if (mDelClickListener != null) {
                                        mDelClickListener.onDelClick();
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
                });
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_invoice, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView delImageView;

        private ViewHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.mainTextView);
            delImageView = (ImageView) view.findViewById(R.id.delImageView);

        }

    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnDelClickListener(onDelClickListener clickListener) {
        this.mDelClickListener = clickListener;
    }

    public interface onDelClickListener {
        void onDelClick();
    }

}