package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;


public class AddressEditActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String address_id;
    private String city_id;
    private String area_id;
    private String area_info;
    private String address;
    private String phone;
    private String name;
    private String is_default;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView areaTextView;
    private EditText addressEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private CheckBox defaultCheckBox;
    private TextView editTextView;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_CHOOSE_AREA:
                    city_id = data.getStringExtra("city_id");
                    area_id = data.getStringExtra("area_id");
                    area_info = data.getStringExtra("area_info");
                    address = data.getStringExtra("address");
                    areaTextView.setText(area_info);
                    addressEditText.setText(address);
                    break;
                default:
                    break;
            }
        } else {
            city_id = "";
            area_id = "";
            area_info = "";
            areaTextView.setText("请选择区域");
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
        setContentView(R.layout.activity_address_edit);
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

        areaTextView = (TextView) findViewById(R.id.areaTextView);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        defaultCheckBox = (CheckBox) findViewById(R.id.defaultCheckBox);
        editTextView = (TextView) findViewById(R.id.editTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        address_id = mActivity.getIntent().getStringExtra("address_id");
        name = mActivity.getIntent().getStringExtra("true_name");
        area_id = mActivity.getIntent().getStringExtra("area_id");
        city_id = mActivity.getIntent().getStringExtra("city_id");
        area_info = mActivity.getIntent().getStringExtra("area_info");
        address = mActivity.getIntent().getStringExtra("address");
        phone = mActivity.getIntent().getStringExtra("tel_phone");
        is_default = mActivity.getIntent().getStringExtra("is_default");

        if (is_default.equals("1")) {
            defaultCheckBox.setChecked(true);
        } else {
            defaultCheckBox.setChecked(false);
        }

        titleTextView.setText("编辑收货地址");
        areaTextView.setText(area_info);
        addressEditText.setText(address);
        nameEditText.setText(name);
        phoneEditText.setText(phone);
        addressEditText.setSelection(address.length());

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        areaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, AddressMapActivity.class), NcApplication.CODE_CHOOSE_AREA);
            }
        });

        defaultCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_default = "1";
                } else {
                    is_default = "0";
                }
            }
        });

        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddress();
            }
        });

    }

    private void editAddress() {

        address = addressEditText.getText().toString();
        name = nameEditText.getText().toString();
        phone = phoneEditText.getText().toString();

        if (TextUtil.isEmpty(city_id)) {
            ToastUtil.show(mActivity, "请选择区域");
            return;
        }

        if (TextUtil.isEmpty(address)) {
            ToastUtil.show(mActivity, "请输入详细地址");
            return;
        }

        if (TextUtil.isEmpty(name)) {
            ToastUtil.show(mActivity, "请输入真实姓名");
            return;
        }

        if (TextUtil.isEmpty(phone)) {
            ToastUtil.show(mActivity, "手机号码");
            return;
        }

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_address");
        ajaxParams.putOp("address_edit");
        ajaxParams.put("address_id", address_id);
        ajaxParams.put("true_name", name);
        ajaxParams.put("city_id", city_id);
        ajaxParams.put("area_id", area_id);
        ajaxParams.put("area_info", area_info);
        ajaxParams.put("address", address);
        ajaxParams.put("tel_phone", phone);
        ajaxParams.put("mob_phone", phone);
        ajaxParams.put("is_default", is_default);
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        if (data.equals("1")) {
                            ToastUtil.showSuccess(mActivity);
                            mActivity.setResult(RESULT_OK);
                            mApplication.finishActivity(mActivity);
                        } else {
                            ToastUtil.showFailure(mActivity);
                        }
                    } else {
                        ToastUtil.show(mActivity, error);
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

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消编辑地址",
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