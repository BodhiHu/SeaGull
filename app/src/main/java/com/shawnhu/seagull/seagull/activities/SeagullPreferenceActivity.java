package com.shawnhu.seagull.seagull.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractPreferenceActivity;

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

        Toast.makeText(this, key + ": " + sharedPreferences.getString(key, ""), Toast.LENGTH_SHORT);
    }
}
