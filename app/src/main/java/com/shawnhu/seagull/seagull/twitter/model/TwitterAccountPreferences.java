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

package com.shawnhu.seagull.seagull.twitter.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.*;


public class TwitterAccountPreferences {

    private final Context mContext;
    private final long mAccountId;
    private final SharedPreferences mPreferences;

    public TwitterAccountPreferences(final Context context, final long accountId) {
        mContext = context;
        mAccountId = accountId;
        final String name = ACCOUNT_PREFERENCES_NAME_PREFIX + accountId;
        mPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public long getAccountId() {
        return mAccountId;
    }

    public int getDefaultNotificationLightColor() {
        final TwitterAccount a = TwitterAccount.getAccount(mContext, mAccountId);
        return a != null ? a.color : android.R.color.holo_blue_light;
    }

    public int getDirectMessagesNotificationOptions() {
        return mPreferences.getInt(KEY_NOTIFICATION_DIRECT_MESSAGES_OPTIONS, DEFAULT_NOTIFICATION_DIRECT_MESSAGES_OPTIONS);
    }

    public int getHomeTimelineNotificationOptions() {
        return mPreferences.getInt(KEY_NOTIFICATION_HOME_OPTIONS, DEFAULT_NOTIFICATION_HOME_OPTIONS);
    }

    public int getMentionsNotificationOptions() {
        return mPreferences.getInt(KEY_NOTIFICATION_MENTIONS_OPTIONS, DEFAULT_NOTIFICATION_MENTIONS_OPTIONS);
    }

    public int getNotificationLightColor() {
        return mPreferences.getInt(KEY_NOTIFICATION_LIGHT_COLOR, getDefaultNotificationLightColor());
    }

    public Uri getNotificationRingtone() {
        final Uri def = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final String path = mPreferences.getString(KEY_NOTIFICATION_RINGTONE, null);
        return TextUtils.isEmpty(path) ? def : Uri.parse(path);
    }

    public boolean isAutoRefreshDirectMessagesEnabled() {
        return mPreferences.getBoolean(KEY_AUTO_REFRESH_DIRECT_MESSAGES, DEFAULT_AUTO_REFRESH_DIRECT_MESSAGES);
    }

    public boolean isAutoRefreshEnabled() {
        return mPreferences.getBoolean(KEY_AUTO_REFRESH, DEFAULT_AUTO_REFRESH);
    }

    public boolean isAutoRefreshHomeTimelineEnabled() {
        return mPreferences.getBoolean(KEY_AUTO_REFRESH_HOME_TIMELINE, DEFAULT_AUTO_REFRESH_HOME_TIMELINE);
    }

    public boolean isAutoRefreshMentionsEnabled() {
        return mPreferences.getBoolean(KEY_AUTO_REFRESH_MENTIONS, DEFAULT_AUTO_REFRESH_MENTIONS);
    }

    public boolean isAutoRefreshTrendsEnabled() {
        return mPreferences.getBoolean(KEY_AUTO_REFRESH_TRENDS, DEFAULT_AUTO_REFRESH_TRENDS);
    }

    public boolean isDirectMessagesNotificationEnabled() {
        return mPreferences.getBoolean(KEY_DIRECT_MESSAGES_NOTIFICATION, DEFAULT_DIRECT_MESSAGES_NOTIFICATION);
    }

    public boolean isHomeTimelineNotificationEnabled() {
        return mPreferences.getBoolean(KEY_HOME_TIMELINE_NOTIFICATION, DEFAULT_HOME_TIMELINE_NOTIFICATION);
    }

    public boolean isMentionsNotificationEnabled() {
        return mPreferences.getBoolean(KEY_MENTIONS_NOTIFICATION, DEFAULT_MENTIONS_NOTIFICATION);
    }

    public boolean isMyFollowingOnly() {
        return mPreferences.getBoolean(KEY_MY_FOLLOWING_ONLY, false);
    }

    public boolean isNotificationEnabled() {
        return mPreferences.getBoolean(KEY_NOTIFICATION, DEFAULT_NOTIFICATION);
    }

    public static TwitterAccountPreferences getAccountPreferences(final TwitterAccountPreferences[] prefs, final long accountId) {
        for (final TwitterAccountPreferences pref : prefs) {
            if (pref.getAccountId() == accountId) return pref;
        }
        return null;
    }

    public static TwitterAccountPreferences[] getAccountPreferences(final Context context, final long[] accountIds) {
        if (context == null || accountIds == null) return null;
        final TwitterAccountPreferences[] preferences = new TwitterAccountPreferences[accountIds.length];
        for (int i = 0, j = preferences.length; i < j; i++) {
            preferences[i] = new TwitterAccountPreferences(context, accountIds[i]);
        }
        return preferences;
    }

    public static long[] getAutoRefreshEnabledAccountIds(final Context context, final long[] accountIds) {
        if (context == null || accountIds == null) return null;
        final long[] temp = new long[accountIds.length];
        int i = 0;
        for (final long accountId : accountIds) {
            if (new TwitterAccountPreferences(context, accountId).isAutoRefreshEnabled()) {
                temp[i++] = accountId;
            }
        }
        final long[] enabledIds = new long[i];
        System.arraycopy(temp, 0, enabledIds, 0, i);
        return enabledIds;
    }

    public static TwitterAccountPreferences[] getNotificationEnabledAccountPreferences(
            final Context context,
            final long[] accountIds) {
        if (context == null || accountIds == null) return null;
        final TwitterAccountPreferences[] temp = new TwitterAccountPreferences[accountIds.length];
        int i = 0;
        for (final long accountId : accountIds) {
            final TwitterAccountPreferences preference = new TwitterAccountPreferences(context, accountId);
            if (preference.isNotificationEnabled()) {
                temp[i++] = preference;
            }
        }
        final TwitterAccountPreferences[] enabledIds = new TwitterAccountPreferences[i];
        System.arraycopy(temp, 0, enabledIds, 0, i);
        return enabledIds;
    }

    public static boolean isNotificationWithLight(final int flags) {
        return (flags & VALUE_NOTIFICATION_FLAG_LIGHT) != 0;
    }

    public static boolean isNotificationWithRingtone(final int flags) {
        return (flags & VALUE_NOTIFICATION_FLAG_RINGTONE) != 0;
    }

    public static boolean isNotificationWithVibration(final int flags) {
        return (flags & VALUE_NOTIFICATION_FLAG_VIBRATION) != 0;
    }
}
