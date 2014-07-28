package com.shawnhu.seagull.utils;

import android.app.Activity;

/**
 * Created by shawn on 14-7-28.
 */
public class ActivityUtils {
    static public void recreate(Activity activity) {
        if (activity != null) {
            activity.recreate();
        }
    }
}
