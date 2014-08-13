package com.shawnhu.seagull.seagull.twitter.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.BuildConfig;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterDirectMessage;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;
import com.shawnhu.seagull.seagull.twitter.utils.content.ContentResolverUtils;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwitterHostResolverFactory;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwitterHttpClientFactory;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.BitmapDecodeHelper;
import com.shawnhu.seagull.utils.HtmlBuilder;
import com.shawnhu.seagull.utils.ParseUtils;
import com.shawnhu.seagull.utils.SysUtils;
import com.shawnhu.seagull.utils.querybuilder.AllColumns;
import com.shawnhu.seagull.utils.querybuilder.Columns;
import com.shawnhu.seagull.utils.querybuilder.OrderBy;
import com.shawnhu.seagull.utils.querybuilder.RawItemArray;
import com.shawnhu.seagull.utils.querybuilder.Selectable;
import com.shawnhu.seagull.utils.querybuilder.Tables;
import com.shawnhu.seagull.utils.querybuilder.Where;
import com.shawnhu.seagull.utils.querybuilder.query.SQLSelectQuery;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;

import crouton.Crouton;
import crouton.CroutonConfiguration;
import crouton.CroutonStyle;
import earlybird.UCDService;
import twitter4j.EntitySupport;
import twitter4j.MediaEntity;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.BasicAuthorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.TwipOModeAuthorization;
import twitter4j.auth.XAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.HostAddressResolverFactory;
import twitter4j.http.HttpClientWrapper;
import twitter4j.http.HttpResponse;

import static android.text.TextUtils.isEmpty;
import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.APP_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.APP_PROJECT_URL;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.DEFAULT_DATABASE_ITEM_LIMIT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.DEFAULT_IMAGE_UPLOAD_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.DEFAULT_QUOTE_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.DEFAULT_SHARE_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.FORMAT_PATTERN_LINK;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.FORMAT_PATTERN_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.FORMAT_PATTERN_TEXT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.FORMAT_PATTERN_TITLE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_COMPACT_CARDS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_CONNECTION_TIMEOUT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_CONSUMER_KEY;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_CONSUMER_SECRET;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_DATABASE_ITEM_LIMIT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_DEFAULT_ACCOUNT_ID;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_ENABLE_PROXY;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_FILTERS_FOR_RTS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_FORCE_USING_PRIVATE_APIS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_GZIP_COMPRESSING;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_IGNORE_SSL_ERROR;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_IMAGE_UPLOAD_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_LINK_HIGHLIGHT_OPTION;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_NAME_FIRST;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_PLAIN_LIST_STYLE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_PROXY_HOST;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_PROXY_PORT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_QUOTE_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_SHARE_FORMAT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_STOP_AUTO_REFRESH_WHEN_BATTERY_LOW;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_UCD_DATA_PROFILING;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.METADATA_KEY_EXTENSION_USE_JSON;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.SHARED_PREFERENCES_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.SILENT_NOTIFICATIONS_PREFERENCE_NAME;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_ACCOUNTS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_HASHTAGS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_USERS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DIRECT_MESSAGES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DIRECT_MESSAGES_CONVERSATION;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DIRECT_MESSAGES_CONVERSATION_SCREEN_NAME;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DRAFTS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_KEYWORDS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_LINKS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_SOURCES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_USERS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_MENTIONS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_TRENDS_LOCAL;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TWITTER_CONSUMER_KEY;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TWITTER_CONSUMER_SECRET;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TWITTER_MAX_IMAGE_HEIGHT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TWITTER_MAX_IMAGE_SIZE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TWITTER_MAX_IMAGE_WIDTH;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_BOTH;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_CODE_BOTH;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_CODE_HIGHLIGHT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_CODE_UNDERLINE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_HIGHLIGHT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_NONE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VALUE_LINK_HIGHLIGHT_OPTION_UNDERLINE;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Accounts;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CACHE_URIS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CachedHashtags;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CachedStatuses;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CachedTrends;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CachedUsers;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.DIRECT_MESSAGES_URIS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.DirectMessages;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Drafts;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Filters;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Mentions;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.STATUSES_URIS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Statuses;
import static com.shawnhu.seagull.seagull.twitter.text.TwitterLinkify.PATTERN_TWITTER_PROFILE_IMAGES;
import static com.shawnhu.seagull.seagull.twitter.text.TwitterLinkify.TWITTER_PROFILE_IMAGES_AVAILABLE_SIZES;
import static com.shawnhu.seagull.utils.HtmlEscapeHelper.toPlainText;
import static twitter4j.TwitterConstants.DEFAULT_SIGNING_OAUTH_BASE_URL;
import static twitter4j.TwitterConstants.DEFAULT_SIGNING_REST_BASE_URL;
import static twitter4j.TwitterConstants.DEFAULT_SIGNING_UPLOAD_BASE_URL;

public final class Utils {

    public static final Pattern PATTERN_XML_RESOURCE_IDENTIFIER     =
                                                    Pattern.compile("res\\/xml\\/([\\w_]+)\\.xml");
    public static final Pattern PATTERN_RESOURCE_IDENTIFIER         =
                                                    Pattern.compile("@([\\w_]+)\\/([\\w_]+)");
    static final String         MAPS_STATIC_IMAGE_URI_TEMPLATE      =
                                                    "https://maps.googleapis.com/maps/api/staticmap?zoom=%d&size=%dx%d&sensor=false&language=%s&center=%f,%f&markers=%f,%f";
    private static final String UA_TEMPLATE                         =
                                                    "Mozilla/5.0 (Linux; Android %s; %s Build/%s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.111 Safari/537.36";

    private static final UriMatcher LINK_HANDLER_URI_MATCHER        = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Accounts.CONTENT_PATH,
                                            TABLE_ID_ACCOUNTS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Statuses.CONTENT_PATH,
                                            TABLE_ID_STATUSES);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Mentions.CONTENT_PATH,
                                            TABLE_ID_MENTIONS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Drafts.CONTENT_PATH,
                                            TABLE_ID_DRAFTS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CachedUsers.CONTENT_PATH,
                                            TABLE_ID_CACHED_USERS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Filters.Users.CONTENT_PATH,
                                            TABLE_ID_FILTERED_USERS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Filters.Keywords.CONTENT_PATH,
                                            TABLE_ID_FILTERED_KEYWORDS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Filters.Sources.CONTENT_PATH,
                                            TABLE_ID_FILTERED_SOURCES);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.Filters.Links.CONTENT_PATH,
                                            TABLE_ID_FILTERED_LINKS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.DirectMessages.CONTENT_PATH,
                                            TABLE_ID_DIRECT_MESSAGES);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CachedTrends.Local.CONTENT_PATH,
                                            TABLE_ID_TRENDS_LOCAL);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CachedStatuses.CONTENT_PATH,
                                            TABLE_ID_CACHED_STATUSES);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CachedHashtags.CONTENT_PATH,
                                            TABLE_ID_CACHED_HASHTAGS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.DNS.CONTENT_PATH + "/*",
                                            TweetStore.VIRTUAL_TABLE_ID_DNS);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CachedImages.CONTENT_PATH,
                                            TweetStore.VIRTUAL_TABLE_ID_CACHED_IMAGES);
        TweetStore.CONTENT_PROVIDER_URI_MATCHER.addURI(TweetStore.AUTHORITY,
                                            TweetStore.CacheFiles.CONTENT_PATH + "/*",
                                            TweetStore.VIRTUAL_TABLE_ID_CACHE_FILES);


        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_STATUS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_STATUS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_TIMELINE,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_TIMELINE);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_FOLLOWERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_FOLLOWERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_FRIENDS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_FRIENDS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_FAVORITES,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_FAVORITES);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_BLOCKS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_BLOCKS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_DIRECT_MESSAGES_CONVERSATION,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_DIRECT_MESSAGES_CONVERSATION);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LIST,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LIST);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LIST_TIMELINE,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LIST_TIMELINE);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LIST_MEMBERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LIST_MEMBERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LIST_SUBSCRIBERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LIST_SUBSCRIBERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LIST_MEMBERSHIPS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LIST_MEMBERSHIPS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_LISTS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_LISTS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_SAVED_SEARCHES,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_SAVED_SEARCHES);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USER_MENTIONS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USER_MENTIONS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_INCOMING_FRIENDSHIPS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_INCOMING_FRIENDSHIPS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_USERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_USERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_STATUSES,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_STATUSES);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_STATUS_RETWEETERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_STATUS_RETWEETERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_STATUS_FAVORITERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_STATUS_FAVORITERS);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_STATUS_REPLIES,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_STATUS_REPLIES);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_SEARCH,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_SEARCH);
        LINK_HANDLER_URI_MATCHER.addURI(SeagullTwitterConstants.AUTHORITY_MUTES_USERS,
                                        null,
                                        SeagullTwitterConstants.LINK_ID_MUTES_USERS);

    }

    private static LongSparseArray<Integer>     sAccountColors      = new LongSparseArray<Integer>();
    private static LongSparseArray<String>      sAccountScreenNames = new LongSparseArray<String>();
    private static LongSparseArray<String>      sAccountNames       = new LongSparseArray<String>();

    private Utils() {
        throw new AssertionError("You are trying to create an instance for this utility class!");
    }

    public static void announceForAccessibilityCompat(final Context context, final View view, final CharSequence text,
            final Class<?> cls) {
        final AccessibilityManager accessibilityManager = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (!accessibilityManager.isEnabled()) return;
        // Prior to SDK 16, announcements could only be made through FOCUSED
        // events. Jelly Bean (SDK 16) added support for speaking text verbatim
        // using the ANNOUNCEMENT event type.
        final int eventType;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            eventType = AccessibilityEvent.TYPE_VIEW_FOCUSED;
        } else {
            eventType = AccessibilityEventCompat.TYPE_ANNOUNCEMENT;
        }

        // Construct an accessibility event with the minimum recommended
        // attributes. An event without a class name or package may be dropped.
        final AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
        event.getText().add(text);
        event.setClassName(cls.getName());
        event.setPackageName(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            event.setSource(view);
        }

        // Sends the event directly through the accessibility manager. If your
        // application only targets SDK 14+, you should just call
        // getParent().requestSendAccessibilityEvent(this, event);
        accessibilityManager.sendAccessibilityEvent(event);
    }

    public static Uri appendQueryParameters(final Uri uri, final NameValuePair... params) {
        final Uri.Builder builder = uri.buildUpon();
        if (params != null) {
            for (final NameValuePair param : params) {
                builder.appendQueryParameter(param.getName(), param.getValue());
            }
        }
        return builder.build();
    }

    public static String buildActivatedStatsWhereClause(final Context context, final String selection) {
        if (context == null) return null;
        final long[] account_ids = getActivatedAccountIds(context);
        final Where accountWhere = Where.in(new Columns.Column(TweetStore.Statuses.ACCOUNT_ID), new RawItemArray(account_ids));
        final Where where;
        if (selection != null) {
            where = Where.and(accountWhere, new Where(selection));
        } else {
            where = accountWhere;
        }
        return where.getSQL();
    }

    public static String buildStatusFilterWhereClause(final String table, final String selection,
            final boolean enableInRts) {
        if (table == null) return null;
        final StringBuilder builder = new StringBuilder();
        if (selection != null) {
            builder.append(selection);
            builder.append(" AND ");
        }
        builder.append(TweetStore.Statuses._ID + " NOT IN ( ");
        builder.append("SELECT DISTINCT " + table + "." + TweetStore.Statuses._ID + " FROM " + table);
        builder.append(" WHERE " + table + "." + TweetStore.Statuses.USER_ID + " IN ( SELECT " + TweetStore.Filters.Users.TABLE_NAME + "."
                + TweetStore.Filters.Users.USER_ID + " FROM " + TweetStore.Filters.Users.TABLE_NAME + " )");
        if (enableInRts) {
            builder.append(" OR " + table + "." + TweetStore.Statuses.RETWEETED_BY_USER_ID + " IN ( SELECT "
                    + TweetStore.Filters.Users.TABLE_NAME + "." + TweetStore.Filters.Users.USER_ID + " FROM " + TweetStore.Filters.Users.TABLE_NAME
                    + " )");
        }
        builder.append(" AND " + table + "." + TweetStore.Statuses.IS_GAP + " IS NULL");
        builder.append(" OR " + table + "." + TweetStore.Statuses.IS_GAP + " == 0");
        builder.append(" UNION ");
        builder.append("SELECT DISTINCT " + table + "." + TweetStore.Statuses._ID + " FROM " + table + ", "
                + TweetStore.Filters.Sources.TABLE_NAME);
        builder.append(" WHERE " + table + "." + TweetStore.Statuses.SOURCE + " LIKE '%>'||" + TweetStore.Filters.Sources.TABLE_NAME + "."
                + TweetStore.Filters.Sources.VALUE + "||'</a>%'");
        builder.append(" AND " + table + "." + TweetStore.Statuses.IS_GAP + " IS NULL");
        builder.append(" OR " + table + "." + TweetStore.Statuses.IS_GAP + " == 0");
        builder.append(" UNION ");
        builder.append("SELECT DISTINCT " + table + "." + TweetStore.Statuses._ID + " FROM " + table + ", "
                + TweetStore.Filters.Keywords.TABLE_NAME);
        builder.append(" WHERE " + table + "." + TweetStore.Statuses.TEXT_PLAIN + " LIKE '%'||" + TweetStore.Filters.Keywords.TABLE_NAME
                + "." + TweetStore.Filters.Keywords.VALUE + "||'%'");
        builder.append(" AND " + table + "." + TweetStore.Statuses.IS_GAP + " IS NULL");
        builder.append(" OR " + table + "." + TweetStore.Statuses.IS_GAP + " == 0");
        builder.append(" UNION ");
        builder.append("SELECT DISTINCT " + table + "." + TweetStore.Statuses._ID + " FROM " + table + ", "
                + TweetStore.Filters.Links.TABLE_NAME);
        builder.append(" WHERE " + table + "." + TweetStore.Statuses.TEXT_HTML + " LIKE '%<a href=\"%'||"
                + TweetStore.Filters.Links.TABLE_NAME + "." + TweetStore.Filters.Links.VALUE + "||'%\">%'");
        builder.append(" OR " + table + "." + TweetStore.Statuses.TEXT_HTML + " LIKE '%>%'||" + TweetStore.Filters.Links.TABLE_NAME + "."
                + TweetStore.Filters.Links.VALUE + "||'%</a>%'");
        builder.append(" AND " + table + "." + TweetStore.Statuses.IS_GAP + " IS NULL");
        builder.append(" OR " + table + "." + TweetStore.Statuses.IS_GAP + " == 0");
        builder.append(" )");
        return builder.toString();
    }

    public static int calculateInSampleSize(final int width, final int height, final int preferredWidth,
            final int preferredHeight) {
        if (preferredHeight > height && preferredWidth > width) return 1;
        final int result = Math.round(Math.max(width, height) / (float) Math.max(preferredWidth, preferredHeight));
        return Math.max(1, result);
    }

    public static int cancelRetweet(final AsyncTwitterWrapper wrapper, final TwitterStatus status) {
        if (wrapper == null || status == null) return -1;
        if (status.my_retweet_id > 0)
            return wrapper.destroyStatusAsync(status.account_id, status.my_retweet_id);
        else if (status.retweeted_by_id == status.account_id)
            return wrapper.destroyStatusAsync(status.account_id, status.retweet_id);
        return -1;
    }

    public static boolean checkActivityValidity(final Context context, final Intent intent) {
        final PackageManager pm = context.getPackageManager();
        return !pm.queryIntentActivities(intent, 0).isEmpty();
    }

    public static synchronized void cleanDatabasesByItemLimit(final Context context) {
        if (context == null) return;
        final ContentResolver resolver = context.getContentResolver();
        final int item_limit = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(
                KEY_DATABASE_ITEM_LIMIT, DEFAULT_DATABASE_ITEM_LIMIT);

        for (final long account_id : getAccountIds(context)) {
            // Clean statuses.
            for (final Uri uri : STATUSES_URIS) {
                if (TweetStore.CachedStatuses.CONTENT_URI.equals(uri)) {
                    continue;
                }
                final String table = getTableNameByUri(uri);
                final Where account_where = new Where(TweetStore.Statuses.ACCOUNT_ID + " = " + account_id);
                final SQLSelectQuery.Builder qb = new SQLSelectQuery.Builder();
                qb.select(new Columns.Column(TweetStore.Statuses._ID)).from(new Tables(table));
                qb.where(new Where(TweetStore.Statuses.ACCOUNT_ID + " = " + account_id));
                qb.orderBy(new OrderBy(TweetStore.Statuses.STATUS_ID + " DESC"));
                qb.limit(item_limit);
                final Where where = Where.and(Where.notIn(new Columns.Column(TweetStore.Statuses._ID), qb.build()), account_where);
                resolver.delete(uri, where.getSQL(), null);
            }
            for (final Uri uri : DIRECT_MESSAGES_URIS) {
                final String table = getTableNameByUri(uri);
                final Where account_where = new Where(TweetStore.DirectMessages.ACCOUNT_ID + " = " + account_id);
                final SQLSelectQuery.Builder qb = new SQLSelectQuery.Builder();
                qb.select(new Columns.Column(TweetStore.DirectMessages._ID)).from(new Tables(table));
                qb.where(new Where(TweetStore.DirectMessages.ACCOUNT_ID + " = " + account_id));
                qb.orderBy(new OrderBy(TweetStore.DirectMessages.MESSAGE_ID + " DESC"));
                qb.limit(item_limit);
                final Where where = Where.and(Where.notIn(new Columns.Column(TweetStore.DirectMessages._ID), qb.build()), account_where);
                resolver.delete(uri, where.getSQL(), null);
            }
        }
        // Clean cached values.
        for (final Uri uri : CACHE_URIS) {
            final String table = getTableNameByUri(uri);
            final SQLSelectQuery.Builder qb = new SQLSelectQuery.Builder();
            qb.select(new Columns.Column(BaseColumns._ID)).from(new Tables(table));
            final Where where = Where.notIn(new Columns.Column(TweetStore.Statuses._ID), qb.build());
            resolver.delete(uri, where.getSQL(), null);
        }
    }

    public static void clearAccountColor() {
        sAccountColors.clear();
    }

    public static void clearAccountName() {
        sAccountScreenNames.clear();
    }

    public static void clearListViewChoices(final AbsListView view) {
        if (view == null) return;
        final ListAdapter adapter = view.getAdapter();
        if (adapter == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.clearChoices();
            view.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        }
        // Workaround for Android bug
        // http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
        final int position = view.getFirstVisiblePosition(), offset = Utils.getFirstChildOffset(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAdapter(adapter);
        }
        Utils.scrollListToPosition(view, position, offset);
    }

    public static void clearListViewChoices(final StaggeredGridView view) {
        if (view == null) return;
        final ListAdapter adapter = view.getAdapter();
        if (adapter == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.clearChoices();
            view.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        }
        view.invalidateViews();
        // Workaround for Android bug
        // http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
        // final int position = view.getFirstVisiblePosition();
        // view.setAdapter(adapter);
        // Utils.scrollListToPosition(view, position);
    }

    public static boolean closeSilently(final Closeable c) {
        if (c == null) return false;
        try {
            c.close();
        } catch (final IOException e) {
            return false;
        }
        return true;
    }

    public static void copyStream(final InputStream is, final OutputStream os) throws IOException {
        final int buffer_size = 8192;
        final byte[] bytes = new byte[buffer_size];
        int count = is.read(bytes, 0, buffer_size);
        while (count != -1) {
            os.write(bytes, 0, count);
            count = is.read(bytes, 0, buffer_size);
        }
    }

    public static Intent createPickImageIntent(final Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    public static Intent createStatusShareIntent(final Context context, final TwitterStatus status) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        final String name = status.user_name, screenName = status.user_screen_name;
        final String timeString = formatToLongTimeString(context, status.timestamp);
        final String subject = context.getString(R.string.share_subject_format, name, screenName, timeString);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, status.text_plain);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent createTakePhotoIntent(final Uri uri) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    public static boolean downscaleImageIfNeeded(final File imageFile, final int quality) {
        if (imageFile == null || !imageFile.isFile()) return false;
        final String path = imageFile.getAbsolutePath();
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        // Corrupted image, so return now.
        if (o.outWidth <= 0 || o.outHeight <= 0) return false;
        o.inJustDecodeBounds = false;
        if (o.outWidth > TWITTER_MAX_IMAGE_WIDTH || o.outHeight > TWITTER_MAX_IMAGE_HEIGHT) {
            // The image dimension is larger than Twitter's limit.
            o.inSampleSize = calculateInSampleSize(o.outWidth, o.outHeight, TWITTER_MAX_IMAGE_WIDTH,
                    TWITTER_MAX_IMAGE_HEIGHT);
            try {
                final Bitmap b = BitmapDecodeHelper.decode(path, o);
                final Bitmap.CompressFormat format = getBitmapCompressFormatByMimetype(o.outMimeType,
                        Bitmap.CompressFormat.PNG);
                final FileOutputStream fos = new FileOutputStream(imageFile);
                return b.compress(format, quality, fos);
            } catch (final OutOfMemoryError e) {
                return false;
            } catch (final FileNotFoundException e) {
                // This shouldn't happen.
            } catch (final IllegalArgumentException e) {
                return false;
            }
        } else if (imageFile.length() > TWITTER_MAX_IMAGE_SIZE) {
            // The file size is larger than Twitter's limit.
            try {
                final Bitmap b = BitmapDecodeHelper.decode(path, o);
                final FileOutputStream fos = new FileOutputStream(imageFile);
                return b.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            } catch (final OutOfMemoryError e) {
                return false;
            } catch (final FileNotFoundException e) {
                // This shouldn't happen.
            }
        }
        return true;
    }

    public static String encodeQueryParams(final String value) throws IOException {
        final String encoded = URLEncoder.encode(value, "UTF-8");
        final StringBuilder buf = new StringBuilder();
        final int length = encoded.length();
        char focus;
        for (int i = 0; i < length; i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && i + 1 < encoded.length() && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    public static TwitterDirectMessage findDirectMessageInDatabases(final Context context, final long account_id,
            final long message_id) {
        if (context == null) return null;
        final ContentResolver resolver = context.getContentResolver();
        TwitterDirectMessage message = null;
        final String where = TweetStore.DirectMessages.ACCOUNT_ID + " = " + account_id + " AND " + TweetStore.DirectMessages.MESSAGE_ID
                + " = " + message_id;
        for (final Uri uri : DIRECT_MESSAGES_URIS) {
            final Cursor cur = ContentResolverUtils.query(resolver, uri, TweetStore.DirectMessages.COLUMNS, where, null, null);
            if (cur == null) {
                continue;
            }
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                message = new TwitterDirectMessage(cur, new TwitterDirectMessage.CursorIndices(cur));
            }
            cur.close();
        }
        return message;
    }

    public static TwitterStatus findStatus(final Context context, final long account_id, final long status_id)
            throws TwitterException {
        if (context == null || account_id <= 0 || status_id <= 0) return null;
        final TwitterStatus p_status = findStatusInDatabases(context, account_id, status_id);
        if (p_status != null) return p_status;
        final Twitter twitter = getTwitterInstance(context, account_id, true);
        if (twitter == null) return null;
        final twitter4j.Status status = twitter.showStatus(status_id);
        if (status == null || status.getId() <= 0) return null;
        final String where = TweetStore.Statuses.ACCOUNT_ID + " = " + account_id + " AND " + TweetStore.Statuses.STATUS_ID + " = "
                + status.getId();
        final ContentResolver resolver = context.getContentResolver();
        resolver.delete(TweetStore.CachedStatuses.CONTENT_URI, where, null);
        resolver.insert(TweetStore.CachedStatuses.CONTENT_URI, ContentValuesCreator.makeStatusContentValues(status, account_id));
        return new TwitterStatus(status, account_id, false);
    }

    public static TwitterStatus findStatusInDatabases(final Context context, final long account_id,
            final long status_id) {
        if (context == null) return null;
        final ContentResolver resolver = context.getContentResolver();
        TwitterStatus status = null;
        final String where = TweetStore.Statuses.ACCOUNT_ID + " = " + account_id + " AND " + TweetStore.Statuses.STATUS_ID + " = "
                + status_id;
        for (final Uri uri : STATUSES_URIS) {
            final Cursor cur = ContentResolverUtils.query(resolver, uri, TweetStore.Statuses.COLUMNS, where, null, null);
            if (cur == null) {
                continue;
            }
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                status = new TwitterStatus(cur, new TwitterStatus.CursorIndices(cur));
            }
            cur.close();
        }
        return status;
    }

    public static String formatDirectMessageText(final twitter4j.DirectMessage message) {
        if (message == null) return null;
        final String text = message.getRawText();
        if (text == null) return null;
        final HtmlBuilder builder = new HtmlBuilder(text, false, true, true);
        parseEntities(builder, message);
        return builder.build().replace("\n", "<br/>");
    }

    public static String formatExpandedUserDescription(final twitter4j.User user) {
        if (user == null) return null;
        final String text = user.getDescription();
        if (text == null) return null;
        final HtmlBuilder builder = new HtmlBuilder(text, false, true, true);
        final URLEntity[] urls = user.getDescriptionEntities();
        if (urls != null) {
            for (final URLEntity url : urls) {
                final String expanded_url = ParseUtils.parseString(url.getExpandedURL());
                if (expanded_url != null) {
                    builder.addLink(expanded_url, expanded_url, url.getStart(), url.getEnd());
                }
            }
        }
        return toPlainText(builder.build().replace("\n", "<br/>"));
    }

    @SuppressWarnings("deprecation")
    public static String formatSameDayTime(final Context context, final long timestamp) {
        if (context == null) return null;
        if (DateUtils.isToday(timestamp))
            return DateUtils.formatDateTime(context, timestamp,
                    DateFormat.is24HourFormat(context) ? DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR
                            : DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR);
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE);
    }

    public static String formatStatusText(final twitter4j.Status status) {
        if (status == null) return null;
        final String text = status.getRawText();
        if (text == null) return null;
        final HtmlBuilder builder = new HtmlBuilder(text, false, true, true);
        parseEntities(builder, status);
        return builder.build().replace("\n", "<br/>");
    }

    @SuppressWarnings("deprecation")
    public static String formatTimeStampString(final Context context, final long timestamp) {
        if (context == null) return null;
        final Time then = new Time();
        then.set(timestamp);
        final Time now = new Time();
        now.setToNow();

        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

        if (then.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            format_flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(context, timestamp, format_flags);
    }

    @SuppressWarnings("deprecation")
    public static String formatTimeStampString(final Context context, final String date_time) {
        if (context == null) return null;
        return formatTimeStampString(context, Date.parse(date_time));
    }

    @SuppressWarnings("deprecation")
    public static String formatToLongTimeString(final Context context, final long timestamp) {
        if (context == null) return null;
        final Time then = new Time();
        then.set(timestamp);
        final Time now = new Time();
        now.setToNow();

        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

        format_flags |= DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;

        return DateUtils.formatDateTime(context, timestamp, format_flags);
    }

    public static String formatUserDescription(final twitter4j.User user) {
        if (user == null) return null;
        final String text = user.getDescription();
        if (text == null) return null;
        final HtmlBuilder builder = new HtmlBuilder(text, false, true, true);
        final URLEntity[] urls = user.getDescriptionEntities();
        if (urls != null) {
            for (final URLEntity url : urls) {
                final URL expanded_url = url.getExpandedURL();
                if (expanded_url != null) {
                    builder.addLink(ParseUtils.parseString(expanded_url), url.getDisplayURL(), url.getStart(),
                            url.getEnd());
                }
            }
        }
        return builder.build().replace("\n", "<br/>");
    }

    public static String generateBrowserUserAgent() {
        return String.format(UA_TEMPLATE, Build.VERSION.RELEASE, Build.MODEL, Build.ID);
    }

    public static int getAccountColor(final Context context, final long account_id) {
        if (context == null) return Color.TRANSPARENT;
        final Integer cached = sAccountColors.get(account_id);
        if (cached != null) return cached;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI,
                new String[] { TweetStore.Accounts.COLOR }, TweetStore.Accounts.ACCOUNT_ID + " = " + account_id, null, null);
        if (cur == null) return Color.TRANSPARENT;
        try {
            if (cur.getCount() > 0 && cur.moveToFirst()) {
                final int color = cur.getInt(0);
                sAccountColors.put(account_id, color);
                return color;
            }
            return Color.TRANSPARENT;
        } finally {
            cur.close();
        }
    }

    public static int[] getAccountColors(final Context context, final long[] account_ids) {
        if (context == null || account_ids == null) return new int[0];
        final String[] cols = new String[] { TweetStore.Accounts.COLOR };
        final String where = Where.in(new Columns.Column(TweetStore.Accounts.ACCOUNT_ID), new RawItemArray(account_ids)).getSQL();
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI, cols, where,
                null, null);
        if (cur == null) return new int[0];
        try {
            cur.moveToFirst();
            final int[] colors = new int[cur.getCount()];
            int i = 0;
            while (!cur.isAfterLast()) {
                colors[i++] = cur.getInt(0);
                cur.moveToNext();
            }
            return colors;
        } finally {
            cur.close();
        }
    }

    public static String getAccountDisplayName(final Context context, final long accountId, final boolean nameFirst) {
        final String name;
        if (nameFirst) {
            name = getAccountName(context, accountId);
        } else {
            name = String.format("@%s", getAccountScreenName(context, accountId));
        }
        return name;
    }

    public static long getAccountId(final Context context, final String screen_name) {
        if (context == null || isEmpty(screen_name)) return -1;
        final Cursor cur = ContentResolverUtils
                .query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI, new String[] { TweetStore.Accounts.ACCOUNT_ID },
                        TweetStore.Accounts.SCREEN_NAME + " = ?", new String[] { screen_name }, null);
        if (cur == null) return -1;
        try {
            if (cur.getCount() > 0 && cur.moveToFirst()) return cur.getLong(0);
            return -1;
        } finally {
            cur.close();
        }
    }

    public static long[] getAccountIds(final Context context) {
        if (context == null) return new long[0];
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI,
                new String[] { TweetStore.Accounts.ACCOUNT_ID }, null, null, null);
        if (cur == null) return new long[0];
        try {
            cur.moveToFirst();
            final long[] ids = new long[cur.getCount()];
            int i = 0;
            while (!cur.isAfterLast()) {
                ids[i++] = cur.getLong(0);
                cur.moveToNext();
            }
            return ids;
        } finally {
            cur.close();
        }
    }

    public static String getAccountName(final Context context, final long accountId) {
        if (context == null) return null;
        final String cached = sAccountNames.get(accountId);
        if (!isEmpty(cached)) return cached;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI,
                new String[] { TweetStore.Accounts.NAME }, TweetStore.Accounts.ACCOUNT_ID + " = " + accountId, null, null);
        if (cur == null) return null;
        try {
            if (cur.getCount() > 0 && cur.moveToFirst()) {
                final String name = cur.getString(0);
                sAccountNames.put(accountId, name);
                return name;
            }
            return null;
        } finally {
            cur.close();
        }
    }

    public static String[] getAccountNames(final Context context) {
        return getAccountScreenNames(context, null);
    }

    public static String[] getAccountNames(final Context context, final long[] accountIds) {
        if (context == null) return new String[0];
        final String[] cols = new String[] { TweetStore.Accounts.NAME };
        final String where = accountIds != null ? Where.in(new Columns.Column(TweetStore.Accounts.ACCOUNT_ID),
                new RawItemArray(accountIds)).getSQL() : null;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI, cols, where,
                null, null);
        if (cur == null) return new String[0];
        try {
            cur.moveToFirst();
            final String[] names = new String[cur.getCount()];
            int i = 0;
            while (!cur.isAfterLast()) {
                names[i++] = cur.getString(0);
                cur.moveToNext();
            }
            return names;
        } finally {
            cur.close();
        }
    }

    public static int getAccountNotificationId(final int notificationType, final long accountId) {
        return Arrays.hashCode(new long[]{notificationType, accountId});
    }

    public static String getAccountScreenName(final Context context, final long accountId) {
        if (context == null) return null;
        final String cached = sAccountScreenNames.get(accountId);
        if (!isEmpty(cached)) return cached;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI,
                new String[] { TweetStore.Accounts.SCREEN_NAME }, TweetStore.Accounts.ACCOUNT_ID + " = " + accountId, null, null);
        if (cur == null) return null;
        try {
            if (cur.getCount() > 0 && cur.moveToFirst()) {
                final String name = cur.getString(0);
                sAccountScreenNames.put(accountId, name);
                return name;
            }
            return null;
        } finally {
            cur.close();
        }
    }

    public static String[] getAccountScreenNames(final Context context) {
        return getAccountScreenNames(context, false);
    }

    public static String[] getAccountScreenNames(final Context context, final boolean includeAtChar) {
        return getAccountScreenNames(context, null, includeAtChar);
    }

    public static String[] getAccountScreenNames(final Context context, final long[] accountIds) {
        return getAccountScreenNames(context, accountIds, false);
    }

    public static String[] getAccountScreenNames(final Context context, final long[] accountIds,
            final boolean includeAtChar) {
        if (context == null) return new String[0];
        final String[] cols = new String[] { TweetStore.Accounts.SCREEN_NAME };
        final String where = accountIds != null ? Where.in(new Columns.Column(TweetStore.Accounts.ACCOUNT_ID),
                new RawItemArray(accountIds)).getSQL() : null;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI, cols, where,
                null, null);
        if (cur == null) return new String[0];
        try {
            cur.moveToFirst();
            final String[] screen_names = new String[cur.getCount()];
            int i = 0;
            while (!cur.isAfterLast()) {
                screen_names[i++] = cur.getString(0);
                cur.moveToNext();
            }
            return screen_names;
        } finally {
            cur.close();
        }
    }

    public static long[] getActivatedAccountIds(final Context context) {
        if (context == null) return new long[0];
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), TweetStore.Accounts.CONTENT_URI,
                new String[] { TweetStore.Accounts.ACCOUNT_ID }, TweetStore.Accounts.IS_ACTIVATED + " = 1", null, null);
        if (cur == null) return new long[0];
        try {
            cur.moveToFirst();
            final long[] ids = new long[cur.getCount()];
            int i = 0;
            while (!cur.isAfterLast()) {
                ids[i++] = cur.getLong(0);
                cur.moveToNext();
            }
            return ids;
        } finally {
            cur.close();
        }
    }

    public static int getAllStatusesCount(final Context context, final Uri uri) {
        if (context == null) return 0;
        final ContentResolver resolver = context.getContentResolver();
        final Cursor cur = ContentResolverUtils.query(resolver, uri, new String[] { TweetStore.Statuses.STATUS_ID },
                buildStatusFilterWhereClause(getTableNameByUri(uri), null, shouldEnableFiltersForRTs(context)), null,
                null);
        if (cur == null) return 0;
        try {
            return cur.getCount();
        } finally {
            cur.close();
        }
    }

    public static long[] getAllStatusesIds(final Context context, final Uri uri) {
        if (context == null) return new long[0];
        final ContentResolver resolver = context.getContentResolver();
        final Cursor cur = ContentResolverUtils.query(resolver, uri, new String[] { TweetStore.Statuses.STATUS_ID },
                buildStatusFilterWhereClause(getTableNameByUri(uri), null, shouldEnableFiltersForRTs(context)), null,
                null);
        if (cur == null) return new long[0];
        final long[] ids = new long[cur.getCount()];
        cur.moveToFirst();
        int i = 0;
        while (!cur.isAfterLast()) {
            ids[i] = cur.getLong(0);
            cur.moveToNext();
            i++;
        }
        cur.close();
        return ids;
    }



    public static String getApiBaseUrl(final String pattern, final String domain) {
        if (pattern == null) return null;
        if (TextUtils.isEmpty(domain)) return pattern.replaceAll("\\[\\.?DOMAIN\\.?\\]", "");
        return pattern.replaceAll("\\[(\\.?)DOMAIN(\\.?)\\]", String.format("$1%s$2", domain));
    }

    public static String getApiUrl(final String pattern, final String domain, final String appendPath) {
        final String urlBase = getApiBaseUrl(pattern, domain);
        if (urlBase == null || appendPath == null) return urlBase;
        final StringBuilder sb = new StringBuilder(urlBase);
        if (urlBase.endsWith("/")) {
            sb.append(appendPath.startsWith("/") ? appendPath.substring(1) : appendPath);
        } else {
            if (appendPath.startsWith("/")) {
                sb.append(appendPath);
            } else {
                sb.append('/');
                sb.append(appendPath);
            }
        }
        return sb.toString();
    }

    public static String getBestBannerType(final int width) {
        if (width <= 320)
            return "mobile";
        else if (width <= 520)
            return "web";
        else if (width <= 626)
            return "ipad";
        else if (width <= 640)
            return "mobile_retina";
        else if (width <= 1040)
            return "web_retina";
        else
            return "ipad_retina";
    }

    public static File getBestCacheDir(final Context context, final String cacheDirName) {
        if (context == null) throw new NullPointerException();
        final File extCacheDir;
        try {
            // Workaround for https://github.com/mariotaku/twidere/issues/138
            extCacheDir = context.getExternalCacheDir();
        } catch (final Exception e) {
            return new File(context.getCacheDir(), cacheDirName);
        }
        if (extCacheDir != null && extCacheDir.isDirectory()) {
            final File cacheDir = new File(extCacheDir, cacheDirName);
            if (cacheDir.isDirectory() || cacheDir.mkdirs()) return cacheDir;
        }
        return new File(context.getCacheDir(), cacheDirName);
    }

    public static String getBiggerTwitterProfileImage(final String url) {
        return getTwitterProfileImageOfSize(url, "bigger");
    }

    public static Bitmap getBitmap(final Drawable drawable) {
        if (drawable instanceof NinePatchDrawable) return null;
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        else if (drawable instanceof TransitionDrawable) {
            final int layer_count = ((TransitionDrawable) drawable).getNumberOfLayers();
            for (int i = 0; i < layer_count; i++) {
                final Drawable layer = ((TransitionDrawable) drawable).getDrawable(i);
                if (layer instanceof BitmapDrawable) return ((BitmapDrawable) layer).getBitmap();
            }
        }
        return null;
    }

    public static Bitmap.CompressFormat getBitmapCompressFormatByMimetype(final String mimeType,
            final Bitmap.CompressFormat def) {
        final String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if ("jpeg".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension))
            return Bitmap.CompressFormat.JPEG;
        else if ("png".equalsIgnoreCase(extension))
            return Bitmap.CompressFormat.PNG;
        else if ("webp".equalsIgnoreCase(extension)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return Bitmap.CompressFormat.WEBP;
            }
        }
        return def;
    }


    public static int getCharacterCount(final String string, final char c) {
        if (string == null) return 0;
        int count = 0;
        while (string.indexOf(c, count) != -1) {
            count++;
        }
        return count;
    }

    public static Selectable getColumnsFromProjection(final String... projection) {
        if (projection == null) return new AllColumns();
        final int length = projection.length;
        final Columns.Column[] columns = new Columns.Column[length];
        for (int i = 0; i < length; i++) {
            columns[i] = new Columns.Column(projection[i]);
        }
        return new Columns(columns);
    }

    public static long getDefaultAccountId(final Context context) {
        if (context == null) return -1;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_DEFAULT_ACCOUNT_ID, -1);
    }

    public static String getDefaultAccountScreenName(final Context context) {
        if (context == null) return null;
        return getAccountScreenName(context, getDefaultAccountId(context));
    }

    public static Twitter getDefaultTwitterInstance(final Context context, final boolean include_entities) {
        if (context == null) return null;
        return getDefaultTwitterInstance(context, include_entities, true, true);
    }

    public static Twitter getDefaultTwitterInstance(final Context context, final boolean includeEntities,
            final boolean includeRetweets, final boolean apacheHttp) {
        if (context == null) return null;
        return getTwitterInstance(context, getDefaultAccountId(context), includeEntities, includeRetweets, apacheHttp);
    }

    public static String getDisplayName(final Context context, final long userId, final String name,
            final String screenName) {
        return getDisplayName(context, userId, name, screenName, false);
    }

    public static String getDisplayName(final Context context, final long user_id, final String name,
            final String screen_name, final boolean ignore_cache) {
        if (context == null) return null;

        return getDisplayName(context, user_id, name, screen_name, true, false, ignore_cache);
    }

    public static String getDisplayName(final Context context, final long user_id, final String name,
            final String screen_name, final boolean name_first, final boolean nickname_only) {
        return getDisplayName(context, user_id, name, screen_name, name_first, nickname_only, false);
    }

    public static String getDisplayName(final Context context, final long user_id, final String name,
            final String screen_name, final boolean name_first, final boolean nickname_only, final boolean ignore_cache) {
        if (context == null) return null;
        final boolean nick_available = false;
        if (!nick_available) return name_first && !isEmpty(name) ? name : "@" + screen_name;
        return context.getString(R.string.name_with_nickname, name_first && !isEmpty(name) ? name : "@" + screen_name,
                null);
    }

    public static String getErrorMessage(final Context context, final CharSequence message) {
        if (context == null) return ParseUtils.parseString(message);
        if (isEmpty(message)) return context.getString(R.string.error_unknown_error);
        return context.getString(R.string.error_message, message);
    }

    public static String getErrorMessage(final Context context, final CharSequence action, final CharSequence message) {
        if (context == null || isEmpty(action)) return ParseUtils.parseString(message);
        if (isEmpty(message)) return context.getString(R.string.error_unknown_error);
        return context.getString(R.string.error_message_with_action, action, message);
    }

    public static String getErrorMessage(final Context context, final CharSequence action, final Throwable t) {
        if (context == null) return null;
        if (t instanceof TwitterException)
            return getTwitterErrorMessage(context, action, (TwitterException) t);
        else if (t != null) return getErrorMessage(context, trimLineBreak(t.getMessage()));
        return context.getString(R.string.error_unknown_error);
    }

    public static String getErrorMessage(final Context context, final Throwable t) {
        if (t == null) return null;
        if (context != null && t instanceof TwitterException)
            return getTwitterErrorMessage(context, (TwitterException) t);
        return t.getMessage();
    }

    public static int getFirstChildOffset(final AbsListView list) {
        if (list == null || list.getChildCount() == 0) return 0;
        return list.getChildAt(0).getTop();
    }

    public static HttpClientWrapper getHttpClient(final Context context, final int timeoutMillis,
            final boolean ignoreSslError, final Proxy proxy, final HostAddressResolverFactory resolverFactory,
            final String userAgent, final boolean twitterClientHeader) {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setHttpConnectionTimeout(timeoutMillis);
        cb.setIgnoreSSLError(ignoreSslError);
        cb.setIncludeTwitterClientHeader(twitterClientHeader);
        if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
            final SocketAddress address = proxy.address();
            if (address instanceof InetSocketAddress) {
                cb.setHttpProxyHost(((InetSocketAddress) address).getHostName());
                cb.setHttpProxyPort(((InetSocketAddress) address).getPort());
            }
        }
        cb.setHostAddressResolverFactory(resolverFactory);
        if (userAgent != null) {
            cb.setHttpUserAgent(userAgent);
        }
        cb.setHttpClientFactory(new TwitterHttpClientFactory(context));
        return new HttpClientWrapper(cb.build());
    }

    public static HttpClientWrapper getImageLoaderHttpClient(final Context context) {
        if (context == null) return null;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final int timeoutMillis = prefs.getInt(KEY_CONNECTION_TIMEOUT, 10000) * 1000;
        final Proxy proxy = getProxy(context);
        final String userAgent = generateBrowserUserAgent();
        final HostAddressResolverFactory resolverFactory = new TwitterHostResolverFactory();
        return getHttpClient(context, timeoutMillis, true, proxy, resolverFactory, userAgent, false);
    }

    public static String getImageMimeType(final File image) {
        if (image == null) return null;
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), o);
        return o.outMimeType;
    }

    public static String getImageMimeType(final InputStream is) {
        if (is == null) return null;
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, o);
        return o.outMimeType;
    }

    public static String getImagePathFromUri(final Context context, final Uri uri) {
        if (context == null || uri == null) return null;

        final String mediaUriStart = ParseUtils.parseString(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (ParseUtils.parseString(uri).startsWith(mediaUriStart)) {

            final String[] proj = { MediaStore.Images.Media.DATA };
            final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), uri, proj, null, null, null);

            if (cur == null) return null;

            final int idxData = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cur.moveToFirst();
            try {
                return cur.getString(idxData);
            } finally {
                cur.close();
            }
        } else {
            final String path = uri.getPath();
            if (path != null && new File(path).exists()) return path;
        }
        return null;
    }

    public static String getImageUploadStatus(final Context context, final CharSequence[] links, final CharSequence text) {
        if (context == null || links == null || links.length == 0) return ParseUtils.parseString(text);
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final String imageUploadFormat = getNonEmptyString(prefs, KEY_IMAGE_UPLOAD_FORMAT, DEFAULT_IMAGE_UPLOAD_FORMAT);
        return imageUploadFormat.replace(FORMAT_PATTERN_LINK, ArrayUtils.toString(links, ' ', false)).replace(
                FORMAT_PATTERN_TEXT, text);
    }

    public static String getInReplyToName(final twitter4j.Status status) {
        if (status == null) return null;
        final twitter4j.Status orig = status.isRetweet() ? status.getRetweetedStatus() : status;
        final long in_reply_to_user_id = status.getInReplyToUserId();
        final UserMentionEntity[] entities = status.getUserMentionEntities();
        if (entities == null) return orig.getInReplyToScreenName();
        for (final UserMentionEntity entity : entities) {
            if (in_reply_to_user_id == entity.getId()) return entity.getName();
        }
        return orig.getInReplyToScreenName();
    }

    public static File getInternalCacheDir(final Context context, final String cacheDirName) {
        if (context == null) throw new NullPointerException();
        final File cacheDir = new File(context.getCacheDir(), cacheDirName);
        if (cacheDir.isDirectory() || cacheDir.mkdirs()) return cacheDir;
        return new File(context.getCacheDir(), cacheDirName);
    }

    public static CharSequence getKeywordBoldedText(final CharSequence orig, final String... keywords) {
        return getKeywordHighlightedText(orig, new StyleSpan(Typeface.BOLD), keywords);
    }

    public static CharSequence getKeywordHighlightedText(final CharSequence orig, final CharacterStyle style,
            final String... keywords) {
        if (keywords == null || keywords.length == 0 || orig == null) return orig;
        final SpannableStringBuilder sb = SpannableStringBuilder.valueOf(orig);
        final StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0, j = keywords.length; i < j; i++) {
            if (i != 0) {
                patternBuilder.append('|');
            }
            patternBuilder.append(Pattern.quote(keywords[i]));
        }
        final Matcher m = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE).matcher(orig);
        while (m.find()) {
            sb.setSpan(style, m.start(), m.end(), SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return sb;
    }

    public static String getLinkHighlightOption(final Context context) {
        if (context == null) return null;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LINK_HIGHLIGHT_OPTION, VALUE_LINK_HIGHLIGHT_OPTION_NONE);
    }

    public static int getLinkHighlightOptionInt(final Context context) {
        return getLinkHighlightOptionInt(getLinkHighlightOption(context));
    }

    public static int getLinkHighlightOptionInt(final String option) {
        if (VALUE_LINK_HIGHLIGHT_OPTION_BOTH.equals(option))
            return VALUE_LINK_HIGHLIGHT_OPTION_CODE_BOTH;
        else if (VALUE_LINK_HIGHLIGHT_OPTION_HIGHLIGHT.equals(option))
            return VALUE_LINK_HIGHLIGHT_OPTION_CODE_HIGHLIGHT;
        else if (VALUE_LINK_HIGHLIGHT_OPTION_UNDERLINE.equals(option))
            return VALUE_LINK_HIGHLIGHT_OPTION_CODE_UNDERLINE;
        return VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE;
    }

    public static String getLocalizedNumber(final Locale locale, final Number number) {
        final NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(number);
    }

    public static String getMapStaticImageUri(final double lat, final double lng, final int zoom, final int w,
            final int h, final Locale locale) {
        return String.format(Locale.US, MAPS_STATIC_IMAGE_URI_TEMPLATE, zoom, w, h, locale.toString(), lat, lng, lat,
                lng);
    }

    public static String getMapStaticImageUri(final double lat, final double lng, final View v) {
        if (v == null) return null;
        final int wSpec = MeasureSpec.makeMeasureSpec(v.getWidth(), MeasureSpec.UNSPECIFIED);
        final int hSpec = MeasureSpec.makeMeasureSpec(v.getHeight(), MeasureSpec.UNSPECIFIED);
        v.measure(wSpec, hSpec);
        return getMapStaticImageUri(lat, lng, 12, v.getMeasuredWidth(), v.getMeasuredHeight(), v.getResources()
                .getConfiguration().locale);
    }

    public static long[] getNewestMessageIdsFromDatabase(final Context context, final Uri uri) {
        final long[] account_ids = getActivatedAccountIds(context);
        return getNewestMessageIdsFromDatabase(context, uri, account_ids);
    }

    public static long[] getNewestMessageIdsFromDatabase(final Context context, final Uri uri, final long[] account_ids) {
        if (context == null || uri == null || account_ids == null) return null;
        final String[] cols = new String[] { DirectMessages.MESSAGE_ID };
        final ContentResolver resolver = context.getContentResolver();
        final long[] status_ids = new long[account_ids.length];
        int idx = 0;
        for (final long account_id : account_ids) {
            final String where = Statuses.ACCOUNT_ID + " = " + account_id;
            final Cursor cur = ContentResolverUtils.query(resolver, uri, cols, where, null,
                    DirectMessages.DEFAULT_SORT_ORDER);
            if (cur == null) {
                continue;
            }

            if (cur.getCount() > 0) {
                cur.moveToFirst();
                status_ids[idx] = cur.getLong(cur.getColumnIndexOrThrow(DirectMessages.MESSAGE_ID));
            }
            cur.close();
            idx++;
        }
        return status_ids;
    }

    public static long[] getNewestStatusIdsFromDatabase(final Context context, final Uri uri) {
        final long[] account_ids = getActivatedAccountIds(context);
        return getNewestStatusIdsFromDatabase(context, uri, account_ids);
    }

    public static long[] getNewestStatusIdsFromDatabase(final Context context, final Uri uri, final long[] account_ids) {
        if (context == null || uri == null || account_ids == null) return null;
        final String[] cols = new String[] { Statuses.STATUS_ID };
        final ContentResolver resolver = context.getContentResolver();
        final long[] status_ids = new long[account_ids.length];
        int idx = 0;
        for (final long account_id : account_ids) {
            final String where = Statuses.ACCOUNT_ID + " = " + account_id;
            final Cursor cur = ContentResolverUtils
                    .query(resolver, uri, cols, where, null, Statuses.DEFAULT_SORT_ORDER);
            if (cur == null) {
                continue;
            }

            if (cur.getCount() > 0) {
                cur.moveToFirst();
                status_ids[idx] = cur.getLong(cur.getColumnIndexOrThrow(Statuses.STATUS_ID));
            }
            cur.close();
            idx++;
        }
        return status_ids;
    }

    public static String getNonEmptyString(final SharedPreferences pref, final String key, final String def) {
        if (pref == null) return def;
        final String val = pref.getString(key, def);
        return isEmpty(val) ? def : val;
    }

    public static String getNormalTwitterProfileImage(final String url) {
        return getTwitterProfileImageOfSize(url, "normal");
    }

    public static Uri getNotificationUri(final int tableId, final Uri def) {
        switch (tableId) {
            case TABLE_ID_DIRECT_MESSAGES:
            case TABLE_ID_DIRECT_MESSAGES_CONVERSATION:
            case TABLE_ID_DIRECT_MESSAGES_CONVERSATION_SCREEN_NAME:
            case TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES:
                return DirectMessages.CONTENT_URI;
        }
        return def;
    }

    public static long[] getOldestMessageIdsFromDatabase(final Context context, final Uri uri) {
        final long[] account_ids = getActivatedAccountIds(context);
        return getOldestMessageIdsFromDatabase(context, uri, account_ids);
    }

    public static long[] getOldestMessageIdsFromDatabase(final Context context, final Uri uri, final long[] account_ids) {
        if (context == null || uri == null) return null;
        final String[] cols = new String[] { DirectMessages.MESSAGE_ID };
        final ContentResolver resolver = context.getContentResolver();
        final long[] status_ids = new long[account_ids.length];
        int idx = 0;
        for (final long account_id : account_ids) {
            final String where = Statuses.ACCOUNT_ID + " = " + account_id;
            final Cursor cur = ContentResolverUtils.query(resolver, uri, cols, where, null, DirectMessages.MESSAGE_ID);
            if (cur == null) {
                continue;
            }

            if (cur.getCount() > 0) {
                cur.moveToFirst();
                status_ids[idx] = cur.getLong(cur.getColumnIndexOrThrow(DirectMessages.MESSAGE_ID));
            }
            cur.close();
            idx++;
        }
        return status_ids;
    }

    public static long[] getOldestStatusIdsFromDatabase(final Context context, final Uri uri) {
        final long[] account_ids = getActivatedAccountIds(context);
        return getOldestStatusIdsFromDatabase(context, uri, account_ids);
    }

    public static long[] getOldestStatusIdsFromDatabase(final Context context, final Uri uri, final long[] account_ids) {
        if (context == null || uri == null || account_ids == null) return null;
        final String[] cols = new String[] { Statuses.STATUS_ID };
        final ContentResolver resolver = context.getContentResolver();
        final long[] status_ids = new long[account_ids.length];
        int idx = 0;
        for (final long account_id : account_ids) {
            final String where = Statuses.ACCOUNT_ID + " = " + account_id;
            final Cursor cur = ContentResolverUtils.query(resolver, uri, cols, where, null, Statuses.STATUS_ID);
            if (cur == null) {
                continue;
            }

            if (cur.getCount() > 0) {
                cur.moveToFirst();
                status_ids[idx] = cur.getLong(cur.getColumnIndexOrThrow(Statuses.STATUS_ID));
            }
            cur.close();
            idx++;
        }
        return status_ids;
    }

    public static String getOriginalTwitterProfileImage(final String url) {
        if (url == null) return null;
        if (PATTERN_TWITTER_PROFILE_IMAGES.matcher(url).matches())
            return replaceLast(url, "_" + TWITTER_PROFILE_IMAGES_AVAILABLE_SIZES, "");
        return url;
    }

    public static Proxy getProxy(final Context context) {
        if (context == null) return null;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final boolean enable_proxy = prefs.getBoolean(KEY_ENABLE_PROXY, false);
        if (!enable_proxy) return Proxy.NO_PROXY;
        final String proxyHost = prefs.getString(KEY_PROXY_HOST, null);
        final int proxyPort = ParseUtils.parseInt(prefs.getString(KEY_PROXY_PORT, "-1"));
        if (!isEmpty(proxyHost) && proxyPort >= 0 && proxyPort < 65535) {
            final SocketAddress addr = InetSocketAddress.createUnresolved(proxyHost, proxyPort);
            return new Proxy(Proxy.Type.HTTP, addr);
        }
        return Proxy.NO_PROXY;
    }

    public static String getQuoteStatus(final Context context, final String screen_name, final String text) {
        if (context == null) return null;
        String quote_format = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(
                KEY_QUOTE_FORMAT, DEFAULT_QUOTE_FORMAT);
        if (isEmpty(quote_format)) {
            quote_format = DEFAULT_QUOTE_FORMAT;
        }
        return quote_format.replace(FORMAT_PATTERN_NAME, screen_name).replace(FORMAT_PATTERN_TEXT, text);
    }

    public static String getReasonablySmallTwitterProfileImage(final String url) {
        return getTwitterProfileImageOfSize(url, "reasonably_small");
    }

    public static HttpResponse getRedirectedHttpResponse(final HttpClientWrapper client, final String url,
            final String signUrl, final Authorization auth) throws TwitterException {
        if (url == null) return null;
        final ArrayList<String> urls = new ArrayList<String>();
        urls.add(url);
        HttpResponse resp;
        try {
            resp = client.get(url, signUrl, auth);
        } catch (final TwitterException te) {
            if (isRedirected(te.getStatusCode())) {
                resp = te.getHttpResponse();
            } else
                throw te;
        }
        while (resp != null && isRedirected(resp.getStatusCode())) {
            final String request_url = resp.getResponseHeader("TwitterLocation");
            if (request_url == null) return null;
            if (urls.contains(request_url)) throw new TwitterException("Too many redirects");
            urls.add(request_url);
            try {
                resp = client.get(request_url, request_url);
            } catch (final TwitterException te) {
                if (isRedirected(te.getStatusCode())) {
                    resp = te.getHttpResponse();
                } else
                    throw te;
            }
        }
        return resp;
    }

    public static int getResId(final Context context, final String string) {
        if (context == null || string == null) return 0;
        Matcher m = PATTERN_RESOURCE_IDENTIFIER.matcher(string);
        final Resources res = context.getResources();
        if (m.matches()) return res.getIdentifier(m.group(2), m.group(1), context.getPackageName());
        m = PATTERN_XML_RESOURCE_IDENTIFIER.matcher(string);
        if (m.matches()) return res.getIdentifier(m.group(1), "xml", context.getPackageName());
        return 0;
    }

    public static String getSenderUserName(final Context context, final TwitterDirectMessage user) {
        if (context == null || user == null) return null;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final boolean display_name = prefs.getBoolean(KEY_NAME_FIRST, true);
        return display_name ? user.sender_name : "@" + user.sender_screen_name;
    }

    public static String getShareStatus(final Context context, final CharSequence title, final CharSequence text) {
        if (context == null) return null;
        String share_format = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(
                KEY_SHARE_FORMAT, DEFAULT_SHARE_FORMAT);
        if (isEmpty(share_format)) {
            share_format = DEFAULT_SHARE_FORMAT;
        }
        if (isEmpty(title)) return ParseUtils.parseString(text);
        return share_format.replace(FORMAT_PATTERN_TITLE, title).replace(FORMAT_PATTERN_TEXT, text != null ? text : "");
    }

    public static ArrayList<Long> getStatusIdsInDatabase(final Context context, final Uri uri, final long account_id) {
        final ArrayList<Long> list = new ArrayList<Long>();
        if (context == null) return list;
        final ContentResolver resolver = context.getContentResolver();
        final String where = Statuses.ACCOUNT_ID + " = " + account_id;
        final String[] projection = new String[] { Statuses.STATUS_ID };
        final Cursor cur = ContentResolverUtils.query(resolver, uri, projection, where, null, null);
        if (cur != null) {
            final int idx = cur.getColumnIndexOrThrow(Statuses.STATUS_ID);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                list.add(cur.getLong(idx));
                cur.moveToNext();
            }
            cur.close();
        }
        return list;
    }

    public static int getStatusTypeIconRes(final boolean is_favorite, final boolean has_location,
            final boolean has_media, final boolean is_possibly_sensitive) {
        if (is_favorite)
            return R.drawable.ic_indicator_starred;
        else if (is_possibly_sensitive && has_media)
            return R.drawable.ic_indicator_reported_media;
        else if (has_media)
            return R.drawable.ic_indicator_media;
        else if (has_location) return R.drawable.ic_indicator_location;
        return 0;
    }

    public static int getTableId(final Uri uri) {
        if (uri == null) return -1;
        return TweetStore.CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }

    public static String getTableNameById(final int id) {
        switch (id) {
            case TABLE_ID_ACCOUNTS:
                return Accounts.TABLE_NAME;
            case TABLE_ID_STATUSES:
                return Statuses.TABLE_NAME;
            case TABLE_ID_MENTIONS:
                return Mentions.TABLE_NAME;
            case TABLE_ID_DRAFTS:
                return Drafts.TABLE_NAME;
            case TABLE_ID_FILTERED_USERS:
                return Filters.Users.TABLE_NAME;
            case TABLE_ID_FILTERED_KEYWORDS:
                return Filters.Keywords.TABLE_NAME;
            case TABLE_ID_FILTERED_SOURCES:
                return Filters.Sources.TABLE_NAME;
            case TABLE_ID_FILTERED_LINKS:
                return Filters.Links.TABLE_NAME;
            case TABLE_ID_DIRECT_MESSAGES:
                return DirectMessages.TABLE_NAME;
            case TABLE_ID_TRENDS_LOCAL:
                return CachedTrends.Local.TABLE_NAME;
            case TABLE_ID_CACHED_STATUSES:
                return CachedStatuses.TABLE_NAME;
            case TABLE_ID_CACHED_USERS:
                return CachedUsers.TABLE_NAME;
            case TABLE_ID_CACHED_HASHTAGS:
                return CachedHashtags.TABLE_NAME;
            default:
                return null;
        }
    }

    public static String getTableNameByUri(final Uri uri) {
        if (uri == null) return null;
        return getTableNameById(getTableId(uri));
    }

    public static int getTextCount(final String string) {
        if (string == null) return 0;
        return ArrayUtils.toStringArray(string).length;
    }

    public static int getTextCount(final TextView view) {
        if (view == null) return 0;
        final String string = ParseUtils.parseString(view.getText());
        return getTextCount(string);
    }

    public static long getTimestampFromDate(final Date date) {
        if (date == null) return -1;
        return date.getTime();
    }

    public static Authorization getTwitterAuthorization(final Context context, final TwitterAccount.TwitterAccountWithCredentials account) {
        if (context == null || account == null) return null;
        switch (account.auth_type) {
            case Accounts.AUTH_TYPE_OAUTH:
            case Accounts.AUTH_TYPE_XAUTH: {
                final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                        Context.MODE_PRIVATE);
                // Here I use old consumer key/secret because it's default
                // key for older
                // versions
                final String prefConsumerKey = prefs.getString(KEY_CONSUMER_KEY, TWITTER_CONSUMER_KEY);
                final String prefConsumerSecret = prefs.getString(KEY_CONSUMER_SECRET, TWITTER_CONSUMER_SECRET);
                final ConfigurationBuilder cb = new ConfigurationBuilder();
                if (!isEmpty(account.api_url_format)) {
                    cb.setRestBaseURL(getApiUrl(account.api_url_format, "api", "/1.1/"));
                    cb.setOAuthBaseURL(getApiUrl(account.api_url_format, "api", "/oauth/"));
                    cb.setUploadBaseURL(getApiUrl(account.api_url_format, "upload", "/1.1/"));
                    if (!account.same_oauth_signing_url) {
                        cb.setSigningRestBaseURL(DEFAULT_SIGNING_REST_BASE_URL);
                        cb.setSigningOAuthBaseURL(DEFAULT_SIGNING_OAUTH_BASE_URL);
                        cb.setSigningUploadBaseURL(DEFAULT_SIGNING_UPLOAD_BASE_URL);
                    }
                }
                if (!isEmpty(account.consumer_key) && !isEmpty(account.consumer_secret)) {
                    cb.setOAuthConsumerKey(account.consumer_key);
                    cb.setOAuthConsumerSecret(account.consumer_secret);
                } else if (!isEmpty(prefConsumerKey) && !isEmpty(prefConsumerSecret)) {
                    cb.setOAuthConsumerKey(prefConsumerKey);
                    cb.setOAuthConsumerSecret(prefConsumerSecret);
                } else {
                    cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                    cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                }
                final OAuthAuthorization auth = new OAuthAuthorization(cb.build());
                auth.setOAuthAccessToken(new AccessToken(account.oauth_token, account.oauth_token_secret));
                return auth;
            }
            case Accounts.AUTH_TYPE_BASIC: {
                final String screenName = account.screen_name;
                final String username = account.basic_auth_username;
                final String loginName = username != null ? username : screenName;
                final String password = account.basic_auth_password;
                if (isEmpty(loginName) || isEmpty(password)) return null;
                return new BasicAuthorization(loginName, password);
            }
            default: {
                return null;
            }
        }
    }

    public static Authorization getTwitterAuthorization(final Context context, final long accountId) {

        final String where = Where.equals(new Columns.Column(Accounts.ACCOUNT_ID), accountId).getSQL();
        final Cursor c = ContentResolverUtils.query(context.getContentResolver(), Accounts.CONTENT_URI,
                Accounts.COLUMNS, where.toString(), null, null);
        if (c == null) return null;
        try {
            if (!c.moveToFirst()) return null;

            switch (c.getInt(c.getColumnIndexOrThrow(Accounts.AUTH_TYPE))) {
                case Accounts.AUTH_TYPE_OAUTH:
                case Accounts.AUTH_TYPE_XAUTH: {
                    final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                            Context.MODE_PRIVATE);
                    // Here I use old consumer key/secret because it's default
                    // key for older
                    // versions
                    final String prefConsumerKey = prefs.getString(KEY_CONSUMER_KEY, TWITTER_CONSUMER_KEY);
                    final String prefConsumerSecret = prefs.getString(KEY_CONSUMER_SECRET, TWITTER_CONSUMER_SECRET);
                    final ConfigurationBuilder cb = new ConfigurationBuilder();
                    final String apiUrlFormat = c.getString(c.getColumnIndex(Accounts.API_URL_FORMAT));
                    final String consumerKey = trim(c.getString(c.getColumnIndex(Accounts.CONSUMER_KEY)));
                    final String consumerSecret = trim(c.getString(c.getColumnIndex(Accounts.CONSUMER_SECRET)));
                    final boolean sameOAuthSigningUrl = c.getInt(c.getColumnIndex(Accounts.SAME_OAUTH_SIGNING_URL)) == 1;
                    if (!isEmpty(apiUrlFormat)) {
                        cb.setRestBaseURL(getApiUrl(apiUrlFormat, "api", "/1.1/"));
                        cb.setOAuthBaseURL(getApiUrl(apiUrlFormat, "api", "/oauth/"));
                        cb.setUploadBaseURL(getApiUrl(apiUrlFormat, "upload", "/1.1/"));
                        if (!sameOAuthSigningUrl) {
                            cb.setSigningRestBaseURL(DEFAULT_SIGNING_REST_BASE_URL);
                            cb.setSigningOAuthBaseURL(DEFAULT_SIGNING_OAUTH_BASE_URL);
                            cb.setSigningUploadBaseURL(DEFAULT_SIGNING_UPLOAD_BASE_URL);
                        }
                    }
                    if (!isEmpty(consumerKey) && !isEmpty(consumerSecret)) {
                        cb.setOAuthConsumerKey(consumerKey);
                        cb.setOAuthConsumerSecret(consumerSecret);
                    } else if (!isEmpty(prefConsumerKey) && !isEmpty(prefConsumerSecret)) {
                        cb.setOAuthConsumerKey(prefConsumerKey);
                        cb.setOAuthConsumerSecret(prefConsumerSecret);
                    } else {
                        cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                        cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                    }
                    final OAuthAuthorization auth = new OAuthAuthorization(cb.build());
                    final String token = c.getString(c.getColumnIndexOrThrow(Accounts.OAUTH_TOKEN));
                    final String tokenSecret = c.getString(c.getColumnIndexOrThrow(Accounts.OAUTH_TOKEN_SECRET));
                    auth.setOAuthAccessToken(new AccessToken(token, tokenSecret));
                    return auth;
                }
                case Accounts.AUTH_TYPE_BASIC: {
                    final String screenName = c.getString(c.getColumnIndexOrThrow(Accounts.SCREEN_NAME));
                    final String username = c.getString(c.getColumnIndexOrThrow(Accounts.BASIC_AUTH_USERNAME));
                    final String loginName = username != null ? username : screenName;
                    final String password = c.getString(c.getColumnIndexOrThrow(Accounts.BASIC_AUTH_PASSWORD));
                    if (isEmpty(loginName) || isEmpty(password)) return null;
                    return new BasicAuthorization(loginName, password);
                }
                default: {
                    return null;
                }
            }
        } finally {
            c.close();
        }
    }

    public static String getTwitterErrorMessage(final Context context, final CharSequence action,
            final TwitterException te) {
        if (context == null) return null;
        if (te == null) return context.getString(R.string.error_unknown_error);
        if (te.exceededRateLimitation()) {
            final RateLimitStatus status = te.getRateLimitStatus();
            final long secUntilReset = status.getSecondsUntilReset() * 1000;
            final String nextResetTime = ParseUtils.parseString(getRelativeTimeSpanString(System.currentTimeMillis()
                    + secUntilReset));
            if (isEmpty(action)) return context.getString(R.string.error_message_rate_limit, nextResetTime.trim());
            return context.getString(R.string.error_message_rate_limit_with_action, action, nextResetTime.trim());
        } else if (te.getErrorCode() > 0) {
            final String msg = StatusCodeMessageUtils.getTwitterErrorMessage(context, te.getErrorCode());
            return getErrorMessage(context, action, msg != null ? msg : trimLineBreak(te.getMessage()));
        } else if (te.getCause() instanceof SSLException) {
            final String msg = te.getCause().getMessage();
            if (msg != null && msg.contains("!="))
                return getErrorMessage(context, action, context.getString(R.string.ssl_error));
            else
                return getErrorMessage(context, action, context.getString(R.string.network_error));
        } else if (te.getCause() instanceof IOException)
            return getErrorMessage(context, action, context.getString(R.string.network_error));
        else if (te.getCause() instanceof JSONException)
            return getErrorMessage(context, action, context.getString(R.string.api_data_corrupted));
        else
            return getErrorMessage(context, action, trimLineBreak(te.getMessage()));
    }

    public static String getTwitterErrorMessage(final Context context, final TwitterException te) {
        if (te == null) return null;
        if (StatusCodeMessageUtils.containsTwitterError(te.getErrorCode()))
            return StatusCodeMessageUtils.getTwitterErrorMessage(context, te.getErrorCode());
        else if (StatusCodeMessageUtils.containsHttpStatus(te.getStatusCode()))
            return StatusCodeMessageUtils.getHttpStatusMessage(context, te.getStatusCode());
        else
            return te.getMessage();
    }

    public static Twitter getTwitterInstance(final Context context, final long account_id,
            final boolean include_entities) {
        return getTwitterInstance(context, account_id, include_entities, true, !SysUtils.isMIUI());
    }

    public static Twitter getTwitterInstance(final Context context, final long accountId,
            final boolean includeEntities, final boolean includeRetweets) {
        return getTwitterInstance(context, accountId, includeEntities, includeRetweets, !SysUtils.isMIUI());
    }

    public static Twitter getTwitterInstance(final Context context, final long accountId,
            final boolean includeEntities, final boolean includeRetweets, final boolean apacheHttp) {
        if (context == null) return null;
        final Application app = (Application) context.getApplicationContext();
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final int connection_timeout = prefs.getInt(KEY_CONNECTION_TIMEOUT, 10) * 1000;
        final boolean enableGzip = prefs.getBoolean(KEY_GZIP_COMPRESSING, true);
        final boolean ignoreSslError = prefs.getBoolean(KEY_IGNORE_SSL_ERROR, false);
        final boolean enableProxy = prefs.getBoolean(KEY_ENABLE_PROXY, false);
        // Here I use old consumer key/secret because it's default key for older
        // versions
        final String where = Where.equals(new Columns.Column(Accounts.ACCOUNT_ID), accountId).getSQL();
        final Cursor c = ContentResolverUtils.query(context.getContentResolver(), Accounts.CONTENT_URI,
                Accounts.COLUMNS, where.toString(), null, null);
        if (c == null) return null;
        try {
            if (!c.moveToFirst()) return null;
            final ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setHostAddressResolverFactory(new TwitterHostResolverFactory());
            if (apacheHttp) {
                cb.setHttpClientFactory(new TwitterHttpClientFactory(app));
            }
            cb.setHttpConnectionTimeout(connection_timeout);
            setUserAgent(context, cb);
            cb.setGZIPEnabled(enableGzip);
            cb.setIgnoreSSLError(ignoreSslError);
            if (enableProxy) {
                final String proxy_host = prefs.getString(KEY_PROXY_HOST, null);
                final int proxy_port = ParseUtils.parseInt(prefs.getString(KEY_PROXY_PORT, "-1"));
                if (!isEmpty(proxy_host) && proxy_port > 0) {
                    cb.setHttpProxyHost(proxy_host);
                    cb.setHttpProxyPort(proxy_port);
                }
            }
            final String prefConsumerKey = prefs.getString(KEY_CONSUMER_KEY, TWITTER_CONSUMER_KEY);
            final String prefConsumerSecret = prefs.getString(KEY_CONSUMER_SECRET, TWITTER_CONSUMER_SECRET);
            final String apiUrlFormat = c.getString(c.getColumnIndex(Accounts.API_URL_FORMAT));
            final String consumerKey = trim(c.getString(c.getColumnIndex(Accounts.CONSUMER_KEY)));
            final String consumerSecret = trim(c.getString(c.getColumnIndex(Accounts.CONSUMER_SECRET)));
            final boolean sameOAuthSigningUrl = c.getInt(c.getColumnIndex(Accounts.SAME_OAUTH_SIGNING_URL)) == 1;
            if (!isEmpty(apiUrlFormat)) {
                cb.setRestBaseURL(getApiUrl(apiUrlFormat, "api", "/1.1/"));
                cb.setOAuthBaseURL(getApiUrl(apiUrlFormat, "api", "/oauth/"));
                cb.setUploadBaseURL(getApiUrl(apiUrlFormat, "upload", "/1.1/"));
                if (!sameOAuthSigningUrl) {
                    cb.setSigningRestBaseURL(DEFAULT_SIGNING_REST_BASE_URL);
                    cb.setSigningOAuthBaseURL(DEFAULT_SIGNING_OAUTH_BASE_URL);
                    cb.setSigningUploadBaseURL(DEFAULT_SIGNING_UPLOAD_BASE_URL);
                }
            }
            cb.setIncludeEntitiesEnabled(includeEntities);
            cb.setIncludeRTsEnabled(includeRetweets);
            switch (c.getInt(c.getColumnIndexOrThrow(Accounts.AUTH_TYPE))) {
                case Accounts.AUTH_TYPE_OAUTH:
                case Accounts.AUTH_TYPE_XAUTH: {
                    if (!isEmpty(consumerKey) && !isEmpty(consumerSecret)) {
                        cb.setOAuthConsumerKey(consumerKey);
                        cb.setOAuthConsumerSecret(consumerSecret);
                    } else if (!isEmpty(prefConsumerKey) && !isEmpty(prefConsumerSecret)) {
                        cb.setOAuthConsumerKey(prefConsumerKey);
                        cb.setOAuthConsumerSecret(prefConsumerSecret);
                    } else {
                        cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                        cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
                    }
                    final String token = c.getString(c.getColumnIndexOrThrow(Accounts.OAUTH_TOKEN));
                    final String tokenSecret = c.getString(c.getColumnIndexOrThrow(Accounts.OAUTH_TOKEN_SECRET));
                    if (isEmpty(token) || isEmpty(tokenSecret)) return null;
                    return new TwitterFactory(cb.build()).getInstance(new AccessToken(token, tokenSecret));
                }
                case Accounts.AUTH_TYPE_BASIC: {
                    final String screenName = c.getString(c.getColumnIndexOrThrow(Accounts.SCREEN_NAME));
                    final String username = c.getString(c.getColumnIndexOrThrow(Accounts.BASIC_AUTH_USERNAME));
                    final String loginName = username != null ? username : screenName;
                    final String password = c.getString(c.getColumnIndexOrThrow(Accounts.BASIC_AUTH_PASSWORD));
                    if (isEmpty(loginName) || isEmpty(password)) return null;
                    return new TwitterFactory(cb.build()).getInstance(new BasicAuthorization(loginName, password));
                }
                case Accounts.AUTH_TYPE_TWIP_O_MODE: {
                    return new TwitterFactory(cb.build()).getInstance(new TwipOModeAuthorization());
                }
                default: {
                    return null;
                }
            }
        } finally {
            c.close();
        }
    }

    public static String getTwitterProfileImageOfSize(final String url, final String size) {
        if (url == null) return null;
        if (PATTERN_TWITTER_PROFILE_IMAGES.matcher(url).matches())
            return replaceLast(url, "_" + TWITTER_PROFILE_IMAGES_AVAILABLE_SIZES, String.format("_%s", size));
        return url;
    }

    public static String getUnescapedStatusString(final String string) {
        if (string == null) return null;
        return string.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
    }

    public static String getUserName(final Context context, final TwitterStatus status) {
        if (context == null || status == null) return null;
        return getDisplayName(context, status.user_id, status.user_name, status.user_screen_name);
    }

    public static String getUserName(final Context context, final TwitterUser user) {
        if (context == null || user == null) return null;
        return getDisplayName(context, user.id, user.name, user.screen_name);
    }

    public static String getUserName(final Context context, final twitter4j.User user) {
        if (context == null || user == null) return null;
        return getDisplayName(context, user.getId(), user.getName(), user.getScreenName());
    }

    public static int getUserTypeIconRes(final boolean is_verified, final boolean is_protected) {
        if (is_verified)
            return R.drawable.ic_indicator_verified;
        else if (is_protected) return R.drawable.ic_indicator_protected;
        return 0;
    }

    public static boolean hasAccountSignedWithOfficialKeys(final Context context) {
        if (context == null) return false;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), Accounts.CONTENT_URI,
                Accounts.COLUMNS, null, null, null);
        if (cur == null) return false;
        final String[] keySecrets = context.getResources().getStringArray(R.array.values_official_consumer_key_secret);
        final TwitterAccount.Indices indices = new TwitterAccount.Indices(cur);
        cur.moveToFirst();
        try {
            while (!cur.isAfterLast()) {
                final String consumerKey = cur.getString(indices.consumer_key);
                final String consumerSecret = cur.getString(indices.consumer_secret);
                for (final String keySecret : keySecrets) {
                    final String[] pair = keySecret.split(";");
                    if (pair[0].equals(consumerKey) && pair[1].equals(consumerSecret)) return true;
                }
                cur.moveToNext();
            }
        } finally {
            cur.close();
        }
        return false;
    }


    public static boolean hasStaggeredTimeline() {
        return false;
    }

    public static int inferStatusBarHeight(final Activity activity) {
        final Window w = activity.getWindow();
        final View decorView = w.getDecorView();
        final Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    public static void initAccountColor(final Context context) {
        if (context == null) return;
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(), Accounts.CONTENT_URI, new String[]{
                Accounts.ACCOUNT_ID, Accounts.COLOR}, null, null, null);
        if (cur == null) return;
        final int id_idx = cur.getColumnIndex(Accounts.ACCOUNT_ID), color_idx = cur.getColumnIndex(Accounts.COLOR);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            sAccountColors.put(cur.getLong(id_idx), cur.getInt(color_idx));
            cur.moveToNext();
        }
        cur.close();
    }

    public static boolean isBatteryOkay(final Context context) {
        if (context == null) return false;
        final Context app = context.getApplicationContext();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent intent = app.registerReceiver(null, filter);
        if (intent == null) return false;
        final boolean plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
        final float level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        final float scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        return plugged || level / scale > 0.15f;
    }

    public static boolean isCompactCards(final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs != null && prefs.getBoolean(KEY_COMPACT_CARDS, false);
    }

    public static boolean isDebugBuild() {
        return BuildConfig.DEBUG;
    }

    public static boolean isDebuggable(final Context context) {
        if (context == null) return false;
        final ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        } catch (final NameNotFoundException e) {
            return false;
        }
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean isFiltered(final SQLiteDatabase database, final long user_id, final String text_plain,
            final String text_html, final String source, final long retweeted_by_id) {
        return isFiltered(database, user_id, text_plain, text_html, source, retweeted_by_id, true);
    }

    public static boolean isFiltered(final SQLiteDatabase database, final long user_id, final String text_plain,
            final String text_html, final String source, final long retweeted_by_id, final boolean filter_rts) {
        if (database == null) return false;
        if (text_plain == null && text_html == null && user_id <= 0 && source == null) return false;
        final StringBuilder builder = new StringBuilder();
        final List<String> selection_args = new ArrayList<String>();
        builder.append("SELECT NULL WHERE");
        if (text_plain != null) {
            selection_args.add(text_plain);
            builder.append("(SELECT 1 IN (SELECT ? LIKE '%'||" + Filters.Keywords.TABLE_NAME + "." + Filters.VALUE
                    + "||'%' FROM " + Filters.Keywords.TABLE_NAME + "))");
        }
        if (text_html != null) {
            if (!selection_args.isEmpty()) {
                builder.append(" OR ");
            }
            selection_args.add(text_html);
            builder.append("(SELECT 1 IN (SELECT ? LIKE '%<a href=\"%'||" + Filters.Links.TABLE_NAME + "."
                    + Filters.VALUE + "||'%\">%' FROM " + Filters.Links.TABLE_NAME + "))");
        }
        if (user_id > 0) {
            if (!selection_args.isEmpty()) {
                builder.append(" OR ");
            }
            builder.append("(SELECT " + user_id + " IN (SELECT " + Filters.Users.USER_ID + " FROM "
                    + Filters.Users.TABLE_NAME + "))");
        }
        if (retweeted_by_id > 0) {
            if (!selection_args.isEmpty()) {
                builder.append(" OR ");
            }
            builder.append("(SELECT " + retweeted_by_id + " IN (SELECT " + Filters.Users.USER_ID + " FROM "
                    + Filters.Users.TABLE_NAME + "))");
        }
        if (source != null) {
            if (!selection_args.isEmpty()) {
                builder.append(" OR ");
            }
            selection_args.add(source);
            builder.append("(SELECT 1 IN (SELECT ? LIKE '%>'||" + Filters.Sources.TABLE_NAME + "." + Filters.VALUE
                    + "||'</a>%' FROM " + Filters.Sources.TABLE_NAME + "))");
        }
        final Cursor cur = database.rawQuery(builder.toString(),
                selection_args.toArray(new String[selection_args.size()]));
        if (cur == null) return false;
        try {
            return cur.getCount() > 0;
        } finally {
            cur.close();
        }
    }

    public static boolean isFiltered(final SQLiteDatabase database, final TwitterStatus status,
            final boolean filter_rts) {
        if (database == null || status == null) return false;
        return isFiltered(database, status.user_id, status.text_plain, status.text_html, status.source,
                status.retweeted_by_id, filter_rts);
    }

    public static boolean isMyAccount(final Context context, final long account_id) {
        if (context == null) return false;
        final ContentResolver resolver = context.getContentResolver();
        final String where = Accounts.ACCOUNT_ID + " = " + account_id;
        final Cursor cur = ContentResolverUtils.query(resolver, Accounts.CONTENT_URI, new String[0], where, null, null);
        try {
            return cur != null && cur.getCount() > 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    public static boolean isMyAccount(final Context context, final String screen_name) {
        if (context == null) return false;
        final ContentResolver resolver = context.getContentResolver();
        final String where = Accounts.SCREEN_NAME + " = ?";
        final Cursor cur = ContentResolverUtils.query(resolver, Accounts.CONTENT_URI, new String[0], where,
                new String[] { screen_name }, null);
        try {
            return cur != null && cur.getCount() > 0;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    public static boolean isMyRetweet(final TwitterStatus status) {
        if (status == null) return false;
        return status.retweeted_by_id == status.account_id || status.my_retweet_id > 0;
    }

    public static boolean isMyUserName(final Context context, final String screen_name) {
        if (context == null) return false;
        for (final String account_screen_name : getAccountScreenNames(context)) {
            if (account_screen_name.equalsIgnoreCase(screen_name)) return true;
        }
        return false;
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isNotificationsSilent(final Context context) {
        if (context == null) return false;
        final SharedPreferences prefs = context.getSharedPreferences(SILENT_NOTIFICATIONS_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        final Calendar now = Calendar.getInstance();
        return prefs.getBoolean("silent_notifications_at_" + now.get(Calendar.HOUR_OF_DAY), false);
    }

    public static boolean isOfficialConsumerKeySecret(final Context context, final String consumerKey,
            final String consumerSecret) {
        if (context == null || consumerKey == null || consumerSecret == null) return false;
        final String[] keySecrets = context.getResources().getStringArray(R.array.values_official_consumer_key_secret);
        for (final String keySecret : keySecrets) {
            final String[] pair = keySecret.split(";");
            if (pair[0].equals(consumerKey) && pair[1].equals(consumerSecret)) return true;
        }
        return false;
    }

    public static boolean isOfficialTwitterInstance(final Context context, final Twitter twitter) {
        if (context == null || twitter == null) return false;
        final Configuration conf = twitter.getConfiguration();
        final Authorization auth = twitter.getAuthorization();
        final boolean isOAuth = auth instanceof OAuthAuthorization || auth instanceof XAuthAuthorization;
        final String consumerKey = conf.getOAuthConsumerKey(), consumerSecret = conf.getOAuthConsumerSecret();
        return isOAuth && isOfficialConsumerKeySecret(context, consumerKey, consumerSecret);
    }

    public static boolean isOnWifi(final Context context) {
        if (context == null) return false;
        final ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && networkInfo.isConnected();
    }

    public static boolean isPlainListStyle(final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs != null && prefs.getBoolean(KEY_PLAIN_LIST_STYLE, false);
    }

    public static boolean isRedirected(final int code) {
        return code == 301 || code == 302 || code == 307;
    }

    public static boolean isRTL(final Context context) {
        if (context == null) return false;
        final Resources res = context.getResources();
        return "ar".equals(res.getConfiguration().locale.getLanguage());
        // return
        // ConfigurationAccessor.getLayoutDirection(res.getConfiguration()) ==
        // SCREENLAYOUT_LAYOUTDIR_RTL;
    }

    public static boolean isSameAccount(final Context context, final long account_id, final long user_id) {
        if (context == null || account_id <= 0 || user_id <= 0) return false;
        return account_id == user_id;
    }

    public static boolean isSameAccount(final Context context, final long account_id, final String screen_name) {
        if (context == null || account_id <= 0 || screen_name == null) return false;
        return screen_name.equalsIgnoreCase(getAccountScreenName(context, account_id));
    }

    public static boolean isUserLoggedIn(final Context context, final long account_id) {
        if (context == null) return false;
        final long[] ids = getAccountIds(context);
        if (ids == null) return false;
        for (final long id : ids) {
            if (id == account_id) return true;
        }
        return false;
    }

    public static boolean isValidImage(final File image) {
        if (image == null) return false;
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), o);
        return o.outHeight > 0 && o.outWidth > 0;
    }

    public static boolean isValidImage(final InputStream is) {
        if (is == null) return false;
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, new Rect(), o);
        return o.outHeight > 0 && o.outWidth > 0;
    }

    public static boolean isValidUrl(final CharSequence text) {
        if (TextUtils.isEmpty(text)) return false;
        return URLUtil.isValidUrl(text.toString());
    }

    public static final int matcherEnd(final Matcher matcher, final int group) {
        try {
            return matcher.end(group);
        } catch (final IllegalStateException e) {
            // Ignore.
        }
        return -1;
    }

    public static final String matcherGroup(final Matcher matcher, final int group) {
        try {
            return matcher.group(group);
        } catch (final IllegalStateException e) {
            // Ignore.
        }
        return null;
    }

    public static final int matcherStart(final Matcher matcher, final int group) {
        try {
            return matcher.start(group);
        } catch (final IllegalStateException e) {
            // Ignore.
        }
        return -1;
    }

    public static int matchLinkId(final Uri uri) {
        return LINK_HANDLER_URI_MATCHER.match(uri);
    }

    public static String replaceLast(final String text, final String regex, final String replacement) {
        if (text == null || regex == null || replacement == null) return text;
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    /**
     * Resizes specific a Bitmap with keeping ratio.
     */
    public static Bitmap resizeBitmap(Bitmap orig, final int desireWidth, final int desireHeight) {
        final int width = orig.getWidth();
        final int height = orig.getHeight();

        if (0 < width && 0 < height && desireWidth < width || desireHeight < height) {
            // Calculate scale
            float scale;
            if (width < height) {
                scale = (float) desireHeight / (float) height;
                if (desireWidth < width * scale) {
                    scale = (float) desireWidth / (float) width;
                }
            } else {
                scale = (float) desireWidth / (float) width;
            }

            // Draw resized image
            final Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            final Bitmap bitmap = Bitmap.createBitmap(orig, 0, 0, width, height, matrix, true);
            final Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            orig = bitmap;
        }

        return orig;
    }

    public static void restartActivity(final Activity activity) {
        if (activity == null) return;
        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.startActivity(activity.getIntent());
        activity.overridePendingTransition(enter_anim, exit_anim);
    }

    public static void scrollListToPosition(final AbsListView list, final int position) {
        scrollListToPosition(list, position, 0);
    }

    public static void scrollListToPosition(final AbsListView list, final int position, final int offset) {
        if (list == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (list instanceof ListView) {
                ((ListView) list).setSelectionFromTop(position, offset);
            } else {
                list.setSelection(position);
            }
            stopListView(list);
        } else {
            stopListView(list);
            if (list instanceof ListView) {
                ((ListView) list).setSelectionFromTop(position, offset);
            } else {
                list.setSelection(position);
            }
        }
    }

    public static void scrollListToTop(final AbsListView list) {
        if (list == null) return;
        scrollListToPosition(list, 0);
    }

    public static void setMenuItemAvailability(final Menu menu, final int id, final boolean available) {
        if (menu == null) return;
        final MenuItem item = menu.findItem(id);
        if (item == null) return;
        item.setVisible(available);
        item.setEnabled(available);
    }

    public static void setMenuItemIcon(final Menu menu, final int id, final int icon) {
        if (menu == null) return;
        final MenuItem item = menu.findItem(id);
        if (item == null) return;
        item.setIcon(icon);
    }

    public static void setMenuItemTitle(final Menu menu, final int id, final int icon) {
        if (menu == null) return;
        final MenuItem item = menu.findItem(id);
        if (item == null) return;
        item.setTitle(icon);
    }

    public static void setUserAgent(final Context context, final ConfigurationBuilder cb) {
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final boolean gzip_compressing = prefs.getBoolean(KEY_GZIP_COMPRESSING, true);
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

        }
    }

    public static boolean shouldEnableFiltersForRTs(final Context context) {
        if (context == null) return false;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FILTERS_FOR_RTS, true);
    }

    public static boolean shouldForceUsingPrivateAPIs(final Context context) {
        if (context == null) return false;
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FORCE_USING_PRIVATE_APIS, false);
    }

    public static boolean shouldStopAutoRefreshOnBatteryLow(final Context context) {
        final SharedPreferences mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return mPreferences.getBoolean(KEY_STOP_AUTO_REFRESH_WHEN_BATTERY_LOW, true);
    }

    public static void showErrorMessage(final Context context, final CharSequence message, final boolean long_message) {
        if (context == null) return;
        if (context instanceof Activity) {
            final Crouton crouton = Crouton.makeText((Activity) context, message, CroutonStyle.ALERT);
            final CroutonConfiguration.Builder cb = new CroutonConfiguration.Builder();
            cb.setDuration(long_message ? CroutonConfiguration.DURATION_LONG : CroutonConfiguration.DURATION_SHORT);
            crouton.setConfiguration(cb.build());
            crouton.show();
        } else {
            final Toast toast = Toast.makeText(context, message, long_message ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showErrorMessage(final Context context, final CharSequence action, final CharSequence message,
            final boolean long_message) {
        if (context == null) return;
        showErrorMessage(context, getErrorMessage(context, message), long_message);
    }

    public static void showErrorMessage(final Context context, final CharSequence action, final Throwable t,
            final boolean long_message) {
        if (context == null) return;
        if (t instanceof TwitterException) {
            showTwitterErrorMessage(context, action, (TwitterException) t, long_message);
            return;
        }
        showErrorMessage(context, getErrorMessage(context, action, t), long_message);
    }

    public static void showErrorMessage(final Context context, final int action, final String desc,
            final boolean long_message) {
        if (context == null) return;
        showErrorMessage(context, context.getString(action), desc, long_message);
    }

    public static void showErrorMessage(final Context context, final int action, final Throwable t,
            final boolean long_message) {
        if (context == null) return;
        showErrorMessage(context, context.getString(action), t, long_message);
    }

    public static void showInfoMessage(final Context context, final CharSequence message, final boolean long_message) {
        if (context == null || isEmpty(message)) return;
        if (context instanceof Activity) {
            final Crouton crouton = Crouton.makeText((Activity) context, message, CroutonStyle.INFO);
            final CroutonConfiguration.Builder cb = new CroutonConfiguration.Builder();
            cb.setDuration(long_message ? CroutonConfiguration.DURATION_LONG : CroutonConfiguration.DURATION_SHORT);
            crouton.setConfiguration(cb.build());
            crouton.show();
        } else {
            final Toast toast = Toast.makeText(context, message, long_message ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showInfoMessage(final Context context, final int resId, final boolean long_message) {
        if (context == null) return;
        showInfoMessage(context, context.getText(resId), long_message);
    }

    public static void showMenuItemToast(final View v, final CharSequence text) {
        final int[] screenPos = new int[2];
        final Rect displayFrame = new Rect();
        v.getLocationOnScreen(screenPos);
        v.getWindowVisibleDisplayFrame(displayFrame);
        final int height = v.getHeight();
        final int midy = screenPos[1] + height / 2;
        showMenuItemToast(v, text, midy >= displayFrame.height());
    }

    public static void showMenuItemToast(final View v, final CharSequence text, final boolean isBottomBar) {
        final int[] screenPos = new int[2];
        final Rect displayFrame = new Rect();
        v.getLocationOnScreen(screenPos);
        v.getWindowVisibleDisplayFrame(displayFrame);
        final int width = v.getWidth();
        final int height = v.getHeight();
        final int screenWidth = v.getResources().getDisplayMetrics().widthPixels;
        final Toast cheatSheet = Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT);
        if (isBottomBar) {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        } else {
            // Show along the top; follow action buttons
            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT, screenWidth - screenPos[0] - width / 2, height);
        }
        cheatSheet.show();
    }

    public static void showOkMessage(final Context context, final CharSequence message, final boolean long_message) {
        if (context == null || isEmpty(message)) return;
        if (context instanceof Activity) {
            final Crouton crouton = Crouton.makeText((Activity) context, message, CroutonStyle.CONFIRM);
            final CroutonConfiguration.Builder cb = new CroutonConfiguration.Builder();
            cb.setDuration(long_message ? CroutonConfiguration.DURATION_LONG : CroutonConfiguration.DURATION_SHORT);
            crouton.setConfiguration(cb.build());
            crouton.show();
        } else {
            final Toast toast = Toast.makeText(context, message, long_message ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showOkMessage(final Context context, final int resId, final boolean long_message) {
        if (context == null) return;
        showOkMessage(context, context.getText(resId), long_message);
    }

    public static void showTwitterErrorMessage(final Context context, final CharSequence action,
            final TwitterException te, final boolean long_message) {
        if (context == null) return;
        final String message;
        if (te != null) {
            if (action != null) {
                if (te.exceededRateLimitation()) {
                    final RateLimitStatus status = te.getRateLimitStatus();
                    final long sec_until_reset = status.getSecondsUntilReset() * 1000;
                    final String next_reset_time = ParseUtils.parseString(getRelativeTimeSpanString(System
                            .currentTimeMillis() + sec_until_reset));
                    message = context.getString(R.string.error_message_rate_limit_with_action, action,
                            next_reset_time.trim());
                } else if (isErrorCodeMessageSupported(te)) {
                    final String msg = StatusCodeMessageUtils
                            .getMessage(context, te.getStatusCode(), te.getErrorCode());
                    message = context.getString(R.string.error_message_with_action, action, msg != null ? msg
                            : trimLineBreak(te.getMessage()));
                } else if (te.getCause() instanceof SSLException) {
                    final String msg = te.getCause().getMessage();
                    if (msg != null && msg.contains("!=")) {
                        message = context.getString(R.string.error_message_with_action, action,
                                context.getString(R.string.ssl_error));
                    } else {
                        message = context.getString(R.string.error_message_with_action, action,
                                context.getString(R.string.network_error));
                    }
                } else if (te.getCause() instanceof IOException) {
                    message = context.getString(R.string.error_message_with_action, action,
                            context.getString(R.string.network_error));
                } else {
                    message = context.getString(R.string.error_message_with_action, action,
                            trimLineBreak(te.getMessage()));
                }
            } else {
                message = context.getString(R.string.error_message, trimLineBreak(te.getMessage()));
            }
        } else {
            message = context.getString(R.string.error_unknown_error);
        }
        showErrorMessage(context, message, long_message);
    }

    public static void showWarnMessage(final Context context, final CharSequence message, final boolean long_message) {
        if (context == null || isEmpty(message)) return;
        if (context instanceof Activity) {
            final Crouton crouton = Crouton.makeText((Activity) context, message, CroutonStyle.WARN);
            final CroutonConfiguration.Builder cb = new CroutonConfiguration.Builder();
            cb.setDuration(long_message ? CroutonConfiguration.DURATION_LONG : CroutonConfiguration.DURATION_SHORT);
            crouton.setConfiguration(cb.build());
            crouton.show();
        } else {
            final Toast toast = Toast.makeText(context, message, long_message ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showWarnMessage(final Context context, final int resId, final boolean long_message) {
        if (context == null) return;
        showWarnMessage(context, context.getText(resId), long_message);
    }

    public static void startProfilingServiceIfNeeded(final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final Intent profilingServiceIntent = new Intent(context, UCDService.class);
        if (prefs.getBoolean(KEY_UCD_DATA_PROFILING, false)) {
            context.startService(profilingServiceIntent);
        } else {
            context.stopService(profilingServiceIntent);
        }
    }

    public static void startStatusShareChooser(final Context context, final TwitterStatus status) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        final String name = status.user_name, screenName = status.user_screen_name;
        final String timeString = formatToLongTimeString(context, status.timestamp);
        final String subject = context.getString(R.string.share_subject_format, name, screenName, timeString);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, status.text_plain);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }

    public static void stopListView(final AbsListView list) {
        if (list == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            list.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_CANCEL, 0, 0, 0));
        } else {
            list.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN, 0, 0, 0));
            list.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP, 0, 0, 0));
        }
    }

    public static String trim(final String str) {
        return str != null ? str.trim() : null;
    }

    public static String trimLineBreak(final String orig) {
        if (orig == null) return null;
        return orig.replaceAll("\\n+", "\n");
    }

    public static boolean truncateMessages(final List<twitter4j.DirectMessage> in, final List<twitter4j.DirectMessage> out,
            final long since_id) {
        if (in == null) return false;
        for (final twitter4j.DirectMessage message : in) {
            if (since_id > 0 && message.getId() <= since_id) {
                continue;
            }
            out.add(message);
        }
        return in.size() != out.size();
    }

    public static boolean truncateStatuses(final List<twitter4j.Status> in, final List<twitter4j.Status> out,
            final long since_id) {
        if (in == null) return false;
        for (final twitter4j.Status status : in) {
            if (since_id > 0 && status.getId() <= since_id) {
                continue;
            }
            out.add(status);
        }
        return in.size() != out.size();
    }

    private static Drawable getMetadataDrawable(final PackageManager pm, final ActivityInfo info, final String key) {
        if (pm == null || info == null || info.metaData == null || key == null || !info.metaData.containsKey(key))
            return null;
        final Drawable d = pm.getDrawable(info.packageName, info.metaData.getInt(key), info.applicationInfo);
        return d;
    }

    private static boolean isErrorCodeMessageSupported(final TwitterException te) {
        if (te == null) return false;
        return StatusCodeMessageUtils.containsHttpStatus(te.getStatusCode())
                || StatusCodeMessageUtils.containsTwitterError(te.getErrorCode());
    }

    private static boolean isExtensionUseJSON(final ResolveInfo info) {
        if (info == null || info.activityInfo == null) return true;
        final ActivityInfo activityInfo = info.activityInfo;
        if (activityInfo.metaData != null && activityInfo.metaData.containsKey(METADATA_KEY_EXTENSION_USE_JSON))
            return activityInfo.metaData.getBoolean(METADATA_KEY_EXTENSION_USE_JSON);
        final ApplicationInfo appInfo = activityInfo.applicationInfo;
        if (appInfo == null) return true;
        if (appInfo.metaData != null && appInfo.metaData.containsKey(METADATA_KEY_EXTENSION_USE_JSON))
            return appInfo.metaData.getBoolean(METADATA_KEY_EXTENSION_USE_JSON);
        return true;
    }

    private static void parseEntities(final HtmlBuilder builder, final EntitySupport entities) {
        // Format media.
        final MediaEntity[] medias = entities.getMediaEntities();
        if (medias != null) {
            for (final MediaEntity media : medias) {
                final int start = media.getStart(), end = media.getEnd();
                final URL mediaUrl = media.getMediaURL();
                if (mediaUrl != null && start >= 0 && end >= 0) {
                    builder.addLink(ParseUtils.parseString(mediaUrl), media.getDisplayURL(), start, end);
                }
            }
        }
        final URLEntity[] urls = entities.getURLEntities();
        if (urls != null) {
            for (final URLEntity url : urls) {
                final int start = url.getStart(), end = url.getEnd();
                final URL expandedUrl = url.getExpandedURL();
                if (expandedUrl != null && start >= 0 && end >= 0) {
                    builder.addLink(ParseUtils.parseString(expandedUrl), url.getDisplayURL(), start, end);
                }
            }
        }
    }
}
