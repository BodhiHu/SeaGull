package com.shawnhu.seagull.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by shawn on 14-7-28.
 */
public class ActivityUtils {
    static public void recreate(Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activity.recreate();
            } else {
                activity.startActivity(new Intent(activity, activity.getClass()));
                activity.finish();
            }
        }
    }

    static public void applyTheme(Activity activity, String key, int themeResId) {
        if (activity != null && key != null && key != "") {
            PreferenceManager.getDefaultSharedPreferences(activity).edit()
                    .putInt(key, themeResId)
                    .commit();
            ActivityUtils.recreate(activity);
        }
    }

    static public int getTheme(Activity activity, String key, int def) {
        if (activity != null && key != null && key != "") {
            return PreferenceManager.getDefaultSharedPreferences(activity).getInt(key, def);
        }

        return def;
    }

}
