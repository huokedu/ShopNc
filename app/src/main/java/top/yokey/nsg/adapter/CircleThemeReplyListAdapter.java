package top.yokey.nsg.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.circle.CircleApplyActivity;
import top.yokey.nsg.activity.circle.CircleDetailedActivity;
import top.yokey.nsg.activity.circle.CircleThemeReplyActivity;
import top.yokey.nsg.activity.circle.CircleThemeReplyReportActivity;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CircleThemeReplyListAdapter extends RecyclerView.Adapter<CircleThemeReplyListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private ArrayList<HashMap<String, String>> mArrayList;

    public CircleThemeReplyListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
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

        ControlUtil.setWebView(holder.contentWebView);
        holder.mImageView.setImageResource(R.mipmap.ic_avatar);
        if (!TextUtil.isEmpty(hashMap.get("member_avatar"))) {
            ImageLoader.getInstance().displayImage(hashMap.get("member_avatar"), holder.mImageView);
        }
        holder.nameTextView.setText(hashMap.get("member_name"));
        holder.fansTextView.setText(hashMap.get("cm_levelname"));
        String info = "#" + hashMap.get("reply_id") + " 楼 ";
        if (TextUtil.isEmpty(hashMap.get("reply_addtime"))) {
            info += TimeUtil.getAll();
        } else {
            info += TimeUtil.longToTime(hashMap.get("reply_addtime"));
        }
        holder.infoTextView.setText(info);
        String content = TextUtil.circleToHtml(hashMap.get("reply_content"));
        if (!TextUtil.isEmpty(hashMap.get("reply_replyid"))) {
            content = "<font color='#666666'>回复 #" + hashMap.get("reply_replyid") + " 楼 @" + hashMap.get("reply_replyname") + "：</font>" + content;
        }
        holder.contentWebView.loadDataWithBaseURL(null, TextUtil.encodeHtml(content), "text/html", "UTF-8", null);

        holder.replyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CircleDetailedActivity.applyBoolean) {
                    Intent intent = new Intent(mActivity, CircleThemeReplyActivity.class);
                    if (!TextUtil.isEmpty(hashMap.get("reply_addtime"))) {
                        intent.putExtra("answer_id", hashMap.get("reply_id"));
                    }
                    mApplication.startActivityLoginSuccess(mActivity, intent);
                } else {
                    DialogUtil.query(mActivity, "加入圈子?", "您尚未加入圈子", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            Intent intent = new Intent(mActivity, CircleApplyActivity.class);
                            mApplication.startActivityLoginSuccess(mActivity, intent);
                        }
                    });
                }
            }
        });

        holder.reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CircleDetailedActivity.applyBoolean) {
                    if (!TextUtil.isEmpty(hashMap.get("reply_addtime"))) {
                        Intent intent = new Intent(mActivity, CircleThemeReplyReportActivity.class);
                        intent.putExtra("reply_id", hashMap.get("reply_id"));
                        mApplication.startActivityLoginSuccess(mActivity, intent);
                    } else {
                        ToastUtil.show(mActivity, "不能举报哦...");
                    }
                } else {
                    DialogUtil.query(mActivity, "加入圈子?", "您尚未加入圈子", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            Intent intent = new Intent(mActivity, CircleApplyActivity.class);
                            mApplication.startActivityLoginSuccess(mActivity, intent);
                        }
                    });
                }
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_circle_theme_reply, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView mImageView;
        private TextView nameTextView;
        private TextView fansTextView;
        private TextView infoTextView;
        private WebView contentWebView;
        private TextView replyTextView;
        private TextView reportTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            mImageView = (ImageView) view.findViewById(R.id.mainImageView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            fansTextView = (TextView) view.findViewById(R.id.fansTextView);
            infoTextView = (TextView) view.findViewById(R.id.infoTextView);
            contentWebView = (WebView) view.findViewById(R.id.contentWebView);
            replyTextView = (TextView) view.findViewById(R.id.replyTextView);
            reportTextView = (TextView) view.findViewById(R.id.reportTextView);

        }

    }

}