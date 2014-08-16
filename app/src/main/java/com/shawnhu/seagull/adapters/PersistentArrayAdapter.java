package com.shawnhu.seagull.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.shawnhu.seagull.lang.Persistent;

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

        NOW_MAP = get_preferences_map();
        restoreNow();
    }

    private String                          PREFERENCE_NAME = "Preferences de " + ((Object) this).getClass().getName();
    private LinkedHashMap<String, String>   NOW_MAP;
    static final public String              DEFAULT_V   = "";

    public void saveNow() {
        if (NOW_MAP != null) {
            for (String key : NOW_MAP.keySet()) {
                getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .edit().putString(key, NOW_MAP.get(key));
            }
        }
    }
    public void restoreNow() {
        if (NOW_MAP != null) {
            for (String key : NOW_MAP.keySet()) {
                String v = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
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
