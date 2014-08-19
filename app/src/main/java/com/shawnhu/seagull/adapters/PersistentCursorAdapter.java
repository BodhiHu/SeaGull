package com.shawnhu.seagull.adapters;


import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.shawnhu.seagull.lang.Persistent;
import com.shawnhu.seagull.utils.PreferencesUtils;

import java.util.LinkedHashMap;

/**
 * Created by shawnhu on 8/16/14.
 */
public abstract class PersistentCursorAdapter extends SimpleCursorAdapter implements Persistent {
    protected Context mContext;

    private LinkedHashMap<String, String> PERSISTENT_MAP = new LinkedHashMap<String, String>();

    static public final String CURRENT_VISIBLE_ITEM_ID = "__CURRENT_VISIBLE_ITEM_ID";

    public PersistentCursorAdapter(Context context, int layout, Cursor c, String[] from,
            int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        mContext = context;

        PERSISTENT_MAP.put(CURRENT_VISIBLE_ITEM_ID, DEFAULT_V);

        restoreNow();
    }

    private String                          PREFERENCE_NAME = "Preferences de " + ((Object) this).getClass().getName();
    static final public String              DEFAULT_V   = "-1";

    public void saveNow() {
        PreferencesUtils.savePreferencesMap(mContext, PREFERENCE_NAME, PERSISTENT_MAP);
    }
    public void restoreNow() {
        PreferencesUtils.readPreferencesToMap(mContext, PREFERENCE_NAME, PERSISTENT_MAP, DEFAULT_V);
    }
    public void setValue(String key, String v) {
        if (PERSISTENT_MAP != null) {
            PERSISTENT_MAP.put(key, v);
        }
    }
    public String getValue(String key) {
        if (PERSISTENT_MAP != null) {
            return PERSISTENT_MAP.get(key);
        }

        return DEFAULT_V;
    }
}
