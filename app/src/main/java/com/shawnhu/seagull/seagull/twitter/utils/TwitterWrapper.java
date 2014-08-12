package com.shawnhu.seagull.seagull.twitter.utils;

import android.content.Context;
import android.net.Uri;

import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;

import java.io.File;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterWrapper {

    public static Response<Boolean> deleteProfileBannerImage(
            final Context context, final long account_id) {
        final Twitter twitter = Utils.getTwitterInstance(context, account_id, false);
        if (twitter == null) return new Response<Boolean>(false, null);
        try {
            twitter.removeProfileBannerImage();
            return new Response<Boolean>(true, null);
        } catch (final TwitterException e) {
            return new Response<Boolean>(false, e);
        }
    }

    public static Response<TwitterUser> updateProfile(
            final Context   context,  final long   account_id,
            final String    name,     final String url,
            final String    location, final String description) {
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

    public static Response<Boolean> updateProfileBannerImage(
            final Context context, final long account_id,
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

    public static Response<TwitterUser> updateProfileImage(
            final Context context, final long account_id,
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
