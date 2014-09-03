package com.shawnhu.seagull.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

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

    /**
     * Hack to set ViewGroup and all its children's alpha
     * @param view
     * @param alpha
     */
    public static void setAlpha(final View view, final float alpha) {
        if (view != null && alpha >=0 && alpha <= 1) {
            Animation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
            if (view.getBackground() != null) {
                view.getBackground().setAlpha((int) (255*alpha));
            }
        }
    }
}
