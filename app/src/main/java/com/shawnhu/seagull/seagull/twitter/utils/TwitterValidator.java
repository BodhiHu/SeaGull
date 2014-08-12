package com.shawnhu.seagull.seagull.twitter.utils;

import android.content.Context;
import android.text.TextUtils;

import com.twitter.Validator;

public class TwitterValidator {

    private final int mMaxTweetLength;
    private final Validator mValidator;

    public TwitterValidator(final Context context) {
        mValidator = new Validator();
            mMaxTweetLength = Validator.MAX_TWEET_LENGTH;
    }

    public int getMaxTweetLength() {
        return mMaxTweetLength;
    }

    public int getTweetLength(final String text) {
        return mValidator.getTweetLength(text);
    }

    public boolean isValidTweet(final String text) {
        return !TextUtils.isEmpty(text) && getTweetLength(text) <= getMaxTweetLength();
    }

}
