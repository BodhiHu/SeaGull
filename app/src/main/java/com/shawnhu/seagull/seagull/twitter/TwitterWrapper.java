package com.shawnhu.seagull.seagull.twitter;

import android.content.Context;
import android.net.Uri;


import com.shawnhu.seagull.seagull.twitter.model.TwitterResponse;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.ListUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter4j.DirectMessage;
import twitter4j.Status;
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

	public static TwitterResponse<Boolean> deleteProfileBannerImage(final Context context, final long account_id) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter == null) return new TwitterResponse<Boolean>(false, null);
		try {
			twitter.removeProfileBannerImage();
			return new TwitterResponse<Boolean>(true, null);
		} catch (final TwitterException e) {
			return new TwitterResponse<Boolean>(false, e);
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

	public static TwitterResponse<TwitterUser> updateProfile(final Context context, final long account_id,
			final String name, final String url, final String location, final String description) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter != null) {
			try {
				final twitter4j.User user = twitter.updateProfile(name, url, location, description);
				return new TwitterResponse<TwitterUser>(new TwitterUser(user, account_id), null);
			} catch (final TwitterException e) {
				return new TwitterResponse<TwitterUser>(null, e);
			}
		}
		return new TwitterResponse<TwitterUser>(null, null);
	}

	public static TwitterResponse<Boolean> updateProfileBannerImage(final Context context, final long account_id,
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
				return new TwitterResponse<Boolean>(true, null);
			} catch (final TwitterException e) {
				return new TwitterResponse<Boolean>(false, e);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new TwitterResponse<Boolean>(false, null);
	}

	public static TwitterResponse<TwitterUser> updateProfileImage(final Context context, final long account_id,
			final Uri image_uri, final boolean delete_image) {
		final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
		if (twitter != null && image_uri != null && "file".equals(image_uri.getScheme())) {
			try {
				final twitter4j.User user = twitter.updateProfileImage(new File(image_uri.getPath()));
				// Wait for 5 seconds, see
				// https://dev.twitter.com/docs/api/1.1/post/account/update_profile_image
				Thread.sleep(5000L);
				return new TwitterResponse<TwitterUser>(new TwitterUser(user, account_id), null);
			} catch (final TwitterException e) {
				return new TwitterResponse<TwitterUser>(null, e);
			} catch (final InterruptedException e) {
				return new TwitterResponse<TwitterUser>(null, e);
			}
		}
		return new TwitterResponse<TwitterUser>(null, null);
	}

	public static final class MessageListResponse extends TwitterListResponse {

		public final boolean truncated;

		public MessageListResponse(final long account_id, final Exception exception) {
			this(account_id, -1, -1, null, false, exception);
		}

		public MessageListResponse(final long account_id, final List<DirectMessage> list) {
			this(account_id, -1, -1, list, false, null);
		}

		public MessageListResponse(final long account_id, final long max_id, final long since_id,
				final int load_item_limit, final List<DirectMessage> list, final boolean truncated) {
			this(account_id, max_id, since_id, list, truncated, null);
		}

		MessageListResponse(final long account_id, final long max_id, final long since_id,
				final List<DirectMessage> list, final boolean truncated, final Exception exception) {
			super(account_id, max_id, since_id, list, exception);
			this.truncated = truncated;
		}

	}

	public static final class StatusListResponse extends TwitterListResponse {

		public final boolean truncated;

		public StatusListResponse(final long account_id, final Exception exception) {
			this(account_id, -1, -1, null, false, exception);
		}

		public StatusListResponse(final long account_id, final List<Status> list) {
			this(account_id, -1, -1, list, false, null);
		}

		public StatusListResponse(final long account_id, final long max_id, final long since_id,
				final int load_item_limit, final List<Status> list, final boolean truncated) {
			this(account_id, max_id, since_id, list, truncated, null);
		}

		StatusListResponse(final long account_id, final long max_id, final long since_id, final List<Status> list,
				final boolean truncated, final Exception exception) {
			super(account_id, max_id, since_id, list, exception);
			this.truncated = truncated;
		}

	}

	public static class TwitterListResponse<Data> extends com.shawnhu.seagull.seagull.twitter.model.TwitterListResponse<Data> {

		public final long account_id, max_id, since_id;

		public TwitterListResponse(final long account_id, final Exception exception) {
			this(account_id, -1, -1, null, exception);
		}

		public TwitterListResponse(final long account_id, final long max_id, final long since_id, final List<Data> list) {
			this(account_id, max_id, since_id, list, null);
		}

		TwitterListResponse(final long account_id, final long max_id, final long since_id, final List<Data> list,
				final Exception exception) {
			super(list, exception);
			this.account_id = account_id;
			this.max_id = max_id;
			this.since_id = since_id;
		}

	}
}
