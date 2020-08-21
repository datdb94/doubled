package com.bd.base;

import android.app.Application;

import com.bd.base.utils.LogUtils;
import com.bd.base.utils.SharedUtils;

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtils.init(BuildConfig.DEBUG);
        SharedUtils.init(this);
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
