package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Bundle;

import java.util.List;

public class ListResponse<Data> extends Response<List<Data>> {

    public ListResponse(final List<Data> list, final Exception exception) {
        super(list, exception);
    }

    public ListResponse(final List<Data> list, final Exception exception, final Bundle extras) {
        super(list, exception, extras);
    }

    public List<Data> getList() {
        return getData();
    }
}
