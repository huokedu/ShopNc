package top.yokey.nsg.utility;

import android.text.Editable;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    //作用：判断是不是 JSON 数据
    public static boolean isJson(String string) {

        if (!isEmpty(string)) {
            if (string.contains("{") && string.contains("}")) {
                return true;
            }
        }

        return false;

    }

    //作用：判断为不为空
    public static boolean isEmpty(String string) {

        return string == null
                || string.isEmpty()
                || string.length() == 0
                || string.equals("")
                || string.equals("null");

    }

    //作用：判断是不是一个URL
    public static boolean isUrlAddress(String url) {
        return url.contains("http") || url.contains("www.") || url.contains(".com") || url.contains(".cn");
    }

    //作用：判断是不是一个邮箱地址
    public static boolean isEmailAddress(String string) {

        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(string);
        return matcher.matches();

    }

    //作用：判断是不是一个电话号码
    public static boolean isMobileNumber(String mobile) {

        String reg = "^((13[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(mobile);
        return m.matches();

    }

    //作用：转换成适合手机阅读的HTML
    public static String encodeHtml(String string) {

        string = "<html><head><style type='text/css'>body{padding:8px;line-height:28px;}p,span,section,img,table,embed,input,dl,dd,tr,td{width:100%;padding:0px;margin:0px;color:#333333;line-height:28px;}</style></head><body>" + string + "</body></html>";
        return string;

    }

    //作用：替换HTML表情
    public static String replaceFace(Editable editable) {

        return Html.toHtml(editable)
                .replace("<imgsrc", "<img src")
                .replace("<p dir=\"ltr\">", "")
                .replace("<p dir=ltr>", "")
                .replace("</p>", "")
                .replace("\n", "");

    }

    //作用：圈子内容转HTML
    public static String circleToHtml(String string) {

        string = string.replace("[IMG]http://", "<img src='http://");
        string = string.replace(".jpg[/IMG]", ".jpg' />");

        return string;

    }

    //作用：String 转 Vector
    public static Vector<String> encodeImage(String content) {
        Vector<String> vector = new Vector<>();
        if (!TextUtil.isEmpty(content)) {
            content = content.replace("[", "").replace("]", "");
            if (!content.contains(",")) {
                if (!TextUtil.isEmpty(content)) {
                    vector.add(content);
                }
            } else {
                while (content.contains(",")) {
                    String temp = content.substring(0, content.indexOf(","));
                    content = content.substring(content.indexOf(",") + 1, content.length());
                    vector.add(temp.replace(" ", ""));
                }
                if (!TextUtil.isEmpty(content)) {
                    vector.add(content.replace(" ", ""));
                }
            }
        }
        return vector;
    }

    //作用：JSONObject 转 HashMap
    public static HashMap<String, String> jsonObjectToHashMap(String string) {

        try {
            HashMap<String, String> hashMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject(string);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                hashMap.put(key, value);
            }
            return hashMap;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    //作用：JSONObject 转 ArrayList 此方法 key 会以 value 方式存储
    public static ArrayList<HashMap<String, String>> jsonObjectToArrayList(String string) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(string);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                HashMap<String, String> hashMap = new HashMap<>();
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                hashMap.put("key", key);
                hashMap.put("value", value);
                arrayList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;

    }

    //作用：ArrayList 转成 Json
    public static String arrayListToJson(ArrayList<HashMap<String, String>> arrayList) {

        if (arrayList.isEmpty()) {
            return "null";
        }

        String json = "[";

        for (int i = 0; i < arrayList.size(); i++) {
            String temp = "{";
            HashMap<String, String> hashMap = new HashMap<>(arrayList.get(i));
            Iterator iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = hashMap.get(key);
                temp += "\"" + key + "\"" + ":" + "\"" + value + "\",";
            }
            temp = temp.substring(0, temp.length() - 1);
            temp = temp + "},";
            json += temp;
        }

        json = json.substring(0, json.length() - 1);
        json = json + "]";

        return json;

    }

}