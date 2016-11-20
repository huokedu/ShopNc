package top.yokey.nsg.utility;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast = null;

    //作用：显示一个 Toast
    public static void show(Activity activity, String string) {

        if (toast == null) {
            toast = Toast.makeText(activity, string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();

    }

    //作用：显示失败 Toast
    public static void showFailure(Activity activity) {

        String content = "失败了,请重试...";

        if (toast == null) {
            toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();

    }

    //作用：显示网络链接失败 Toast
    public static void showFailureNetwork(Activity activity) {

        String content = "网络链接失败...";

        if (toast == null) {
            toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();

    }

    //作用：显示失败 Toast
    public static void showSuccess(Activity activity) {

        String content = "成功...";

        if (toast == null) {
            toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();

    }

}