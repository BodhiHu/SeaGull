package com.shawnhu.seagull.utils;

import android.content.Context;

import java.util.Map;

/**
 * Created by shawn on 14-8-19.
 */
public class PreferencesUtils {
    static public void savePreferencesMap(Context context, String pref, Map<String, String> map) {
        if (context != null && map != null) {
            for (String key : map.keySet()) {
                context.getSharedPreferences(pref, Context.MODE_PRIVATE)
                        .edit().putString(key, map.get(key));
            }
        }
    }

    static public void readPreferencesToMap(Context context, String pref, Map<String, String> map, String dft_v) {
        if (context != null && pref != null) {
            for (String key : map.keySet()) {
                String v = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
                        .getString(key, dft_v);
                map.put(key, v);
            }
        }
    }
}
