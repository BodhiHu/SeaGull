package com.shawnhu.seagull.seagull.twitter.utils.net;

import android.util.Log;

import com.shawnhu.seagull.seagull.twitter.TwitterManager;

import twitter4j.http.HostAddressResolver;
import twitter4j.http.HostAddressResolverFactory;
import twitter4j.http.HttpClientConfiguration;

public class TwitterHostResolverFactory implements HostAddressResolverFactory {

	public TwitterHostResolverFactory() {
	}

	@Override
	public HostAddressResolver getInstance(final HttpClientConfiguration conf) {
        try {
            return TwitterManager.getInstance().getHostAddressResolver();
        } catch(NullPointerException e) {
            Log.e("TwitterHostResolverFactory", e.toString());
            e.printStackTrace();
        }

        return null;
	}

}
