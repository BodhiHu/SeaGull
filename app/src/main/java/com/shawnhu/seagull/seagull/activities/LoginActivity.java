package com.shawnhu.seagull.seagull.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractLoginActivity;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.auth.OAuthPasswordAuthenticator;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwitterHostResolverFactory;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwitterHttpClientFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.APP_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.APP_PROJECT_URL;
import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeAccountContentValuesOAuth;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.isUserLoggedIn;

public class LoginActivity extends AbstractLoginActivity {
    static final public String TAG = "LoginActivity";

    @Override
    protected int isEmailValid(String email) {
        return super.isEmailValid(email);
    }
    @Override
    protected int isPasswordValid(String password) {
        return super.isPasswordValid(password);
    }
    protected int asyncLoginUser(String acc, String pwd) {
        int ret = SUCCESS_CODE;
        SigninResponse response;
        response = Utils.authOAuth(getBaseContext(), acc, pwd, false);

        if (response != null) {
            if (response.succeed) {
                final ContentValues values;
                switch (response.auth_type) {
                    case TweetStore.Accounts.AUTH_TYPE_OAUTH:
                        values = makeAccountContentValuesOAuth(response.conf,
                                response.access_token, response.user, response.auth_type,
                                response.color, response.api_url_format,
                                response.same_oauth_signing_url);
                        break;
                    default:
                        values = null;
                        ret = R.string.auth_type_not_supported;
                }
                if (values != null) {
                    getApplicationContext()
                            .getContentResolver()
                            .insert(TweetStore.Accounts.CONTENT_URI, values);
                }
                long mLoggedId = response.user.getId();
                Intent home = new Intent(this, SeagullHomeActivity.class);
                home.putExtra(SeagullTwitterConstants.EXTRA_USER_ID, mLoggedId);
                setTargetIntent(home);
            } else if (response.already_logged_in) {
                ret = R.string.error_already_logged_in;

                long mLoggedId = response.user.getId();
                Intent home = new Intent(this, SeagullHomeActivity.class);
                home.putExtra(SeagullTwitterConstants.EXTRA_USER_ID, mLoggedId);
                setTargetIntent(home);
            } else if (response.exception != null) {
                Exception e = response.exception;

                e.printStackTrace();
                if (e instanceof OAuthPasswordAuthenticator.AuthenticityTokenException) {
                    ret = R.string.wrong_api_key;
                } else if (e instanceof OAuthPasswordAuthenticator.WrongUserPassException) {
                    ret = R.string.wrong_username_password;
                } else if (e instanceof OAuthPasswordAuthenticator.AuthenticationException) {
                    ret = R.string.error_unknown_error;
                } else {
                    ret = R.string.error_unknown_error;
                }
            } else {
                Log.e(TAG, "Got null Exception instance while user was not logged in," +
                        "this might be a bug.");
                ret = R.string.error_unknown_error;
            }
        } else {
            Log.e(TAG, "Got null SigninResponse, this is a bug.");
            ret = R.string.error_unknown_error;
        }

        return ret;
    }
    protected int asyncSignUpUser(String acc, String pwd) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(SeagullTwitterConstants.TWITTER_SIGNUP_URL));
        setTargetIntent(intent);
        return SUCCESS_CODE;
    }

    static final public class Utils {
        public static SigninResponse authOAuth(
                Context context,
                String username, String password,
                boolean same_oauth_signing_url)
        {
            try {
                Configuration conf =
                        getConfiguration(context);
                final Twitter twitter =
                        new TwitterFactory(conf).getInstance();
                final OAuthPasswordAuthenticator authenticator =
                        new OAuthPasswordAuthenticator(twitter);
                final AccessToken access_token =
                        authenticator.getOAuthAccessToken(username, password);

                final long user_id = access_token.getUserId();
                if (user_id <= 0) {
                    return new SigninResponse(false, false, null);
                }

                final User user = twitter.verifyCredentials();

                if (isUserLoggedIn(context, user_id)) {
                    return new SigninResponse(true, user, false, null);
                }

                final int color = android.R.color.holo_orange_light;

                return new SigninResponse(conf, access_token, user,
                        TweetStore.Accounts.AUTH_TYPE_OAUTH, color, null,
                        same_oauth_signing_url);
            } catch (final TwitterException e) {
                return new SigninResponse(false, false, e);
            } catch (final OAuthPasswordAuthenticator.AuthenticationException e) {
                return new SigninResponse(false, false, e);
            } catch (final Exception e) {
                return new SigninResponse(false, false, e);
            }
        }
        public static void setUpTwitterUserAgent(
                final Context context,
                final ConfigurationBuilder cb,
                boolean gzip_compressing)
        {
            final PackageManager pm = context.getPackageManager();
            try {
                final PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                final String version_name = pi.versionName;

                cb.setClientVersion(pi.versionName);
                cb.setClientName(APP_NAME);
                cb.setClientURL(APP_PROJECT_URL);
                cb.setHttpUserAgent(APP_NAME + " " + APP_PROJECT_URL + " / " + version_name
                        + (gzip_compressing ? " (gzip)" : ""));

            } catch (final PackageManager.NameNotFoundException e) {
                Log.e("Utils", e.toString());
                e.printStackTrace();
            }
        }
        public static Configuration getConfiguration(Context context)
        {
            if (context == null) {
                throw new NullPointerException("Context can not be null");
            }

            final ConfigurationBuilder cb           = new ConfigurationBuilder();
            final boolean enable_gzip_compressing   = false;
            final boolean ignore_ssl_error          = false;
            final boolean enable_proxy              = false;

            cb.setHostAddressResolverFactory(new TwitterHostResolverFactory())
                .setHttpClientFactory(new TwitterHttpClientFactory(context));

            setUpTwitterUserAgent(context, cb, true);

            cb.setOAuthConsumerKey(SeagullTwitterConstants.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(SeagullTwitterConstants.TWITTER_CONSUMER_SECRET)
                .setGZIPEnabled(enable_gzip_compressing)
                .setIgnoreSSLError(ignore_ssl_error);

            return cb.build();
        }
    }

    static class SigninResponse {

        public final boolean        already_logged_in, succeed;
        public final Exception      exception;
        public final Configuration  conf;
        public final String         basic_username, basic_password;
        public final AccessToken    access_token;
        public final User           user;
        public final int            auth_type, color;
        public final String         api_url_format;
        public final boolean        same_oauth_signing_url;

        public SigninResponse(final boolean     already_logged_in,
                              final User        user,
                              final boolean     succeed,
                              final Exception   exception) {
            this(already_logged_in, succeed, exception, null, null, null, null, user, 0, 0, null, false);
        }

        public SigninResponse(final boolean     already_logged_in,
                              final boolean     succeed,
                              final Exception   exception) {
            this(already_logged_in, succeed, exception,
                    null, null, null, null, null, 0, 0, null, false);
        }

        public SigninResponse(final boolean     already_logged_in,
                              final boolean     succeed,
                              final Exception   exception,
                              final Configuration conf,
                              final String      basic_username,
                              final String      basic_password,
                              final AccessToken access_token,
                              final User        user,
                              final int         auth_type,
                              final int         color,
                              final String      api_url_format,
                              final boolean     same_oauth_signing_url) {
            this.already_logged_in          = already_logged_in;
            this.succeed                    = succeed;
            this.exception                  = exception;
            this.conf                       = conf;
            this.basic_username             = basic_username;
            this.basic_password             = basic_password;
            this.access_token               = access_token;
            this.user                       = user;
            this.auth_type                  = auth_type;
            this.color                      = color;
            this.api_url_format             = api_url_format;
            this.same_oauth_signing_url     = same_oauth_signing_url;
        }

        public SigninResponse(final Configuration conf, final AccessToken access_token,
                              final User user, final int auth_type, final int color,
                              final String api_url_format,
                              final boolean same_oauth_signing_url) {
            this(false, true, null, conf, null, null, access_token, user, auth_type, color, api_url_format,
                    same_oauth_signing_url);
        }

        public SigninResponse(final Configuration conf, final String basic_username, final String basic_password,
                final User user, final int color, final String api_url_format) {
            this(false, true, null, conf, basic_username, basic_password, null, user, TweetStore.Accounts.AUTH_TYPE_BASIC, color,
                    api_url_format, false);
        }

        public SigninResponse(final Configuration conf, final User user, final int color, final String api_url_format) {
            this(false, true, null, conf, null, null, null, user, TweetStore.Accounts.AUTH_TYPE_TWIP_O_MODE, color,
                    api_url_format, false);
        }
    }
}
