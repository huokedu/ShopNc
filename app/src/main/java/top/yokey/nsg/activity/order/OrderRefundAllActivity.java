package top.yokey.nsg.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.adapter.GoodsOrderListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.ImageUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class OrderRefundAllActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private int posInt;
    private File[] imageFile;
    private String[] imagePath;
    private Vector<String> imageVector;

    private String order_id;
    private String buyer_message;

    private HashMap<String, String> mHashMap;
    private HashMap<String, String> orderHashMap;
    private ArrayList<HashMap<String, String>> goodsArrayList;

    private ImageView backImageView;
    private TextView titleTextView;
    private TextView storeTextView;
    private RecyclerView mListView;
    private EditText moneyEditText;
    private EditText messageEditText;
    private ImageView[] mImageView;
    private TextView submitTextView;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_CHOOSE_PHOTO:
                    imagePath[posInt] = AndroidUtil.getMediaPath(mActivity, data.getData());
                    if (posInt != 0) {
                        for (int i = 0; i < 3; i++) {
                            if (i != posInt) {
                                if (imagePath[posInt].equals(imagePath[i])) {
                                    imagePath[posInt] = "null";
                                    ToastUtil.show(mActivity, "两张图片不能一样");
                                    return;
                                }
                            }
                        }
                    }
                    setImage();
                    break;
                case NcApplication.CODE_CHOOSE_CAMERA:
                    setImage();
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
        setContentView(R.layout.activity_order_refund_all);
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

        storeTextView = (TextView) findViewById(R.id.storeTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        moneyEditText = (EditText) findViewById(R.id.moneyEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        mImageView = new ImageView[3];
        mImageView[0] = (ImageView) findViewById(R.id.upload1ImageView);
        mImageView[1] = (ImageView) findViewById(R.id.upload2ImageView);
        mImageView[2] = (ImageView) findViewById(R.id.upload3ImageView);
        submitTextView = (TextView) findViewById(R.id.submitTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        posInt = 0;
        imageFile = new File[3];
        imagePath = new String[3];
        imageVector = new Vector<>();

        order_id = mActivity.getIntent().getStringExtra("order_id");

        if (TextUtil.isEmpty(order_id)) {
            ToastUtil.show(mActivity, "参数错误");
            mApplication.finishActivity(mActivity);
        }

        titleTextView.setText("订单退款");

        for (int i = 0; i < 3; i++) {
            imageFile[i] = null;
            imagePath[i] = "null";
        }

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        for (int i = 0; i < mImageView.length; i++) {
            final int position = i;
            mImageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogUtil.image(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            imageFile[position] = new File(FileUtil.getImagePath() + "refund_all" + position + ".jpg");
                            imagePath[position] = imageFile[position].getAbsolutePath();
                            mApplication.startCamera(mActivity, imageFile[position]);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtil.cancel();
                            posInt = position;
                            mApplication.startPhoto(mActivity);
                        }
                    });
                }
            });
        }

        submitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyer_message = messageEditText.getText().toString();
                if (TextUtil.isEmpty(buyer_message)) {
                    ToastUtil.show(mActivity, "说明不能为空");
                    return;
                }
                DialogUtil.progress(mActivity);
                upload(0);
            }
        });

    }

    private void setImage() {

        imageVector.clear();

        for (int i = 0; i < 3; i++) {
            mImageView[i].setImageResource(R.mipmap.ic_transparent);
            if (!imagePath[i].equals("null")) {
                imageVector.add(imagePath[i]);
            }
        }

        for (int i = 0; i < imageVector.size(); i++) {
            mImageView[i].setImageBitmap(ImageUtil.getSmall(imageVector.get(i)));
            imagePath[i] = imageVector.get(i);
        }

        for (int i = imageVector.size(); i < 3; i++) {
            imagePath[i] = "null";
        }

        if (imageVector.size() < 3) {
            mImageView[imageVector.size()].setImageResource(R.mipmap.ic_add_img);
        }

        imageVector.clear();

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_refund");
        ajaxParams.putOp("refund_all_form");
        ajaxParams.put("order_id", order_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        try {
                            goodsArrayList = new ArrayList<>();
                            String data = mApplication.getJsonData(o.toString());
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(data));
                            orderHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(mHashMap.get("order")));
                            JSONArray jsonArray = new JSONArray(mHashMap.get("goods_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                hashMap.put("goods_image_url", hashMap.get("goods_img_360"));
                                goodsArrayList.add(hashMap);
                            }
                            storeTextView.setText(orderHashMap.get("store_name"));
                            mListView.setLayoutManager(new LinearLayoutManager(mActivity));
                            mListView.setAdapter(new GoodsOrderListAdapter(mApplication, mActivity, goodsArrayList));
                            moneyEditText.setText("最多可退 ￥");
                            moneyEditText.append(orderHashMap.get("allow_refund_amount"));
                            moneyEditText.append(" 元");
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
                "确认您的选择",
                "读取数据失败，是否重试？",
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
                }
        );

    }

    private void upload(final int i) {

        if (i == 3) {
            orderRefund();
            return;
        }

        if (imagePath[i].equals("null")) {
            DialogUtil.cancel();
            orderRefund();
            return;
        }

        try {
            KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
            ajaxParams.putAct("member_refund");
            ajaxParams.putOp("upload_pic");
            ajaxParams.put("refund_pic", new File(FileUtil.createJpgByBitmap("refund_all" + i, ImageUtil.toBitmap(ImageUtil.toString(imagePath[i])))));
            mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    if (TextUtil.isJson(o.toString())) {
                        String error = mApplication.getJsonError(o.toString());
                        if (TextUtil.isEmpty(error)) {
                            String data = mApplication.getJsonData(o.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                imageVector.add(jsonObject.getString("file_name"));
                                upload(i + 1);
                            } catch (JSONException e) {
                                ToastUtil.showFailure(mActivity);
                                e.printStackTrace();
                                upload(i);
                            }
                        } else {
                            ToastUtil.showFailure(mActivity);
                            upload(i);
                        }
                    } else {
                        ToastUtil.showFailure(mActivity);
                        upload(i);
                    }
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    ToastUtil.showFailure(mActivity);
                    upload(i);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void orderRefund() {

        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_refund");
        ajaxParams.putOp("refund_all_post");
        ajaxParams.put("order_id", order_id);
        ajaxParams.put("buyer_message", buyer_message);
        for (int i = 0; i < imageVector.size(); i++) {
            ajaxParams.put("refund_pic[" + i + "]", imageVector.get(i));
        }

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (mApplication.getJsonSuccess(o.toString())) {
                    mActivity.setResult(RESULT_OK);
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

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消退款？",
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