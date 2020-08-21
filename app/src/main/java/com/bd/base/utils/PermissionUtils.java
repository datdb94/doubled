package com.bd.base.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {
    public static boolean checkPermission(Activity activity, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, int requestCode, String... permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permission, requestCode);
        }
    }

    public static boolean checkGrantResults(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
