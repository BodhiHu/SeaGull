package com.shawnhu.seagull.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
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
                                     int         fromScrollX,   int         toScrollX,
                                     int         fromScrollY,   int         toScrollY,
                                     final View  alphaView,
                                     final float fromAlpha,     final float toAlpha,
                                     final long duration,
                                     Animator.AnimatorListener l) {
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView, "scrollX", fromScrollX, toScrollX);
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", fromScrollY, toScrollY);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(duration);
        animators.playTogether(xTranslate, yTranslate);

        Animator.AnimatorListener alphaAnimatorListner =
            new Animator.AnimatorListener() {
                long startTimeMillis;

                @Override public void onAnimationStart(Animator animation) {
                    startTimeMillis = android.view.animation.AnimationUtils.currentAnimationTimeMillis();
                }
                @Override public void onAnimationEnd(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) {
                    long currentTimeMillis = android.view.animation.AnimationUtils.currentAnimationTimeMillis();
                    float fraction = ((float) (currentTimeMillis - startTimeMillis) / duration);
                    fraction = fraction <= 1 ? fraction : 1;

                    float alpha = fromAlpha + (toAlpha - fromAlpha) * fraction;
                    Animation alphaAnima = new AlphaAnimation(fromAlpha, toAlpha);
                    alphaAnima.setDuration(0);
                    alphaAnima.setFillAfter(true);

                    alphaView.startAnimation(alphaAnima);
                }
            };
        Animator.AnimatorListener layoutAnimatorListener =
            new Animator.AnimatorListener() {
                int startScrollX, startScrollY;
                int startHeight , startWidth;
                @Override public void onAnimationStart(Animator animation) {
                    startScrollX = scrollView.getScrollX();
                    startScrollY = scrollView.getScrollY();
                    startHeight = scrollView.getHeight();
                    startWidth = scrollView.getWidth();
                }
                @Override public void onAnimationEnd(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) {
                    int nextHeight = startHeight + (scrollView.getScrollY() - startScrollY);
                    int nextWidth = startWidth + (scrollView.getScrollX() - startScrollX);
                    ViewGroup.LayoutParams layout = scrollView.getLayoutParams();
                    layout.height = nextHeight;
                    layout.width = nextWidth;
                    scrollView.setLayoutParams(layout);
                }
            };

        //for (Animator animator : animators.getChildAnimations()) {
            if (alphaView != null) {
                //alpha
                animators.addListener(alphaAnimatorListner);
            }
            //layout
            animators.addListener(layoutAnimatorListener);
            if (l != null) {
                //custom
                animators.addListener(l);
            }
        //}

        animators.start();
    }


}
