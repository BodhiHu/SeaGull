package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.shawnhu.seagull.adapters.PersistentCursorAdapter;

import java.util.LinkedHashMap;

public class StatusesAdapter extends PersistentCursorAdapter implements SimpleCursorAdapter.ViewBinder {

    public StatusesAdapter(Context context, int layout, Cursor c, String[] from,

                                   int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        setViewBinder(this);
    }

    static protected LinkedHashMap<String, String> KEY_MAP;
    static public final String START_ID            = "__START_ID";
    static public final String END_ID              = "__END_ID";
    static public final String CURRENT_POSITION    = "__CURRENT_POSITION";
    static {
        KEY_MAP.put(START_ID,           DEFAULT_V);
        KEY_MAP.put(END_ID,             DEFAULT_V);
        KEY_MAP.put(CURRENT_POSITION,   DEFAULT_V);
    }

    @Override
    protected LinkedHashMap<String, String> get_preferences_map() {
        return KEY_MAP;
    }
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        return true;
    }

}
