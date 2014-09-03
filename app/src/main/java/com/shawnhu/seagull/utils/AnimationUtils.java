package com.shawnhu.seagull.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationUtils {
    static public void animateAlpha(View v, float from, float to, long duration) {
        Animation animation = new AlphaAnimation(from, to);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    static public void animateScroll(final View  scrollView,
                                     final int   fromScrollX, final int   toScrollX,
                                     final int   fromScrollY, final int   toScrollY,
                                     final View  alphaView,
                                     final float fromAlpha,   final float toAlpha,
                                     final long  duration,
                                     ValueAnimator.AnimatorUpdateListener l) {
        //scroll
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView, "scrollX", fromScrollX, toScrollX);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", fromScrollY, toScrollY);

        //resize height
        ValueAnimator heightAnimator =
                ValueAnimator.ofInt(scrollView.getHeight(), scrollView.getHeight() + (toScrollY - fromScrollY));
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.height = value;
                scrollView.setLayoutParams(layoutParams);
            }
        });

        //resize width
        ValueAnimator widthAnimator =
                ValueAnimator.ofInt(scrollView.getWidth(), scrollView.getWidth() + (toScrollX - fromScrollX));
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.width = value;
                scrollView.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(duration);
        animators.playTogether(xTranslate, yTranslate, heightAnimator);
        animators.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator.AnimatorUpdateListener alphaAnimatorListner =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        long currentTimeMillis = animation.getCurrentPlayTime();
                        float fraction = ((float) (currentTimeMillis) / duration);
                        fraction = fraction <= 1 ? fraction : 1;

                        float alpha = fromAlpha + (toAlpha - fromAlpha) * fraction;
                        Animation alphaAnima = new AlphaAnimation(alpha, alpha);
                        alphaAnima.setDuration(0);
                        alphaAnima.setFillAfter(true);

                        alphaView.startAnimation(alphaAnima);
                    }
                };

        for (Animator animator : animators.getChildAnimations()) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            if (alphaView != null) {
                //alpha
                valueAnimator.addUpdateListener(alphaAnimatorListner);
            }
            if (l != null) {
                //custom
                valueAnimator.addUpdateListener(l);
            }
        }

        animators.start();
    }


}
