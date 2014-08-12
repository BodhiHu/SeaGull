package com.shawnhu.seagull.seagull.twitter.utils.net.ssl;

import android.content.Context;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;

public class TwitterHostnameVerifier extends AbstractCheckSignatureVerifier {

    private final Context context;
    private final boolean ignoreSSLErrors;

    public TwitterHostnameVerifier(final Context context, final boolean ignoreSSLErrors)
            throws NoSuchAlgorithmException, KeyStoreException {
        this.context = context;
        this.ignoreSSLErrors = ignoreSSLErrors;
    }

    @Override
    public void verify(final String host, final String[] cns, final String[] subjectAlts, final X509Certificate cert)
            throws SSLException {
        if (ignoreSSLErrors) return;
        if (!checkCert(cert)) throw new SSLException(String.format("Untrusted cert %s", cert));
        if (!verify(host, cns, subjectAlts, false)) throw new SSLException(String.format("Unable to verify %s", host));
    }

    private boolean checkCert(final X509Certificate cert) {
        return true;
    }

}
