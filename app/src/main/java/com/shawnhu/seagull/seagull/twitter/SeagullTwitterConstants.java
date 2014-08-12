package com.shawnhu.seagull.seagull.twitter;

import com.shawnhu.seagull.seagull.twitter.annotations.Preference;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;

import static com.shawnhu.seagull.seagull.twitter.annotations.Preference.Type.BOOLEAN;
import static com.shawnhu.seagull.seagull.twitter.annotations.Preference.Type.INT;
import static com.shawnhu.seagull.seagull.twitter.annotations.Preference.Type.LONG;
import static com.shawnhu.seagull.seagull.twitter.annotations.Preference.Type.STRING;

/**
 * Created by shawn on 14-7-31.
 */
public class SeagullTwitterConstants {

    public static final String APP_NAME = "SeagullCity";
    public static final String APP_PACKAGE_NAME = "com.shawnhu.seagull";
    public static final String APP_PROJECT_URL = "";
    public static final String APP_PROJECT_EMAIL = "";

    public static final String DATABASES_NAME = "seagull.db";
    public static final int    DATABASES_VERSION = 1;

    public static final String TWITTER_SIGNUP_URL = "https://twitter.com/signup";

    public static final String SHARED_PREFERENCES_NAME = "twitter-preferences";
    public static final String SILENT_NOTIFICATIONS_PREFERENCE_NAME = "silent_notifications";
    public static final String ACCOUNT_PREFERENCES_NAME_PREFIX = "account_preferences_";

    public static final String TWITTER_CONSUMER_KEY = "";
    public static final String TWITTER_CONSUMER_SECRET = "";

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";
    public static final String SCHEME_CONTENT = "content";
    public static final String SCHEME_TWIDERE = "twidere";

    public static final String PROTOCOL_HTTP = SCHEME_HTTP + "://";
    public static final String PROTOCOL_HTTPS = SCHEME_HTTPS + "://";
    public static final String PROTOCOL_CONTENT = SCHEME_CONTENT + "://";
    public static final String PROTOCOL_TWIDERE = SCHEME_TWIDERE + "://";

    public static final String AUTHORITY_USER = "user";
    public static final String AUTHORITY_USERS = "users";
    public static final String AUTHORITY_USER_TIMELINE = "user_timeline";
    public static final String AUTHORITY_USER_FAVORITES = "user_favorites";
    public static final String AUTHORITY_USER_FOLLOWERS = "user_followers";
    public static final String AUTHORITY_USER_FRIENDS = "user_friends";
    public static final String AUTHORITY_USER_BLOCKS = "user_blocks";
    public static final String AUTHORITY_STATUS = "status";
    public static final String AUTHORITY_STATUSES = "statuses";
    public static final String AUTHORITY_DIRECT_MESSAGES_CONVERSATION = "direct_messages_conversation";
    public static final String AUTHORITY_SEARCH = "search";
    public static final String AUTHORITY_MAP = "map";
    public static final String AUTHORITY_USER_LIST = "user_list";
    public static final String AUTHORITY_USER_LIST_TIMELINE = "user_list_timeline";
    public static final String AUTHORITY_USER_LIST_MEMBERS = "user_list_members";
    public static final String AUTHORITY_USER_LIST_SUBSCRIBERS = "user_list_subscribers";
    public static final String AUTHORITY_USER_LIST_MEMBERSHIPS = "user_list_memberships";
    public static final String AUTHORITY_USER_LISTS = "user_lists";
    public static final String AUTHORITY_USERS_RETWEETED_STATUS = "users_retweeted_status";
    public static final String AUTHORITY_SAVED_SEARCHES = "saved_searches";
    public static final String AUTHORITY_SEARCH_USERS = "search_users";
    public static final String AUTHORITY_SEARCH_TWEETS = "search_tweets";
    public static final String AUTHORITY_TRENDS = "trends";
    public static final String AUTHORITY_USER_MENTIONS = "user_mentions";
    public static final String AUTHORITY_ACTIVITIES_ABOUT_ME = "activities_about_me";
    public static final String AUTHORITY_ACTIVITIES_BY_FRIENDS = "activities_by_friends";
    public static final String AUTHORITY_INCOMING_FRIENDSHIPS = "incoming_friendships";
    public static final String AUTHORITY_STATUS_RETWEETERS = "status_retweeters";
    public static final String AUTHORITY_STATUS_FAVORITERS = "status_favoriters";
    public static final String AUTHORITY_STATUS_REPLIES = "status_replies";
    public static final String AUTHORITY_RETWEETS_OF_ME = "retweets_of_me";
    public static final String AUTHORITY_MUTES_USERS = "mutes_users";

    public static final int LINK_ID_STATUS = 1;
    public static final int LINK_ID_USER = 2;
    public static final int LINK_ID_USER_TIMELINE = 3;
    public static final int LINK_ID_USER_FAVORITES = 4;
    public static final int LINK_ID_USER_FOLLOWERS = 5;
    public static final int LINK_ID_USER_FRIENDS = 6;
    public static final int LINK_ID_USER_BLOCKS = 7;
    public static final int LINK_ID_DIRECT_MESSAGES_CONVERSATION = 9;
    public static final int LINK_ID_USER_LIST = 10;
    public static final int LINK_ID_USER_LISTS = 11;
    public static final int LINK_ID_USER_LIST_TIMELINE = 12;
    public static final int LINK_ID_USER_LIST_MEMBERS = 13;
    public static final int LINK_ID_USER_LIST_SUBSCRIBERS = 14;
    public static final int LINK_ID_USER_LIST_MEMBERSHIPS = 15;
    public static final int LINK_ID_SAVED_SEARCHES = 19;
    public static final int LINK_ID_USER_MENTIONS = 21;
    public static final int LINK_ID_INCOMING_FRIENDSHIPS = 22;
    public static final int LINK_ID_USERS = 23;
    public static final int LINK_ID_STATUSES = 24;
    public static final int LINK_ID_STATUS_RETWEETERS = 25;
    public static final int LINK_ID_STATUS_REPLIES = 26;
    public static final int LINK_ID_STATUS_FAVORITERS = 27;
    public static final int LINK_ID_SEARCH = 28;
    public static final int LINK_ID_MUTES_USERS = 41;


    public static final String QUERY_PARAM_ACCOUNT_ID = "account_id";
    public static final String QUERY_PARAM_ACCOUNT_IDS = "account_ids";
    public static final String QUERY_PARAM_ACCOUNT_NAME = "account_name";
    public static final String QUERY_PARAM_STATUS_ID = "status_id";
    public static final String QUERY_PARAM_USER_ID = "user_id";
    public static final String QUERY_PARAM_LIST_ID = "list_id";
    public static final String QUERY_PARAM_SCREEN_NAME = "screen_name";
    public static final String QUERY_PARAM_LIST_NAME = "list_name";
    public static final String QUERY_PARAM_QUERY = "query";
    public static final String QUERY_PARAM_TYPE = "type";
    public static final String QUERY_PARAM_VALUE_USERS = "users";
    public static final String QUERY_PARAM_VALUE_TWEETS = "tweets";
    public static final String QUERY_PARAM_NOTIFY = "notify";
    public static final String QUERY_PARAM_LAT = "lat";
    public static final String QUERY_PARAM_LNG = "lng";
    public static final String QUERY_PARAM_URL = "url";
    public static final String QUERY_PARAM_NAME = "name";
    public static final String QUERY_PARAM_FINISH_ONLY = "finish_only";
    public static final String QUERY_PARAM_NEW_ITEMS_COUNT = "new_items_count";
    public static final String QUERY_PARAM_RECIPIENT_ID = "recipient_id";

    public static final String DEFAULT_PROTOCOL = PROTOCOL_HTTPS;

    public static final String OAUTH_CALLBACK_OOB = "oob";
    public static final String OAUTH_CALLBACK_URL = PROTOCOL_TWIDERE + "com.twitter.oauth/";

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_SELECT_ACCOUNT = 3;
    public static final int REQUEST_COMPOSE = 4;
    public static final int REQUEST_EDIT_API = 5;
    public static final int REQUEST_BROWSER_SIGN_IN = 6;
    public static final int REQUEST_SET_COLOR = 7;
    public static final int REQUEST_SAVE_FILE = 8;
    public static final int REQUEST_EDIT_IMAGE = 9;
    public static final int REQUEST_EXTENSION_COMPOSE = 10;
    public static final int REQUEST_ADD_TAB = 11;
    public static final int REQUEST_EDIT_TAB = 12;
    public static final int REQUEST_PICK_FILE = 13;
    public static final int REQUEST_PICK_DIRECTORY = 14;
    public static final int REQUEST_ADD_TO_LIST = 15;
    public static final int REQUEST_SELECT_USER = 16;
    public static final int REQUEST_SELECT_USER_LIST = 17;
    public static final int REQUEST_PICK_ACTIVITY = 18;
    public static final int REQUEST_SETTINGS = 19;
    public static final int REQUEST_OPEN_DOCUMENT = 20;
    public static final int REQUEST_SWIPEBACK_ACTIVITY = 101;

    public static final int NOTIFICATION_ID_HOME_TIMELINE = 1;
    public static final int NOTIFICATION_ID_MENTIONS = 2;
    public static final int NOTIFICATION_ID_DIRECT_MESSAGES = 3;
    public static final int NOTIFICATION_ID_DRAFTS = 4;
    public static final int NOTIFICATION_ID_DATA_PROFILING = 5;
    public static final int NOTIFICATION_ID_UPDATE_STATUS = 101;
    public static final int NOTIFICATION_ID_SEND_DIRECT_MESSAGE = 102;

    public static final String ICON_SPECIAL_TYPE_CUSTOMIZE = "_customize";

    public static final String TASK_TAG_GET_HOME_TIMELINE = "get_home_tomeline";
    public static final String TASK_TAG_GET_MENTIONS = "get_mentions";
    public static final String TASK_TAG_GET_SENT_DIRECT_MESSAGES = "get_sent_direct_messages";
    public static final String TASK_TAG_GET_RECEIVED_DIRECT_MESSAGES = "get_received_direct_messages";
    public static final String TASK_TAG_GET_TRENDS = "get_trends";
    public static final String TASK_TAG_STORE_HOME_TIMELINE = "store_home_tomeline";
    public static final String TASK_TAG_STORE_MENTIONS = "store_mentions";
    public static final String TASK_TAG_STORE_SENT_DIRECT_MESSAGES = "store_sent_direct_messages";
    public static final String TASK_TAG_STORE_RECEIVED_DIRECT_MESSAGES = "store_received_direct_messages";
    public static final String TASK_TAG_STORE_TRENDS = "store_trends";

    public static final String SERVICE_COMMAND_REFRESH_ALL = "refresh_all";
    public static final String SERVICE_COMMAND_GET_HOME_TIMELINE = "get_home_timeline";
    public static final String SERVICE_COMMAND_GET_MENTIONS = "get_mentions";
    public static final String SERVICE_COMMAND_GET_SENT_DIRECT_MESSAGES = "get_sent_direct_messages";
    public static final String SERVICE_COMMAND_GET_RECEIVED_DIRECT_MESSAGES = "get_received_direct_messages";

    public static final String METADATA_KEY_EXTENSION = "org.mariotaku.twidere.extension";
    public static final String METADATA_KEY_EXTENSION_PERMISSIONS = "org.mariotaku.twidere.extension.permissions";
    public static final String METADATA_KEY_EXTENSION_SETTINGS = "org.mariotaku.twidere.extension.settings";
    public static final String METADATA_KEY_EXTENSION_ICON = "org.mariotaku.twidere.extension.icon";
    public static final String METADATA_KEY_EXTENSION_USE_JSON = "org.mariotaku.twidere.extension.use_json";

    public static final char SEPARATOR_PERMISSION = '|';
    public static final String SEPARATOR_PERMISSION_REGEX = "\\" + SEPARATOR_PERMISSION;

    public static final String PERMISSION_DENIED = "denied";
    public static final String PERMISSION_REFRESH = "refresh";
    public static final String PERMISSION_READ = "read";
    public static final String PERMISSION_WRITE = "write";
    public static final String PERMISSION_DIRECT_MESSAGES = "direct_messages";
    public static final String PERMISSION_ACCOUNTS = "accounts";
    public static final String PERMISSION_PREFERENCES = "preferences";

    public static final int TWITTER_MAX_IMAGE_SIZE = 3145728;
    public static final int TWITTER_MAX_IMAGE_WIDTH = 1024;
    public static final int TWITTER_MAX_IMAGE_HEIGHT = 2048;

    public static final String DIR_NAME_IMAGE_CACHE = "image_cache";


    /**
     * BEGIN PREFERENCES
     */
    public static final String FORMAT_PATTERN_TITLE = "[TITLE]";
    public static final String FORMAT_PATTERN_TEXT = "[TEXT]";
    public static final String FORMAT_PATTERN_NAME = "[NAME]";
    public static final String FORMAT_PATTERN_LINK = "[LINK]";

    public static final String VALUE_NONE = "none";
    public static final String VALUE_LINK_HIGHLIGHT_OPTION_NONE = VALUE_NONE;
    public static final String VALUE_LINK_HIGHLIGHT_OPTION_HIGHLIGHT = "highlight";
    public static final String VALUE_LINK_HIGHLIGHT_OPTION_UNDERLINE = "underline";
    public static final String VALUE_LINK_HIGHLIGHT_OPTION_BOTH = "both";
    public static final int VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE = 0x0;
    public static final int VALUE_LINK_HIGHLIGHT_OPTION_CODE_HIGHLIGHT = 0x1;
    public static final int VALUE_LINK_HIGHLIGHT_OPTION_CODE_UNDERLINE = 0x2;
    public static final int VALUE_LINK_HIGHLIGHT_OPTION_CODE_BOTH = VALUE_LINK_HIGHLIGHT_OPTION_CODE_HIGHLIGHT
            | VALUE_LINK_HIGHLIGHT_OPTION_CODE_UNDERLINE;

    public static final String VALUE_THEME_FONT_FAMILY_REGULAR = "sans-serif";
    public static final String VALUE_THEME_FONT_FAMILY_CONDENSED = "sans-serif-condensed";
    public static final String VALUE_THEME_FONT_FAMILY_LIGHT = "sans-serif-light";
    public static final String VALUE_THEME_FONT_FAMILY_THIN = "sans-serif-thin";

    public static final int VALUE_NOTIFICATION_FLAG_NONE = 0x0;
    public static final int VALUE_NOTIFICATION_FLAG_RINGTONE = 0x1;
    public static final int VALUE_NOTIFICATION_FLAG_VIBRATION = 0x2;
    public static final int VALUE_NOTIFICATION_FLAG_LIGHT = 0x4;

    public static final String VALUE_COMPOSE_QUIT_ACTION_ASK = "ask";
    public static final String VALUE_COMPOSE_QUIT_ACTION_SAVE = "save";
    public static final String VALUE_COMPOSE_QUIT_ACTION_DISCARD = "discard";

    public static final String VALUE_TAB_DIPLAY_OPTION_ICON = "icon";
    public static final String VALUE_TAB_DIPLAY_OPTION_LABEL = "label";
    public static final String VALUE_TAB_DIPLAY_OPTION_BOTH = "both";
    public static final int VALUE_TAB_DIPLAY_OPTION_CODE_ICON = 0x1;
    public static final int VALUE_TAB_DIPLAY_OPTION_CODE_LABEL = 0x2;
    public static final int VALUE_TAB_DIPLAY_OPTION_CODE_BOTH = VALUE_TAB_DIPLAY_OPTION_CODE_ICON
            | VALUE_TAB_DIPLAY_OPTION_CODE_LABEL;

    public static final String VALUE_THEME_BACKGROUND_DEFAULT = "default";
    public static final String VALUE_THEME_BACKGROUND_SOLID = "solid";
    public static final String VALUE_THEME_BACKGROUND_TRANSPARENT = "transparent";

    public static final String VALUE_THEME_NAME_TWIDERE = "twidere";
    public static final String VALUE_THEME_NAME_DARK = "dark";
    public static final String VALUE_THEME_NAME_LIGHT = "light";

    public static final String VALUE_COMPOSE_NOW_ACTION_COMPOSE = "compose";
    public static final String VALUE_COMPOSE_NOW_ACTION_TAKE_PHOTO = "take_photo";
    public static final String VALUE_COMPOSE_NOW_ACTION_PICK_IMAGE = "pick_image";

    public static final String VALUE_CARD_HIGHLIGHT_OPTION_NONE = VALUE_NONE;
    public static final String VALUE_CARD_HIGHLIGHT_OPTION_BACKGROUND = "background";
    public static final String VALUE_CARD_HIGHLIGHT_OPTION_LINE = "line";

    public static final int VALUE_CARD_HIGHLIGHT_OPTION_CODE_NONE = 0x0;
    public static final int VALUE_CARD_HIGHLIGHT_OPTION_CODE_BACKGROUND = 0x1;
    public static final int VALUE_CARD_HIGHLIGHT_OPTION_CODE_LINE = 0x2;

    public static final String DEFAULT_THEME = VALUE_THEME_NAME_TWIDERE;
    public static final String DEFAULT_THEME_BACKGROUND = VALUE_THEME_BACKGROUND_DEFAULT;
    public static final String DEFAULT_THEME_FONT_FAMILY = VALUE_THEME_FONT_FAMILY_REGULAR;
    public static final int DEFAULT_THEME_BACKGROUND_ALPHA = 160;

    public static final String DEFAULT_QUOTE_FORMAT = "RT @" + FORMAT_PATTERN_NAME + ": " + FORMAT_PATTERN_TEXT;
    public static final String DEFAULT_SHARE_FORMAT = FORMAT_PATTERN_TITLE + " - " + FORMAT_PATTERN_TEXT;
    public static final String DEFAULT_IMAGE_UPLOAD_FORMAT = FORMAT_PATTERN_TEXT + " " + FORMAT_PATTERN_LINK;

    public static final String DEFAULT_REFRESH_INTERVAL = "15";
    public static final boolean DEFAULT_AUTO_REFRESH = true;
    public static final boolean DEFAULT_AUTO_REFRESH_HOME_TIMELINE = false;
    public static final boolean DEFAULT_AUTO_REFRESH_MENTIONS = true;
    public static final boolean DEFAULT_AUTO_REFRESH_DIRECT_MESSAGES = true;
    public static final boolean DEFAULT_AUTO_REFRESH_TRENDS = false;
    public static final boolean DEFAULT_NOTIFICATION = true;
    public static final int DEFAULT_NOTIFICATION_HOME_OPTIONS = VALUE_NOTIFICATION_FLAG_NONE;
    public static final int DEFAULT_NOTIFICATION_MENTIONS_OPTIONS = VALUE_NOTIFICATION_FLAG_VIBRATION
            | VALUE_NOTIFICATION_FLAG_LIGHT;
    public static final int DEFAULT_NOTIFICATION_DIRECT_MESSAGES_OPTIONS = VALUE_NOTIFICATION_FLAG_RINGTONE
            | VALUE_NOTIFICATION_FLAG_VIBRATION | VALUE_NOTIFICATION_FLAG_LIGHT;

    public static final boolean DEFAULT_HOME_TIMELINE_NOTIFICATION = false;
    public static final boolean DEFAULT_MENTIONS_NOTIFICATION = true;
    public static final boolean DEFAULT_DIRECT_MESSAGES_NOTIFICATION = true;

    public static final int DEFAULT_DATABASE_ITEM_LIMIT = 100;
    public static final int DEFAULT_LOAD_ITEM_LIMIT = 20;
    public static final String DEFAULT_CARD_HIGHLIGHT_OPTION = VALUE_CARD_HIGHLIGHT_OPTION_BACKGROUND;

    @Preference(type = INT, hasDefault = true, defaultInt = DEFAULT_DATABASE_ITEM_LIMIT)
    public static final String KEY_DATABASE_ITEM_LIMIT = "database_item_limit";
    @Preference(type = INT, hasDefault = true, defaultInt = DEFAULT_LOAD_ITEM_LIMIT)
    public static final String KEY_LOAD_ITEM_LIMIT = "load_item_limit";
    @Preference(type = INT)
    public static final String KEY_TEXT_SIZE = "text_size_int";
    @Preference(type = STRING, hasDefault = true, defaultString = DEFAULT_THEME)
    public static final String KEY_THEME = "theme";
    @Preference(type = STRING, hasDefault = true, defaultString = DEFAULT_THEME_BACKGROUND)
    public static final String KEY_THEME_BACKGROUND = "theme_background";
    @Preference(type = INT, hasDefault = true, defaultInt = DEFAULT_THEME_BACKGROUND_ALPHA)
    public static final String KEY_THEME_BACKGROUND_ALPHA = "theme_background_alpha";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_THEME_DARK_ACTIONBAR = "theme_dark_actionbar";
    @Preference(type = INT)
    public static final String KEY_THEME_COLOR = "theme_color";
    @Preference(type = STRING, hasDefault = true, defaultString = DEFAULT_THEME_FONT_FAMILY)
    public static final String KEY_THEME_FONT_FAMILY = "theme_font_family";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_DISPLAY_PROFILE_IMAGE = "display_profile_image";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_DISPLAY_IMAGE_PREVIEW = "display_image_preview";
    @Preference(type = BOOLEAN)
    public static final String KEY_BOTTOM_COMPOSE_BUTTON = "bottom_compose_button";
    @Preference(type = BOOLEAN)
    public static final String KEY_LEFTSIDE_COMPOSE_BUTTON = "leftside_compose_button";
    @Preference(type = BOOLEAN)
    public static final String KEY_BOTTOM_SEND_BUTTON = "bottom_send_button";
    @Preference(type = BOOLEAN)
    public static final String KEY_ATTACH_LOCATION = "attach_location";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_GZIP_COMPRESSING = "gzip_compressing";
    @Preference(type = BOOLEAN)
    public static final String KEY_IGNORE_SSL_ERROR = "ignore_ssl_error";
    @Preference(type = BOOLEAN)
    public static final String KEY_LOAD_MORE_AUTOMATICALLY = "load_more_automatically";
    @Preference(type = STRING)
    public static final String KEY_QUOTE_FORMAT = "quote_format";
    @Preference(type = BOOLEAN)
    public static final String KEY_REMEMBER_POSITION = "remember_position";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_LOAD_MORE_FROM_TOP = "load_more_from_top";
    @Preference(type = INT, exportable = false)
    public static final String KEY_SAVED_TAB_POSITION = "saved_tab_position";
    @Preference(type = BOOLEAN)
    public static final String KEY_ENABLE_PROXY = "enable_proxy";
    @Preference(type = STRING)
    public static final String KEY_PROXY_HOST = "proxy_host";
    @Preference(type = STRING)
    public static final String KEY_PROXY_PORT = "proxy_port";
    @Preference(type = BOOLEAN)
    public static final String KEY_REFRESH_ON_START = "refresh_on_start";
    @Preference(type = BOOLEAN)
    public static final String KEY_REFRESH_AFTER_TWEET = "refresh_after_tweet";
    @Preference(type = BOOLEAN)
    public static final String KEY_AUTO_REFRESH = "auto_refresh";
    @Preference(type = STRING)
    public static final String KEY_REFRESH_INTERVAL = "refresh_interval";
    @Preference(type = BOOLEAN)
    public static final String KEY_AUTO_REFRESH_HOME_TIMELINE = "auto_refresh_home_timeline";
    @Preference(type = BOOLEAN)
    public static final String KEY_AUTO_REFRESH_MENTIONS = "auto_refresh_mentions";
    @Preference(type = BOOLEAN)
    public static final String KEY_AUTO_REFRESH_DIRECT_MESSAGES = "auto_refresh_direct_messages";
    @Preference(type = BOOLEAN)
    public static final String KEY_AUTO_REFRESH_TRENDS = "auto_refresh_trends";
    @Preference(type = BOOLEAN)
    public static final String KEY_HOME_TIMELINE_NOTIFICATION = "home_timeline_notification";
    @Preference(type = BOOLEAN)
    public static final String KEY_MENTIONS_NOTIFICATION = "mentions_notification";
    @Preference(type = BOOLEAN)
    public static final String KEY_DIRECT_MESSAGES_NOTIFICATION = "direct_messages_notification";
    @Preference(type = INT)
    public static final String KEY_LOCAL_TRENDS_WOEID = "local_trends_woeid";
    public static final String KEY_NOTIFICATION_RINGTONE = "notification_ringtone";
    public static final String KEY_NOTIFICATION_LIGHT_COLOR = "notification_light_color";
    public static final String KEY_SHARE_FORMAT = "share_format";
    public static final String KEY_HOME_REFRESH_MENTIONS = "home_refresh_mentions";
    public static final String KEY_HOME_REFRESH_DIRECT_MESSAGES = "home_refresh_direct_messages";
    public static final String KEY_HOME_REFRESH_TRENDS = "home_refresh_trends";
    public static final String KEY_IMAGE_UPLOAD_FORMAT = "image_upload_format";
    public static final String KEY_STATUS_SHORTENER = "status_shortener";
    public static final String KEY_MEDIA_UPLOADER = "media_uploader";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_SHOW_ABSOLUTE_TIME = "show_absolute_time";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_QUICK_SEND = "quick_send";
    @Preference(type = STRING, exportable = false)
    public static final String KEY_COMPOSE_ACCOUNTS = "compose_accounts";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_TCP_DNS_QUERY = "tcp_dns_query";
    @Preference(type = STRING, hasDefault = true, defaultString = "8.8.8.8")
    public static final String KEY_DNS_SERVER = "dns_server";
    public static final String KEY_CONNECTION_TIMEOUT = "connection_timeout";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_NAME_FIRST = "name_first";
    public static final String KEY_STOP_AUTO_REFRESH_WHEN_BATTERY_LOW = "stop_auto_refresh_when_battery_low";
    @Preference(type = BOOLEAN, exportable = false)
    public static final String KEY_UCD_DATA_PROFILING = "ucd_data_profiling";
    @Preference(type = BOOLEAN, exportable = false)
    public static final String KEY_SHOW_UCD_DATA_PROFILING_REQUEST = "show_ucd_data_profiling_request";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_DISPLAY_SENSITIVE_CONTENTS = "display_sensitive_contents";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_PHISHING_LINK_WARNING = "phishing_link_warning";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_FAST_SCROLL_THUMB = "fast_scroll_thumb";
    public static final String KEY_LINK_HIGHLIGHT_OPTION = "link_highlight_option";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_INDICATE_MY_STATUS = "indicate_my_status";
    public static final String KEY_PRELOAD_PROFILE_IMAGES = "preload_profile_images";
    public static final String KEY_PRELOAD_PREVIEW_IMAGES = "preload_preview_images";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_PRELOAD_WIFI_ONLY = "preload_wifi_only";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_DISABLE_TAB_SWIPE = "disable_tab_swipe";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_LINK_TO_QUOTED_TWEET = "link_to_quoted_tweet";
    @Preference(type = BOOLEAN)
    public static final String KEY_BACKGROUND_TOAST_NOTIFICATION = "background_toast_notification";
    @Preference(type = STRING)
    public static final String KEY_COMPOSE_QUIT_ACTION = "compose_quit_action";
    @Preference(type = BOOLEAN)
    public static final String KEY_NO_CLOSE_AFTER_TWEET_SENT = "no_close_after_tweet_sent";
    @Preference(type = BOOLEAN)
    public static final String KEY_FAST_IMAGE_LOADING = "fast_image_loading";
    @Preference(type = STRING, hasDefault = false)
    public static final String KEY_API_URL_FORMAT = "api_url_format";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_SAME_OAUTH_SIGNING_URL = "same_oauth_signing_url";
    @Preference(type = INT, hasDefault = true, defaultInt = TweetStore.Accounts.AUTH_TYPE_OAUTH)
    public static final String KEY_AUTH_TYPE = "auth_type";
    @Preference(type = STRING, hasDefault = true, defaultString = TWITTER_CONSUMER_KEY)
    public static final String KEY_CONSUMER_KEY = "consumer_key";
    @Preference(type = STRING, hasDefault = true, defaultString = TWITTER_CONSUMER_SECRET)
    public static final String KEY_CONSUMER_SECRET = "consumer_secret";
    public static final String KEY_FILTERS_IN_HOME_TIMELINE = "filters_in_home_timeline";
    public static final String KEY_FILTERS_IN_MENTIONS = "filters_in_mentions";
    public static final String KEY_FILTERS_FOR_RTS = "filters_for_rts";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_NICKNAME_ONLY = "nickname_only";
    public static final String KEY_SETTINGS_WIZARD_COMPLETED = "settings_wizard_completed";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_CARD_ANIMATION = "card_animation";
    public static final String KEY_UNREAD_COUNT = "unread_count";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_NOTIFICATION_HOME_OPTIONS = "notification_type_home";
    public static final String KEY_NOTIFICATION_MENTIONS_OPTIONS = "notification_type_mentions";
    public static final String KEY_NOTIFICATION_DIRECT_MESSAGES_OPTIONS = "notification_type_direct_messages";
    public static final String KEY_MY_FOLLOWING_ONLY = "my_following_only";

    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_COMPACT_CARDS = "compact_cards";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_LONG_CLICK_TO_OPEN_MENU = "long_click_to_open_menu";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_SWIPE_BACK = "swipe_back";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_FORCE_USING_PRIVATE_APIS = "force_using_private_apis";
    @Preference(type = STRING, hasDefault = true, defaultString = "140")
    public static final String KEY_STATUS_TEXT_LIMIT = "status_text_limit";
    @Preference(type = STRING, hasDefault = true, defaultString = VALUE_COMPOSE_NOW_ACTION_COMPOSE)
    public static final String KEY_COMPOSE_NOW_ACTION = "compose_now_action";
    public static final String KEY_FALLBACK_TWITTER_LINK_HANDLER = "fallback_twitter_link_handler";
    @Preference(type = STRING, hasDefault = true, defaultString = "CENTER_CROP")
    public static final String KEY_IMAGE_PREVIEW_SCALE_TYPE = "image_preview_scale_type";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = false)
    public static final String KEY_PLAIN_LIST_STYLE = "plain_list_style";
    @Preference(type = BOOLEAN, hasDefault = true, defaultBoolean = true)
    public static final String KEY_DARK_DRAWER = "dark_drawer";

    public static final String KEY_QUICK_MENU_EXPANDED = "quick_menu_expanded";

    @Preference(type = STRING)
    public static final String KEY_TRANSLATION_DESTINATION = "translation_destination";
    @Preference(type = STRING)
    public static final String KEY_TAB_DISPLAY_OPTION = "tab_display_option";
    @Preference(type = STRING)
    public static final String KEY_CARD_HIGHLIGHT_OPTION = "card_highlight_option";
    @Preference(type = INT, exportable = false)
    public static final String KEY_LIVE_WALLPAPER_SCALE = "live_wallpaper_scale";
    @Preference(type = LONG, exportable = false)
    public static final String KEY_API_LAST_CHANGE = "api_last_change";
    @Preference(type = LONG, exportable = false)
    public static final String KEY_DEFAULT_ACCOUNT_ID = "default_account_id";
    /**
     * END PREFERENCES
     */

    /**
     *  Intents */
    public static final String INTENT_PACKAGE_PREFIX = "com.shawnhu.seagull.";
    public static final String INTENT_ACTION_COMPOSE = INTENT_PACKAGE_PREFIX + "COMPOSE";
    public static final String INTENT_ACTION_REPLY = INTENT_PACKAGE_PREFIX + "REPLY";
    public static final String INTENT_ACTION_QUOTE = INTENT_PACKAGE_PREFIX + "QUOTE";
    public static final String INTENT_ACTION_EDIT_DRAFT = INTENT_PACKAGE_PREFIX + "EDIT_DRAFT";
    public static final String INTENT_ACTION_MENTION = INTENT_PACKAGE_PREFIX + "MENTION";
    public static final String INTENT_ACTION_REPLY_MULTIPLE = INTENT_PACKAGE_PREFIX + "REPLY_MULTIPLE";
    public static final String INTENT_ACTION_SETTINGS = INTENT_PACKAGE_PREFIX + "SETTINGS";
    public static final String INTENT_ACTION_SELECT_ACCOUNT = INTENT_PACKAGE_PREFIX + "SELECT_ACCOUNT";
    public static final String INTENT_ACTION_VIEW_IMAGE = INTENT_PACKAGE_PREFIX + "VIEW_IMAGE";
    public static final String INTENT_ACTION_FILTERS = INTENT_PACKAGE_PREFIX + "FILTERS";
    public static final String INTENT_ACTION_TWITTER_LOGIN = INTENT_PACKAGE_PREFIX + "TWITTER_LOGIN";
    public static final String INTENT_ACTION_DRAFTS = INTENT_PACKAGE_PREFIX + "DRAFTS";
    public static final String INTENT_ACTION_PICK_FILE = INTENT_PACKAGE_PREFIX + "PICK_FILE";
    public static final String INTENT_ACTION_PICK_DIRECTORY = INTENT_PACKAGE_PREFIX + "PICK_DIRECTORY";
    public static final String INTENT_ACTION_VIEW_WEBPAGE = INTENT_PACKAGE_PREFIX + "VIEW_WEBPAGE";
    public static final String INTENT_ACTION_EXTENSIONS = INTENT_PACKAGE_PREFIX + "EXTENSIONS";
    public static final String INTENT_ACTION_CUSTOM_TABS = INTENT_PACKAGE_PREFIX + "CUSTOM_TABS";
    public static final String INTENT_ACTION_ADD_TAB = INTENT_PACKAGE_PREFIX + "ADD_TAB";
    public static final String INTENT_ACTION_EDIT_TAB = INTENT_PACKAGE_PREFIX + "EDIT_TAB";
    public static final String INTENT_ACTION_EDIT_USER_PROFILE = INTENT_PACKAGE_PREFIX + "EDIT_USER_PROFILE";
    public static final String INTENT_ACTION_SERVICE_COMMAND = INTENT_PACKAGE_PREFIX + "SERVICE_COMMAND";
    public static final String INTENT_ACTION_REQUEST_PERMISSIONS = INTENT_PACKAGE_PREFIX + "REQUEST_PERMISSIONS";
    public static final String INTENT_ACTION_SELECT_USER_LIST = INTENT_PACKAGE_PREFIX + "SELECT_USER_LIST";
    public static final String INTENT_ACTION_SELECT_USER = INTENT_PACKAGE_PREFIX + "SELECT_USER";
    public static final String INTENT_ACTION_COMPOSE_TAKE_PHOTO = INTENT_PACKAGE_PREFIX + "COMPOSE_TAKE_PHOTO";
    public static final String INTENT_ACTION_COMPOSE_PICK_IMAGE = INTENT_PACKAGE_PREFIX + "COMPOSE_PICK_IMAGE";
    public static final String INTENT_ACTION_TAKE_PHOTO = INTENT_PACKAGE_PREFIX + "TAKE_PHOTO";
    public static final String INTENT_ACTION_PICK_IMAGE = INTENT_PACKAGE_PREFIX + "PICK_IMAGE";

    public static final String INTENT_ACTION_EXTENSION_EDIT_IMAGE = INTENT_PACKAGE_PREFIX + "EXTENSION_EDIT_IMAGE";
    public static final String INTENT_ACTION_EXTENSION_UPLOAD = INTENT_PACKAGE_PREFIX + "EXTENSION_UPLOAD";
    public static final String INTENT_ACTION_EXTENSION_OPEN_STATUS = INTENT_PACKAGE_PREFIX + "EXTENSION_OPEN_STATUS";
    public static final String INTENT_ACTION_EXTENSION_OPEN_USER = INTENT_PACKAGE_PREFIX + "EXTENSION_OPEN_USER";
    public static final String INTENT_ACTION_EXTENSION_OPEN_USER_LIST = INTENT_PACKAGE_PREFIX
            + "EXTENSION_OPEN_USER_LIST";
    public static final String INTENT_ACTION_EXTENSION_COMPOSE = INTENT_PACKAGE_PREFIX + "EXTENSION_COMPOSE";
    public static final String INTENT_ACTION_EXTENSION_UPLOAD_MEDIA = INTENT_PACKAGE_PREFIX + "EXTENSION_UPLOAD_MEDIA";
    public static final String INTENT_ACTION_EXTENSION_SHORTEN_STATUS = INTENT_PACKAGE_PREFIX
            + "EXTENSION_SHORTEN_STATUS";
    public static final String INTENT_ACTION_EXTENSION_SYNC_TIMELINE = INTENT_PACKAGE_PREFIX
            + "EXTENSION_SYNC_TIMELINE";
    public static final String INTENT_ACTION_EXTENSION_SETTINGS = INTENT_PACKAGE_PREFIX + "EXTENSION_SETTINGS";

    public static final String INTENT_ACTION_UPDATE_STATUS = INTENT_PACKAGE_PREFIX + "UPDATE_STATUS";
    public static final String INTENT_ACTION_SEND_DIRECT_MESSAGE = INTENT_PACKAGE_PREFIX + "SEND_DIRECT_MESSAGE";
    public static final String INTENT_ACTION_PICK_ACTIVITY = INTENT_PACKAGE_PREFIX + "PICK_ACTIVITY";


    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_URI_ORIG = "uri_orig";
    public static final String EXTRA_MENTIONS = "mentions";
    public static final String EXTRA_ACCOUNT_ID = "account_id";
    public static final String EXTRA_ACCOUNT_IDS = "account_ids";
    public static final String EXTRA_PAGE = "page";
    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_QUERY = "query";
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_IDS = "user_ids";
    public static final String EXTRA_LIST_ID = "list_id";
    public static final String EXTRA_MAX_ID = "max_id";
    public static final String EXTRA_MAX_IDS = "max_ids";
    public static final String EXTRA_SINCE_ID = "since_id";
    public static final String EXTRA_SINCE_IDS = "since_ids";
    public static final String EXTRA_STATUS_ID = "status_id";
    public static final String EXTRA_SCREEN_NAME = "screen_name";
    public static final String EXTRA_SCREEN_NAMES = "screen_names";
    public static final String EXTRA_LIST_NAME = "list_name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_IN_REPLY_TO_ID = "in_reply_to_id";
    public static final String EXTRA_IN_REPLY_TO_NAME = "in_reply_to_name";
    public static final String EXTRA_IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_SUCCEED = "succeed";
    public static final String EXTRA_IDS = "ids";
    public static final String EXTRA_IS_SHARE = "is_share";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_STATUS_JSON = "status_json";
    public static final String EXTRA_STATUSES = "statuses";
    public static final String EXTRA_DRAFT = "draft";
    public static final String EXTRA_FAVORITED = "favorited";
    public static final String EXTRA_RETWEETED = "retweeted";
    public static final String EXTRA_FILENAME = "filename";
    public static final String EXTRA_FILE_SOURCE = "file_source";
    public static final String EXTRA_FILE_EXTENSIONS = "file_extensions";
    public static final String EXTRA_ITEMS_INSERTED = "items_inserted";
    public static final String EXTRA_INITIAL_TAB = "initial_tab";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_NOTIFICATION_ACCOUNT = "notification_account";
    public static final String EXTRA_FROM_NOTIFICATION = "from_notification";
    public static final String EXTRA_IS_PUBLIC = "is_public";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_USERS = "users";
    public static final String EXTRA_USER_LIST = "user_list";
    public static final String EXTRA_APPEND_TEXT = "append_text";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TEXT1 = "text1";
    public static final String EXTRA_TEXT2 = "text2";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_ARGUMENTS = "arguments";
    public static final String EXTRA_ICON = "icon";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_RESID = "resid";
    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String EXTRA_ATTACHED_IMAGE_TYPE = "attached_image_type";
    public static final String EXTRA_ACTIVATED_ONLY = "activated_only";
    public static final String EXTRA_HAS_RUNNING_TASK = "has_running_task";
    public static final String EXTRA_OAUTH_VERIFIER = "oauth_verifier";
    public static final String EXTRA_REQUEST_TOKEN = "request_token";
    public static final String EXTRA_REQUEST_TOKEN_SECRET = "request_token_secret";
    public static final String EXTRA_OMIT_INTENT_EXTRA = "omit_intent_extra";
    public static final String EXTRA_COMMAND = "command";
    public static final String EXTRA_WIDTH = "width";
    public static final String EXTRA_ALLOW_SELECT_NONE = "allow_select_none";
    public static final String EXTRA_SINGLE_SELECTION = "single_selection";
    public static final String EXTRA_OAUTH_ONLY = "oauth_only";
    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_NEXT_CURSOR = "next_cursor";
    public static final String EXTRA_PREV_CURSOR = "prev_cursor";
    public static final String EXTRA_EXTRA_INTENT = "extra_intent";
    public static final String EXTRA_IS_MY_ACCOUNT = "is_my_account";
    public static final String EXTRA_TAB_TYPE = "tab_type";
    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_ACTIVITY_SCREENSHOT_ID = "activity_screenshot_id";
    public static final String EXTRA_COLOR = "color";
    public static final String EXTRA_ALPHA_SLIDER = "alpha_slider";
    public static final String EXTRA_OPEN_ACCOUNTS_DRAWER = "open_accounts_drawer";
    public static final String EXTRA_RECIPIENT_ID = "recipient_id";
    public static final String EXTRA_OFFICIAL_KEY_ONLY = "official_key_only";
    public static final String EXTRA_SEARCH_ID = "search_id";
    public static final String EXTRA_CLEAR_BUTTON = "clear_button";
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_FLAGS = "flags";
    public static final String EXTRA_INTENT = "intent";
    public static final String EXTRA_BLACKLIST = "blacklist";
    public static final String EXTRA_MEDIAS = "medias";
    public static final String EXTRA_EXTRAS = "extras";
    public static final String EXTRA_MY_FOLLOWING_ONLY = "my_following_only";
    public static final String EXTRA_RESTART_ACTIVITY = "restart_activity";

    public static final String BROADCAST_HOME_TIMELINE_REFRESHED = INTENT_PACKAGE_PREFIX + "HOME_TIMELINE_REFRESHED";
    public static final String BROADCAST_MENTIONS_REFRESHED = INTENT_PACKAGE_PREFIX + "MENTIONS_REFRESHED";
    public static final String BROADCAST_TASK_STATE_CHANGED = INTENT_PACKAGE_PREFIX + "TASK_STATE_CHANGED";
    public static final String BROADCAST_NOTIFICATION_DELETED = INTENT_PACKAGE_PREFIX + "NOTIFICATION_DELETED";
    public static final String BROADCAST_FRIENDSHIP_CHANGED = INTENT_PACKAGE_PREFIX + "FRIENDSHIP_CHANGED";
    public static final String BROADCAST_BLOCKSTATE_CHANGED = INTENT_PACKAGE_PREFIX + "BLOCKSTATE_CHANGED";
    public static final String BROADCAST_PROFILE_UPDATED = INTENT_PACKAGE_PREFIX + "PROFILE_UPDATED";
    public static final String BROADCAST_PROFILE_IMAGE_UPDATED = INTENT_PACKAGE_PREFIX + "PROFILE_IMAGE_UPDATED";
    public static final String BROADCAST_PROFILE_BANNER_UPDATED = INTENT_PACKAGE_PREFIX + "PROFILE_BANNER_UPDATED";
    public static final String BROADCAST_USER_LIST_DETAILS_UPDATED = INTENT_PACKAGE_PREFIX
            + "USER_LIST_DETAILS_UPDATED";
    public static final String BROADCAST_FRIENDSHIP_ACCEPTED = INTENT_PACKAGE_PREFIX + "FRIENDSHIP_ACCEPTED";
    public static final String BROADCAST_FRIENDSHIP_DENIED = INTENT_PACKAGE_PREFIX + "FRIENDSHIP_DENIED";

    public static final String BROADCAST_FAVORITE_CHANGED = INTENT_PACKAGE_PREFIX + "FAVORITE_CHANGED";
    public static final String BROADCAST_RETWEET_CHANGED = INTENT_PACKAGE_PREFIX + "RETWEET_CHANGED";
    public static final String BROADCAST_STATUS_UPDATED         = INTENT_PACKAGE_PREFIX + "STATUS_UPDATED";
    public static final String BROADCAST_DIRECT_MESSAGE_SENT    = INTENT_PACKAGE_PREFIX + "DIRECT_MESSAGE_SENT";

    public static final String BROADCAST_STATUS_DESTROYED = INTENT_PACKAGE_PREFIX + "STATUS_DESTROYED";
    public static final String BROADCAST_USER_LIST_MEMBERS_DELETED = INTENT_PACKAGE_PREFIX + "USER_LIST_MEMBER_DELETED";
    public static final String BROADCAST_USER_LIST_MEMBERS_ADDED = INTENT_PACKAGE_PREFIX + "USER_LIST_MEMBER_ADDED";
    public static final String BROADCAST_USER_LIST_SUBSCRIBED = INTENT_PACKAGE_PREFIX + "USER_LIST_SUBSRCIBED";
    public static final String BROADCAST_USER_LIST_UNSUBSCRIBED = INTENT_PACKAGE_PREFIX + "USER_LIST_UNSUBSCRIBED";
    public static final String BROADCAST_USER_LIST_CREATED = INTENT_PACKAGE_PREFIX + "USER_LIST_CREATED";
    public static final String BROADCAST_USER_LIST_DELETED = INTENT_PACKAGE_PREFIX + "USER_LIST_DELETED";
    public static final String BROADCAST_FILTERS_UPDATED = INTENT_PACKAGE_PREFIX + "FILTERS_UPDATED";
    public static final String BROADCAST_REFRESH_HOME_TIMELINE = INTENT_PACKAGE_PREFIX + "REFRESH_HOME_TIMELINE";
    public static final String BROADCAST_REFRESH_MENTIONS = INTENT_PACKAGE_PREFIX + "REFRESH_MENTIONS";
    public static final String BROADCAST_REFRESH_DIRECT_MESSAGES = INTENT_PACKAGE_PREFIX + "REFRESH_DIRECT_MESSAGES";
    public static final String BROADCAST_REFRESH_TRENDS = INTENT_PACKAGE_PREFIX + "REFRESH_TRENDS";
    public static final String BROADCAST_RESCHEDULE_HOME_TIMELINE_REFRESHING = INTENT_PACKAGE_PREFIX
            + "RESCHEDULE_HOME_TIMELINE_REFRESHING";
    public static final String BROADCAST_RESCHEDULE_MENTIONS_REFRESHING = INTENT_PACKAGE_PREFIX
            + "RESCHEDULE_MENTIONS_REFRESHING";
    public static final String BROADCAST_RESCHEDULE_DIRECT_MESSAGES_REFRESHING = INTENT_PACKAGE_PREFIX
            + "RESCHEDULE_DIRECT_MESSAGES_REFRESHING";
    public static final String BROADCAST_RESCHEDULE_TRENDS_REFRESHING = INTENT_PACKAGE_PREFIX
            + "RESCHEDULE_TRENDS_REFRESHING";
    public static final String BROADCAST_MULTI_BLOCKSTATE_CHANGED = INTENT_PACKAGE_PREFIX + "MULTI_BLOCKSTATE_CHANGED";
    public static final String BROADCAST_MULTI_MUTESTATE_CHANGED = INTENT_PACKAGE_PREFIX + "MULTI_MUTESTATE_CHANGED";
    public static final String BROADCAST_HOME_ACTIVITY_ONCREATE = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONCREATE";
    public static final String BROADCAST_HOME_ACTIVITY_ONSTART = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONSTART";
    public static final String BROADCAST_HOME_ACTIVITY_ONRESUME = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONRESUME";
    public static final String BROADCAST_HOME_ACTIVITY_ONPAUSE = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONPAUSE";
    public static final String BROADCAST_HOME_ACTIVITY_ONSTOP = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONSTOP";
    public static final String BROADCAST_HOME_ACTIVITY_ONDESTROY = INTENT_PACKAGE_PREFIX + "HOME_ACTIVITY_ONDESTROY";
    public static final String BROADCAST_UNREAD_COUNT_UPDATED = INTENT_PACKAGE_PREFIX + "UNREAD_COUNT_UPDATED";
    public static final String BROADCAST_DATABASE_READY = INTENT_PACKAGE_PREFIX + "DATABASE_READY";

    /**
     */

    /**
     * BEGIN MISC
     */
    public static final String EASTER_EGG_TRIGGER_TEXT = "\u718A\u5B69\u5B50";
    public static final String EASTER_EGG_RESTORE_TEXT_PART1 = "\u5927\u738B";
    public static final String EASTER_EGG_RESTORE_TEXT_PART2 = "\u5C0F\u7684";
    public static final String EASTER_EGG_RESTORE_TEXT_PART3 = "\u77E5\u9519";
    /**
     * END MISC
     */

}
