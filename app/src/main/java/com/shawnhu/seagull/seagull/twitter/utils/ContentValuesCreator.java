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

import android.content.ContentValues;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterLocation;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMedia;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMediaUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterDirectMessage;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUserMention;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.HtmlEscapeHelper;
import com.shawnhu.seagull.utils.JSON.JSONSerializer;
import com.shawnhu.seagull.utils.ParseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;

import static com.shawnhu.seagull.seagull.twitter.TweetStore.*;

public final class ContentValuesCreator {

	public static ContentValues makeAccountContentValuesBasic(final Configuration conf, final String basicUsername,
			final String basicPassword, final twitter4j.User user, final int color, final String apiUrlFormat) {
		if (user == null || user.getId() <= 0) return null;
		final ContentValues values = new ContentValues();
		if (basicUsername == null || basicPassword == null) return null;
		values.put(Accounts.BASIC_AUTH_USERNAME, basicUsername);
		values.put(Accounts.BASIC_AUTH_PASSWORD, basicPassword);
		values.put(Accounts.AUTH_TYPE, Accounts.AUTH_TYPE_BASIC);
		values.put(Accounts.ACCOUNT_ID, user.getId());
		values.put(Accounts.SCREEN_NAME, user.getScreenName());
		values.put(Accounts.NAME, user.getName());
		values.put(Accounts.PROFILE_IMAGE_URL, ParseUtils.parseString(user.getProfileImageUrlHttps()));
		values.put(Accounts.PROFILE_BANNER_URL, ParseUtils.parseString(user.getProfileBannerImageUrl()));
		values.put(Accounts.COLOR, color);
		values.put(Accounts.IS_ACTIVATED, 1);
		values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
		return values;
	}

	public static ContentValues makeAccountContentValuesOAuth(final Configuration conf, final AccessToken accessToken,
			final twitter4j.User user, final int authType, final int color, final String apiUrlFormat,
			final boolean sameOAuthSigningUrl) {
		if (user == null || user.getId() <= 0 || accessToken == null || user.getId() != accessToken.getUserId())
			return null;
		final ContentValues values = new ContentValues();
		values.put(Accounts.OAUTH_TOKEN, accessToken.getToken());
		values.put(Accounts.OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
		values.put(Accounts.CONSUMER_KEY, conf.getOAuthConsumerKey());
		values.put(Accounts.CONSUMER_SECRET, conf.getOAuthConsumerSecret());
		values.put(Accounts.AUTH_TYPE, authType);
		values.put(Accounts.ACCOUNT_ID, user.getId());
		values.put(Accounts.SCREEN_NAME, user.getScreenName());
		values.put(Accounts.NAME, user.getName());
		values.put(Accounts.PROFILE_IMAGE_URL, ParseUtils.parseString(user.getProfileImageUrlHttps()));
		values.put(Accounts.PROFILE_BANNER_URL, ParseUtils.parseString(user.getProfileBannerImageUrl()));
		values.put(Accounts.COLOR, color);
		values.put(Accounts.IS_ACTIVATED, 1);
		values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
		values.put(Accounts.SAME_OAUTH_SIGNING_URL, sameOAuthSigningUrl);
		return values;
	}

	public static ContentValues makeAccountContentValuesTWIP(final Configuration conf, final twitter4j.User user,
			final int color, final String apiUrlFormat) {
		if (user == null || user.getId() <= 0) return null;
		final ContentValues values = new ContentValues();
		values.put(Accounts.AUTH_TYPE, Accounts.AUTH_TYPE_TWIP_O_MODE);
		values.put(Accounts.ACCOUNT_ID, user.getId());
		values.put(Accounts.SCREEN_NAME, user.getScreenName());
		values.put(Accounts.NAME, user.getName());
		values.put(Accounts.PROFILE_IMAGE_URL, ParseUtils.parseString(user.getProfileImageUrlHttps()));
		values.put(Accounts.PROFILE_BANNER_URL, ParseUtils.parseString(user.getProfileBannerImageUrl()));
		values.put(Accounts.COLOR, color);
		values.put(Accounts.IS_ACTIVATED, 1);
		values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
		return values;
	}

	public static ContentValues makeCachedUserContentValues(final twitter4j.User user) {
		if (user == null || user.getId() <= 0) return null;
		final String profile_image_url = ParseUtils.parseString(user.getProfileImageUrlHttps());
		final String url = ParseUtils.parseString(user.getURL());
		final URLEntity[] urls = user.getURLEntities();
		final ContentValues values = new ContentValues();
		values.put(CachedUsers.USER_ID, user.getId());
		values.put(CachedUsers.NAME, user.getName());
		values.put(CachedUsers.SCREEN_NAME, user.getScreenName());
		values.put(CachedUsers.PROFILE_IMAGE_URL, profile_image_url);
		values.put(CachedUsers.CREATED_AT, user.getCreatedAt().getTime());
		values.put(CachedUsers.IS_PROTECTED, user.isProtected());
		values.put(CachedUsers.IS_VERIFIED, user.isVerified());
		values.put(CachedUsers.IS_FOLLOWING, user.isFollowing());
		values.put(CachedUsers.FAVORITES_COUNT, user.getFavouritesCount());
		values.put(CachedUsers.FOLLOWERS_COUNT, user.getFollowersCount());
		values.put(CachedUsers.FRIENDS_COUNT, user.getFriendsCount());
		values.put(CachedUsers.STATUSES_COUNT, user.getStatusesCount());
		values.put(CachedUsers.LOCATION, user.getLocation());
		values.put(CachedUsers.DESCRIPTION_PLAIN, user.getDescription());
		values.put(CachedUsers.DESCRIPTION_HTML, Utils.formatUserDescription(user));
		values.put(CachedUsers.DESCRIPTION_EXPANDED, Utils.formatExpandedUserDescription(user));
		values.put(CachedUsers.URL, url);
		values.put(CachedUsers.URL_EXPANDED,
				url != null && urls != null && urls.length > 0 ? ParseUtils.parseString(urls[0].getExpandedURL())
						: null);
		values.put(CachedUsers.PROFILE_BANNER_URL, user.getProfileBannerImageUrl());
		return values;
	}

	public static ContentValues makeDirectMessageContentValues(final twitter4j.DirectMessage message, final long account_id,
			final boolean is_outgoing) {
		if (message == null || message.getId() <= 0) return null;
		final ContentValues values = new ContentValues();
		final twitter4j.User sender = message.getSender(), recipient = message.getRecipient();
		if (sender == null || recipient == null) return null;
		final String sender_profile_image_url = ParseUtils.parseString(sender.getProfileImageUrlHttps());
		final String recipient_profile_image_url = ParseUtils.parseString(recipient.getProfileImageUrlHttps());
		values.put(DirectMessages.ACCOUNT_ID, account_id);
		values.put(DirectMessages.MESSAGE_ID, message.getId());
		values.put(DirectMessages.MESSAGE_TIMESTAMP, message.getCreatedAt().getTime());
		values.put(DirectMessages.SENDER_ID, sender.getId());
		values.put(DirectMessages.RECIPIENT_ID, recipient.getId());
		final String text_html = Utils.formatDirectMessageText(message);
		values.put(DirectMessages.TEXT_HTML, text_html);
		values.put(DirectMessages.TEXT_PLAIN, message.getText());
		values.put(DirectMessages.TEXT_UNESCAPED, HtmlEscapeHelper.toPlainText(text_html));
		values.put(DirectMessages.IS_OUTGOING, is_outgoing);
		values.put(DirectMessages.SENDER_NAME, sender.getName());
		values.put(DirectMessages.SENDER_SCREEN_NAME, sender.getScreenName());
		values.put(DirectMessages.RECIPIENT_NAME, recipient.getName());
		values.put(DirectMessages.RECIPIENT_SCREEN_NAME, recipient.getScreenName());
		values.put(DirectMessages.SENDER_PROFILE_IMAGE_URL, sender_profile_image_url);
		values.put(DirectMessages.RECIPIENT_PROFILE_IMAGE_URL, recipient_profile_image_url);
		final TwitterMedia[] medias = TwitterMedia.fromEntities(message);
		if (medias != null) {
			values.put(DirectMessages.MEDIAS, JSONSerializer.toJSONArrayString(medias));
			values.put(DirectMessages.FIRST_MEDIA, medias[0].url);
		}
		return values;
	}

	public static ContentValues makeDirectMessageContentValues(final TwitterDirectMessage message) {
		if (message == null || message.id <= 0) return null;
		final ContentValues values = new ContentValues();
		values.put(DirectMessages.ACCOUNT_ID, message.account_id);
		values.put(DirectMessages.MESSAGE_ID, message.id);
		values.put(DirectMessages.MESSAGE_TIMESTAMP, message.timestamp);
		values.put(DirectMessages.SENDER_ID, message.sender_id);
		values.put(DirectMessages.RECIPIENT_ID, message.recipient_id);
		values.put(DirectMessages.TEXT_HTML, message.text_html);
		values.put(DirectMessages.TEXT_PLAIN, message.text_plain);
		values.put(DirectMessages.IS_OUTGOING, message.is_outgoing);
		values.put(DirectMessages.SENDER_NAME, message.sender_name);
		values.put(DirectMessages.SENDER_SCREEN_NAME, message.sender_screen_name);
		values.put(DirectMessages.RECIPIENT_NAME, message.recipient_name);
		values.put(DirectMessages.RECIPIENT_SCREEN_NAME, message.recipient_screen_name);
		values.put(DirectMessages.SENDER_PROFILE_IMAGE_URL, message.sender_profile_image_url);
		values.put(DirectMessages.RECIPIENT_PROFILE_IMAGE_URL, message.recipient_profile_image_url);
		if (message.medias != null) {
			values.put(Statuses.MEDIAS, JSONSerializer.toJSONArrayString(message.medias));
			values.put(Statuses.FIRST_MEDIA, message.medias[0].url);
		}
		return values;
	}

	public static ContentValues makeDirectMessageDraftContentValues(final long accountId, final long recipientId,
			final String text, final String imageUri) {
		final ContentValues values = new ContentValues();
		values.put(Drafts.ACTION_TYPE, Drafts.ACTION_SEND_DIRECT_MESSAGE);
		values.put(Drafts.TEXT, text);
		values.put(Drafts.ACCOUNT_IDS, ArrayUtils.toString(new long[]{accountId}, ',', false));
		values.put(Drafts.TIMESTAMP, System.currentTimeMillis());
		if (imageUri != null) {
			final TwitterMediaUpdate[] medias = { new TwitterMediaUpdate(imageUri, 0) };
			values.put(Drafts.MEDIAS, JSONSerializer.toJSONArrayString(medias));
		}
		final JSONObject extras = new JSONObject();
		try {
			extras.put(SeagullTwitterConstants.EXTRA_RECIPIENT_ID, recipientId);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		values.put(Drafts.ACTION_EXTRAS, extras.toString());
		return values;
	}

	public static ContentValues makeFilterdUserContentValues(final TwitterStatus status) {
		if (status == null) return null;
		final ContentValues values = new ContentValues();
		values.put(Filters.Users.USER_ID, status.user_id);
		values.put(Filters.Users.NAME, status.user_name);
		values.put(Filters.Users.SCREEN_NAME, status.user_screen_name);
		return values;
	}

	public static ContentValues makeFilterdUserContentValues(final TwitterUser user) {
		if (user == null) return null;
		final ContentValues values = new ContentValues();
		values.put(Filters.Users.USER_ID, user.id);
		values.put(Filters.Users.NAME, user.name);
		values.put(Filters.Users.SCREEN_NAME, user.screen_name);
		return values;
	}

	public static ContentValues makeFilterdUserContentValues(final TwitterUserMention user) {
		if (user == null) return null;
		final ContentValues values = new ContentValues();
		values.put(Filters.Users.USER_ID, user.id);
		values.put(Filters.Users.NAME, user.name);
		values.put(Filters.Users.SCREEN_NAME, user.screen_name);
		return values;
	}

	public static ContentValues makeStatusContentValues(final twitter4j.Status orig, final long account_id) {
		if (orig == null || orig.getId() <= 0) return null;
		final ContentValues values = new ContentValues();
		values.put(Statuses.ACCOUNT_ID, account_id);
		values.put(Statuses.STATUS_ID, orig.getId());
		values.put(Statuses.STATUS_TIMESTAMP, orig.getCreatedAt().getTime());
		values.put(Statuses.MY_RETWEET_ID, orig.getCurrentUserRetweet());
		final boolean is_retweet = orig.isRetweet();
		final twitter4j.Status status;
		final twitter4j.Status retweeted_status = is_retweet ? orig.getRetweetedStatus() : null;
		if (retweeted_status != null) {
			final twitter4j.User retweet_user = orig.getUser();
			values.put(Statuses.RETWEET_ID, retweeted_status.getId());
			values.put(Statuses.RETWEET_TIMESTAMP, retweeted_status.getCreatedAt().getTime());
			values.put(Statuses.RETWEETED_BY_USER_ID, retweet_user.getId());
			values.put(Statuses.RETWEETED_BY_USER_NAME, retweet_user.getName());
			values.put(Statuses.RETWEETED_BY_USER_SCREEN_NAME, retweet_user.getScreenName());
			status = retweeted_status;
		} else {
			status = orig;
		}
		final twitter4j.User user = status.getUser();
		if (user != null) {
			final long userId = user.getId();
			final String profileImageUrl = ParseUtils.parseString(user.getProfileImageUrlHttps());
			final String name = user.getName(), screenName = user.getScreenName();
			values.put(Statuses.USER_ID, userId);
			values.put(Statuses.USER_NAME, name);
			values.put(Statuses.USER_SCREEN_NAME, screenName);
			values.put(Statuses.IS_PROTECTED, user.isProtected());
			values.put(Statuses.IS_VERIFIED, user.isVerified());
			values.put(Statuses.USER_PROFILE_IMAGE_URL, profileImageUrl);
			values.put(CachedUsers.IS_FOLLOWING, user.isFollowing());
		}
		final String text_html = Utils.formatStatusText(status);
		values.put(Statuses.TEXT_HTML, text_html);
		values.put(Statuses.TEXT_PLAIN, status.getText());
		values.put(Statuses.TEXT_UNESCAPED, HtmlEscapeHelper.toPlainText(text_html));
		values.put(Statuses.RETWEET_COUNT, status.getRetweetCount());
		values.put(Statuses.IN_REPLY_TO_STATUS_ID, status.getInReplyToStatusId());
		values.put(Statuses.IN_REPLY_TO_USER_ID, status.getInReplyToUserId());
		values.put(Statuses.IN_REPLY_TO_USER_NAME, Utils.getInReplyToName(status));
		values.put(Statuses.IN_REPLY_TO_USER_SCREEN_NAME, status.getInReplyToScreenName());
		values.put(Statuses.SOURCE, status.getSource());
		values.put(Statuses.IS_POSSIBLY_SENSITIVE, status.isPossiblySensitive());
		final GeoLocation location = status.getGeoLocation();
		if (location != null) {
			values.put(Statuses.LOCATION, location.getLatitude() + "," + location.getLongitude());
		}
		values.put(Statuses.IS_RETWEET, is_retweet);
		values.put(Statuses.IS_FAVORITE, status.isFavorited());
		final TwitterMedia[] medias = TwitterMedia.fromEntities(status);
		if (medias != null) {
			values.put(Statuses.MEDIAS, JSONSerializer.toJSONArrayString(medias));
			values.put(Statuses.FIRST_MEDIA, medias[0].url);
		}
		final TwitterUserMention[] mentions = TwitterUserMention.fromStatus(status);
		if (mentions != null) {
			values.put(Statuses.MENTIONS, JSONSerializer.toJSONArrayString(mentions));
		}
		return values;
	}

	public static ContentValues makeStatusDraftContentValues(final TwitterStatusUpdate status) {
		return makeStatusDraftContentValues(status, TwitterAccount.getAccountIds(status.accounts));
	}

	public static ContentValues makeStatusDraftContentValues(final TwitterStatusUpdate status,
			final long[] accountIds) {
		final ContentValues values = new ContentValues();
		values.put(Drafts.ACTION_TYPE, Drafts.ACTION_UPDATE_STATUS);
		values.put(Drafts.TEXT, status.text);
		values.put(Drafts.ACCOUNT_IDS, ArrayUtils.toString(accountIds, ',', false));
		values.put(Drafts.IN_REPLY_TO_STATUS_ID, status.in_reply_to_status_id);
		values.put(Drafts.LOCATION, TwitterLocation.toString(status.location));
		values.put(Drafts.IS_POSSIBLY_SENSITIVE, status.is_possibly_sensitive);
		values.put(Drafts.TIMESTAMP, System.currentTimeMillis());
		if (status.medias != null) {
			values.put(Drafts.MEDIAS, JSONSerializer.toJSONArrayString(status.medias));
		}
		return values;
	}

	public static ContentValues[] makeTrendsContentValues(final List<Trends> trends_list) {
		if (trends_list == null) return new ContentValues[0];
		final List<ContentValues> result_list = new ArrayList<ContentValues>();
		for (final Trends trends : trends_list) {
			if (trends == null) {
				continue;
			}
			final long timestamp = trends.getTrendAt().getTime();
			for (final Trend trend : trends.getTrends()) {
				final ContentValues values = new ContentValues();
				values.put(CachedTrends.NAME, trend.getName());
				values.put(CachedTrends.TIMESTAMP, timestamp);
				result_list.add(values);
			}
		}
		return result_list.toArray(new ContentValues[result_list.size()]);
	}

}