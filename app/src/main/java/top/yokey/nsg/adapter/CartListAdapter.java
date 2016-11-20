package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private onTextWatcherListener mTextWatcher;
    private onDelClickListener mDelClickListener;
    private onCheckClickListener mCheckClickListener;
    private ArrayList<HashMap<String, String>> mArrayList;

    public CartListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mTextWatcher = null;
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mDelClickListener = null;
        this.mCheckClickListener = null;
        this.mApplication = application;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        //数据处理
        int goods_storage_temp;
        final int goods_storage;
        final String cart_id = hashMap.get("cart_id");
        try {
            goods_storage_temp = Integer.parseInt(hashMap.get("goods_storage"));
        } catch (Exception e) {
            e.printStackTrace();
            goods_storage_temp = 0;
        }
        goods_storage = goods_storage_temp;

        //是否选择
        if (hashMap.get("click").equals("1")) {
            holder.chooseCheckBox.setChecked(true);
        } else {
            holder.chooseCheckBox.setChecked(false);
        }

        //店铺名称
        if (hashMap.get("show_store").equals("1")) {
            holder.storeTextView.setVisibility(View.VISIBLE);
            holder.storeTextView.setText(hashMap.get("store_name"));
        } else {
            holder.storeTextView.setVisibility(View.GONE);
        }

        //商品显示
        ImageLoader.getInstance().displayImage(hashMap.get("goods_image_url"), holder.goodsImageView);
        holder.goodsNameTextView.setText(hashMap.get("goods_name"));
        holder.priceTextView.setText("￥");
        holder.priceTextView.append(hashMap.get("goods_price"));
        holder.numberEditText.setText(hashMap.get("goods_num"));

        //线条
        if (hashMap.get("show_line").equals("1")) {
            holder.lineView.setVisibility(View.VISIBLE);
        } else {
            holder.lineView.setVisibility(View.GONE);
        }

        //取消选择
        holder.chooseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.chooseCheckBox.isChecked()) {
                    mArrayList.get(holder.getAdapterPosition()).put("click", "1");
                } else {
                    mArrayList.get(holder.getAdapterPosition()).put("click", "0");
                }
                if (mCheckClickListener != null) {
                    mCheckClickListener.onCheckClick();
                }
            }
        });

        //删除购物车的商品
        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "删除这个商品",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtil.cancel();
                                KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                                ajaxParams.putAct("member_cart");
                                ajaxParams.putOp("cart_del");
                                ajaxParams.put("cart_id", hashMap.get("cart_id"));
                                mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        super.onSuccess(o);
                                        if (TextUtil.isJson(o.toString())) {
                                            String error = mApplication.getJsonError(o.toString());
                                            if (TextUtil.isEmpty(error)) {
                                                String data = mApplication.getJsonData(o.toString());
                                                if (data.equals("1")) {
                                                    if (mDelClickListener != null) {
                                                        mDelClickListener.onDelClick();
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
                                    }
                                });
                            }
                        }
                );
            }
        });

        //添加购物数量
        holder.addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(holder.numberEditText.getText().toString());
                if (number < goods_storage) {
                    String numberString = number + 1 + "";
                    holder.numberEditText.setText(numberString);
                    holder.numberEditText.setSelection(numberString.length());
                    mArrayList.get(holder.getAdapterPosition()).put("data", cart_id + "|" + numberString);
                    mArrayList.get(holder.getAdapterPosition()).put("goods_num", numberString);
                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                    ajaxParams.putAct("member_cart");
                    ajaxParams.putOp("cart_edit_quantity");
                    ajaxParams.put("cart_id", hashMap.get("cart_id"));
                    ajaxParams.put("quantity", numberString);
                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);
                    if (mTextWatcher != null) {
                        mTextWatcher.onTextWatcher();
                    }
                }
            }
        });

        //减少购物数量
        holder.subTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(holder.numberEditText.getText().toString());
                if (number > 1) {
                    String numberString = number - 1 + "";
                    holder.numberEditText.setText(numberString);
                    holder.numberEditText.setSelection(numberString.length());
                    mArrayList.get(holder.getAdapterPosition()).put("data", cart_id + "|" + numberString);
                    mArrayList.get(holder.getAdapterPosition()).put("goods_num", numberString);
                    KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
                    ajaxParams.putAct("member_cart");
                    ajaxParams.putOp("cart_edit_quantity");
                    ajaxParams.put("cart_id", hashMap.get("cart_id"));
                    ajaxParams.put("quantity", numberString);
                    mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);
                    if (mTextWatcher != null) {
                        mTextWatcher.onTextWatcher();
                    }
                }
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_cart, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView storeTextView;
        private CheckBox chooseCheckBox;
        private ImageView goodsImageView;
        private TextView goodsNameTextView;
        private TextView priceTextView;
        private ImageView delImageView;
        private TextView subTextView;
        private EditText numberEditText;
        private TextView addTextView;
        private View lineView;

        private ViewHolder(View view) {
            super(view);

            storeTextView = (TextView) view.findViewById(R.id.storeTextView);
            chooseCheckBox = (CheckBox) view.findViewById(R.id.chooseCheckBox);
            goodsImageView = (ImageView) view.findViewById(R.id.goodsImageView);
            goodsNameTextView = (TextView) view.findViewById(R.id.goodsNameTextView);
            priceTextView = (TextView) view.findViewById(R.id.priceTextView);
            delImageView = (ImageView) view.findViewById(R.id.delImageView);
            subTextView = (TextView) view.findViewById(R.id.subTextView);
            numberEditText = (EditText) view.findViewById(R.id.numberEditText);
            addTextView = (TextView) view.findViewById(R.id.addTextView);
            lineView = view.findViewById(R.id.lineView);

        }

    }

    public void setOnTextWatcherListener(onTextWatcherListener textWatcher) {
        this.mTextWatcher = textWatcher;
    }

    public interface onTextWatcherListener {
        void onTextWatcher();
    }

    public void setOnDelClickListener(onDelClickListener clickListener) {
        this.mDelClickListener = clickListener;
    }

    public interface onDelClickListener {
        void onDelClick();
    }

    public void setOnCheckClickListener(onCheckClickListener clickListener) {
        this.mCheckClickListener = clickListener;
    }

    public interface onCheckClickListener {
        void onCheckClick();
    }

}