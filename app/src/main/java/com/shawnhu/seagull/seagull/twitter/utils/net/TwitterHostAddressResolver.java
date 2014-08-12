package com.shawnhu.seagull.seagull.twitter.utils.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.shawnhu.seagull.seagull.twitter.utils.HostsFileParser;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;

import org.apache.http.conn.util.InetAddressUtilsHC4;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;

import twitter4j.http.HostAddressResolver;

import static android.text.TextUtils.isEmpty;

public class TwitterHostAddressResolver implements HostAddressResolver {

    private static final String     TAG = "TwitterHostAddressResolver";

    protected static final String   HOST_MAPPING_PREFERENCES_NAME = "host_mapping";
    protected static final String   DEFAULT_DNS_SERVER_ADDRESS = "8.8.8.8";

    private final SharedPreferences mHostMapping;
    private final HostsFileParser   mSystemHosts = new HostsFileParser();
    private final HostCache         mHostCache = new HostCache(512);
    private final boolean           mLocalMappingOnly;
    private final String            mDnsAddress;
    protected boolean               mUseTcpDnsQuery = false;

    private       Resolver          mDns;

    public TwitterHostAddressResolver(final Context context) {
        this(context, false);
    }

    public TwitterHostAddressResolver(final Context context, final boolean local_only) {
        mHostMapping = context.getSharedPreferences(HOST_MAPPING_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mDnsAddress = DEFAULT_DNS_SERVER_ADDRESS;
        mLocalMappingOnly = local_only;
    }

    public synchronized void removeCachedHost(final String host) {
        mHostCache.remove(host);
    }

    public void setUseTcpDnsQuery(boolean useTCP) {
        mUseTcpDnsQuery = useTCP;
    }

    @Override
    public String resolve(final String host) throws IOException {
        if (host == null) return null;
        if (isValidIpAddress(host)) return null;
        // First, I'll try to load address cached.
        if (mHostCache.containsKey(host)) {
            if (Utils.isDebugBuild()) {
                Log.d(TAG, "Got cached address " + mHostCache.get(host) + " for host " + host);
            }
            return mHostCache.get(host);
        }
        // Then I'll try to load from custom host mapping.
        // Stupid way to find top domain, but really fast.
        if (mHostMapping.contains(host)) {
            final String mappedAddr = mHostMapping.getString(host, null);
            mHostCache.put(host, mappedAddr);
            if (Utils.isDebugBuild()) {
                Log.d(TAG, "Got mapped address " + mappedAddr + " for host " + host);
            }
            return mappedAddr;
        }
        mSystemHosts.reloadIfNeeded();
        if (mSystemHosts.contains(host)) {
            final String hostAddr = mSystemHosts.getAddress(host);
            mHostCache.put(host, hostAddr);
            if (Utils.isDebugBuild()) {
                Log.d(TAG, "Got mapped address " + hostAddr + " for host " + host);
            }
            return hostAddr;
        }
        final String customMappedHost = findHost(host);
        if (customMappedHost != null) {
            mHostCache.put(host, customMappedHost);
            if (Utils.isDebugBuild()) {
                Log.d(TAG, "Got mapped address " + customMappedHost + " for host " + host);
            }
            return customMappedHost;
        }
        initDns();
        // Use TCP DNS Query if enabled.
        if (mDns != null && mUseTcpDnsQuery) {
            final Name name = new Name(host);
            final Record query = Record.newRecord(name, Type.A, DClass.IN);
            if (query == null) return host;
            final Message response;
            try {
                response = mDns.send(Message.newQuery(query));
            } catch (final IOException e) {
                return host;
            }
            if (response == null) return host;
            final Record[] records = response.getSectionArray(Section.ANSWER);
            if (records == null || records.length < 1) throw new IOException("Could not find " + host);
            String hostAddr = null;
            // Test each IP address resolved.
            for (final Record record : records) {
                if (record instanceof ARecord) {
                    final InetAddress ipv4Addr = ((ARecord) record).getAddress();
                    if (ipv4Addr.isReachable(300)) {
                        hostAddr = ipv4Addr.getHostAddress();
                    }
                } else if (record instanceof AAAARecord) {
                    final InetAddress ipv6Addr = ((AAAARecord) record).getAddress();
                    if (ipv6Addr.isReachable(300)) {
                        hostAddr = ipv6Addr.getHostAddress();
                    }
                }
                if (hostAddr != null) {
                    mHostCache.put(host, hostAddr);
                    if (Utils.isDebugBuild()) {
                        Log.d(TAG, "Resolved address " + hostAddr + " for host " + host);
                    }
                    return hostAddr;
                }
            }
            // No address is reachable, but I believe the IP is correct.
            final Record record = records[0];
            if (record instanceof ARecord) {
                final InetAddress ipv4Addr = ((ARecord) record).getAddress();
                hostAddr = ipv4Addr.getHostAddress();
            } else if (record instanceof AAAARecord) {
                final InetAddress ipv6Addr = ((AAAARecord) record).getAddress();
                hostAddr = ipv6Addr.getHostAddress();
            } else if (record instanceof CNAMERecord) return resolve(((CNAMERecord) record).getTarget().toString());
            mHostCache.put(host, hostAddr);
            if (Utils.isDebugBuild()) {
                Log.d(TAG, "Resolved address " + hostAddr + " for host " + host);
            }
            return hostAddr;
        }
        if (Utils.isDebugBuild()) {
            Log.w(TAG, "Resolve address " + host + " failed, using original host");
        }
        return host;
    }

    private String findHost(final String host) {
        for (final String rule : mHostMapping.getAll().keySet()) {
            if (hostMatches(host, rule)) return mHostMapping.getString(rule, null);
        }
        return null;
    }

    private void initDns() throws IOException {
        if (mDns != null) return;
        mDns = mLocalMappingOnly ? null : new SimpleResolver(mDnsAddress);
        if (mDns != null) {
            mDns.setTCP(true);
        }
    }

    private static boolean hostMatches(final String host, final String rule) {
        if (rule == null || host == null) return false;
        if (rule.startsWith(".")) return host.toLowerCase().endsWith(rule.toLowerCase());
        return host.equalsIgnoreCase(rule);
    }

    private static boolean isValidIpAddress(final String address) {
        if (isEmpty(address)) return false;
        return InetAddressUtilsHC4.isIPv4Address(address) || InetAddressUtilsHC4.isIPv6Address(address);
    }

    private static class HostCache extends LinkedHashMap<String, String> {

        private static final long serialVersionUID = -9216545511009449147L;

        HostCache(final int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public String put(final String key, final String value) {
            if (value == null) return value;
            return super.put(key, value);
        }
    }
}
