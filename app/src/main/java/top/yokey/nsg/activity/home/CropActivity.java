package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import top.yokey.nsg.control.CropImageLayout;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.ImageUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class CropActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;
    private ImageView confirmImageView;

    private CropImageLayout mCropImageLayout;

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
        setContentView(R.layout.activity_crop);
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
        confirmImageView = (ImageView) findViewById(R.id.moreImageView);

        mCropImageLayout = (CropImageLayout) findViewById(R.id.mainCropImageLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("图片剪裁");
        confirmImageView.setImageResource(R.mipmap.ic_action_add);

        if (!TextUtil.isEmpty(mActivity.getIntent().getStringExtra("path"))) {
            try {
                String content = ImageUtil.toString(mActivity.getIntent().getStringExtra("path"));
                Bitmap bitmap = ImageUtil.toBitmap(content);
                String new_path = FileUtil.createJpgByBitmap("crop", bitmap);
                bitmap = ImageUtil.getSmall(new_path);
                mCropImageLayout.setImageBitmap(bitmap);
            } catch (OutOfMemoryError e) {
                ToastUtil.show(mActivity, "图片过大，请重新选择!");
                mApplication.finishActivity(mActivity);
            }
        } else {
            ToastUtil.show(mActivity, "图片路径为空!");
            mApplication.finishActivity(mActivity);
        }

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        confirmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.mBitmap = mCropImageLayout.clip();
                mActivity.setResult(RESULT_OK);
                mApplication.finishActivity(mActivity);
            }
        });

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消剪裁图片？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

}