package com.shawnhu.seagull.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.utils.ActivityUtils;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by shawnhu on 7/28/14.
 */
public class AppPreferences {

    /**
     * INTERNAL PREFERENCES
     */
    static final public String PREF_HOME_NAV_CURRENT_POSITION   = "PREF_HOME_NAV_CURRENT_POSITION";
    static       public int    mPrefHomeNavCurrentPosition = -1;
    static final public String PREF_USER_LEARNED_DRAWER         = "PREF_USER_LEARNED_DRAWER";
    static       public boolean mPrefUserLearnedDrawer = false;

    /**
     * EXPORTED PREFERENCES
     * TODO: exported preferences keys should reside in app/?res?xml
     */
    static public String PREF_APP_THEME;

    /**
     * APP DEFAULTS
     */
    static public int mDefaultAppTheme; //read from manifest

    /**
     * Preferences map
     *   Key: Array of values(String), with index representing user choices in UI.
     *
     * Handles:
     *   1. themes change;
     *   2. ...
     */
    static public HashMap<String, String[]> PREFERENCES_MAP = new HashMap<String, String[]>();

    static public void init(Context context) {
        if (context != null) {
            PREF_APP_THEME = context.getString(R.string.PREF_APP_THEME);
            mDefaultAppTheme = context.getApplicationInfo().theme;
            mDefaultAppTheme = ActivityUtils.getTheme(context, mDefaultAppTheme);

            //TODO: set initial preferences for first launch
        }
    }

    static public void addPreferencesToMap(String key, String[] v) {
        PREFERENCES_MAP.put(key, v);
    }
}

