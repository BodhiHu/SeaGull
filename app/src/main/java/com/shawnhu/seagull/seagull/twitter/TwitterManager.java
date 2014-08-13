package com.shawnhu.seagull.seagull.twitter;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shawnhu.seagull.seagull.twitter.providers.TwitterSQLiteOpenHelper;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTaskManager;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTwitterWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.MessagesManager;
import com.shawnhu.seagull.seagull.twitter.utils.MultiSelectManager;
import com.shawnhu.seagull.seagull.twitter.utils.net.TwitterHostAddressResolver;

import twitter4j.http.HostAddressResolver;

public class TwitterManager {

    static private Context sAppContext;
    static private TwitterManager sTwitterManager;

    TwitterManager(Context context) {
        if (sAppContext == null && context == null) {
            throw new NullPointerException("context must not be null");
        }
        sAppContext = context;
    }

    public static TwitterManager getInstance() throws NullPointerException {
        if (sTwitterManager == null) {
            throw new NullPointerException("TwitterManager was not instantiated yet.");
        }

        return sTwitterManager;
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
            sHostAddrResolver = new TwitterHostAddressResolver(sAppContext);
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
