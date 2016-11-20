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

public class CircleCreateActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private File imageFile;
    private String imagePath;

    private ImageView backImageView;
    private TextView titleTextView;

    private EditText nameEditText;
    private EditText tagEditText;
    private EditText pursuerEditText;
    private EditText descEditText;

    private RelativeLayout mRelativeLayout;
    private ImageView mImageView;

    private TextView createTextView;

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
                    imagePath = FileUtil.createJpgByBitmap("circle_create", mApplication.mBitmap);
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
        setContentView(R.layout.activity_circle_create);
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

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        tagEditText = (EditText) findViewById(R.id.tagEditText);
        pursuerEditText = (EditText) findViewById(R.id.pursuerEditText);
        descEditText = (EditText) findViewById(R.id.descEditText);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        mImageView = (ImageView) findViewById(R.id.mainImageView);

        createTextView = (TextView) findViewById(R.id.createTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        imageFile = null;
        imagePath = "null";

        titleTextView.setText("创建圈子");

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
                        imageFile = new File(FileUtil.getImagePath() + "circle_create.jpg");
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

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });

    }

    private void create() {

        String name = nameEditText.getText().toString();
        String tag = tagEditText.getText().toString();
        String desc = descEditText.getText().toString();
        String pursuer = pursuerEditText.getText().toString();

        if (TextUtil.isEmpty(name) || TextUtil.isEmpty(tag) || TextUtil.isEmpty(desc) || TextUtil.isEmpty(pursuer)) {
            ToastUtil.show(mActivity, "内容未填写完整");
            return;
        }

        if (imageFile == null) {
            ToastUtil.show(mActivity, "请选择图片");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_circle");
        ajaxParams.putOp("create");
        ajaxParams.put("circle_tag", tag);
        ajaxParams.put("circle_name", name);
        ajaxParams.put("circle_desc", desc);
        ajaxParams.put("circle_pursuer", pursuer);

        if (imageFile == null) {
            ajaxParams.put("file", "");
        } else {
            try {
                ajaxParams.put("file", imageFile);
            } catch (FileNotFoundException e) {
                ajaxParams.put("file", "");
            }
        }

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.show(mActivity, "圈子创建成功，等待管理员审核");
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

    private void returnActivity() {

        DialogUtil.query(mActivity, "确认您的选择", "取消创建圈子", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.cancel();
                mApplication.finishActivity(mActivity);
            }
        });

    }

}