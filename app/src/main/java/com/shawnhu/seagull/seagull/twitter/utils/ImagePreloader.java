package com.shawnhu.seagull.seagull.twitter.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
/**
 * @author mariotaku
 */
public class ImagePreloader {

    public static final String LOGTAG = "ImagePreloader";

    private final Context mContext;
    private final Handler mHandler;
    private final DiskCache mDiskCache;
    private final ImageLoader mImageLoader;

    private boolean wifiOnly = true;

    public ImagePreloader(final Context context, final ImageLoader loader) {
        mContext = context;
        mImageLoader = loader;
        mDiskCache = loader.getDiskCache();
        mHandler = new Handler();
    }

    public File getCachedImageFile(final String url) {
        if (url == null) return null;
        final File cache = mDiskCache.get(url);
        if (ImageValidator.checkImageValidity(cache))
            return cache;
        else {
            preloadImage(url);
        }
        return null;
    }

    public void preloadImage(final String url) {
        if (TextUtils.isEmpty(url)) return;
        if (!Utils.isOnWifi(mContext) && wifiOnly) return;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mImageLoader.loadImage(url, null);
            }

        });
    }

}
