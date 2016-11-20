package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import top.yokey.nsg.activity.goods.GoodsListActivity;
import top.yokey.nsg.activity.store.StoreListActivity;
import top.yokey.nsg.adapter.SearchListAdapter;
import top.yokey.nsg.R;
import top.yokey.nsg.utility.ControlUtil;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class SearchActivity extends AppCompatActivity {

    private Activity mActivity;
    private NcApplication mApplication;

    private String typeString;
    private String keywordString;

    private ImageView backImageView;
    private EditText keywordEditText;
    private ImageView searchImageView;

    private Spinner mSpinner;
    private TextView tipsTextView;
    private TextView stateTextView;
    private RecyclerView mListView;
    private SearchListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<HashMap<String, String>> mArrayList;
    private ArrayList<HashMap<String, String>> tempArrayList;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            returnActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
        keywordEditText = (EditText) findViewById(R.id.keywordEditText);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);

        mSpinner = (Spinner) findViewById(R.id.typeSpinner);
        tipsTextView = (TextView) findViewById(R.id.tipsTextView);
        stateTextView = (TextView) findViewById(R.id.stateTextView);
        mListView = (RecyclerView) findViewById(R.id.mainListView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

    }

    private void initData() {

        mActivity = this;
        mApplication = (NcApplication) getApplication();

        mArrayList = new ArrayList<>();
        tempArrayList = new ArrayList<>();
        mAdapter = new SearchListAdapter(mApplication, mActivity, tempArrayList);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"宝贝", "店铺"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        ControlUtil.setSwipeRefreshLayout(mSwipeRefreshLayout);

        getJson();

    }

    private void initEven() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnActivity();
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keywordString = keywordEditText.getText().toString();
                if (!TextUtil.isEmpty(keywordString)) {
                    Intent intent = new Intent();
                    if (typeString.equals("宝贝")) {
                        intent.setClass(mActivity, GoodsListActivity.class);
                    } else {
                        intent.setClass(mActivity, StoreListActivity.class);
                    }
                    intent.putExtra("type", "keyword");
                    intent.putExtra("keyword", keywordString);
                    mApplication.startActivity(mActivity, intent);
                    mApplication.finishActivity(mActivity);
                    saveJson();
                } else {
                    ToastUtil.show(mActivity, "关键字不能为空");
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getJson();
                    }
                }, 1000);
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14.0f);
                typeString = textView.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "清除搜索记录？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                                mApplication.mSharedPreferencesEditor.putString("json_search_list", "").apply();
                                getJson();
                            }
                        }
                );
            }
        });

    }

    private void getJson() {

        String json = mApplication.mSharedPreferences.getString("json_search_list", "");

        if (json.isEmpty() || json.equals("[]")) {
            stateTextView.setVisibility(View.GONE);
            tipsTextView.setVisibility(View.VISIBLE);
            tipsTextView.setText("暂无搜索记录\n\n赶紧搜索吧");
        } else {
            try {
                mArrayList.clear();
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", jsonObject.getString("title"));
                    hashMap.put("type", jsonObject.getString("type"));
                    mArrayList.add(hashMap);
                }
                tempArrayList.clear();
                for (int i = mArrayList.size() - 1; i >= 0; i--) {
                    tempArrayList.add(mArrayList.get(i));
                }
                stateTextView.setText("清除记录");
                stateTextView.setVisibility(View.VISIBLE);
                tipsTextView.setVisibility(View.GONE);
                stateTextView.setTextColor(Color.RED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();

    }

    private void saveJson() {

        if (mArrayList.size() != 0) {
            for (int i = 0; i < mArrayList.size(); i++) {
                String title = mArrayList.get(i).get("title");
                String type = mArrayList.get(i).get("type");
                if (title.equals(keywordString) && type.equals(typeString)) {
                    mArrayList.remove(i);
                    break;
                }
            }
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", keywordString);
        hashMap.put("type", typeString);
        mArrayList.add(hashMap);

        mApplication.mSharedPreferencesEditor.putString("json_search_list", TextUtil.arrayListToJson(mArrayList)).apply();
        keywordEditText.setText("");
        keywordString = "";
        getJson();

    }

    private void returnActivity() {

        if (keywordEditText.getText().length() != 0) {
            keywordEditText.setText("");
        } else {
            mApplication.finishActivity(mActivity);
        }

    }

}