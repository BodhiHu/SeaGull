package com.shawnhu.seagull.seagull.twitter.utils;

import android.app.Activity;
import android.content.Context;

public final class MessagesManager {

	static private Context          sContext;
    static private MessagesManager  sMessageManager;

    static public MessagesManager getInstance(final Context context) {
        if (sMessageManager == null) {
            sMessageManager = new MessagesManager(context);
        }

        return sMessageManager;
    }

	MessagesManager(final Context context) {
        if (sContext == null) {
            if (context == null) {
                throw new NullPointerException("Context can not be null");
            }
            sContext = context;
        }
	}

    static public void setVisibleActivity(Activity a) {
        if (a == null)  {
            throw new NullPointerException("Activity can not be null");
        }

        sContext = a;
    }

	public void showErrorMessage(final CharSequence message, final boolean long_message) {
		if (showToast()) {
			Utils.showErrorMessage(sContext, message, long_message);
			return;
		}
	}

	public void showErrorMessage(final int actionRes, final Exception e, final boolean long_message) {
		final String action = sContext.getString(actionRes);

		if (showToast()) {
			Utils.showErrorMessage(sContext, action, e, long_message);
			return;
		}
	}

	public void showErrorMessage(final int action_res, final String message, final boolean long_message) {
		final String action = sContext.getString(action_res);

		if (showToast()) {
			Utils.showErrorMessage(sContext, action, message, long_message);
			return;
		}
	}

	public void showInfoMessage(final CharSequence message, final boolean long_message) {

		if (showToast()) {
			Utils.showInfoMessage(sContext, message, long_message);
			return;
		}
	}

	public void showInfoMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showInfoMessage(sContext, message_res, long_message);
			return;
		}
	}

	public void showOkMessage(final CharSequence message, final boolean long_message) {

		if (showToast()) {
			Utils.showOkMessage(sContext, message, long_message);
		}
	}

	public void showOkMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showOkMessage(sContext, message_res, long_message);
			return;
		}
	}

	public void showWarnMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showWarnMessage(sContext, message_res, long_message);
		}
	}

	private boolean showToast() {
		return false;
	}
}
