package com.shawnhu.seagull.seagull.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractPreferenceActivity;
import com.shawnhu.seagull.preferences.AbstractPreferenceFragment;

/**
 * Created by shawnhu on 7/27/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AccountPreferenceFragment  extends AbstractPreferenceFragment {
    Activity mActivity = getActivity();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ((AbstractPreferenceActivity) mActivity).onSharedPreferenceChanged(sharedPreferences, key);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        mPreferenceResId = R.xml.pref_general;

        super.onCreate(saveInstanceState);
    }

}
