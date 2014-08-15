package com.shawnhu.seagull.seagull.activities;

import android.content.Intent;
import android.os.Bundle;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.LauncherActivity;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getAccountIds;

public class SeagullLauncherActivity extends LauncherActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        setContentView(R.layout.splash_eiffel_baloon);

        super.onCreate(saveInstanceState);
    }

    protected boolean isUserAlreadyLoggedIn() {
        long ids[] = getAccountIds(this);
        return (ids != null && ids.length > 0);
    }
    protected int     getContentViewId() {
        return R.id.contentView;
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
