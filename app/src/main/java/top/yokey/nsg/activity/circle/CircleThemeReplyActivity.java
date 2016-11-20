package top.yokey.nsg.activity.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CircleThemeReplyActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String answer_id;

    private File imageFile;
    private String imagePath;
    private String imageContent;
    private ImageView backImageView;
    private TextView titleTextView;
    private EditText contentEditText;
    private RelativeLayout mRelativeLayout;
    private ImageView mImageView;
    private TextView replyTextView;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_CHOOSE_PHOTO:
                    imagePath = AndroidUtil.getMediaPath(mActivity, data.getData());
                    mApplication.startPhotoCrop(mActivity, imagePath);
                    break;
                case NcApplication.CODE_CHOOSE_CAMERA:
                    mApplication.startPhotoCrop(mActivity, imagePath);
                    break;
                case NcApplication.CODE_CHOOSE_PHOTO_CROP:
                    imagePath = FileUtil.createJpgByBitmap("circle_theme_create", mApplication.mBitmap);
                    mImageView.setImageBitmap(mApplication.mBitmap);
                    imageFile = new File(imagePath);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_theme_reply);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        mImageView = (ImageView) findViewById(R.id.mainImageView);
        replyTextView = (TextView) findViewById(R.id.replyTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        imageFile = null;
        imagePath = "null";
        imageContent = "null";
        answer_id = mActivity.getIntent().getStringExtra("answer_id");
        String hint = "回复主题：" + CircleThemeDetailedActivity.theme_name;
        if (!TextUtil.isEmpty(answer_id)) {
            hint = hint + " ( #" + answer_id + "楼 )";
        }

        titleTextView.setText("回复主题");
        contentEditText.setHint(hint);

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.image(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        imageFile = new File(FileUtil.getImagePath() + "circle_theme_create.jpg");
                        imagePath = imageFile.getAbsolutePath();
                        mApplication.startCamera(mActivity, imageFile);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.startPhoto(mActivity);
                    }
                });
            }
        });

        replyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFile == null) {
                    saveReply();
                } else {
                    uploadFile();
                }
            }
        });

    }

    private void saveReply() {

        String content = contentEditText.getText().toString();

        if (TextUtil.isEmpty(content)) {
            ToastUtil.show(mActivity, "內容不能为空");
            return;
        }

        if (!TextUtil.isEmpty(imageContent)) {
            content = content + imageContent;
        }

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("theme_reply");
        ajaxParams.put("theme_id", CircleThemeDetailedActivity.theme_id);
        ajaxParams.put("circle_id", CircleDetailedActivity.circle_id);
        ajaxParams.put("member_id", CircleThemeDetailedActivity.member_id);
        ajaxParams.put("member_name", CircleThemeDetailedActivity.member_name);
        ajaxParams.put("replycontent", content);
        ajaxParams.put("answer_id", answer_id);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.showSuccess(mActivity);
                    mApplication.finishActivity(mActivity);
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

    private void uploadFile() {

        DialogUtil.progress(mActivity, "上传图片...");

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("theme_upload");
        ajaxParams.put("circle_id", CircleDetailedActivity.circle_id);
        ajaxParams.put("type", "reply");
        try {
            ajaxParams.put("file", imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            DialogUtil.cancel();
            ToastUtil.show(mActivity, "文件错误，请重新选择");
            return;
        }

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String data = mApplication.getJsonData(o.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        imageContent = "[IMG]" + jsonObject.getString("file_insert") + "[/IMG]";
                        saveReply();
                    } catch (JSONException e) {
                        ToastUtil.showFailure(mActivity);
                        e.printStackTrace();
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

    private void returnActivity() {

        DialogUtil.query(mActivity, "确认您的选择", "取消回复主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.cancel();
                mApplication.finishActivity(mActivity);
            }
        });

    }

}