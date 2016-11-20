package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Vector;

import top.yokey.nsg.R;
import top.yokey.nsg.system.MyCountTime;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.DisplayUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.NetworkUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

public class LoadActivity extends AppCompatActivity {

    private Activity mActivity;
    private MyHandler mHandler;
    private NcApplication mApplication;

    private boolean loadBoolean;
    private boolean leaveBoolean;
    private boolean threadBoolean;
    private boolean versionBoolean;

    private String advertLinkString;
    private String advertImageString;
    private String systemVersionString;
    private String systemContentString;
    private String systemDownloadString;
    private String systemControlString;

    private ImageView mImageView;

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
        setContentView(R.layout.activity_load);
        initView();
        initData();
        initEven();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        leaveBoolean = false;
        if (loadBoolean) {
            startMain();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadBoolean = false;
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

        mImageView = (ImageView) findViewById(R.id.mainImageView);

    }

    private void initData() {

        mActivity = this;
        mHandler = new MyHandler(LoadActivity.this);
        mApplication = (NcApplication) getApplication();

        loadBoolean = false;
        leaveBoolean = false;
        threadBoolean = true;
        versionBoolean = false;

        advertLinkString = "";
        advertImageString = "";
        systemVersionString = "";
        systemContentString = "";
        systemDownloadString = "";
        systemControlString = "";

        if (NetworkUtil.isConnection(mActivity)) {
            //如果网络链接存在，获取启动页广告
            getAdvert();
        } else {
            //如果网络链接不存在 提示 并且 开启新线程一秒判断一次网络是否存在
            ToastUtil.showFailureNetwork(mActivity);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (threadBoolean) {
                        try {
                            if (NetworkUtil.isConnection(mActivity)) {
                                mHandler.sendEmptyMessage(0);
                                threadBoolean = false;
                            } else {
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        //设置全屏
        DisplayUtil.setFullScreen(mActivity);

    }

    private void initEven() {

        //广告图片
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.isConnection(mActivity)) {
                    if (!TextUtil.isEmpty(advertLinkString)) {
                        Intent intent = new Intent(mActivity, BrowserActivity.class);
                        intent.putExtra("model", "normal");
                        intent.putExtra("link", advertLinkString);
                        mApplication.startActivity(mActivity, intent);
                        leaveBoolean = true;
                    }
                } else {
                    mApplication.startSetting(mActivity, "Wifi");
                }
            }
        });

    }

    private void getAdvert() {

        //读取历史的广告信息
        advertLinkString = mApplication.mSharedPreferences.getString("load_advert_link", "");
        advertImageString = mApplication.mSharedPreferences.getString("load_advert_image", "");

        //下载广告
        if (!TextUtil.isEmpty(advertImageString)) {
            ImageLoader.getInstance().displayImage(advertImageString, mImageView);
        }

        //获取新的广告
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("submit", "get_load");
        mApplication.mFinalHttp.post(mApplication.urlString + "android/api/advert.php", ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    try {
                        JSONArray jsonArray = new JSONArray(o.toString());
                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                        advertLinkString = jsonObject.getString("link");
                        advertImageString = jsonObject.getString("image");
                        mApplication.mSharedPreferencesEditor.putString("load_advert_link", advertLinkString);
                        mApplication.mSharedPreferencesEditor.putString("load_advert_image", advertImageString);
                        mApplication.mSharedPreferencesEditor.apply();
                        getSystem();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getAdvertFailure();
                    }
                } else {
                    getAdvertFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getAdvertFailure();
            }
        });


    }

    private void getAdvertFailure() {

        ToastUtil.show(mActivity, "读取广告失败!正在重试...");

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                getAdvert();
            }
        }.start();

    }

    private void getSystem() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("submit", "get");
        mApplication.mFinalHttp.post(mApplication.urlString + "android/api/system.php", ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (TextUtil.isJson(o.toString())) {
                    try {
                        Vector<String> mVector = new Vector<>();
                        JSONArray jsonArray = new JSONArray(o.toString());
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                mVector.add(jsonObject.getString("value"));
                            }
                        }
                        if (mVector.size() >= 4) {
                            systemVersionString = mVector.get(0);
                            systemContentString = mVector.get(1);
                            systemDownloadString = mVector.get(2);
                            systemControlString = mVector.get(3);
                            checkVersion();
                        } else {
                            getSystemFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getSystemFailure();
                    }
                } else {
                    getSystemFailure();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                getSystemFailure();
            }
        });

    }

    private void getSystemFailure() {

        ToastUtil.show(mActivity, "读取系统信息失败!正在重试...");

        new MyCountTime(2000, 1000) {
            @Override
            public void onFinish() {
                getSystem();
            }
        }.start();

    }

    private void checkVersion() {

        if (!systemControlString.contains(mApplication.getVersion() + ":1")) {
            DialogUtil.query(
                    mActivity,
                    "确认您的选择",
                    "该版本已弃用，确认升级程序？",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadApk();
                            DialogUtil.cancel();
                            versionBoolean = true;
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mApplication.finishActivity(mActivity);
                        }
                    });
        } else {
            checkUpdate();
        }

    }

    private void checkUpdate() {

        if (!systemVersionString.contains(mApplication.getVersion())) {
            DialogUtil.query(
                    mActivity,
                    "发现新版本",
                    Html.fromHtml(systemContentString),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadApk();
                            versionBoolean = false;
                            DialogUtil.cancel();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startMain();
                        }
                    });
        } else {

            new CountDownTimer(1000, 500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    startMain();
                }
            }.start();

        }

    }

    private void downloadApk() {

        final Dialog dialog = new AlertDialog.Builder(mActivity).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_query);
        final TextView titleTextView = (TextView) window.findViewById(R.id.titleTextView);
        final TextView contentTextView = (TextView) window.findViewById(R.id.contentTextView);
        final TextView confirmTextView = (TextView) window.findViewById(R.id.confirmTextView);
        final TextView cancelTextView = (TextView) window.findViewById(R.id.cancelTextView);
        cancelTextView.setVisibility(View.GONE);
        confirmTextView.setText("取消");

        titleTextView.setText("正在下载");
        contentTextView.setText("已下载");
        contentTextView.append(": 0 %");

        final String filePath = FileUtil.getDownPath() + "/nsg" + mApplication.getVersion() + ".apk";

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        final HttpHandler httpHandler;

        httpHandler = mApplication.mFinalHttp.download(systemDownloadString, filePath, new AjaxCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
                contentTextView.setText("已下载");
                contentTextView.append(": 0 %");
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                int progress;
                if (current != count && current != 0) {
                    progress = (int) (current / (float) count * 100);
                } else {
                    progress = 100;
                }
                String progressString = "已下载：" + progress + " %";
                contentTextView.setText(progressString);
            }

            @Override
            public void onSuccess(File t) {
                super.onSuccess(t);
                mApplication.startInstallApk(mActivity, new File(filePath));
                dialog.cancel();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(mActivity);
                dialog.cancel();
                if (!versionBoolean) {
                    startMain();
                } else {
                    mApplication.finishActivity(mActivity);
                }
            }
        });

        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.query(
                        mActivity,
                        "确认您的选择",
                        "取消下载？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!versionBoolean) {
                                    httpHandler.stop();
                                    dialog.cancel();
                                    startMain();
                                } else {
                                    mApplication.finishActivity(mActivity);
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogUtil.cancel();
                            }
                        });
            }
        });

    }

    private void startMain() {

        loadBoolean = true;

        if (leaveBoolean) {
            return;
        }

        mApplication.startActivity(mActivity, new Intent(mActivity, MainActivity.class));
        mApplication.finishActivity(mActivity);

    }

    private void returnActivity() {

        DialogUtil.query(
                mActivity,
                "确认您的选择",
                "退出程序？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.cancel();
                        mApplication.finishActivity(mActivity);
                    }
                }
        );

    }

    static class MyHandler extends Handler {

        private WeakReference<LoadActivity> mWeakActivity;

        MyHandler(LoadActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoadActivity activity = mWeakActivity.get();
            if (activity != null) {
                activity.getAdvert();
            }
        }

    }

}