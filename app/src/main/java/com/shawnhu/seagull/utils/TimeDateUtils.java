package com.shawnhu.seagull.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;



public class TimeDateUtils {
	public static String formatTimeStampString(final Context context, final long timestamp) {
		if (context == null) return null;
		final Time then = new Time();
		then.set(timestamp);
		final Time now = new Time();
		now.setToNow();

		int format_flags = DateUtils.FORMAT_NO_NOON     |
                           DateUtils.FORMAT_NO_MIDNIGHT |
                           DateUtils.FORMAT_ABBREV_ALL;

		if (then.year != now.year) {
			format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			format_flags |= DateUtils.FORMAT_SHOW_DATE;
		} else {
			format_flags |= DateUtils.FORMAT_SHOW_TIME;
		}

		return DateUtils.formatDateTime(context, timestamp, format_flags);
	}
}
