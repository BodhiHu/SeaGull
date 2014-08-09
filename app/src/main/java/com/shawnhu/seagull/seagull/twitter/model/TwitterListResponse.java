package com.shawnhu.seagull.seagull.twitter.model;

import java.util.List;

/**
* Created by shawnhu on 8/9/14.
*/
public class TwitterListResponse<Data> extends ListResponse<Data> {

    public final long account_id, max_id, since_id;

    public TwitterListResponse(final long account_id, final Exception exception) {
        this(account_id, -1, -1, null, exception);
    }

    public TwitterListResponse(final long account_id, final long max_id, final long since_id, final List<Data> list) {
        this(account_id, max_id, since_id, list, null);
    }

    public TwitterListResponse(final long account_id, final long max_id, final long since_id, final List<Data> list,
                               final Exception exception) {
        super(list, exception);
        this.account_id = account_id;
        this.max_id = max_id;
        this.since_id = since_id;
    }

}
