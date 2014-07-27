package com.shawnhu.seagull.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by shawnhu on 7/27/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class AbstractPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

/* Subclass fragments should
     1. provide preference res;
     2. implement SharedPreferences.OnSharedPreferenceChangeListener;

   This is for being able to proxy all the preferences changes to a preference activity.
 */
    protected int mPreferenceResId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(mPreferenceResId);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
