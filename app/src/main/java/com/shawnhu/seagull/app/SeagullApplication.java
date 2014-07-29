package com.shawnhu.seagull.app;

import android.app.Application;

/**
 * Created by shawn on 14-7-29.
 */
public class SeagullApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppPreferences.init(this);
    }
}
