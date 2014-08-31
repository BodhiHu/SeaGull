package com.shawnhu.seagull.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Subclasses should override safe version onPre..., onPost... and onProgre...
 * The two methods run on the UI thread, so we don't need to worry Fragment/Activity got destroyed
 * when running in the two.
 */

abstract public class ContextAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Activity mActivity;
    private Fragment mFragment;
    private boolean  mMayInterruptIfRunning = true;
    public ContextAsyncTask(Context context) {
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }
    public ContextAsyncTask(Activity activity) {
        mActivity = activity;
    }
    public ContextAsyncTask(Fragment fragment) {
        mFragment = fragment;
    }

    public Activity getHostActivity() {
        return mActivity;
    }
    public Fragment getHostFragment() {
        return mFragment;
    }
    public void setHost(Activity activity) {
        mActivity = activity;
    }
    public void setHost(Fragment fragment) {
        mFragment = fragment;
    }
    public void setMayInterruptIfRunning(boolean may) {
        mMayInterruptIfRunning = may;
    }


    protected void onPostExecuteSafe(Result result) {}
    protected void onProgressUpdateSafe(Progress... values) {}
    protected void onPreExecuteSafe() {}

    @Override
    final protected void onPreExecute() {
        if (canRunOnUiThread()) {
            onPreExecuteSafe();
        } else {
            cancel(mMayInterruptIfRunning);
        }
    }

    @Override
    final protected void onPostExecute(Result result) {
        if (canRunOnUiThread()) {
            onPostExecuteSafe(result);
        }
    }

    @Override
    final protected void onProgressUpdate(Progress... values) {
        if (canRunOnUiThread()) {
            onProgressUpdateSafe(values);
        } else {
            cancel(mMayInterruptIfRunning);
        }
    }

    protected boolean canRunOnUiThread() {
        if (mFragment == null && mActivity == null) {
            Log.e(getClass().getSimpleName().toString(),
                    "Host Fragment/Activity are null, did you forget to set them?");

            throw new NullPointerException("You must set the host Fragment or Activity for this " +
                    "task.");
        }

        boolean canRunOnUiThread = false;

        if (mFragment != null && mFragment.isVisible()) {
            canRunOnUiThread = true;
        } else if (mActivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!(mActivity.isDestroyed())) {
                    canRunOnUiThread = true;
                }
            } else {
                if (mActivity.getCurrentFocus() != null) {
                    canRunOnUiThread = true;
                }
            }

        }

        return canRunOnUiThread;
    }
}
