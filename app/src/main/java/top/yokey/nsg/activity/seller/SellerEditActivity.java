package top.yokey.nsg.activity.seller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;


import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.io.FileNotFoundException;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SellerEditActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private File imageFile;
    private String imagePath;
    private boolean saveBoolean;

    private ImageView backImageView;
    private TextView titleTextView;

    private RelativeLayout avatarRelativeLayout;
    private ImageView avatarImageView;
    private EditText qqEditText;
    private EditText wwEditText;
    private EditText phoneEditText;
    private EditText zyEditText;
    private EditText descriptionEditText;
    private EditText keywordEditText;

    private TextView saveTextView;

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
                    imagePath = FileUtil.createJpgByBitmap("user_avatar", mApplication.mBitmap);
                    avatarImageView.setImageBitmap(mApplication.mBitmap);
                    imageFile = new File(imagePath);
                    saveBoolean = true;
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_seller_edit);
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

        avatarRelativeLayout = (RelativeLayout) findViewById(R.id.avatarRelativeLayout);
        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        qqEditText = (EditText) findViewById(R.id.qqEditText);
        wwEditText = (EditText) findViewById(R.id.wwEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        zyEditText = (EditText) findViewById(R.id.zyEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        keywordEditText = (EditText) findViewById(R.id.keywordEditText);

        saveTextView = (TextView) findViewById(R.id.saveTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        imageFile = null;
        imagePath = "null";
        saveBoolean = false;

        titleTextView.setText("修改店铺资料");

        //店铺信息填充
        ImageLoader.getInstance().displayImage(mApplication.storeHashMap.get("store_avatar"), avatarImageView);

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_qq"))) {
            qqEditText.setText("未填写");
        } else {
            qqEditText.setText(mApplication.storeHashMap.get("store_qq"));
        }

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_ww"))) {
            wwEditText.setText("未填写");
        } else {
            wwEditText.setText(mApplication.storeHashMap.get("store_ww"));
        }

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_phone"))) {
            phoneEditText.setText("未填写");
        } else {
            phoneEditText.setText(mApplication.storeHashMap.get("store_phone"));
        }

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_zy"))) {
            zyEditText.setText("未填写");
        } else {
            zyEditText.setText(mApplication.storeHashMap.get("store_zy"));
        }

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_description"))) {
            descriptionEditText.setText("未填写");
        } else {
            descriptionEditText.setText(mApplication.storeHashMap.get("store_description"));
        }

        if (TextUtil.isEmpty(mApplication.storeHashMap.get("store_keywords"))) {
            keywordEditText.setText("未填写");
        } else {
            keywordEditText.setText(mApplication.storeHashMap.get("store_keywords"));
        }

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        avatarRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.image(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        imageFile = new File(FileUtil.getImagePath() + "user_avatar.jpg");
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

        qqEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        zyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        keywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveBoolean = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!saveBoolean) {
                    ToastUtil.show(mActivity, "信息未改变，不需要修改");
                } else {
                    saveInfo();
                }
            }
        });

    }

    private void saveInfo() {

        final String qq = qqEditText.getText().toString();
        final String ww = wwEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();
        final String zy = zyEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();
        final String keyword = keywordEditText.getText().toString();

        if (TextUtil.isEmpty(qq)) {
            ToastUtil.show(mActivity, "QQ号码不能为空");
            return;
        }

        if (TextUtil.isEmpty(ww)) {
            ToastUtil.show(mActivity, "旺旺不能为空");
            return;
        }

        if (TextUtil.isEmpty(phone)) {
            ToastUtil.show(mActivity, "电话号码不能为空");
            return;
        }

        if (TextUtil.isEmpty(zy)) {
            ToastUtil.show(mActivity, "主营说明不能为空");
            return;
        }

        if (TextUtil.isEmpty(description)) {
            ToastUtil.show(mActivity, "描述信息不能为空");
            return;
        }

        if (TextUtil.isEmpty(keyword)) {
            ToastUtil.show(mActivity, "关键字不能为空");
            return;
        }

        DialogUtil.progress(mActivity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(mApplication);
        ajaxParams.putAct("seller_store");
        ajaxParams.putOp("store_edit");

        if (imageFile == null) {
            ajaxParams.put("store_label", "");
        } else {
            try {
                ajaxParams.put("store_label", imageFile);
            } catch (FileNotFoundException e) {
                ajaxParams.put("store_label", "");
            }
        }

        ajaxParams.put("store_qq", qq);
        ajaxParams.put("store_ww", ww);
        ajaxParams.put("store_phone", phone);
        ajaxParams.put("store_zy", zy);
        ajaxParams.put("seo_keywords", description);
        ajaxParams.put("seo_description", keyword);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    mApplication.storeHashMap.put("store_qq", qq);
                    mApplication.storeHashMap.put("store_ww", ww);
                    mApplication.storeHashMap.put("store_phone", phone);
                    mApplication.storeHashMap.put("store_zy", zy);
                    mApplication.storeHashMap.put("store_description", description);
                    mApplication.storeHashMap.put("store_keywords", keyword);
                    ToastUtil.showSuccess(mActivity);
                    mActivity.setResult(RESULT_OK);
                    mApplication.finishActivity(mActivity);
                } else {
                    saveInfoFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                saveInfoFailure();
            }
        });

    }

    private void saveInfoFailure() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "修改店铺信息失败，是否重试",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        saveInfo();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void returnActivity() {

        if (saveBoolean) {
            DialogUtil.query(
                    mActivity,
                    "确认您的选择",
                    "放弃修改店铺资料",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DialogUtil.cancel();
                            mApplication.finishActivity(mActivity);
                        }
                    }
            );
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}