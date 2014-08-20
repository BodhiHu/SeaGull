package com.shawnhu.seagull.seagull.twitter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.utils.imageloader.AccountExtra;
import com.shawnhu.seagull.seagull.twitter.utils.imageloader.TwitterImageDownloader;
import com.shawnhu.seagull.seagull.twitter.utils.imageloader.URLFileNameGenerator;
import com.shawnhu.seagull.utils.ImageLoadingHandler;

import java.io.File;


public class ImageLoaderWrapper {
    private ImageLoaderWrapper() {}

    static private ImageLoaderWrapper sInstance;
    static private Context            sContext;
    static private ImageLoader        sImageLoader;

    static public ImageLoaderWrapper getInstance(Context context) {
        if (sInstance == null) {
            sContext = context;
            sImageLoader = ImageLoader.getInstance();
            sImageLoader.init(new ImageLoaderConfiguration.Builder(context)
                            .threadPriority(Thread.NORM_PRIORITY - 2)
                            .denyCacheImageMultipleSizesInMemory()
                            .tasksProcessingOrder(QueueProcessingType.LIFO)
                            //.memoryCache(new ImageMemoryCache(41));
                            .diskCache(getImageLoaderDiskCache())
                            .imageDownloader(new TwitterImageDownloader(context, false))
                            .build()
            );

            L.writeDebugLogs(Utils.isDebugBuild());

            sInstance = new ImageLoaderWrapper();
        }


        return sInstance;
    }

    public ImageLoader getImageLoader() {
        return sImageLoader;
    }

    static final DisplayImageOptions mProfileImageDisplayOptions =
            (new DisplayImageOptions.Builder())
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageForEmptyUri(R.drawable.ic_profile_image_default)
            .showImageOnFail(R.drawable.ic_profile_image_default)
            .showImageOnLoading(R.drawable.ic_profile_image_default)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .resetViewBeforeLoading(true)
            .build();
    public static final DisplayImageOptions mImageDisplayOptions =
            (new DisplayImageOptions.Builder())
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .resetViewBeforeLoading(true)
            .build();
    static final DisplayImageOptions mBannerDisplayOptions       =
            (new DisplayImageOptions.Builder())
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .build();

    public void clearFileCache()
    {
        sImageLoader.clearDiskCache();
    }

    public void clearMemoryCache()
    {
        sImageLoader.clearMemoryCache();
    }

    public void displayPreviewImage(
            final ImageView view,
            final String    url)
    {
        sImageLoader.displayImage(url, view, mImageDisplayOptions);
    }

    public void displayPreviewImage(
            final ImageView view,
            final String    url,
            final ImageLoadingHandler loadingHandler)
    {
        sImageLoader.displayImage(url, view, mImageDisplayOptions, loadingHandler, loadingHandler);
    }

    public void displayPreviewImageWithCredentials(
            final ImageView view,
            final String    url,
            final long      accountId,
            final ImageLoadingHandler loadingHandler)
    {
        final DisplayImageOptions.Builder b = new DisplayImageOptions.Builder();
        b.cloneFrom(mImageDisplayOptions);
        b.extraForDownloader(new AccountExtra(accountId));
        sImageLoader.displayImage(url, view, b.build(), loadingHandler, loadingHandler);
    }

    public void displayProfileBanner(
            final ImageView view,
            final String    base_url,
            final int       width)
    {
        final String type = Utils.getBestBannerType(width);
        final String url = TextUtils.isEmpty(base_url) ? null : base_url + "/" + type;
        sImageLoader.displayImage(url, view, mBannerDisplayOptions);
    }

    public void displayProfileImage(
            final ImageView view,
            final String    url)
    {
        sImageLoader.displayImage(url, view, mProfileImageDisplayOptions);
    }

    public void loadProfileImage(
            final String url,
            final ImageLoadingListener listener)
    {
        sImageLoader.loadImage(url, mProfileImageDisplayOptions, listener);
    }

    static private DiskCache getImageLoaderDiskCache()
    {
        final String dirName = SeagullTwitterConstants.DIR_NAME_IMAGE_CACHE;
        final File cacheDir = Utils.getBestCacheDir(sContext, dirName);
        final File fallbackCacheDir = Utils.getInternalCacheDir(sContext, dirName);
        return new UnlimitedDiscCache(cacheDir, fallbackCacheDir, new URLFileNameGenerator());
    }
}
