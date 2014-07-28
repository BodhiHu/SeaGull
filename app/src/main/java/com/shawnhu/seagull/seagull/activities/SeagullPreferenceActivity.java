package com.shawnhu.seagull.seagull.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractPreferenceActivity;
import com.shawnhu.seagull.preferences.AppPreferences;
import com.shawnhu.seagull.utils.ActivityUtils;

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
        //TODO: handle preference changes

        Toast.makeText(this, key + "'s value changed", Toast.LENGTH_SHORT).show();

        if (key == AppPreferences.PREF_APP_THEME) {
            int t = Integer.getInteger(((ListPreference) sharedPreferences).getValue());
            int themeResId;
            switch (t) {
                case 0:
                    themeResId = R.style.Theme_Day;
                    break;
                case 1:
                    themeResId = R.style.Theme_Night;
                    break;
                //TODO: hacker theme
                case 2:
                    themeResId = R.style.Theme_Night;
                    break;
                default:
                    themeResId = R.style.Theme_Night;
                    break;
            }
            ActivityUtils.applyTheme(this, AppPreferences.PREF_APP_THEME, themeResId);
        }
    }
}
