package com.bd.base.utils;

import android.util.Log;

public class LogUtils {
    private static boolean debug = true;
    private static final String tag = "datdb";


    public static void init(boolean isDebug) {
        debug = isDebug;
    }

    public static void d(Object object) {
        if (debug) {
            if (object != null) Log.d(tag, String.valueOf(object));
            else Log.d(tag, "null");
        }
    }

    public static void e(Object object) {
        if (debug) {
            if (object != null) Log.e(tag, String.valueOf(object));
            else Log.e(tag, "null");
        }
    }
}
