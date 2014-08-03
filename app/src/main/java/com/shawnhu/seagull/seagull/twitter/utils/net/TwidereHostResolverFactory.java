package com.shawnhu.seagull.seagull.twitter.utils.net;

import com.shawnhu.seagull.seagull.twitter.TwitterManager;

import twitter4j.http.HostAddressResolver;
import twitter4j.http.HostAddressResolverFactory;
import twitter4j.http.HttpClientConfiguration;

public class TwidereHostResolverFactory implements HostAddressResolverFactory {

	public TwidereHostResolverFactory() {
	}

	@Override
	public HostAddressResolver getInstance(final HttpClientConfiguration conf) {
		return TwitterManager.getHostAddressResolver();
	}

}
