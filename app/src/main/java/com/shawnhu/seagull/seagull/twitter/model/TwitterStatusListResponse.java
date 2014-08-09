package com.shawnhu.seagull.seagull.twitter.model;

import java.util.List;

import twitter4j.Status;

/**
* Created by shawnhu on 8/9/14.
*/
public final class TwitterStatusListResponse extends TwitterListResponse {

    public final boolean truncated;

    public TwitterStatusListResponse(final long account_id, final Exception exception) {
        this(account_id, -1, -1, null, false, exception);
    }

    public TwitterStatusListResponse(final long account_id, final List<Status> list) {
        this(account_id, -1, -1, list, false, null);
    }

    public TwitterStatusListResponse(final long account_id, final long max_id, final long since_id,
                                     final int load_item_limit, final List<Status> list, final boolean truncated) {
        this(account_id, max_id, since_id, list, truncated, null);
    }

    public TwitterStatusListResponse(final long account_id, final long max_id, final long since_id, final List<Status> list,
                                     final boolean truncated, final Exception exception) {
        super(account_id, max_id, since_id, list, exception);
        this.truncated = truncated;
    }

}
