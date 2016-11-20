package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.goods.GoodsListActivity;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.TextUtil;

public class Category2ListAdapter extends RecyclerView.Adapter<Category2ListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public Category2ListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.titleTextView.setText(hashMap.get("gc_name"));

        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mActivity, GoodsListActivity.class);
                intent.putExtra("type", "category");
                intent.putExtra("keyword", hashMap.get("gc_id"));
                mApplication.startActivity(mActivity, intent);
            }
        });

        for (int i = 0; i < holder.contentTextView.length; i++) {
            holder.contentTextView[i].setText("");
        }

        if (hashMap.get("gc_class3").equals("null")) {
            mApplication.mFinalHttp.get(mApplication.apiUrlString + "act=goods_class&gc_id=" + hashMap.get("gc_id"), new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    if (TextUtil.isJson(o.toString())) {
                        try {
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            hashMap.put("gc_class3", jsonObject.getString("class_list"));
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            try {
                JSONArray jsonArray = new JSONArray(hashMap.get("gc_class3"));
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        final String gc_id = jsonObject.getString("gc_id");
                        holder.contentTextView[i].setText(Html.fromHtml(jsonObject.getString("gc_name")));
                        holder.contentTextView[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(mActivity, GoodsListActivity.class);
                                intent.putExtra("type", "category");
                                intent.putExtra("keyword", gc_id);
                                mApplication.startActivity(mActivity, intent);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < holder.contentTextView.length; i++) {
            if (holder.contentTextView[i].getText().toString().length() == 0) {
                holder.contentTextView[i].setVisibility(View.GONE);
            } else {
                holder.contentTextView[i].setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_category2, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView[] contentTextView;

        private ViewHolder(View view) {
            super(view);

            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            contentTextView = new TextView[51];
            for (int i = 0; i < contentTextView.length; i++) {
                contentTextView[i] = (TextView) view.findViewById(R.id.content1TextView + i);
            }

        }

    }

}