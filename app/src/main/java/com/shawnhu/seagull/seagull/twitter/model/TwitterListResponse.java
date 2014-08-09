package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Bundle;

import java.util.List;

public class TwitterListResponse<Data> extends TwitterResponse<List<Data>> {

    public TwitterListResponse(final List<Data> list, final Exception exception) {
        super(list, exception);
    }

    public TwitterListResponse(final List<Data> list, final Exception exception, final Bundle extras) {
        super(list, exception, extras);
    }

    public List<Data> getList() {
        return getData();
    }
}
