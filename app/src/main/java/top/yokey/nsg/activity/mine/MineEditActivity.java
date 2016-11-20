package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
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
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class MineEditActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private File imageFile;
    private String imagePath;
    private boolean saveBoolean;

    private String member_truename;
    private String member_sex;
    private String member_qq;
    private String member_ww;
    private String member_birthday;
    private String member_provinceid;
    private String member_cityid;
    private String member_areaid;
    private String member_areainfo;

    private ImageView backImageView;
    private TextView titleTextView;

    private RelativeLayout avatarRelativeLayout;
    private ImageView avatarImageView;
    private LinearLayout nameLinearLayout;
    private TextView nameTextView;
    private LinearLayout sexLinearLayout;
    private TextView sexTextView;
    private LinearLayout qqLinearLayout;
    private TextView qqTextView;
    private LinearLayout wwLinearLayout;
    private TextView wwTextView;
    private LinearLayout birthdayLinearLayout;
    private TextView birthdayTextView;
    private LinearLayout areaLinearLayout;
    private TextView areaTextView;
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
                    imageFile = new File(imagePath);
                    saveAvatar();
                    break;
                case NcApplication.CODE_CHOOSE_AREA:
                    member_provinceid = data.getStringExtra("province_id");
                    member_cityid = data.getStringExtra("city_id");
                    member_areaid = data.getStringExtra("area_id");
                    member_areainfo = data.getStringExtra("area_info");
                    areaTextView.setText(data.getStringExtra("area_info"));
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
        setContentView(R.layout.activity_mine_edit);
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
        nameLinearLayout = (LinearLayout) findViewById(R.id.nameLinearLayout);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        sexLinearLayout = (LinearLayout) findViewById(R.id.sexLinearLayout);
        sexTextView = (TextView) findViewById(R.id.sexTextView);
        qqLinearLayout = (LinearLayout) findViewById(R.id.qqLinearLayout);
        qqTextView = (TextView) findViewById(R.id.qqTextView);
        wwLinearLayout = (LinearLayout) findViewById(R.id.wwLinearLayout);
        wwTextView = (TextView) findViewById(R.id.wwTextView);
        birthdayLinearLayout = (LinearLayout) findViewById(R.id.birthdayLinearLayout);
        birthdayTextView = (TextView) findViewById(R.id.birthdayTextView);
        areaLinearLayout = (LinearLayout) findViewById(R.id.areaLinearLayout);
        areaTextView = (TextView) findViewById(R.id.areaTextView);

        saveTextView = (TextView) findViewById(R.id.saveTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        imageFile = null;
        imagePath = "null";
        saveBoolean = false;

        titleTextView.setText("修改资料");
        ImageLoader.getInstance().displayImage(mApplication.userHashMap.get("avator"), avatarImageView);

        member_truename = mApplication.userHashMap.get("member_truename");
        member_sex = mApplication.userHashMap.get("member_sex");
        member_qq = mApplication.userHashMap.get("member_qq");
        member_ww = mApplication.userHashMap.get("member_ww");
        member_birthday = mApplication.userHashMap.get("member_birthday");
        member_provinceid = mApplication.userHashMap.get("member_provinceid");
        member_cityid = mApplication.userHashMap.get("member_cityid");
        member_areaid = mApplication.userHashMap.get("member_areaid");
        member_areainfo = mApplication.userHashMap.get("member_areainfo");

        if (TextUtil.isEmpty(member_truename)) {
            member_truename = "未填写";
        }

        if (TextUtil.isEmpty(member_qq)) {
            member_qq = "未填写";
        }

        if (TextUtil.isEmpty(member_ww)) {
            member_ww = "未填写";
        }

        if (TextUtil.isEmpty(member_birthday) || member_birthday.contains("0000")) {
            member_birthday = "未填写";
        }

        if (TextUtil.isEmpty(member_areainfo)) {
            member_areainfo = "未填写";
        }

        qqTextView.setText(member_qq);
        wwTextView.setText(member_ww);
        areaTextView.setText(member_areainfo);
        nameTextView.setText(member_truename);
        birthdayTextView.setText(member_birthday);

        switch (member_sex) {
            case "1":
                sexTextView.setText("男");
                break;
            case "2":
                sexTextView.setText("女");
                break;
            case "3":
                sexTextView.setText("保密");
                break;
            default:
                sexTextView.setText("未填写");
                break;
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

        nameLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new AlertDialog.Builder(mActivity).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Window window = dialog.getWindow();
                window.setContentView(R.layout.dialog_input);
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

                TextView titleTextView = (TextView) window.findViewById(R.id.titleTextView);
                titleTextView.setText("请输入您的姓名");

                final EditText contentEditText = (EditText) window.findViewById(R.id.contentEditText);
                contentEditText.setText(member_truename);

                contentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                contentEditText.setSelection(member_truename.length());

                TextView confirmTextView = (TextView) window.findViewById(R.id.confirmTextView);
                confirmTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = contentEditText.getText().toString();
                        if (content.length() < 2 || content.length() > 6) {
                            ToastUtil.show(mActivity, "姓名必须大于2位且小于6位");
                            return;
                        }
                        member_truename = contentEditText.getText().toString();
                        nameTextView.setText(member_truename);
                        AndroidUtil.hideKeyboard(v);
                        saveBoolean = true;
                        dialog.cancel();
                    }
                });

                TextView cancelTextView = (TextView) window.findViewById(R.id.cancelTextView);
                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

            }
        });

        sexLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogUtil.gender(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sexTextView.setText("男");
                        saveBoolean = true;
                        DialogUtil.cancel();
                        member_sex = "1";
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sexTextView.setText("女");
                        saveBoolean = true;
                        DialogUtil.cancel();
                        member_sex = "2";
                    }
                });

            }
        });

        qqLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new AlertDialog.Builder(mActivity).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Window window = dialog.getWindow();
                window.setContentView(R.layout.dialog_input);
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

                TextView titleTextView = (TextView) window.findViewById(R.id.titleTextView);
                titleTextView.setText("请输入QQ号码");

                final EditText contentEditText = (EditText) window.findViewById(R.id.contentEditText);
                contentEditText.setText(member_qq);

                contentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                contentEditText.setSelection(member_qq.length());

                TextView confirmTextView = (TextView) window.findViewById(R.id.confirmTextView);
                confirmTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = contentEditText.getText().toString();
                        if (TextUtil.isEmpty(content)) {
                            ToastUtil.show(mActivity, "不能为空");
                            return;
                        }
                        member_qq = contentEditText.getText().toString();
                        qqTextView.setText(member_qq);
                        AndroidUtil.hideKeyboard(v);
                        saveBoolean = true;
                        dialog.cancel();
                    }
                });

                TextView cancelTextView = (TextView) window.findViewById(R.id.cancelTextView);
                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

            }
        });

        wwLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new AlertDialog.Builder(mActivity).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Window window = dialog.getWindow();
                window.setContentView(R.layout.dialog_input);
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

                TextView titleTextView = (TextView) window.findViewById(R.id.titleTextView);
                titleTextView.setText("请输入旺旺号码");

                final EditText contentEditText = (EditText) window.findViewById(R.id.contentEditText);
                contentEditText.setText(member_ww);

                contentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                contentEditText.setSelection(member_ww.length());

                TextView confirmTextView = (TextView) window.findViewById(R.id.confirmTextView);
                confirmTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = contentEditText.getText().toString();
                        if (TextUtil.isEmpty(content)) {
                            ToastUtil.show(mActivity, "不能为空");
                            return;
                        }
                        member_ww = contentEditText.getText().toString();
                        wwTextView.setText(member_ww);
                        AndroidUtil.hideKeyboard(v);
                        saveBoolean = true;
                        dialog.cancel();
                    }
                });

                TextView cancelTextView = (TextView) window.findViewById(R.id.cancelTextView);
                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

            }
        });

        birthdayLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(
                        mActivity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String temp;
                                int yue = i1 + 1;
                                if (yue < 10) {
                                    temp = i + "-0" + yue + "-" + i2;
                                } else {
                                    temp = i + "-" + yue + "-" + i2;
                                }
                                birthdayTextView.setText(temp);
                                member_birthday = temp;
                                saveBoolean = true;
                            }
                        },
                        Integer.parseInt(TimeUtil.getYear()),
                        Integer.parseInt(TimeUtil.getMouth()) - 1,
                        Integer.parseInt(TimeUtil.getDay())
                );

                dialog.setCancelable(false);
                dialog.show();

            }
        });

        areaLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.startActivity(mActivity, new Intent(mActivity, AddressMapActivity.class), NcApplication.CODE_CHOOSE_AREA);
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

    private void saveAvatar() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("modify_avatar");
        try {
            ajaxParams.put("file", imageFile);
        } catch (FileNotFoundException e) {
            ToastUtil.show(mActivity, "文件不存在");
            DialogUtil.cancel();
            e.printStackTrace();
            return;
        }

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.show(mActivity, "头像更换成功，下次启动生效");
                    File file = ImageLoader.getInstance().getDiskCache().get(mApplication.userHashMap.get("avator"));
                    if (file != null) {
                        if (!file.isDirectory()) {
                            file.delete();
                        }
                    }
                } else {
                    ToastUtil.showFailure(mActivity);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
            }
        });

    }

    private void saveInfo() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("modify_info");
        ajaxParams.put("member_truename", member_truename);
        ajaxParams.put("member_sex", member_sex);
        ajaxParams.put("member_qq", member_qq);
        ajaxParams.put("member_ww", member_ww);
        ajaxParams.put("member_birthday", member_birthday);
        ajaxParams.put("member_areaid", member_areaid);
        ajaxParams.put("member_cityid", member_cityid);
        ajaxParams.put("member_provinceid", member_provinceid);
        ajaxParams.put("member_areainfo", member_areainfo);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    mApplication.userHashMap.put("member_truename", member_truename);
                    mApplication.userHashMap.put("member_sex", member_sex);
                    mApplication.userHashMap.put("member_qq", member_qq);
                    mApplication.userHashMap.put("member_ww", member_ww);
                    mApplication.userHashMap.put("member_birthday", member_birthday);
                    mApplication.userHashMap.put("member_provinceid", member_provinceid);
                    mApplication.userHashMap.put("member_cityid", member_cityid);
                    mApplication.userHashMap.put("member_areaid", member_areaid);
                    mApplication.userHashMap.put("member_areainfo", member_areainfo);
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
                "修改个人信息失败，是否重试",
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
                    "放弃修改个人资料",
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