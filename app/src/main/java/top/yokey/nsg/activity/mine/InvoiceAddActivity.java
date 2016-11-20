package top.yokey.nsg.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class InvoiceAddActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String titleString;
    private String selectString;
    private String contentString;
    private Vector<String> contentVector;

    private ImageView backImageView;
    private TextView titleTextView;

    private Spinner typeSpinner;
    private Spinner contentSpinner;
    private EditText headEditText;
    private TextView addTextView;

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
        setContentView(R.layout.activity_invoice_add);
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

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        contentSpinner = (Spinner) findViewById(R.id.contentSpinner);
        headEditText = (EditText) findViewById(R.id.headEditText);
        addTextView = (TextView) findViewById(R.id.addTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleString = "";
        selectString = "";
        contentString = "";
        titleTextView.setText("新增发票内容");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"个人", "单位"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        getContent();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectString = "person";
                        titleString = mApplication.userHashMap.get("member_name");
                        headEditText.setVisibility(View.GONE);
                        break;
                    case 1:
                        selectString = "company";
                        titleString = "";
                        headEditText.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contentString = contentVector.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInvoice();
            }
        });

    }

    private void getContent() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_invoice");
        ajaxParams.putOp("invoice_content_list");

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            contentVector = new Vector<>();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("invoice_content_list"));
                            if (jsonArray.length() == 0) {
                                ToastUtil.show(mActivity, "系统未开放发票申请，请联系管理员");
                                mApplication.finishActivity(mActivity);
                                return;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                contentVector.add(jsonArray.getString(i));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, contentVector);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            contentSpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getContentFailure();
                        }
                    } else {
                        getContentFailure();
                    }
                } else {
                    getContentFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getContentFailure();
            }
        });

    }

    private void getContentFailure() {

        DialogUtil.query(
                mActivity,
                "是否重试？",
                "读取数据失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getContent();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void addInvoice() {

        if (!selectString.equals("person")) {
            titleString = headEditText.getText().toString();
        }

        if (TextUtil.isEmpty(selectString)) {
            ToastUtil.show(mActivity, "请选择类型");
            return;
        }

        if (TextUtil.isEmpty(titleString)) {
            ToastUtil.show(mActivity, "请填写抬头");
            return;
        }

        if (TextUtil.isEmpty(contentString)) {
            ToastUtil.show(mActivity, "请填写内容");
            return;
        }

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_invoice");
        ajaxParams.putOp("invoice_add");
        ajaxParams.put("inv_title_select", selectString);
        ajaxParams.put("inv_title", titleString);
        ajaxParams.put("inv_content", contentString);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        if (data.contains("inv_id")) {
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
                "取消添加收货地址",
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