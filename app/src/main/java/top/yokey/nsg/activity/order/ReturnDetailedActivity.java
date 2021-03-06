package top.yokey.nsg.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class ReturnDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String refund_id;

    private ImageView backImageView;
    private TextView titleTextView;

    private TextView snTextView;
    private TextView reasonTextView;
    private TextView moneyTextView;
    private TextView messageTextView;
    private ImageView[] mImageView;

    private TextView storeStateTextView;
    private TextView storeMessageTextView;
    private TextView adminStateTextView;
    private TextView adminMessageTextView;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_return_detailed);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        snTextView = (TextView) findViewById(R.id.snTextView);
        reasonTextView = (TextView) findViewById(R.id.reasonTextView);
        moneyTextView = (TextView) findViewById(R.id.moneyTextView);
        messageTextView = (TextView) findViewById(R.id.messageTextView);

        mImageView = new ImageView[3];
        mImageView[0] = (ImageView) findViewById(R.id.thrImageView);
        mImageView[1] = (ImageView) findViewById(R.id.twoImageView);
        mImageView[2] = (ImageView) findViewById(R.id.oneImageView);

        storeStateTextView = (TextView) findViewById(R.id.storeStateTextView);
        storeMessageTextView = (TextView) findViewById(R.id.storeMessageTextView);
        adminStateTextView = (TextView) findViewById(R.id.adminStateTextView);
        adminMessageTextView = (TextView) findViewById(R.id.adminMessageTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        refund_id = mActivity.getIntent().getStringExtra("refund_id");
        if (TextUtil.isEmpty(refund_id)) {
            ToastUtil.show(mActivity, "传入参数不对!");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("退货详细");

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnActivity();
            }
        });

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        //初始化POST数据
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_return");
        ajaxParams.putOp("get_return_info");
        ajaxParams.put("return_id", refund_id);

        //POST提交
        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            String data = mApplication.getJsonData(o.toString());
                            JSONObject jsonObject = new JSONObject(data);
                            //基本信息
                            JSONObject mJsonObject = new JSONObject(jsonObject.getString("return_info"));
                            snTextView.setText(mJsonObject.getString("refund_sn"));
                            reasonTextView.setText(mJsonObject.getString("reason_info"));
                            moneyTextView.setText(mJsonObject.getString("refund_amount"));
                            messageTextView.setText(mJsonObject.getString("buyer_message"));
                            adminStateTextView.setText(mJsonObject.getString("admin_state"));
                            if (TextUtil.isEmpty(mJsonObject.getString("seller_message"))) {
                                storeMessageTextView.setText("暂无备注");
                                storeStateTextView.setText("商家处理中");
                            } else {
                                storeStateTextView.setText("商家已处理");
                                storeMessageTextView.setText(mJsonObject.getString("seller_message"));
                            }
                            if (TextUtil.isEmpty(mJsonObject.getString("admin_message"))) {
                                adminMessageTextView.setText("暂无备注");
                            } else {
                                adminMessageTextView.setText(mJsonObject.getString("admin_message"));
                            }
                            //图片
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("pic_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (i < 3) {
                                    ImageLoader.getInstance().displayImage(jsonArray.get(i).toString(), mImageView[i]);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getJsonFailure();
                        }
                    } else {
                        getJsonFailure();
                    }
                } else {
                    getJsonFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                DialogUtil.cancel();
                getJsonFailure();
            }
        });

    }

    private void getJsonFailure() {

        DialogUtil.query(
                mActivity,
                " 是否重试?",
                "读取数据失败",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getJson();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                });

    }

    private void returnActivity() {

        mApplication.finishActivity(mActivity);

    }

}