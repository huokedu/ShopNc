package top.yokey.nsg.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;

public class CircleFriendListAdapter extends RecyclerView.Adapter<CircleFriendListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public CircleFriendListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.avatarImageView.setImageResource(R.mipmap.ic_avatar);
        ImageLoader.getInstance().displayImage(hashMap.get("member_avatar"), holder.avatarImageView);
        holder.nameTextView.setText(hashMap.get("member_name"));
        String join = "<font color='#999999'>" + TimeUtil.decode(TimeUtil.longToTime(hashMap.get("cm_applytime"))) + "</font> 加入";
        holder.timeTextView.setText(Html.fromHtml(join));
        String level = hashMap.get("cm_levelname") + " V" + hashMap.get("cm_level");
        holder.fansTextView.setText(level);
        if (TextUtil.isEmpty(hashMap.get("cm_intro"))) {
            holder.descTextView.setText("暂无简介");
        } else {
            holder.descTextView.setText(hashMap.get("cm_intro"));
        }
        if (hashMap.get("member_id").equals(hashMap.get("circle_masterid"))) {
            holder.mTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mTextView.setVisibility(View.GONE);
        }
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startChat(mActivity, hashMap.get("member_id"));
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_circle_friend, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView timeTextView;
        private TextView fansTextView;
        private TextView descTextView;
        private TextView mTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            avatarImageView = (ImageView) view.findViewById(R.id.avatarImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            fansTextView = (TextView) view.findViewById(R.id.fansTextView);
            descTextView = (TextView) view.findViewById(R.id.descTextView);
            mTextView = (TextView) view.findViewById(R.id.mainTextView);

        }

    }

}