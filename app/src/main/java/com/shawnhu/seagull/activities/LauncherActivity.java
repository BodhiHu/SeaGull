package com.shawnhu.seagull.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.shawnhu.seagull.activities.util.SystemUiHider;

public abstract class LauncherActivity extends ActionBarActivity {

    abstract protected boolean isUserAlreadyLoggedIn();
    abstract protected int     getContentViewId();
    abstract protected int     getContentView();
    abstract protected boolean isSplashActivity();
    abstract protected Intent  getLoginIntent();
    abstract protected Intent  getHomeIntent();

    private Intent mHomeIntent;
    private Intent mLoginIntent;

    SystemUiHider mSystemUiHider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginIntent = getLoginIntent();
        mHomeIntent  = getHomeIntent();
        if (mLoginIntent == null || mHomeIntent == null) {
            throw new NullPointerException("Null login/home intent");
        }

        setContentView(getContentView());
        View contentView = findViewById(getContentViewId());
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_FULLSCREEN);
        mSystemUiHider.setup();
        mSystemUiHider.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().hide();
        }

        if (isSplashActivity()) {
            final Intent targetA;
            if (isUserAlreadyLoggedIn()) {
                targetA = mHomeIntent;
            } else {
                targetA = mLoginIntent;
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startActivity(targetA);
                    finish();
                }
            };

            Utils.delayedRun(r, 3000, true);
        }
    }

    public static class Utils {
        static public void delayedRun(final Runnable r, final int delayMilli, boolean sameThread) {
            if (r != null) {
                if (sameThread) {
                    Handler mHandler = new Handler();
                    mHandler.removeCallbacks(r);
                    mHandler.postDelayed(r, delayMilli);
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                super.run();
                                sleep(delayMilli);
                            } catch (InterruptedException ie) {
                            } finally {
                                r.run();
                            }
                        }
                    }.start();
                }
            }
        }
    }
}
