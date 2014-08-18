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

    private LinkedHashMap<String, String> NOW_MAP = new LinkedHashMap<String, String>();

    static public final String _ID_OF_HEAD_ITEM = "__START_ID";
    static public final String _ID_OF_TAIL_ITEM = "__END_ID";
    static public final String CURRENT_VISIBLE_ITEM_ID = "__CURRENT_VISIBLE_ITEM_ID";

    public PersistentCursorAdapter(Context context, int layout, Cursor c, String[] from,
            int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        mContext = context;

        NOW_MAP.put(_ID_OF_HEAD_ITEM,           DEFAULT_V);
        NOW_MAP.put(_ID_OF_TAIL_ITEM,             DEFAULT_V);
        NOW_MAP.put(CURRENT_VISIBLE_ITEM_ID,   DEFAULT_V);

        restoreNow();
    }

    private String                          PREFERENCE_NAME = "Preferences de " + ((Object) this).getClass().getName();
    static final public String              DEFAULT_V   = "-1";

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
    public void setValue(String key, String v) {
        if (NOW_MAP != null) {
            NOW_MAP.put(key, v);
        }
    }
    public String getValue(String key) {
        if (NOW_MAP != null) {
            return NOW_MAP.get(key);
        }

        return DEFAULT_V;
    }
}
