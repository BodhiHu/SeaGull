package com.shawnhu.seagull.seagull.twitter.utils;

import com.shawnhu.seagull.utils.ParseUtils;

import org.apache.http.NameValuePair;

public class NameValuePairImpl implements NameValuePair {

    private final String name, value;

    public NameValuePairImpl(final String name, final Object value) {
        this.name = name;
        this.value = ParseUtils.parseString(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

}
