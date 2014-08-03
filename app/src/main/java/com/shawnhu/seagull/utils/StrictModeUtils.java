package com.shawnhu.seagull.utils;

import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.util.Log;

import java.util.Locale;

public class StrictModeUtils {

	public static final String LOGTAG = "StrictModeUtils";
	public static final String CLASS_NAME = StrictModeUtils.class.getName();

	public static void checkDiskIO() {
		check("Disk IO");
	}

	public static void checkLengthyOperation() {
		check("Lengthy operation");
	}

	public static void detectAllThreadPolicy() {
		final ThreadPolicy.Builder threadPolicyBuilder = new ThreadPolicy.Builder();
		threadPolicyBuilder.detectAll();
		threadPolicyBuilder.penaltyLog();
		StrictMode.setThreadPolicy(threadPolicyBuilder.build());
	}

	public static void detectAllVmPolicy() {
		final VmPolicy.Builder vmPolicyBuilder = new VmPolicy.Builder();
		vmPolicyBuilder.detectAll();
		vmPolicyBuilder.penaltyLog();
		StrictMode.setVmPolicy(vmPolicyBuilder.build());
	}

	private static void check(final String message) {
		final Thread thread = Thread.currentThread();
		if (thread == null || thread.getId() != 1) return;
		final StackTraceElement[] framesArray = thread.getStackTrace();

		// look for the last stack frame from this class and then whatever is
		// next is the caller we want to know about
		int logCounter = -1;
		for (final StackTraceElement stackFrame : framesArray) {
			final String className = stackFrame.getClassName();
			if (logCounter >= 0 && logCounter < 3) {
				final String file = stackFrame.getFileName(), method = stackFrame.getMethodName();
				final int line = stackFrame.getLineNumber();
				final String nonEmptyFile = file != null ? file : "Unknown";
				if (logCounter == 0) {
					Log.w(LOGTAG, String.format(Locale.US, "%s on main thread:\n", message));
				}
				Log.w(LOGTAG, String.format(Locale.US, "\t at %s.%s(%s:%d)", className, method, nonEmptyFile, line));
				if (++logCounter == 3) return;
			} else if (CLASS_NAME.equals(className) && logCounter == -1) {
				logCounter = 0;
			}
		}
	}

}
