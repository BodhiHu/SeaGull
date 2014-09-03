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
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView, "scrollX", fromScrollX, toScrollX);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", fromScrollY, toScrollY);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(duration);
        animators.playTogether(xTranslate, yTranslate);
        animators.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator.AnimatorUpdateListener alphaAnimatorListner =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
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
        ValueAnimator.AnimatorUpdateListener layoutAnimatorListener =
            new ValueAnimator.AnimatorUpdateListener() {
                int startScrollX = scrollView.getScrollX();
                int startScrollY = scrollView.getScrollY();
                int startHeight  = scrollView.getHeight();
                int startWidth   = scrollView.getWidth();

                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    int nextHeight = startHeight + (scrollView.getScrollY() - startScrollY);
                    int nextWidth = startWidth + (scrollView.getScrollX() - startScrollX);
                    ViewGroup.LayoutParams layout = scrollView.getLayoutParams();
                    layout.height = nextHeight;
                    layout.width = nextWidth;
                    scrollView.setLayoutParams(layout);
                }
            };
        Animator.AnimatorListener endLayoutFixListener = new Animator.AnimatorListener() {
            int startScrollX;
            int startScrollY;
            int startHeight;
            int startWidth;
            @Override public void onAnimationStart(Animator animation) {
                startScrollX = scrollView.getScrollX();
                startScrollY = scrollView.getScrollY();
                startHeight  = scrollView.getHeight();
                startWidth   = scrollView.getWidth();
            }
            @Override public void onAnimationEnd(Animator animation) {
                int endScrollX = scrollView.getScrollX();
                int endScrollY = scrollView.getScrollY();

                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.height = startHeight + (endScrollY - startScrollY);
                layoutParams.width  = startWidth  + (endScrollX - startScrollX);

                scrollView.setLayoutParams(layoutParams);
            }
            @Override public void onAnimationCancel(Animator animation) {  }
            @Override public void onAnimationRepeat(Animator animation) {  }
        };

        for (Animator animator : animators.getChildAnimations()) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            if (alphaView != null) {
                //alpha
                valueAnimator.addUpdateListener(alphaAnimatorListner);
            }
            //this will have visible delay
            valueAnimator.addUpdateListener(layoutAnimatorListener);
            if (l != null) {
                //custom
                valueAnimator.addUpdateListener(l);
            }
        }

        animators.addListener(endLayoutFixListener);
        animators.start();
    }


}
