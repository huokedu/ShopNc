package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.circle.CircleThemeDetailedActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;

public class CircleThemeListAdapter extends RecyclerView.Adapter<CircleThemeListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public CircleThemeListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        holder.nameTextView.setText(hashMap.get("theme_name"));
        holder.timeTextView.setText(TimeUtil.decode(TimeUtil.longToTime(hashMap.get("theme_addtime"))));
        holder.contentTextView.setText(hashMap.get("theme_content"));
        holder.browserTextView.setText(hashMap.get("theme_browsecount"));
        holder.commentTextView.setText(hashMap.get("theme_commentcount"));
        holder.likeTextView.setText(hashMap.get("theme_likecount"));
        if (TextUtil.isEmpty(hashMap.get("lastspeak_name"))) {
            holder.speakTextView.setText("暂无回复");
        } else {
            holder.speakTextView.setText(hashMap.get("lastspeak_name"));
        }

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CircleThemeDetailedActivity.class);
                intent.putExtra("theme_id", hashMap.get("theme_id"));
                intent.putExtra("theme_name", hashMap.get("theme_name"));
                intent.putExtra("theme_content", hashMap.get("theme_content"));
                intent.putExtra("theme_author", hashMap.get("member_name"));
                intent.putExtra("theme_browser", hashMap.get("theme_browsecount"));
                intent.putExtra("theme_like", hashMap.get("theme_likecount"));
                intent.putExtra("theme_comment", hashMap.get("theme_commentcount"));
                intent.putExtra("member_id", hashMap.get("member_id"));
                intent.putExtra("member_name", hashMap.get("member_name"));
                intent.putExtra("member_avatar", hashMap.get("member_avatar"));
                mApplication.startActivity(mActivity, intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_circle_theme, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private TextView nameTextView;
        private TextView timeTextView;
        private TextView contentTextView;
        private TextView speakTextView;
        private TextView browserTextView;
        private TextView commentTextView;
        private TextView likeTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);
            speakTextView = (TextView) view.findViewById(R.id.speakTextView);
            browserTextView = (TextView) view.findViewById(R.id.browserTextView);
            commentTextView = (TextView) view.findViewById(R.id.commentTextView);
            likeTextView = (TextView) view.findViewById(R.id.likeTextView);

        }

    }

}