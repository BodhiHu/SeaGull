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

package com.shawnhu.seagull.seagull.twitter.utils.imageloader;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMedia;
import com.shawnhu.seagull.seagull.twitter.text.TwitterLinkify;
import com.shawnhu.seagull.seagull.twitter.utils.MediaPreviewUtils;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import twitter4j.TwitterException;
import twitter4j.auth.Authorization;
import twitter4j.http.HttpClientWrapper;
import twitter4j.http.HttpResponse;

public class TwitterImageDownloader extends BaseImageDownloader {

    private final Context mContext;
    private HttpClientWrapper mClient;
    private boolean mFastImageLoading = true;
    private final boolean mFullImage;
    private final String mTwitterProfileImageSize;

    public TwitterImageDownloader(final Context context, final boolean fullImage) {
        super(context);
        mContext = context;
        mFullImage = fullImage;
        mTwitterProfileImageSize = context.getString(R.string.profile_image_size);
        reloadConnectivitySettings();
    }

    public void reloadConnectivitySettings() {
        mClient = Utils.getImageLoaderHttpClient(mContext);
    }

    @Override
    protected InputStream getStreamFromNetwork(final String uriString, final Object extras) throws IOException {
        if (uriString == null) return null;
        final TwitterMedia media = MediaPreviewUtils.getAllAvailableImage(uriString, mFullImage, mFullImage
                || !mFastImageLoading ? mClient : null);
        try {
            final String mediaUrl = media != null ? media.media_url : uriString;
            if (isTwitterProfileImage(uriString)) {
                final String replaced = Utils.getTwitterProfileImageOfSize(mediaUrl, mTwitterProfileImageSize);
                return getStreamFromNetworkInternal(replaced, extras);
            } else
                return getStreamFromNetworkInternal(mediaUrl, extras);
        } catch (final TwitterException e) {
            final int statusCode = e.getStatusCode();
            if (statusCode != -1 && isTwitterProfileImage(uriString) && !uriString.contains("_normal.")) {
                try {
                    return getStreamFromNetworkInternal(Utils.getNormalTwitterProfileImage(uriString), extras);
                } catch (final TwitterException e2) {

                }
            }
            throw new IOException(String.format(Locale.US, "Error downloading image %s, error code: %d", uriString,
                    statusCode));
        }
    }

    private String getReplacedUri(final Uri uri, final String apiUrlFormat) {
        if (uri == null) return null;
        if (apiUrlFormat == null) return uri.toString();
        if (isTwitterUri(uri)) {
            final StringBuilder sb = new StringBuilder();
            final String domain = uri.getHost().replaceAll("\\.?twitter.com", "");
            final String path = uri.getPath();
            sb.append(Utils.getApiUrl(apiUrlFormat, domain, path));
            final String query = uri.getQuery();
            if (!TextUtils.isEmpty(query)) {
                sb.append("?");
                sb.append(query);
            }
            final String fragment = uri.getFragment();
            if (!TextUtils.isEmpty(fragment)) {
                sb.append("#");
                sb.append(fragment);
            }
            return sb.toString();
        }
        return uri.toString();
    }

    private ContentLengthInputStream getStreamFromNetworkInternal(final String uriString, final Object extras)
            throws IOException, TwitterException {
        final Uri uri = Uri.parse(uriString);
        final Authorization auth;
        final TwitterAccount.TwitterAccountWithCredentials account;
        if (isTwitterAuthRequired(uri) && extras instanceof AccountExtra) {
            final AccountExtra accountExtra = (AccountExtra) extras;
            account = TwitterAccount.getAccountWithCredentials(mContext, accountExtra.account_id);
            auth = Utils.getTwitterAuthorization(mContext, accountExtra.account_id);
        } else {
            account = null;
            auth = null;
        }
        final String modifiedUri = getReplacedUri(uri, account != null ? account.api_url_format : null);
        final HttpResponse resp = Utils.getRedirectedHttpResponse(mClient, modifiedUri, uriString, auth);
        return new ContentLengthInputStream(resp.asStream(), (int) resp.getContentLength());
    }

    private boolean isTwitterAuthRequired(final Uri uri) {
        if (uri == null) return false;
        return "ton.twitter.com".equalsIgnoreCase(uri.getHost());
    }

    private boolean isTwitterProfileImage(final String uriString) {
        if (TextUtils.isEmpty(uriString)) return false;
        return TwitterLinkify.PATTERN_TWITTER_PROFILE_IMAGES.matcher(uriString).matches();
    }

    private boolean isTwitterUri(final Uri uri) {
        if (uri == null) return false;
        return "ton.twitter.com".equalsIgnoreCase(uri.getHost());
    }

}
