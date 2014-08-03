/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shawnhu.seagull.seagull.twitter;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTaskManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.MessagesManager;
import com.shawnhu.seagull.seagull.twitter.utils.MultiSelectManager;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.*;
import com.shawnhu.seagull.seagull.twitter.utils.content.TwitterSQLiteOpenHelper;
import com.shawnhu.seagull.seagull.twitter.utils.imageloader.TwitterImageDownloader;
import com.shawnhu.seagull.seagull.twitter.utils.imageloader.URLFileNameGenerator;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwidereHostAddressResolver;
import com.shawnhu.seagull.utils.StrictModeUtils;

import java.io.File;

import twitter4j.http.HostAddressResolver;

public class TwitterManager {

    static private Context sAppContext;
    static private TwitterManager sTwitterManager;

    TwitterManager(Context context) {
        if (context == null || !(context instanceof Application)) {
            throw new NullPointerException("context must be null && instanceof Application");
        }
        sAppContext = context;
    }

    public static TwitterManager getInstance(Context context) {
        if (sTwitterManager == null) {
            sTwitterManager = new TwitterManager(context);
        }

        return sTwitterManager;
    }

	private ImageLoaderWrapper mImageLoaderWrapper;
	private ImageLoader mImageLoader;
	private AsyncTaskManager mAsyncTaskManager;
	private AsyncTwitterWrapper mTwitterWrapper;
	private MultiSelectManager mMultiSelectManager;
	private TwitterImageDownloader mImageDownloader, mFullImageDownloader;
	private DiskCache mDiskCache, mFullDiskCache;
	private MessagesManager mCroutonsManager;
	private SQLiteOpenHelper mSQLiteOpenHelper;
	static private HostAddressResolver mResolver;
	private SQLiteDatabase mDatabase;

    static public Application getApplicationContext() {
        return (Application) sAppContext;
    }

	public AsyncTaskManager getAsyncTaskManager() {
		if (mAsyncTaskManager != null) return mAsyncTaskManager;
		return mAsyncTaskManager = AsyncTaskManager.getInstance();
	}

	public DiskCache getDiskCache() {
		if (mDiskCache != null) return mDiskCache;
		return mDiskCache = getDiskCache(SeagullTwitterConstants.DIR_NAME_IMAGE_CACHE);
	}

	public DiskCache getFullDiskCache() {
		if (mFullDiskCache != null) return mFullDiskCache;
		return mFullDiskCache = getDiskCache(SeagullTwitterConstants.DIR_NAME_FULL_IMAGE_CACHE);
	}

	public ImageDownloader getFullImageDownloader() {
		if (mFullImageDownloader != null) return mFullImageDownloader;
		return mFullImageDownloader = new TwitterImageDownloader(sAppContext, true);
	}

	static public HostAddressResolver getHostAddressResolver() {
		if (mResolver != null) return mResolver;
		return mResolver = new TwidereHostAddressResolver(sAppContext);
	}

	public ImageDownloader getImageDownloader() {
		if (mImageDownloader != null) return mImageDownloader;
		return mImageDownloader = new TwitterImageDownloader(sAppContext, false);
	}

	public ImageLoader getImageLoader() {
		if (mImageLoader != null) return mImageLoader;
		final ImageLoader loader = ImageLoader.getInstance();
		final ImageLoaderConfiguration.Builder cb = new ImageLoaderConfiguration.Builder(sAppContext);
		cb.threadPriority(Thread.NORM_PRIORITY - 2);
		cb.denyCacheImageMultipleSizesInMemory();
		cb.tasksProcessingOrder(QueueProcessingType.LIFO);
		// cb.memoryCache(new ImageMemoryCache(40));
		cb.diskCache(getDiskCache());
		cb.imageDownloader(getImageDownloader());
		L.writeDebugLogs(Utils.isDebugBuild());
		loader.init(cb.build());
		return mImageLoader = loader;
	}

	public ImageLoaderWrapper getImageLoaderWrapper() {
		if (mImageLoaderWrapper != null) return mImageLoaderWrapper;
		return mImageLoaderWrapper = new ImageLoaderWrapper(getImageLoader());
	}

	public MessagesManager getMessagesManager() {
		if (mCroutonsManager != null) return mCroutonsManager;
		return mCroutonsManager = MessagesManager.getInstance(sAppContext);
	}

	public MultiSelectManager getMultiSelectManager() {
		if (mMultiSelectManager != null) return mMultiSelectManager;
		return mMultiSelectManager = new MultiSelectManager();
	}

	public SQLiteDatabase getSQLiteDatabase() {
		if (mDatabase != null) return mDatabase;

		StrictModeUtils.checkDiskIO();
		return mDatabase = getSQLiteOpenHelper().getWritableDatabase();
	}

	public SQLiteOpenHelper getSQLiteOpenHelper() {
		if (mSQLiteOpenHelper != null) return mSQLiteOpenHelper;
		return mSQLiteOpenHelper = new TwitterSQLiteOpenHelper(sAppContext,
                SeagullTwitterConstants.DATABASES_NAME, SeagullTwitterConstants.DATABASES_VERSION);
	}

	public AsyncTwitterWrapper getTwitterWrapper() {
		if (mTwitterWrapper != null) return mTwitterWrapper;
		return mTwitterWrapper = AsyncTwitterWrapper.getInstance(sAppContext);
	}

	public void reloadConnectivitySettings() {
		if (mImageDownloader != null) {
			mImageDownloader.reloadConnectivitySettings();
		}
	}

	private DiskCache getDiskCache(final String dirName) {
		final File cacheDir = getBestCacheDir(sAppContext, dirName);
		final File fallbackCacheDir = getInternalCacheDir(sAppContext, dirName);
		return new UnlimitedDiscCache(cacheDir, fallbackCacheDir, new URLFileNameGenerator());
	}
}
