package com.shawnhu.seagull.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.shawnhu.seagull.app.AppPreferences;

/**
 * Created by shawn on 14-7-28.
 */
public class ActivityUtils {
    static public void recreate(Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activity.recreate();
            } else {
                activity.startActivity(new Intent(activity, activity.getClass()));
                /**
                 * TODO : add animation
                 *   activity.overridePendingTransition(...);
                 */
                activity.finish();
            }
        }
    }

    static public void applyTheme(Activity activity) {
        if (activity != null) {
            recreate(activity);
        }
    }

    static public void saveTheme(Context context, String themeResIndexStr) {
        if (context != null && themeResIndexStr != null && themeResIndexStr != "") {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString(AppPreferences.PREF_APP_THEME, themeResIndexStr)
                    .commit();
        }
    }

    static public int getTheme(Context context, int currentTheme) {
        if (context != null) {
            try {
                String iS = PreferenceManager.getDefaultSharedPreferences(context)
                                .getString(AppPreferences.PREF_APP_THEME, "");
                if (iS != null && iS != "") {
                    int i = Integer.parseInt(iS);

                    return Integer.parseInt(
                            AppPreferences.PREFERENCES_MAP
                                    .get(AppPreferences.PREF_APP_THEME)[i]
                    );
                }
                //else, Key's value was not set yet, return app's current theme
            } catch(Exception e) {
                Log.e(ActivityUtils.class.toString(), e.toString());
                e.printStackTrace();
            }

        } else {
            throw new NullPointerException("context can not be null");
        }

        return currentTheme;
    }

    public static Bitmap getActivityScreenshotInternal(final Activity activity) {
        if (activity == null) return null;
        final Window w = activity.getWindow();
        final View view = w.getDecorView();
        final int width = view.getWidth(), height = view.getHeight();
        if (width <= 0 || height <= 0) return null;
        final Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        final Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        // Remove window background behind status bar.
        final Canvas c = new Canvas(b);
        view.draw(c);
        final Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawRect(frame.left, 0, frame.right, frame.top, paint);
        return b;
    }

}
