package com.shawnhu.seagull.adapters;


import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.shawnhu.seagull.lang.Persistent;

import java.util.LinkedHashMap;

/**
 * Created by shawnhu on 8/16/14.
 */
public abstract class PersistentCursorAdapter extends SimpleCursorAdapter implements Persistent {
    protected Context mContext;
    public PersistentCursorAdapter(Context context, int layout, Cursor c, String[] from,
            int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        mContext = context;

        NOW_MAP = get_preferences_map();
        restoreNow();
    }

    private String                          PREFERENCE_NAME = "Preferences de " + ((Object) this).getClass().getName();
    private LinkedHashMap<String, String>   NOW_MAP;
    static final public String              DEFAULT_V   = "";

    public void saveNow() {
        if (NOW_MAP != null) {
            for (String key : NOW_MAP.keySet()) {
                mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .edit().putString(key, NOW_MAP.get(key));
            }
        }
    }
    public void restoreNow() {
        if (NOW_MAP != null) {
            for (String key : NOW_MAP.keySet()) {
                String v = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .getString(key, DEFAULT_V);
                NOW_MAP.put(key, v);
            }
        }
    }
    public void saveValue(String key, String v) {
        if (NOW_MAP != null) {
            NOW_MAP.put(key, v);
        }
    }
    public String getSavedValue(String key) {
        if (NOW_MAP != null) {
            return NOW_MAP.get(key);
        }

        return DEFAULT_V;
    }

    abstract protected LinkedHashMap<String, String> get_preferences_map();
}