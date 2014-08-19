package com.shawnhu.seagull.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.shawnhu.seagull.lang.Persistent;
import com.shawnhu.seagull.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by shawnhu on 8/16/14.
 */
public abstract class PersistentArrayAdapter<T> extends ArrayAdapter<T> implements Persistent {
    public PersistentArrayAdapter(Context context, int resource) {
        this(context, resource, 0, new ArrayList<T>());
    }
    public PersistentArrayAdapter(Context context, int resource, int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList<T>());
    }
    public PersistentArrayAdapter(Context context, int resource, T[] objects) {
        this(context, resource, 0, Arrays.asList(objects));
    }
    public PersistentArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));
    }
    public PersistentArrayAdapter(Context context, int resource, List<T> objects) {
        this(context, resource, 0, objects);
    }

    public PersistentArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);

        PERSISTENT_MAP.put(CURRENT_ITEM_ID, DEFAULT_V);

        restoreNow();
    }

    private LinkedHashMap<String, String> PERSISTENT_MAP = new LinkedHashMap<String, String>();

    static public final String CURRENT_ITEM_ID = "__CURRENT_ITEM_ID";

    private String PREFERENCE_NAME = "Preferences de " + ((Object) this).getClass().getName();
    static final public String              DEFAULT_V   = "-1";

    public void saveNow() {
        PreferencesUtils.savePreferencesMap(getContext(), PREFERENCE_NAME, PERSISTENT_MAP);
    }
    public void restoreNow() {
        PreferencesUtils.readPreferencesToMap(getContext(), PREFERENCE_NAME, PERSISTENT_MAP, DEFAULT_V);
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
