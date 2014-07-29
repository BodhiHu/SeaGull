package com.shawnhu.seagull.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.shawnhu.seagull.activities.AbstractPreferenceActivity;
import com.shawnhu.seagull.app.AppPreferences;

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

    static public void applyTheme(Activity activity) {
        if (activity != null) {
            recreate(activity);
        }
    }

    static public void saveTheme(Context context, String themeResIndexStr) {
        if (context != null && themeResIndexStr != null && themeResIndexStr != "") {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString(AppPreferences.PREF_APP_THEME, themeResIndexStr)
                    .commit();
        }
    }

    static public int getTheme(Context context, int currentTheme) {
        if (context != null) {
            try {
                int themeResIndex =
                        Integer.getInteger(
                                PreferenceManager.getDefaultSharedPreferences(context)
                                .getString(AppPreferences.PREF_APP_THEME, ""));

                return Integer.getInteger(
                        AppPreferences.PREFERENCES_MAP
                        .get(AppPreferences.PREF_APP_THEME)[themeResIndex]);
            } catch(NullPointerException e) {

            } catch(Exception e) {
                Log.e(ActivityUtils.class.toString(), e.toString());
            }

            return currentTheme;
        } else {
            throw new NullPointerException("context can not be null");
        }
    }

}
