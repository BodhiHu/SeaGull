package com.shawnhu.seagull.seagull.activities;

import android.content.Intent;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.LauncherActivity;
import com.shawnhu.seagull.seagull.Seagull;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getAccountIds;

public class SeagullLauncherActivity extends LauncherActivity {

    protected boolean isUserAlreadyLoggedIn() {
        long ids[] = getAccountIds(this);
        if (ids != null && ids.length > 0) {
            Seagull.sCurrentAccount.sAccountId = ids[0];
            return true;
        }
        return false;
    }
    protected int     getContentView() {
        return R.layout.layout_splash;
    }
    protected boolean isSplashActivity() {
        return true;
    }
    protected Intent  getLoginIntent() {
        return new Intent(this, LoginActivity.class);
    }
    protected Intent  getHomeIntent() {
        return new Intent(this, SeagullHomeActivity.class);
    }
}
