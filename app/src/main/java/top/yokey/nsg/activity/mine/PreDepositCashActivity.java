package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class PreDepositCashActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView moneyTextView;
    private EditText moneyEditText;
    private EditText bankNameEditText;
    private EditText bankCardEditText;
    private EditText nameEditText;
    private EditText payPassEditText;
    private TextView confirmTextView;

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
        setContentView(R.layout.activity_pre_deposit_cash);
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

        moneyTextView = (TextView) findViewById(R.id.moneyTextView);
        moneyEditText = (EditText) findViewById(R.id.moneyEditText);
        bankNameEditText = (EditText) findViewById(R.id.bankNameEditText);
        bankCardEditText = (EditText) findViewById(R.id.bankCardEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        payPassEditText = (EditText) findViewById(R.id.payPassEditText);
        confirmTextView = (TextView) findViewById(R.id.confirmTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleTextView.setText("余额提现");
        getDeposit();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });

    }

    private void getDeposit() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_index");
        ajaxParams.putOp("my_asset");
        ajaxParams.put("fields", "predepoit");

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            String temp = "余额 ￥ " + jsonObject.getString("predepoit");
                            moneyTextView.setText(temp);
                            temp = "最多可提现 " + jsonObject.getString("predepoit") + " 元";
                            moneyEditText.setHint(temp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getDepositFailure();
                        }
                    } else {
                        getDepositFailure();
                    }
                } else {
                    getDepositFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getDepositFailure();
            }
        });

    }

    private void getDepositFailure() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "读取数据失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getDeposit();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void submitData() {

        String pdc_amount = moneyEditText.getText().toString();
        String pdc_bank_name = bankNameEditText.getText().toString();
        String pdc_bank_no = bankCardEditText.getText().toString();
        String pdc_bank_user = nameEditText.getText().toString();
        String password = payPassEditText.getText().toString();

        if (TextUtil.isEmpty(pdc_amount)) {
            ToastUtil.show(mActivity, "金额不能为空");
            return;
        }

        if (TextUtil.isEmpty(pdc_bank_name)) {
            ToastUtil.show(mActivity, "银行未填写");
            return;
        }

        if (TextUtil.isEmpty(pdc_bank_no)) {
            ToastUtil.show(mActivity, "账号未填写");
            return;
        }

        if (TextUtil.isEmpty(pdc_bank_user)) {
            ToastUtil.show(mActivity, "姓名未填写");
            return;
        }

        if (TextUtil.isEmpty(password)) {
            ToastUtil.show(mActivity, "支付密码未填写");
            return;
        }

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_fund");
        ajaxParams.putOp("pdcash_add");
        ajaxParams.put("pdc_amount", pdc_amount);
        ajaxParams.put("pdc_bank_name", pdc_bank_name);
        ajaxParams.put("pdc_bank_no", pdc_bank_no);
        ajaxParams.put("pdc_bank_user", pdc_bank_user);
        ajaxParams.put("password", password);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    ToastUtil.show(mActivity, "提现申请已提交");
                    mApplication.finishActivity(mActivity);
                } else {
                    ToastUtil.show(mActivity, mApplication.getJsonError(o.toString()));
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailureNetwork(mActivity);
                DialogUtil.cancel();
            }
        });

    }

    private void returnActivity() {

        DialogUtil.query(mActivity, "确认您的选择", "返回？", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.finishActivity(mActivity);
            }
        });

    }

}