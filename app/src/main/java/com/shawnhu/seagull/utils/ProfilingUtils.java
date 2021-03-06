package com.shawnhu.seagull.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.shawnhu.seagull.utils.AppLog.T;

import java.util.ArrayList;

/**
 * forked from android.util.TimingLogger to use AppLog instead of Log + new static interface.
 */
public class ProfilingUtils {
    private static ProfilingUtils sInstance;

    private String mLabel;
    private ArrayList<Long> mSplits;
    private ArrayList<String> mSplitLabels;

    public static void start(String label) {
        getInstance().reset(label);
    }

    public static void split(String splitLabel) {
        getInstance().addSplit(splitLabel);
    }

    public static void dump() {
        getInstance().dumpToLog();
    }

    private static ProfilingUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ProfilingUtils();
        }
        return sInstance;
    }

    public ProfilingUtils() {
        reset("init");
    }

    public void reset(String label) {
        mLabel = label;
        reset();
    }

    public void reset() {
        if (mSplits == null) {
            mSplits = new ArrayList<Long>();
            mSplitLabels = new ArrayList<String>();
        } else {
            mSplits.clear();
            mSplitLabels.clear();
        }
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        long now = SystemClock.elapsedRealtime();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        AppLog.d(T.PROFILING, mLabel + ": begin");
        final long first = mSplits.get(0);
        long now = first;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);
            AppLog.d(T.PROFILING, mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
        }
        AppLog.d(T.PROFILING, mLabel + ": end, " + (now - first) + " ms");
    }

    // Returns app version name String
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName == null ? "" : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}

