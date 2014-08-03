package com.shawnhu.seagull.utils;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by shawnhu on 8/2/14.
 */
public class ViewUtils {
    @SuppressWarnings("deprecation")
	public static void setBackground(final View view, final Drawable background) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
	}
}
