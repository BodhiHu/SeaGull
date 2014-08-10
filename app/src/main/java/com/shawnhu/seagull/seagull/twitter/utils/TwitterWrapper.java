package com.shawnhu.seagull.seagull.twitter.utils;

import android.content.Context;
import android.net.Uri;


import com.shawnhu.seagull.seagull.twitter.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.ListUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterWrapper {

	public static int clearNotification(final Context context, final int notificationType, final long accountId) {
		final Uri.Builder builder = TweetStore.Notifications.CONTENT_URI.buildUpon();
		builder.appendPath(String.valueOf(notificationType));
		if (accountId > 0) {
			builder.appendPath(String.valueOf(accountId));
		}
		return context.getContentResolver().delete(builder.build(), null, null);
	}

	public static int clearUnreadCount(final Context context, final int position) {
		if (context == null || position < 0) return 0;
		final Uri uri = TweetStore.UnreadCounts.CONTENT_URI.buildUpon().appendPath(String.valueOf(position)).build();
		return context.getContentResolver().delete(uri, null, null);
	}

	public static Response<Boolean> deleteProfileBannerImage(final Context context, final long account_id) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter == null) return new Response<Boolean>(false, null);
		try {
			twitter.removeProfileBannerImage();
			return new Response<Boolean>(true, null);
		} catch (final TwitterException e) {
			return new Response<Boolean>(false, e);
		}
	}

	public static int removeUnreadCounts(final Context context, final int position, final long account_id,
			final long... status_ids) {
		if (context == null || position < 0 || status_ids == null || status_ids.length == 0) return 0;
		int result = 0;
		final Uri.Builder builder = TweetStore.UnreadCounts.CONTENT_URI.buildUpon();
		builder.appendPath(String.valueOf(position));
		builder.appendPath(String.valueOf(account_id));
		builder.appendPath(ArrayUtils.toString(status_ids, ',', false));
		result += context.getContentResolver().delete(builder.build(), null, null);
		return result;
	}

	public static int removeUnreadCounts(final Context context, final int position, final Map<Long, Set<Long>> counts) {
		if (context == null || position < 0 || counts == null) return 0;
		int result = 0;
		for (final Entry<Long, Set<Long>> entry : counts.entrySet()) {
			final Uri.Builder builder = TweetStore.UnreadCounts.CONTENT_URI.buildUpon();
			builder.appendPath(String.valueOf(position));
			builder.appendPath(String.valueOf(entry.getKey()));
			builder.appendPath(ListUtils.toString(new ArrayList<Long>(entry.getValue()), ',', false));
			result += context.getContentResolver().delete(builder.build(), null, null);
		}
		return result;
	}

	public static Response<TwitterUser> updateProfile(final Context context, final long account_id,
			final String name, final String url, final String location, final String description) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter != null) {
			try {
				final twitter4j.User user = twitter.updateProfile(name, url, location, description);
				return new Response<TwitterUser>(new TwitterUser(user, account_id), null);
			} catch (final TwitterException e) {
				return new Response<TwitterUser>(null, e);
			}
		}
		return new Response<TwitterUser>(null, null);
	}

	public static Response<Boolean> updateProfileBannerImage(final Context context, final long account_id,
			final Uri image_uri, final boolean delete_image) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter != null && image_uri != null && "file".equals(image_uri.getScheme())) {
			try {
				final File file = new File(image_uri.getPath());
				twitter.updateProfileBannerImage(file);
				// Wait for 5 seconds, see
				// https://dev.twitter.com/docs/api/1.1/post/account/update_profile_image
				Thread.sleep(5000L);
				if (delete_image) {
					file.delete();
				}
				return new Response<Boolean>(true, null);
			} catch (final TwitterException e) {
				return new Response<Boolean>(false, e);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Response<Boolean>(false, null);
	}

	public static Response<TwitterUser> updateProfileImage(final Context context, final long account_id,
			final Uri image_uri, final boolean delete_image) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter != null && image_uri != null && "file".equals(image_uri.getScheme())) {
			try {
				final twitter4j.User user = twitter.updateProfileImage(new File(image_uri.getPath()));
				// Wait for 5 seconds, see
				// https://dev.twitter.com/docs/api/1.1/post/account/update_profile_image
				Thread.sleep(5000L);
				return new Response<TwitterUser>(new TwitterUser(user, account_id), null);
			} catch (final TwitterException e) {
				return new Response<TwitterUser>(null, e);
			} catch (final InterruptedException e) {
				return new Response<TwitterUser>(null, e);
			}
		}
		return new Response<TwitterUser>(null, null);
	}

}
