package top.yokey.nsg.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.utility.TimeUtil;

public class PointsListAdapter extends RecyclerView.Adapter<PointsListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> mArrayList;

    public PointsListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.mArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        String time = TimeUtil.decode(TimeUtil.longToTime(hashMap.get("pl_addtime")));
        String temp = time + " : " + hashMap.get("pl_desc") + "<font color='#FF0000'> + " + hashMap.get("pl_points") + " </font> ";
        holder.mTextView.setText(Html.fromHtml(temp));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_points, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        private ViewHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.mainTextView);

        }

    }

}