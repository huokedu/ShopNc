package top.yokey.nsg.activity.home;

import android.Manifest;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CheckActivity extends ActivityGroup implements ActivityCompat.OnRequestPermissionsResultCallback {

    private boolean isNeedCheck = true;

    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] per, int[] param) {
        if (requestCode == 0) {
            if (!verifyPermissions(param)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认您的选择");
        builder.setMessage("缺少必要的运行权限，是否跳到设置？");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionsList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionsList && needRequestPermissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, needRequestPermissionsList.toArray(new String[needRequestPermissionsList.size()]), 0);
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionsList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissionsList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    needRequestPermissionsList.add(perm);
                }
            }
        }
        return needRequestPermissionsList;
    }

}