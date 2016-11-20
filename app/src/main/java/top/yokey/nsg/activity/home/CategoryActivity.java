package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.adapter.Category1ListAdapter;
import top.yokey.nsg.adapter.Category2ListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;

public class CategoryActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private ImageView scanImageView;
    private EditText titleEditText;
    private ImageView messageImageView;

    private RecyclerView category1ListView;
    private Category1ListAdapter category1Adapter;
    private ArrayList<HashMap<String, String>> category1ArrayList;

    private RecyclerView category2ListView;
    private Category2ListAdapter category2Adapter;
    private ArrayList<HashMap<String, String>> category2ArrayList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_category);
        initView();
        initData();
        initEven();
    }

    private void initView() {

        scanImageView = (ImageView) findViewById(R.id.scanImageView);
        titleEditText = (EditText) findViewById(R.id.keywordEditText);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);

        category1ListView = (RecyclerView) findViewById(R.id.category1ListView);
        category2ListView = (RecyclerView) findViewById(R.id.category2ListView);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        titleEditText.setFocusable(false);

        category1ArrayList = new ArrayList<>();
        category1Adapter = new Category1ListAdapter(mActivity, category1ArrayList);
        category1ListView.setLayoutManager(new LinearLayoutManager(this));
        category1ListView.setAdapter(category1Adapter);

        category2ArrayList = new ArrayList<>();
        category2Adapter = new Category2ListAdapter(mApplication, mActivity, category2ArrayList);
        category2ListView.setLayoutManager(new LinearLayoutManager(this));
        category2ListView.setAdapter(category2Adapter);

        getCategory1Json();

    }

    private void initEven() {

        scanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, ScanActivity.class));
            }
        });

        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivity(mActivity, new Intent(mActivity, SearchActivity.class));
            }
        });

        messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.startActivityLoginSuccess(mActivity, new Intent(mActivity, ChatListActivity.class));
            }
        });

        category1Adapter.setOnItemClickListener(new Category1ListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(String id, String value) {
                getCategory2Json(id);
                for (int i = 0; i < category1ArrayList.size(); i++) {
                    String gc_id = category1ArrayList.get(i).get("gc_id");
                    if (gc_id.equals(id)) {
                        category1ArrayList.get(i).put("click", "1");
                    } else {
                        category1ArrayList.get(i).put("click", "0");
                    }
                }
                category1Adapter.notifyDataSetChanged();
            }
        });

    }

    private void getCategory1Json() {

        mApplication.mFinalHttp.get(mApplication.apiUrlString + "act=goods_class", new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("class_list"));
                            if (jsonArray.length() == 8) {
                                for (int i = 0; i < jsonArray.length() - 3; i++) {
                                    HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                    hashMap.put("click", "0");
                                    category1ArrayList.add(hashMap);
                                }
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                    hashMap.put("click", "0");
                                    category1ArrayList.add(hashMap);
                                }
                            }
                            getCategory2Json(category1ArrayList.get(0).get("gc_id"));
                            category1ArrayList.get(0).put("click", "1");
                            category1Adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getCategory1Failure();
                        }
                    } else {
                        getCategory1Failure();
                    }
                } else {
                    getCategory1Failure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getCategory1Failure();
            }
        });

    }

    private void getCategory1Failure() {

        DialogUtil.query(mActivity,
                "确认您的选择",
                "数据加载失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getCategory1Json();
                    }
                });

    }

    private void getCategory2Json(final String gc_id) {

        mApplication.mFinalHttp.get(mApplication.apiUrlString + "act=goods_class&gc_id=" + gc_id, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    String error = mApplication.getJsonError(o.toString());
                    if (TextUtil.isEmpty(error)) {
                        String data = mApplication.getJsonData(o.toString());
                        try {
                            category2ArrayList.clear();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("class_list"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>(TextUtil.jsonObjectToHashMap(jsonArray.get(i).toString()));
                                hashMap.put("gc_class3", "null");
                                category2ArrayList.add(hashMap);
                            }
                            category2Adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            getCategory2Failure(gc_id);
                            e.printStackTrace();
                        }
                    } else {
                        getCategory2Failure(gc_id);
                    }
                } else {
                    getCategory2Failure(gc_id);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getCategory2Failure(gc_id);
            }
        });

    }

    private void getCategory2Failure(final String gc_id) {

        DialogUtil.query(mActivity,
                "确认您的选择",
                "数据加载失败，是否重试？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.cancel();
                        getCategory2Json(gc_id);
                    }
                });

    }

}