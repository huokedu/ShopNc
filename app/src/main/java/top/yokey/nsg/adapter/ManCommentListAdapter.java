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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;

public class ManCommentListAdapter extends RecyclerView.Adapter<ManCommentListAdapter.ViewHolder> {

    private Activity mActivity;
    private NcApplication mApplication;
    private onItemClickListener itemClickListener;
    private ArrayList<HashMap<String, String>> mArrayList;

    public ManCommentListAdapter(NcApplication application, Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mApplication = application;
        this.itemClickListener = null;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final HashMap<String, String> hashMap = mArrayList.get(position);

        try {

            if (hashMap.get("comment_uid").equals("0")) {

                holder.nicknameTextView.setText("管理员");
                holder.genderImageView.setImageResource(R.mipmap.ic_default_boy);
                holder.timeTextView.setText("发表于 ");
                holder.timeTextView.append(TimeUtil.decode(hashMap.get("comment_time")));
                holder.contentTextView.setText(Html.fromHtml(hashMap.get("comment_content"), mApplication.mImageGetter, null));

            } else {

                JSONObject jsonObject = new JSONObject(hashMap.get("user_info"));

                if (TextUtil.isEmpty(jsonObject.getString("member_avatar"))) {
                    holder.avatarImageView.setImageResource(R.mipmap.ic_avatar);
                } else {
                    ImageLoader.getInstance().displayImage(jsonObject.getString("member_avatar"), holder.avatarImageView);
                }

                holder.nicknameTextView.setText(jsonObject.getString("member_name"));

                if (jsonObject.getString("member_sex").equals("1")) {
                    holder.genderImageView.setImageResource(R.mipmap.ic_default_boy);
                } else {
                    holder.genderImageView.setImageResource(R.mipmap.ic_default_girl);
                }

                holder.timeTextView.setText("发表于 ");
                holder.timeTextView.append(TimeUtil.decode(hashMap.get("comment_time")));
                holder.contentTextView.setText(Html.fromHtml(hashMap.get("comment_content"), mApplication.mImageGetter, null));

            }

            if (!hashMap.get("comment_rid").equals("-1")) {
                String name;
                holder.replyTextView.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject(hashMap.get("comment_reply"));
                if (jsonObject.getString("comment_uid").equals("0")) {
                    name = "管理员";
                } else {
                    name = jsonObject.getString("member_name");
                }
                String content = "回复@" + name + "：" + jsonObject.getString("comment_content");
                holder.replyTextView.setText(Html.fromHtml(content, mApplication.mImageGetter, null));
            } else {
                holder.replyTextView.setVisibility(View.GONE);
            }

            holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(hashMap.get("comment_id"), holder.nicknameTextView.getText().toString());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_man_comment, group, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.itemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRelativeLayout;
        private ImageView avatarImageView;
        private TextView nicknameTextView;
        private ImageView genderImageView;
        private TextView timeTextView;
        private TextView contentTextView;
        private TextView replyTextView;

        private ViewHolder(View view) {
            super(view);

            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mainRelativeLayout);
            avatarImageView = (ImageView) view.findViewById(R.id.avatarImageView);
            nicknameTextView = (TextView) view.findViewById(R.id.nicknameTextView);
            genderImageView = (ImageView) view.findViewById(R.id.genderImageView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);
            replyTextView = (TextView) view.findViewById(R.id.replyTextView);

        }

    }

    public interface onItemClickListener {
        void onItemClick(String comment_id, String nick_name);
    }

}