package com.shawnhu.seagull.seagull.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractPreferenceActivity;
import com.shawnhu.seagull.app.AppPreferences;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.utils.ActivityUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shawnhu on 7/27/14.
 */
public class SeagullPreferenceActivity extends AbstractPreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferenceResIds.add(R.xml.pref_general);
        mPreferenceResIds.add(R.xml.pref_account);

        mPreferenceHeaderResId = R.xml.pref_headers;

        super.onCreate(savedInstanceState);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences == null || key == null) {
            return;
        }

        //TODO: handle preference changes


        if (key == Seagull.PREF_SEAGULL_NOTIFICATION_ON) {

        } else if (key == Seagull.PREF_SEAGULL_NOTIFICATION_RINGTONE) {

        } else if (key == Seagull.PREF_SEAGULL_NOTIFICATION_VIRATE) {

        } else {
            super.onSharedPreferenceChanged(sharedPreferences, key);
        }
    }
}
