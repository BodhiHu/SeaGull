package com.shawnhu.seagull.seagull.twitter.model;

import java.util.List;

import twitter4j.DirectMessage;

/**
* Created by shawnhu on 8/9/14.
*/
public final class TwitterMessageListResponse extends TwitterListResponse {

    public final boolean truncated;

    public TwitterMessageListResponse(final long account_id, final Exception exception) {
        this(account_id, -1, -1, null, false, exception);
    }

    public TwitterMessageListResponse(final long account_id, final List<DirectMessage> list) {
        this(account_id, -1, -1, list, false, null);
    }

    public TwitterMessageListResponse(final long account_id, final long max_id, final long since_id,
                                      final int load_item_limit, final List<DirectMessage> list, final boolean truncated) {
        this(account_id, max_id, since_id, list, truncated, null);
    }

    public TwitterMessageListResponse(final long account_id, final long max_id, final long since_id,
                                      final List<DirectMessage> list, final boolean truncated, final Exception exception) {
        super(account_id, max_id, since_id, list, exception);
        this.truncated = truncated;
    }

}
