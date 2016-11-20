package top.yokey.nsg.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class OrderEvaluateActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private int posInt;
    private File[] imageFile;
    private String[] imagePath;
    private Vector<String> imageVector;

    private String order_id;
    private String store_desc;
    private String goods_score;
    private String store_server;
    private String store_deliver;
    private String goods_comment;
    private Vector<String> rec_id;
    private HashMap<String, String> mHashMap;

    private ImageView backImageView;
    private TextView titleTextView;
    private TextView storeTextView;
    private TextView infoTextView;
    private EditText evaluateEditText;
    private ImageView[] mImageView;
    private RatingBar scoreRatingBar;
    private RatingBar descRatingBar;
    private RatingBar serverRatingBar;
    private RatingBar deliverRatingBar;
    private TextView evaluateTextView;

    private RecyclerView mListView;
    private GoodsOrderListAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mArrayList;

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case NcApplication.CODE_CHOOSE_PHOTO:
                    imagePath[posInt] = AndroidUtil.getMediaPath(mActivity, data.getData());
                    if (posInt != 0) {
                        for (int i = 0; i < 5; i++) {
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
        setContentView(R.layout.activity_order_evaluate);
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
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        evaluateEditText = (EditText) findViewById(R.id.evaluateEditText);

        mImageView = new ImageView[5];
        mImageView[0] = (ImageView) findViewById(R.id.upload1ImageView);
        mImageView[1] = (ImageView) findViewById(R.id.upload2ImageView);
        mImageView[2] = (ImageView) findViewById(R.id.upload3ImageView);
        mImageView[3] = (ImageView) findViewById(R.id.upload4ImageView);
        mImageView[4] = (ImageView) findViewById(R.id.upload5ImageView);

        scoreRatingBar = (RatingBar) findViewById(R.id.scoreRatingBar);
        descRatingBar = (RatingBar) findViewById(R.id.descRatingBar);
        serverRatingBar = (RatingBar) findViewById(R.id.serverRatingBar);
        deliverRatingBar = (RatingBar) findViewById(R.id.deliverRatingBar);

        evaluateTextView = (TextView) findViewById(R.id.evaluateTextView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        posInt = 0;
        store_desc = "";
        goods_score = "";
        store_server = "";
        store_deliver = "";
        goods_comment = "";
        imageFile = new File[5];
        rec_id = new Vector<>();
        imagePath = new String[5];
        imageVector = new Vector<>();
        order_id = mActivity.getIntent().getStringExtra("order_id");

        if (TextUtil.isEmpty(order_id)) {
            ToastUtil.show(mActivity, "参数错误");
            mApplication.finishActivity(mActivity);
        }

        for (int i = 0; i < 5; i++) {
            imageFile[i] = null;
            imagePath[i] = "null";
        }

        titleTextView.setText("订单评价");

        mArrayList = new ArrayList<>();
        mAdapter = new GoodsOrderListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        scoreRatingBar.setRating(5.0f);
        descRatingBar.setRating(5.0f);
        serverRatingBar.setRating(5.0f);
        deliverRatingBar.setRating(5.0f);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });


        scoreRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                goods_score = v + "";
            }
        });

        descRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                store_desc = v + "";
            }
        });

        serverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                store_server = v + "";
            }
        });

        deliverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                store_deliver = v + "";
            }
        });

        evaluateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goods_comment = evaluateEditText.getText().toString();
                if (TextUtil.isEmpty(goods_comment)) {
                    ToastUtil.show(mActivity, "评价不能为空");
                    return;
                }
                DialogUtil.progress(mActivity);
                upload(0);
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
                            imageFile[position] = new File(FileUtil.getImagePath() + "evaluate" + position + ".jpg");
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

    }

    private void setImage() {

        imageVector.clear();

        for (int i = 0; i < 5; i++) {
            mImageView[i].setImageResource(R.mipmap.ic_transparent);
            if (!imagePath[i].equals("null")) {
                imageVector.add(imagePath[i]);
            }
        }

        for (int i = 0; i < imageVector.size(); i++) {
            mImageView[i].setImageBitmap(ImageUtil.getSmall(imageVector.get(i)));
            imagePath[i] = imageVector.get(i);
        }

        for (int i = imageVector.size(); i < 5; i++) {
            imagePath[i] = "null";
        }

        if (imageVector.size() < 5) {
            mImageView[imageVector.size()].setImageResource(R.mipmap.ic_add_img);
        }

        imageVector.clear();

    }

    private void getJson() {

        rec_id.clear();
        mArrayList.clear();
        DialogUtil.progress(mActivity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_evaluate");
        ajaxParams.putOp("index");
        ajaxParams.put("order_id", order_id);

        mApplication.mFinalHttp.get(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            Double money = 0.0;
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("order_goods"));
                            mHashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonObject.getString("store_info")));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString())));
                            }
                            for (int i = 0; i < mArrayList.size(); i++) {
                                rec_id.add(mArrayList.get(i).get("rec_id"));
                                money += Double.parseDouble(mArrayList.get(i).get("goods_pay_price"));
                            }
                            storeTextView.setText(mHashMap.get("store_name"));
                            String total = "共 <font color='#FF5001'>" + mArrayList.size() + "</font> 件商品";
                            total += "，共 <font color='#FF5001'>￥ " + money + "</font> 元（含运费）";
                            infoTextView.setText(Html.fromHtml(total));
                            mAdapter.notifyDataSetChanged();
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

    private void orderEvaluate() {

        DialogUtil.progress(mActivity);
        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("member_evaluate");
        ajaxParams.putOp("save");
        ajaxParams.put("order_id", order_id);
        for (int i = 0; i < rec_id.size(); i++) {
            ajaxParams.put("goods[" + rec_id.get(i) + "][score]", goods_score);
            ajaxParams.put("goods[" + rec_id.get(i) + "][comment]", goods_comment);
            for (int j = 0; j < 5; j++) {
                if (j < imageVector.size()) {
                    ajaxParams.put("goods[" + rec_id.get(i) + "][evaluate_image][" + j + "]", imageVector.get(j));
                } else {
                    ajaxParams.put("goods[" + rec_id.get(i) + "][evaluate_image][" + j + "]", "");
                }
            }
        }
        ajaxParams.put("store_desccredit", store_desc);
        ajaxParams.put("store_servicecredit", store_server);
        ajaxParams.put("store_deliverycredit", store_deliver);

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        mActivity.setResult(RESULT_OK);
                        ToastUtil.showSuccess(mActivity);
                        mApplication.finishActivity(mActivity);
                    } else {
                        ToastUtil.showFailure(mActivity);
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

    private void upload(final int i) {

        if (i == 5) {
            orderEvaluate();
            return;
        }

        if (imagePath[i].equals("null")) {
            DialogUtil.cancel();
            orderEvaluate();
            return;
        }

        try {
            KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
            ajaxParams.putAct("sns_album");
            ajaxParams.putOp("file_upload");
            ajaxParams.put("file", new File(FileUtil.createJpgByBitmap("evaluate" + i, ImageUtil.toBitmap(ImageUtil.toString(imagePath[i])))));
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
                                imageVector.add(jsonObject.getString("file_path"));
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

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "取消评价？",
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