package top.yokey.nsg.activity.man;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.R;
import top.yokey.nsg.activity.home.NcApplication;
import top.yokey.nsg.activity.home.ShareActivity;
import top.yokey.nsg.adapter.ManCommentListAdapter;
import top.yokey.nsg.control.CustomScrollView;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.AndroidUtil;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.LogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.TimeUtil;
import top.yokey.nsg.utility.ToastUtil;

public class ManDetailedActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String idString;
    private String timeString;
    private String titleString;
    private String imageString;
    private boolean startBoolean;
    private boolean praiseBoolean;
    private boolean collectionBoolean;

    private ImageView backImageView;
    private TextView titleTextView;
    private TextView praiseTextView;
    private TextView collectTextView;
    private TextView commentTextView;
    private ImageView shareImageView;

    private WebView mWebView;
    private WebView goneWebView;
    private CustomScrollView mScrollView;

    private LinearLayout inputLinearLayout;
    private EditText contentEditText;
    private ImageView faceImageView;
    private ImageView sendImageView;
    private LinearLayout faceLinearLayout;
    private ImageView[] faceImageViews;

    private String rid;
    private RecyclerView mListView;
    private TextView commentTitleTextView;
    private ManCommentListAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mArrayList;

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
        setContentView(R.layout.activity_man_detailed);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        startBoolean = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.backImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        praiseTextView = (TextView) findViewById(R.id.praiseTextView);
        collectTextView = (TextView) findViewById(R.id.collectTextView);
        commentTextView = (TextView) findViewById(R.id.commentTextView);
        shareImageView = (ImageView) findViewById(R.id.shareImageView);

        mWebView = (WebView) findViewById(R.id.mainWebView);
        goneWebView = (WebView) findViewById(R.id.goneWebView);
        mScrollView = (CustomScrollView) findViewById(R.id.mainScrollView);

        inputLinearLayout = (LinearLayout) findViewById(R.id.inputLinearLayout);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
        faceImageView = (ImageView) findViewById(R.id.faceImageView);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);
        faceLinearLayout = (LinearLayout) findViewById(R.id.faceLinearLayout);
        faceImageViews = new ImageView[48];
        faceImageViews[0] = (ImageView) findViewById(R.id.e000ImageView);
        faceImageViews[1] = (ImageView) findViewById(R.id.e001ImageView);
        faceImageViews[2] = (ImageView) findViewById(R.id.e002ImageView);
        faceImageViews[3] = (ImageView) findViewById(R.id.e003ImageView);
        faceImageViews[4] = (ImageView) findViewById(R.id.e004ImageView);
        faceImageViews[5] = (ImageView) findViewById(R.id.e005ImageView);
        faceImageViews[6] = (ImageView) findViewById(R.id.e006ImageView);
        faceImageViews[7] = (ImageView) findViewById(R.id.e007ImageView);
        faceImageViews[8] = (ImageView) findViewById(R.id.e008ImageView);
        faceImageViews[9] = (ImageView) findViewById(R.id.e009ImageView);
        faceImageViews[10] = (ImageView) findViewById(R.id.e010ImageView);
        faceImageViews[11] = (ImageView) findViewById(R.id.e011ImageView);
        faceImageViews[12] = (ImageView) findViewById(R.id.e012ImageView);
        faceImageViews[13] = (ImageView) findViewById(R.id.e013ImageView);
        faceImageViews[14] = (ImageView) findViewById(R.id.e014ImageView);
        faceImageViews[15] = (ImageView) findViewById(R.id.e015ImageView);
        faceImageViews[16] = (ImageView) findViewById(R.id.e016ImageView);
        faceImageViews[17] = (ImageView) findViewById(R.id.e017ImageView);
        faceImageViews[18] = (ImageView) findViewById(R.id.e018ImageView);
        faceImageViews[19] = (ImageView) findViewById(R.id.e019ImageView);
        faceImageViews[20] = (ImageView) findViewById(R.id.e020ImageView);
        faceImageViews[21] = (ImageView) findViewById(R.id.e021ImageView);
        faceImageViews[22] = (ImageView) findViewById(R.id.e022ImageView);
        faceImageViews[23] = (ImageView) findViewById(R.id.e023ImageView);
        faceImageViews[24] = (ImageView) findViewById(R.id.e024ImageView);
        faceImageViews[25] = (ImageView) findViewById(R.id.e025ImageView);
        faceImageViews[26] = (ImageView) findViewById(R.id.e026ImageView);
        faceImageViews[27] = (ImageView) findViewById(R.id.e027ImageView);
        faceImageViews[28] = (ImageView) findViewById(R.id.e028ImageView);
        faceImageViews[29] = (ImageView) findViewById(R.id.e029ImageView);
        faceImageViews[30] = (ImageView) findViewById(R.id.e030ImageView);
        faceImageViews[31] = (ImageView) findViewById(R.id.e031ImageView);
        faceImageViews[32] = (ImageView) findViewById(R.id.e032ImageView);
        faceImageViews[33] = (ImageView) findViewById(R.id.e033ImageView);
        faceImageViews[34] = (ImageView) findViewById(R.id.e034ImageView);
        faceImageViews[35] = (ImageView) findViewById(R.id.e035ImageView);
        faceImageViews[36] = (ImageView) findViewById(R.id.e036ImageView);
        faceImageViews[37] = (ImageView) findViewById(R.id.e037ImageView);
        faceImageViews[38] = (ImageView) findViewById(R.id.e038ImageView);
        faceImageViews[39] = (ImageView) findViewById(R.id.e039ImageView);
        faceImageViews[40] = (ImageView) findViewById(R.id.e040ImageView);
        faceImageViews[41] = (ImageView) findViewById(R.id.e041ImageView);
        faceImageViews[42] = (ImageView) findViewById(R.id.e042ImageView);
        faceImageViews[43] = (ImageView) findViewById(R.id.e043ImageView);
        faceImageViews[44] = (ImageView) findViewById(R.id.e044ImageView);
        faceImageViews[45] = (ImageView) findViewById(R.id.e045ImageView);
        faceImageViews[46] = (ImageView) findViewById(R.id.e046ImageView);
        faceImageViews[47] = (ImageView) findViewById(R.id.e047ImageView);

        commentTitleTextView = (TextView) findViewById(R.id.commentTitleTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        startBoolean = false;
        praiseBoolean = false;
        collectionBoolean = false;
        idString = mActivity.getIntent().getStringExtra("id");
        timeString = mActivity.getIntent().getStringExtra("time");
        titleString = mActivity.getIntent().getStringExtra("title");
        imageString = mActivity.getIntent().getStringExtra("image");

        if (TextUtil.isEmpty(idString)) {
            ToastUtil.show(mActivity, "传入参数有误");
            mApplication.finishActivity(mActivity);
            return;
        }

        rid = "-1";
        mArrayList = new ArrayList<>();
        mAdapter = new ManCommentListAdapter(mApplication, mActivity, mArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapter(mAdapter);

        praiseTextView.setText(mActivity.getIntent().getStringExtra("praise"));
        commentTextView.setText(mActivity.getIntent().getStringExtra("comment"));
        collectTextView.setText(mActivity.getIntent().getStringExtra("collection"));
        inputLinearLayout.setVisibility(View.GONE);
        faceLinearLayout.setVisibility(View.GONE);
        ControlUtil.setFocusable(contentEditText);
        ControlUtil.setWebView(goneWebView);
        ControlUtil.setWebView(mWebView);
        titleTextView.setText("");

        getJson();
        isPraise();
        getComment();
        isCollection();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        praiseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isEmpty(mApplication.userKeyString)) {
                    mApplication.startLogin(mActivity);
                } else {
                    if (praiseBoolean) {
                        cancelPraise();
                    } else {
                        praise();
                    }
                }
            }
        });

        collectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmpty(mApplication.userKeyString)) {
                    mApplication.startLogin(mActivity);
                } else {
                    if (collectionBoolean) {
                        cancelCollection();
                    } else {
                        collection();
                    }
                }
            }
        });

        commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputLinearLayout.getVisibility() == View.GONE) {
                    AndroidUtil.showKeyboard(contentEditText);
                    inputLinearLayout.setVisibility(View.VISIBLE);
                    inputLinearLayout.startAnimation(mApplication.showAlphaAnimation);
                    new MyCountTime(500, 250) {
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }.start();
                } else {
                    AndroidUtil.hideKeyboard(contentEditText);
                    inputLinearLayout.setVisibility(View.GONE);
                    inputLinearLayout.startAnimation(mApplication.goneAlphaAnimation);
                }
            }
        });

        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ShareActivity.class);
                intent.putExtra("title", "文章分享");
                intent.putExtra("name", titleString);
                intent.putExtra("jingle", "");
                intent.putExtra("image", imageString);
                intent.putExtra("link", mApplication.apiUrlString + "act=man&op=read_share&id=" + idString);
                mApplication.startActivity(mActivity, intent);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                goneWebView.loadUrl(url);
                return true;
            }

        });

        goneWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String link) {
                super.onPageFinished(view, link);
                if (!link.contains("about:blank")) {
                    if (!startBoolean) {
                        if (link.contains("gc_id")) {
                            String gc_id = link.substring(link.lastIndexOf("=") + 1, link.length());
                            mApplication.startCategory(mActivity, gc_id);
                            startBoolean = true;
                        } else if (link.contains("goods_id")) {
                            String goods_id = link.substring(link.lastIndexOf("=") + 1, link.length());
                            mApplication.startGoods(mActivity, goods_id);
                            startBoolean = true;
                        } else if (link.contains("product_list.html") && !link.contains("?")) {
                            mApplication.startKeyword(mActivity, "");
                            startBoolean = true;
                        } else if (link.contains("product_list.html") && link.contains("keyword")) {
                            mApplication.startKeyword(mActivity, link.substring(link.lastIndexOf("=") + 1, link.length()));
                            startBoolean = true;
                        }
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtil.isEmpty(mApplication.userKeyString)) {
                    mApplication.startLogin(mActivity);
                } else {
                    comment();
                }
            }
        });

        faceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faceLinearLayout.getVisibility() == View.GONE) {
                    AndroidUtil.hideKeyboard(view);
                    faceLinearLayout.setVisibility(View.VISIBLE);
                    faceImageView.setImageResource(R.mipmap.ic_input_keyboard_comment);
                } else {
                    faceLinearLayout.setVisibility(View.GONE);
                    faceImageView.setImageResource(R.mipmap.ic_input_face_comment);
                    AndroidUtil.showKeyboard(view);
                }
            }
        });

        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentEditText.isFocusable()) {
                    AndroidUtil.showKeyboard(view);
                    faceLinearLayout.setVisibility(View.GONE);
                    faceImageView.setImageResource(R.mipmap.ic_input_face_comment);
                }
            }
        });

        mScrollView.setOnScrollListener(new CustomScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                if (!rid.equals("-1")) {
                    rid = "-1";
                    contentEditText.setHint("请输入内容");
                    AndroidUtil.hideKeyboard(contentEditText);
                }
            }
        });

        mAdapter.setOnItemClickListener(new ManCommentListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(String comment_id, String nick_name) {
                rid = comment_id;
                String hint = "回复：" + nick_name;
                contentEditText.setHint(hint);
                ControlUtil.setFocusable(contentEditText);
                if (faceLinearLayout.getVisibility() == View.GONE) {
                    AndroidUtil.showKeyboard(contentEditText);
                }
            }
        });

        for (int i = 0; i < faceImageViews.length; i++) {
            final String sNum;
            if (i < 10) {
                sNum = "e00" + i;
            } else {
                sNum = "e0" + i;
            }
            faceImageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = TextUtil.replaceFace(contentEditText.getText()) + "<img src=\"" + sNum + "\">";
                    contentEditText.setText(Html.fromHtml(content, mApplication.mImageGetter, null));
                    contentEditText.setSelection(contentEditText.getText().length());
                }
            });
        }

    }

    private void getJson() {

        DialogUtil.progress(mActivity);

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "man");
        ajaxParams.put("op", "get_detailed");
        ajaxParams.put("id", idString);

        mApplication.mFinalHttp.get(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                String header = "<p style='font-size:18px;color:#FF5001'>" + titleString + "</p>";
                header = header + "<p style='font-size:14px;color:#666666'>来源 : 男士购 | 发布时间 : " + TimeUtil.decode(timeString) + "</p>";
                header = header + "<hr size='1px' color='#CCCCCC'>";
                String dataString = header + mApplication.getJsonData(o.toString()).replace("style", "other");
                mWebView.loadDataWithBaseURL(null, TextUtil.encodeHtml(dataString), "text/html", "UTF-8", null);
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
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        getJson();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    private void getComment() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("act", "man_comment");
        ajaxParams.put("op", "get_list");
        ajaxParams.put("id", idString);

        mApplication.mFinalHttp.post(mApplication.apiUrlString + ajaxParams.toString(), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                LogUtil.show(o.toString());
                if (TextUtil.isJson(o.toString())) {
                    try {
                        String data = mApplication.getJsonData(o.toString());
                        if (data.equals("[]")) {
                            commentTitleTextView.setText("评论列表（暂无评论）");
                        } else {
                            mArrayList.clear();
                            JSONArray jsonArray = new JSONArray(data);
                            commentTitleTextView.setText("评论列表（共 ");
                            commentTitleTextView.append(jsonArray.length() + "");
                            commentTitleTextView.append(" 条）");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mArrayList.add(new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.getString(i))));
                            }
                            commentTitleTextView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getCommentFailure();
                    }
                } else {
                    getCommentFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getCommentFailure();
            }
        });

    }

    private void getCommentFailure() {

        if (!mActivity.isFinishing()) {
            new AlertDialog
                    .Builder(mActivity)
                    .setCancelable(false)
                    .setTitle("是否重试?")
                    .setMessage("读取数据失败")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getCommentFailure();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }

    }

    private void praise() {

        praiseBoolean = true;
        String temp = (Integer.parseInt(praiseTextView.getText().toString()) + 1) + "";
        praiseTextView.setText(temp);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_praise");
        ajaxParams.putOp("praise");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);

    }

    private void isPraise() {

        if (TextUtil.isEmpty(mApplication.userKeyString)) {
            return;
        }

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_praise");
        ajaxParams.putOp("praise_check");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                praiseBoolean = mApplication.getJsonSuccess(o.toString());
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                new MyCountTime(2000, 1000) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        isPraise();
                    }
                }.start();
            }
        });

    }

    private void cancelPraise() {

        praiseBoolean = false;
        String temp = (Integer.parseInt(praiseTextView.getText().toString()) - 1) + "";
        ToastUtil.show(mActivity, "取消赞成功");
        praiseTextView.setText(temp);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_praise");
        ajaxParams.putOp("praise_cancel");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);

    }

    private void comment() {

        if (TextUtil.isEmpty(contentEditText.getText().toString())) {
            ControlUtil.setFocusable(contentEditText);
            if (faceLinearLayout.getVisibility() == View.GONE) {
                AndroidUtil.showKeyboard(contentEditText);
            }
        } else {
            contentEditText.setEnabled(false);
            AndroidUtil.hideKeyboard(contentEditText);
            AjaxParams ajaxParams = new AjaxParams();
            ajaxParams.put("act", "man_comment");
            ajaxParams.put("op", "comment");
            ajaxParams.put("id", idString);
            ajaxParams.put("rid", rid);
            ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
            ajaxParams.put("content", TextUtil.replaceFace(contentEditText.getText()));
            mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
                @Override
                public void onSuccess(Object o) {
                    super.onSuccess(o);
                    contentEditText.setEnabled(true);
                    if (mApplication.getJsonSuccess(o.toString())) {
                        ToastUtil.showSuccess(mActivity);
                        contentEditText.setText("");
                        getComment();
                    } else {
                        ToastUtil.showFailure(mActivity);
                    }
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    ToastUtil.showFailure(mActivity);
                    contentEditText.setEnabled(true);
                }
            });
        }

    }

    private void collection() {

        collectionBoolean = true;

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_collection");
        ajaxParams.putOp("collection");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);

    }

    private void isCollection() {

        if (TextUtil.isEmpty(mApplication.userKeyString)) {
            return;
        }

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_collection");
        ajaxParams.putOp("collection_check");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));

        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                collectionBoolean = mApplication.getJsonSuccess(o.toString());
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                new MyCountTime(2000, 1000) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        isCollection();
                    }
                }.start();
            }
        });

    }

    private void cancelCollection() {

        collectionBoolean = false;

        KeyAjaxParams ajaxParams = new KeyAjaxParams(mApplication);
        ajaxParams.putAct("man_collection");
        ajaxParams.putOp("collection_cancel");
        ajaxParams.put("id", idString);
        ajaxParams.put("uid", mApplication.userHashMap.get("member_id"));
        mApplication.mFinalHttp.post(mApplication.apiUrlString, ajaxParams, null);

    }

    private void returnActivity() {

        if (inputLinearLayout.getVisibility() == View.VISIBLE) {
            inputLinearLayout.setVisibility(View.GONE);
            inputLinearLayout.startAnimation(mApplication.goneAlphaAnimation);
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}