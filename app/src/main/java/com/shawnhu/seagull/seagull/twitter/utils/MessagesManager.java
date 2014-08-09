/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shawnhu.seagull.seagull.twitter.utils;

import android.app.Activity;
import android.content.Context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class MessagesManager {

	private final Set<Activity> mMessageCallbacks = Collections.synchronizedSet(new HashSet<Activity>());
	static private Context mContext;
    static private MessagesManager sMessageManager;

	MessagesManager(final Context context) {
        if (mContext == null) {
            if (context == null) {
                throw new NullPointerException("Context can not be null");
            }
            mContext = context;
        }
	}

    static public void setVisibleActivity(Activity a) {
        if (a == null)  {
            throw new NullPointerException("TwitterActivity can not be null");
        }

        mContext = a;
    }

	public void showErrorMessage(final CharSequence message, final boolean long_message) {
		if (showToast()) {
			Utils.showErrorMessage(mContext, message, long_message);
			return;
		}
	}

	public void showErrorMessage(final int actionRes, final Exception e, final boolean long_message) {
		final String action = mContext.getString(actionRes);

		if (showToast()) {
			Utils.showErrorMessage(mContext, action, e, long_message);
			return;
		}
	}

	public void showErrorMessage(final int action_res, final String message, final boolean long_message) {
		final String action = mContext.getString(action_res);

		if (showToast()) {
			Utils.showErrorMessage(mContext, action, message, long_message);
			return;
		}
	}

	public void showInfoMessage(final CharSequence message, final boolean long_message) {

		if (showToast()) {
			Utils.showInfoMessage(mContext, message, long_message);
			return;
		}
	}

	public void showInfoMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showInfoMessage(mContext, message_res, long_message);
			return;
		}
	}

	public void showOkMessage(final CharSequence message, final boolean long_message) {

		if (showToast()) {
			Utils.showOkMessage(mContext, message, long_message);
		}
	}

	public void showOkMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showOkMessage(mContext, message_res, long_message);
			return;
		}
	}

	public void showWarnMessage(final int message_res, final boolean long_message) {

		if (showToast()) {
			Utils.showWarnMessage(mContext, message_res, long_message);
		}
	}

	private boolean showToast() {
		return false;
	}

    static public MessagesManager getInstance(final Context context) {
        if (sMessageManager == null) {
            sMessageManager = new MessagesManager(context);
        }

        return sMessageManager;
    }
}
