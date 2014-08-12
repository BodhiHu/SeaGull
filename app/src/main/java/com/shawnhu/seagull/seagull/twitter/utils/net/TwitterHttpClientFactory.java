package com.shawnhu.seagull.seagull.twitter.utils.net;

import android.content.Context;

import twitter4j.http.HttpClient;
import twitter4j.http.HttpClientConfiguration;
import twitter4j.http.HttpClientFactory;

public class TwitterHttpClientFactory implements HttpClientFactory {

    private final Context context;

    public TwitterHttpClientFactory(final Context context) {
        this.context = context;
    }

    @Override
    public HttpClient getInstance(final HttpClientConfiguration conf) {
        return new TwitterHttpClientImpl(context, conf);
    }

}
