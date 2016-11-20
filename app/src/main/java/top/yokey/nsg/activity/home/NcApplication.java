package top.yokey.nsg.activity.home;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieManager;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import top.yokey.nsg.R;
import top.yokey.nsg.activity.goods.GoodsDetailedActivity;
import top.yokey.nsg.activity.goods.GoodsListActivity;
import top.yokey.nsg.activity.mine.LoginActivity;
import top.yokey.nsg.activity.seller.SellerLoginActivity;
import top.yokey.nsg.activity.store.StoreActivity;
import top.yokey.nsg.system.KeyAjaxParams;
import top.yokey.nsg.system.SellerAjaxParams;
import top.yokey.nsg.utility.DialogUtil;
import top.yokey.nsg.utility.FileUtil;
import top.yokey.nsg.utility.TextUtil;
import top.yokey.nsg.utility.ToastUtil;

/*
*
* 作者：Yokey软件工作室
*
* 企鹅：1002285057
*
* 网址：www.yokey.top
*
* 作用：全局 Application
*
* 更新：2016-09-28
*
* PS：本人的技术并不算很高，所以各位大神看到代码有些不规范或者能优化的更好的，可以加我QQ给我提意见
*
* PS：请大家尊重我的劳动成果，尊重版权，如果这份代码对您有帮助，如果您手头富裕，请支持一下我吧！
*
* PS：支付宝账号 15207713074 谢谢！
*
* PS：程序会一直更新一直开发！
*
* PS：截止 2016-04-11
*
* 问：为什么大量使用View？
*
* 答：之前有用过用背景色来画出横线，这在高端机上没什么问题，遇到低端机就是一片白，所以每条横线都用View来画
*
* 问：....
*
*/

public class NcApplication extends Application {

    public static final int CODE_LOGIN = 0;
    public static final int CODE_REGISTER = 1;

    public static final int CODE_MINE_MODIFY = 5;

    public static final int CODE_CHOOSE_AREA = 10;
    public static final int CODE_CHOOSE_PHOTO = 11;
    public static final int CODE_CHOOSE_CAMERA = 12;
    public static final int CODE_CHOOSE_ADDRESS = 13;
    public static final int CODE_CHOOSE_INVOICE = 14;
    public static final int CODE_CHOOSE_PHOTO_CROP = 15;

    public static final int CODE_ADDRESS_ADD = 20;
    public static final int CODE_ADDRESS_EDIT = 21;

    public static final int CODE_INVOICE_ADD = 25;

    public static final int CODE_BIND_EMAIL = 31;
    public static final int CODE_BIND_MOBILE = 31;
    public static final int CODE_BIND_PAY_PASS = 32;

    public static final int CODE_ORDER_PAY = 35;
    public static final int CODE_ORDER_REFUND = 36;
    public static final int CODE_ORDER_DETAILED = 37;
    public static final int CODE_ORDER_EVALUATE = 38;

    public static final int CODE_SELLER_ORDER_CANCEL = 50;
    public static final int CODE_SELLER_ORDER_MODIFY = 51;
    public static final int CODE_SELLER_ORDER_DELIVER = 52;
    public static final int CODE_SELLER_ORDER_DETAILED = 52;

    //全局变量
    public Bitmap mBitmap;
    public IWXAPI mIwxapi;
    public Tencent mTencent;
    public FinalHttp mFinalHttp;
    public CookieManager mCookieManager;
    public Html.ImageGetter mImageGetter;
    public AlphaAnimation showAlphaAnimation;
    public AlphaAnimation goneAlphaAnimation;
    public SharedPreferences mSharedPreferences;
    public TranslateAnimation upTranslateAnimation;
    public TranslateAnimation downTranslateAnimation;
    public SharedPreferences.Editor mSharedPreferencesEditor;

    //系统变量
    public String urlString;
    public String apiUrlString;
    public String helpUrlString;
    public String aboutUrlString;
    public String goodsUrlString;
    public String storeUrlString;
    public String findPassUrlString;
    public String circlePicUrlString;
    public String loginQQUrlString;
    public String loginWBUrlString;

    //公用变量
    public ArrayList<HashMap<String, String>>[] orderArrayList;
    public ArrayList<HashMap<String, String>> voucherArrayList;
    public ArrayList<HashMap<String, String>> redPackArrayList;

    //用户信息
    public HashMap<String, String> userHashMap;
    public String userUsernameString;
    public String userKeyString = "";
    public String userIdString = "";

    //卖家信息
    public HashMap<String, String> storeHashMap;
    public String sellerNameString;
    public String sellerKeyString;

    @Override
    public void onCreate() {
        super.onCreate();

        //全局变量初始化
        mFinalHttp = new FinalHttp();
        mFinalHttp.configTimeout(5000);//超时时间 5 秒
        mFinalHttp.configCharset("UTF-8");//默认编码 UTF-8
        mCookieManager = CookieManager.getInstance();
        mSharedPreferences = this.getSharedPreferences("yokey_nsg", MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.apply();
        showAlphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        showAlphaAnimation.setDuration(1000);
        goneAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        goneAlphaAnimation.setDuration(1000);
        upTranslateAnimation = new TranslateAnimation(0.0f, 0.0f, 600.0f, 0.0f);
        upTranslateAnimation.setDuration(500);
        downTranslateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 600.0f);
        downTranslateAnimation.setDuration(500);
        mImageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                try {
                    int id = getResources().getIdentifier(source, "mipmap", getPackageName());
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), id);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_img_load_no)
                .showImageOnFail(R.mipmap.ic_img_load_failure)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(4)).build();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(new File(FileUtil.getCachePath())))
                .memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
                .defaultDisplayImageOptions(displayImageOptions)
                .build());

        //系统变量初始化
        urlString = "http://www.nanshig.com/";
        apiUrlString = urlString + "mobile/index.php?";
        helpUrlString = urlString + "android/public/help.html";
        aboutUrlString = urlString + "android/public/about.html";
        circlePicUrlString = urlString + "data/upload/circle/group/";
        storeUrlString = urlString + "wap/tmpl/store.html?store_id=";
        loginQQUrlString = apiUrlString + "act=connect&op=get_qq_oauth2";
        loginWBUrlString = apiUrlString + "act=connect&op=get_sina_oauth2";
        findPassUrlString = urlString + "wap/tmpl/member/find_password.html";
        goodsUrlString = urlString + "wap/tmpl/product_detail.html?goods_id=";

        //公用变量
        orderArrayList = new ArrayList[6];
        voucherArrayList = new ArrayList<>();
        redPackArrayList = new ArrayList<>();
        for (int i = 0; i < orderArrayList.length; i++) {
            orderArrayList[i] = new ArrayList<>();
        }

        //用户信息初始化
        userHashMap = new HashMap<>();
        userIdString = mSharedPreferences.getString("user_id", "");
        userKeyString = mSharedPreferences.getString("user_key", "");
        userUsernameString = mSharedPreferences.getString("user_username", "");

        storeHashMap = new HashMap<>();
        sellerNameString = mSharedPreferences.getString("seller_name", "");
        sellerKeyString = mSharedPreferences.getString("seller_key", "");

        //QQ登陆
        mTencent = Tencent.createInstance("101260555", this);

        //微信配置
        mIwxapi = WXAPIFactory.createWXAPI(this, null);
        mIwxapi.registerApp("wx67bc79b681fee1b3");

        //ShareSDK初始化
        ShareSDK.initSDK(this);

        //友盟统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        //一些子程序
        FileUtil.createDownPath();
        FileUtil.createCachePath();
        FileUtil.createImagePath();

    }

    //获取程序版本
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    //跳到拨号
    public void startCall(Activity activity, String phone) {

        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.show(activity, "未检测到电话程序");
        }

    }

    //跳到聊天
    public void startChat(Activity activity, String id) {

        Intent intent = new Intent(activity, ChatOnlyActivity.class);
        intent.putExtra("u_id", id);
        startActivityLoginSuccess(activity, intent);

    }

    //跳到商品
    public void startGoods(Activity activity, String id) {

        Intent intent = new Intent(activity, GoodsDetailedActivity.class);
        intent.putExtra("goods_id", id);
        startActivity(activity, intent);

    }

    //跳到物流查询
    public void startLogistics(final Activity activity, String id) {

        DialogUtil.progress(activity);

        KeyAjaxParams ajaxParams = new KeyAjaxParams(this);
        ajaxParams.putAct("member_order");
        ajaxParams.putOp("search_deliver");
        ajaxParams.put("order_id", id);

        mFinalHttp.post(apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(getJsonData(o.toString()));
                    Intent intent = new Intent(activity, BrowserActivity.class);
                    intent.putExtra("model", "normal");
                    intent.putExtra("link", jsonObject.getString("deliver_info"));
                    startActivity(activity, intent);
                } catch (JSONException e) {
                    ToastUtil.showFailure(activity);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(activity);
                DialogUtil.cancel();
            }
        });

    }

    //跳到物流查询
    public void startLogisticsSeller(final Activity activity, String id, String buyer_id) {

        DialogUtil.progress(activity);

        SellerAjaxParams ajaxParams = new SellerAjaxParams(this);
        ajaxParams.putAct("seller_order");
        ajaxParams.putOp("order_deliver_search");
        ajaxParams.put("order_id", id);
        ajaxParams.put("buyer_id", buyer_id);

        mFinalHttp.post(apiUrlString, ajaxParams, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                DialogUtil.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(getJsonData(o.toString()));
                    Intent intent = new Intent(activity, BrowserActivity.class);
                    intent.putExtra("model", "normal");
                    intent.putExtra("link", jsonObject.getString("deliver_info"));
                    startActivity(activity, intent);
                } catch (JSONException e) {
                    ToastUtil.showFailure(activity);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.showFailure(activity);
                DialogUtil.cancel();
            }
        });

    }

    //跳到店铺
    public void startStore(Activity activity, String id) {

        Intent intent = new Intent(activity, StoreActivity.class);
        intent.putExtra("store_id", id);
        startActivity(activity, intent);

    }

    //跳到专题
    public void startSpecial(Activity activity, String id) {

        Intent intent = new Intent(activity, SpecialActivity.class);
        intent.putExtra("special_id", id);
        startActivity(activity, intent);

    }

    //跳到关键字
    public void startKeyword(Activity activity, String id) {

        Intent intent = new Intent();
        intent.setClass(activity, GoodsListActivity.class);
        intent.putExtra("type", "keyword");
        intent.putExtra("keyword", id);
        startActivity(activity, intent);

    }

    //跳到分类
    public void startCategory(Activity activity, String gc_id) {

        Intent intent = new Intent();
        intent.setClass(activity, GoodsListActivity.class);
        intent.putExtra("type", "category");
        intent.putExtra("keyword", gc_id);
        startActivity(activity, intent);

    }

    //跳到登录
    public void startLogin(Activity activity) {

        activity.startActivity(new Intent(activity, LoginActivity.class));

    }

    //跳到相册
    public void startPhoto(Activity activity) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        activity.startActivityForResult(intent, CODE_CHOOSE_PHOTO);

    }

    //跳到拍照
    public void startCamera(Activity activity, File file) {

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            activity.startActivityForResult(intent, CODE_CHOOSE_CAMERA);
        } catch (Exception e) {
            ToastUtil.show(activity, "未检测到相机");
        }

    }

    //跳到图片裁剪
    public void startPhotoCrop(Activity activity, String path) {

        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra("path", path);
        activity.startActivityForResult(intent, CODE_CHOOSE_PHOTO_CROP);

    }

    //跳转到设置
    public void startSetting(Activity activity, String string) {

        try {
            switch (string) {
                case "All":
                    activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    break;
                case "Wifi":
                    activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
                default:
                    activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //跳转到安装 APK
    public void startInstallApk(Activity activity, File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        activity.startActivity(intent);

    }

    //开始一个 Activity
    public void startActivity(Activity activity, Intent intent) {

        activity.startActivity(intent);

    }

    //开始一个 Activity 并带返回参数
    public void startActivity(Activity activity, Intent intent, int i) {

        activity.startActivityForResult(intent, i);

    }

    //开始一个 Activity 并检测是否已登录成功
    public void startActivityLoginSuccess(Activity activity, Intent intent) {

        if (TextUtil.isEmpty(userKeyString)) {
            activity.startActivity(new Intent(activity, LoginActivity.class));
            return;
        }

        if (userHashMap.isEmpty()) {
            ToastUtil.show(activity, "请等待登录成功");
            return;
        }

        activity.startActivity(intent);

    }

    //开始一个 Activity 并检测是否已登录成功且带返回值
    public void startActivityLoginSuccess(Activity activity, Intent intent, int i) {

        if (TextUtil.isEmpty(userKeyString)) {
            activity.startActivity(new Intent(activity, LoginActivity.class));
            return;
        }

        if (userHashMap.isEmpty()) {
            ToastUtil.show(activity, "请等待登录成功");
            return;
        }

        activity.startActivityForResult(intent, i);

    }

    //开始一个 Activity 并检测是否已商家登录成功
    public void startActivitySellerLoginSuccess(Activity activity, Intent intent) {

        if (TextUtil.isEmpty(sellerKeyString)) {
            activity.startActivity(new Intent(activity, SellerLoginActivity.class));
            return;
        }

        if (storeHashMap.isEmpty()) {
            ToastUtil.show(activity, "请等待登录成功");
            return;
        }

        activity.startActivity(intent);

    }

    //结束一个 Activity
    public void finishActivity(Activity activity) {

        activity.finish();

    }

    //获取返回数据的 JSON 数据的 data
    public String getJsonData(String json) {

        if (!TextUtil.isJson(json)) {
            return "null";
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("datas");
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }

    }

    //获取返回数据的 JSON 数据的 error
    public String getJsonError(String json) {

        if (!TextUtil.isJson(json)) {
            return "null";
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.getString("datas");
            if (data.contains("error")) {
                jsonObject = new JSONObject(data);
                return jsonObject.getString("error");
            } else {
                return "null";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }

    }

    //获取返回数据的 JSON 数据的 hasmore
    public boolean getJsonHasMore(String json) {

        if (!TextUtil.isJson(json)) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("hasmore").equals("true");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    //获取返回数据是否成功
    public boolean getJsonSuccess(String json) {

        return getJsonData(json).equals("1");

    }

}