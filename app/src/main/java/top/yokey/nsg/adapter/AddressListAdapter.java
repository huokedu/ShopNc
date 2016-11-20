package top.yokey.nsg.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    private onItemClickListener itemClickListener;
    private onItemLongClickListener itemLongClickListener;
    private ArrayList<HashMap<String, String>> mArrayList;

    public AddressListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.mArrayList = arrayList;
        this.itemClickListener = null;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        holder.nameTextView.setText(hashMap.get("true_name"));

        if (TextUtil.isEmpty(hashMap.get("tel_phone"))) {
            holder.phoneTextView.setText(hashMap.get("mob_phone"));
        } else {
            holder.phoneTextView.setText(hashMap.get("tel_phone"));
        }

        if (hashMap.get("is_default").equals("1")) {
            holder.addressTextView.setText("[默认] ");
            holder.addressTextView.append(hashMap.get("area_info"));
        } else {
            holder.addressTextView.setText(hashMap.get("area_info"));
        }

        holder.addressTextView.append(" ");
        holder.addressTextView.append(hashMap.get("address"));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });

        holder.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    itemLongClickListener.onItemLongClick(holder.getAdapterPosition());
                }
                return false;
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_address, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView nameTextView;
        private TextView phoneTextView;
        private TextView addressTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
            addressTextView = (TextView) view.findViewById(R.id.addressTextView);

        }

    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemLongClickListener(onItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public interface onItemLongClickListener {
        void onItemLongClick(int position);
    }

}