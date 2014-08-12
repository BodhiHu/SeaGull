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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTaskManager;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTwitterWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.MessagesManager;
import com.shawnhu.seagull.seagull.twitter.utils.MultiSelectManager;
import com.shawnhu.seagull.seagull.twitter.utils.content.TwitterSQLiteOpenHelper;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwidereHostAddressResolver;

import twitter4j.http.HostAddressResolver;

public class TwitterManager {

    static private Context sAppContext;
    static private TwitterManager sTwitterManager;

    TwitterManager(Context context) {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        sAppContext = context;
    }

    public static TwitterManager getInstance(Context context) {
        if (sTwitterManager == null) {
            sTwitterManager = new TwitterManager(context);
        }

        return sTwitterManager;
    }

    static private ImageLoaderWrapper   sImageLoaderWrapper;
    static private AsyncTaskManager     sAsyncTaskManager;
    static private AsyncTwitterWrapper  sTwitterWrapper;
    static private MultiSelectManager   sMultiSelectManager;
    static private MessagesManager      sCroutonsManager;
    static private SQLiteOpenHelper     sSQLiteOpenHelper;
    static private HostAddressResolver  sHostAddrResolver;
    static private SQLiteDatabase       sDatabase;

    public Application getApplicationContext() {
        return (Application) sAppContext;
    }

    public AsyncTaskManager getAsyncTaskManager() {
        if (sAsyncTaskManager == null) {
            sAsyncTaskManager = AsyncTaskManager.getInstance();
        }

        return sAsyncTaskManager;
    }

    public HostAddressResolver getHostAddressResolver() {
        if (sHostAddrResolver == null) {
            sHostAddrResolver = new TwidereHostAddressResolver(sAppContext);
        }
        return sHostAddrResolver;
    }

    public ImageLoader getImageLoader() {
        return getImageLoaderWrapper().getImageLoader();
    }

    public ImageLoaderWrapper getImageLoaderWrapper() {
        if (sImageLoaderWrapper == null) {
            sImageLoaderWrapper = ImageLoaderWrapper.getInstance(sAppContext);
        }

        return sImageLoaderWrapper;
    }

    public MessagesManager getMessagesManager() {
        if (sCroutonsManager == null) {
            sCroutonsManager = MessagesManager.getInstance(sAppContext);
        }

        return sCroutonsManager;
    }

    public MultiSelectManager getMultiSelectManager() {
        if (sMultiSelectManager == null) {
            sMultiSelectManager = new MultiSelectManager();
        }

        return sMultiSelectManager = new MultiSelectManager();
    }

    public SQLiteDatabase getWritableSQLiteDatabase() {
        if (sDatabase != null) return sDatabase;

        return sDatabase = getSQLiteOpenHelper().getWritableDatabase();
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        if (sSQLiteOpenHelper == null) {
            sSQLiteOpenHelper = new TwitterSQLiteOpenHelper(sAppContext,
                    SeagullTwitterConstants.DATABASES_NAME,
                    SeagullTwitterConstants.DATABASES_VERSION);
        }

        return sSQLiteOpenHelper;
    }

    public AsyncTwitterWrapper getTwitterWrapper() {
        if (sTwitterWrapper != null) return sTwitterWrapper;

        return sTwitterWrapper = AsyncTwitterWrapper.getInstance(sAppContext);
    }
}
