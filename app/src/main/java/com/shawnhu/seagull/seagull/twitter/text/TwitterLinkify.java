package com.shawnhu.seagull.seagull.twitter.text;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import com.shawnhu.seagull.seagull.twitter.utils.MediaPreviewUtils;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.utils.ParseUtils;
import com.twitter.Extractor;
import com.twitter.Extractor.Entity;
import com.twitter.Regex;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TwitterLinkify {
    private static final String TAG = "TwitterLinkify";

    public static final int LINK_TYPE_MENTION = 1;
    public static final int LINK_TYPE_HASHTAG = 2;
    public static final int LINK_TYPE_LINK    = 4;
    public static final int LINK_TYPE_LIST    = 6;
    public static final int LINK_TYPE_CASHTAG = 7;
    public static final int LINK_TYPE_USER_ID = 8;
    public static final int LINK_TYPE_STATUS  = 9;

    public static final int[] ALL_LINK_TYPES  = new int[] {
                    LINK_TYPE_LINK,
                    LINK_TYPE_MENTION,
                    LINK_TYPE_HASHTAG,
                    LINK_TYPE_STATUS,
                    LINK_TYPE_CASHTAG,
    };

    public static final String AVAILABLE_URL_SCHEME_PREFIX                       =
            "(https?:\\/\\/)?";
    public static final String TWITTER_PROFILE_IMAGES_AVAILABLE_SIZES            =
            "(bigger|normal|mini|reasonably_small)";
    public static final String STRING_PATTERN_TWITTER_PROFILE_IMAGES_NO_SCHEME   =
            "(twimg[\\d\\w\\-]+\\.akamaihd\\.net|[\\w\\d]+\\.twimg\\.com)\\/profile_images\\/([\\d\\w\\-_]+)\\/([\\d\\w\\-_]+)_"
            + TWITTER_PROFILE_IMAGES_AVAILABLE_SIZES
            + "(\\.?" + MediaPreviewUtils.AVAILABLE_IMAGE_SUFFIX + ")?";
    public static final String STRING_PATTERN_TWITTER_STATUS_NO_SCHEME           =
            "((mobile|www)\\.)?twitter\\.com\\/(?:#!\\/)?(\\w+)\\/status(es)?\\/(\\d+)(\\/photo\\/\\d)?\\/?";
    public static final String STRING_PATTERN_TWITTER_LIST_NO_SCHEME             =
            "((mobile|www)\\.)?twitter\\.com\\/(?:#!\\/)?(\\w+)\\/lists\\/(.+)\\/?";

    public static final String STRING_PATTERN_TWITTER_PROFILE_IMAGES             =
            AVAILABLE_URL_SCHEME_PREFIX + STRING_PATTERN_TWITTER_PROFILE_IMAGES_NO_SCHEME;
    public static final String STRING_PATTERN_TWITTER_STATUS                     =
            AVAILABLE_URL_SCHEME_PREFIX + STRING_PATTERN_TWITTER_STATUS_NO_SCHEME;
    public static final String STRING_PATTERN_TWITTER_LIST                       =
            AVAILABLE_URL_SCHEME_PREFIX + STRING_PATTERN_TWITTER_LIST_NO_SCHEME;

    public static final Pattern PATTERN_TWITTER_PROFILE_IMAGES                   =
            Pattern.compile(STRING_PATTERN_TWITTER_PROFILE_IMAGES,  Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_TWITTER_STATUS                           =
            Pattern.compile(STRING_PATTERN_TWITTER_STATUS,          Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTERN_TWITTER_LIST                             =
            Pattern.compile(STRING_PATTERN_TWITTER_LIST,            Pattern.CASE_INSENSITIVE);

    protected static final int GROUP_ID_TWITTER_STATUS_SCREEN_NAME  = 4;
    protected static final int GROUP_ID_TWITTER_STATUS_STATUS_ID    = 6;
    protected static final int GROUP_ID_TWITTER_LIST_SCREEN_NAME    = 4;
    protected static final int GROUP_ID_TWITTER_LIST_LIST_NAME      = 5;

    protected static final Extractor mExtractor = new Extractor();

    protected static TwitterURLSpan.OnLinkClickListener mOnLinkClickListener =
            new TwitterURLSpan.OnLinkClickListener() {
                @Override
                public void onLinkClick(Context context,
                                        String link, String orig, long account_id, int type,
                                        boolean sensitive) {
                    defaultLinkHandler(context, link, orig, account_id, type, sensitive);
                }
            };

    private TwitterLinkify() {}

    static public final void applyAllLinks(
            final TextView view, final long account_id,
            final boolean sensitive)
    {
        applyAllLinks(view, account_id, sensitive, mOnLinkClickListener);
    }

    static public final void applyAllLinks(
            final TextView view, final long account_id,
            final boolean sensitive,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        view.setMovementMethod(LinkMovementMethod.getInstance());
        final SpannableString string = SpannableString.valueOf(view.getText());
        for (final int type : ALL_LINK_TYPES) {
            addLinks(string, account_id, type, sensitive, listener);
        }
        view.setText(string);
        addLinkMovementMethod(view);
    }

    static public final void applyUserProfileLink(
            final TextView view, final long account_id, final long user_id,
            final String screen_name)
    {
        applyUserProfileLink(view, account_id, user_id, screen_name, mOnLinkClickListener);
    }

    static public final void applyUserProfileLinkNoHighlight(
            final TextView view, final long account_id, final long user_id,
            final String screen_name)
    {
        applyUserProfileLink(view, account_id, user_id, screen_name, mOnLinkClickListener);
    }

    static public final void applyUserProfileLink(
            final TextView view, final long account_id, final long user_id,
            final String screen_name,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        view.setMovementMethod(LinkMovementMethod.getInstance());
        final SpannableString string = SpannableString.valueOf(view.getText());
        final URLSpan[] spans = string.getSpans(0, string.length(), URLSpan.class);
        for (final URLSpan span : spans) {
            string.removeSpan(span);
        }
        if (user_id > 0) {
            applyLink(String.valueOf(user_id), 0, string.length(), string, account_id, LINK_TYPE_USER_ID, false, listener);
        } else if (screen_name != null) {
            applyLink(screen_name, 0, string.length(), string, account_id, LINK_TYPE_MENTION, false, listener);
        }
        view.setText(string);
        addLinkMovementMethod(view);
    }

    static private final boolean addCashtagLinks(
            final Spannable spannable, final long account_id,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        boolean hasMatches = false;
        for (final Entity entity : mExtractor.extractCashtagsWithIndices(spannable.toString())) {
            final int start = entity.getStart();
            final int end = entity.getEnd();
            applyLink(entity.getValue(), start, end, spannable, account_id, LINK_TYPE_CASHTAG, false, listener);
            hasMatches = true;
        }
        return hasMatches;
    }

    static private final boolean addHashtagLinks(
            final Spannable spannable, final long account_id,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        boolean hasMatches = false;
        for (final Entity entity : mExtractor.extractHashtagsWithIndices(spannable.toString())) {
            final int start = entity.getStart();
            final int end = entity.getEnd();
            applyLink(entity.getValue(), start, end, spannable, account_id, LINK_TYPE_HASHTAG, false, listener);
            hasMatches = true;
        }
        return hasMatches;
    }

    static private final void addLinks(
            final SpannableString string, final long accountId, final int type,
            final boolean sensitive,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        switch (type) {
            case LINK_TYPE_MENTION: {
                addMentionOrListLinks(string, accountId, listener);
                break;
            }
            case LINK_TYPE_HASHTAG: {
                addHashtagLinks(string, accountId, listener);
                break;
            }
            case LINK_TYPE_LINK: {
                final URLSpan[] spans = string.getSpans(0, string.length(), URLSpan.class);
                for (final URLSpan span : spans) {
                    final int start = string.getSpanStart(span);
                    final int end = string.getSpanEnd(span);
                    if (start < 0 || end > string.length() || start > end) {
                        continue;
                    }
                    string.removeSpan(span);
                    applyLink(span.getURL(), start, end, string, accountId, LINK_TYPE_LINK, sensitive, listener);
                }
                final List<Entity> urls = mExtractor.extractURLsWithIndices(ParseUtils.parseString(string));
                for (final Entity entity : urls) {
                    final int start = entity.getStart(), end = entity.getEnd();
                    if (entity.getType() != Entity.Type.URL
                            || string.getSpans(start, end, URLSpan.class).length > 0) {
                        continue;
                    }
                    applyLink(entity.getValue(), start, end, string, accountId, LINK_TYPE_LINK, sensitive, listener);
                }
                break;
            }
            case LINK_TYPE_STATUS: {
                final URLSpan[] spans = string.getSpans(0, string.length(), URLSpan.class);
                for (final URLSpan span : spans) {
                    final Matcher matcher = PATTERN_TWITTER_STATUS.matcher(span.getURL());
                    if (matcher.matches()) {
                        final int start = string.getSpanStart(span);
                        final int end = string.getSpanEnd(span);
                        final String url = Utils.matcherGroup(matcher, GROUP_ID_TWITTER_STATUS_STATUS_ID);
                        string.removeSpan(span);
                        applyLink(url, start, end, string, accountId, LINK_TYPE_STATUS, sensitive, listener);
                    }
                }
                break;
            }
            case LINK_TYPE_CASHTAG: {
                addCashtagLinks(string, accountId, listener);
                break;
            }
            default: {
                return;
            }

        }
    }

    static private final boolean addMentionOrListLinks(
            final Spannable spannable, final long accountId,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        boolean hasMatches = false;
        // Extract lists from status text
        final Matcher matcher = Regex.VALID_MENTION_OR_LIST.matcher(spannable);
        while (matcher.find()) {
            final int start = Utils.matcherStart(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_AT);
            final int username_end = Utils.matcherEnd(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_USERNAME);
            final int listStart = Utils.matcherStart(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_LIST);
            final int listEnd = Utils.matcherEnd(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_LIST);
            final String username = Utils.matcherGroup(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_USERNAME);
            final String list = Utils.matcherGroup(matcher, Regex.VALID_MENTION_OR_LIST_GROUP_LIST);
            applyLink(username, start, username_end, spannable, accountId, LINK_TYPE_MENTION, false, listener);
            if (listStart >= 0 && listEnd >= 0) {
                applyLink(String.format("%s/%s", username, list.substring(list.startsWith("/") ? 1 : 0)), listStart,
                        listEnd, spannable, accountId, LINK_TYPE_LIST, false, listener);
            }
            hasMatches = true;
        }
        // Extract lists from twitter.com links.
        final URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (final URLSpan span : spans) {
            final Matcher m = PATTERN_TWITTER_LIST.matcher(span.getURL());
            if (m.matches()) {
                final int start = spannable.getSpanStart(span);
                final int end = spannable.getSpanEnd(span);
                final String screenName = Utils.matcherGroup(m, GROUP_ID_TWITTER_LIST_SCREEN_NAME);
                final String listName = Utils.matcherGroup(m, GROUP_ID_TWITTER_LIST_LIST_NAME);
                spannable.removeSpan(span);
                applyLink(screenName + "/" + listName, start, end, spannable, accountId, LINK_TYPE_LIST, false,
                        listener);
                hasMatches = true;
            }
        }
        return hasMatches;
    }

    static private final void applyLink(
            final String url,
            final int start, final int end,
            final Spannable text,
            final long accountId, final int type, final boolean sensitive,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        applyLink(url, null, start, end, text, accountId, type, sensitive, listener);
    }

    static private final void applyLink(
            final String url,
            final String orig, final int start, final int end,
            final Spannable text,
            final long accountId, final int type, final boolean sensitive,
            final TwitterURLSpan.OnLinkClickListener listener)
    {
        final TwitterURLSpan span = new TwitterURLSpan(url, orig, accountId, type, sensitive, listener);
        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    static private final void addLinkMovementMethod(
            final TextView t)
    {
        final MovementMethod m = t.getMovementMethod();
        if (m == null || !(m instanceof LinkMovementMethod)) {
            if (t.getLinksClickable()) {
                t.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    static protected void defaultLinkHandler(
            Context context,
            final String link, final String orig,
            final long account_id, final int type,
            final boolean sensitive)
    {
        //TODO: handle links
        switch (type) {
            case TwitterLinkify.LINK_TYPE_MENTION: {
                break;
            }
            case TwitterLinkify.LINK_TYPE_HASHTAG: {
                break;
            }
            case TwitterLinkify.LINK_TYPE_LINK: {
                if (MediaPreviewUtils.isLinkSupported(link)) {
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TwitterLinkify.LINK_TYPE_LIST: {
                final String[] mention_list = link.split("\\/");
                if (mention_list == null || mention_list.length != 2) {
                    break;
                }
                break;
            }
            case TwitterLinkify.LINK_TYPE_CASHTAG: {
                break;
            }
            case TwitterLinkify.LINK_TYPE_USER_ID: {
                break;
            }
            case TwitterLinkify.LINK_TYPE_STATUS: {
                break;
            }
        }
    }
}
