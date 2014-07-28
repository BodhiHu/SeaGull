package com.shawnhu.seagull.preferences;

import android.content.SharedPreferences;

import java.security.InvalidParameterException;

/**
 * Created by shawnhu on 7/28/14.
 */
public class AppPreferences {
    static final public String PREF_APP_THEME                   = "PREF_APP_THEME";
    static       public int    mAppTheme = -1;
    static final public String PREF_HOME_NAV_CURRENT_POSITION  = "PREF_HOME_NAV_CURRENT_POSITION";
    static       public int    mPrefHomeNavCurrentPosition = -1;
    static final public String PREF_USER_LEARNED_DRAWER         = "PREF_USER_LEARNED_DRAWER";
    static       public boolean mPrefUserLearnedDrawer = false;
}

