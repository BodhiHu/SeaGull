package com.shawnhu.seagull.utils.gestures;

import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.shawnhu.seagull.utils.AnimationUtils;
import com.shawnhu.seagull.utils.ViewUtils;

import java.security.InvalidParameterException;

public class VerticalPinGestureTracker implements GestureDetector.OnGestureListener {
    public VerticalPinGestureTracker(View container, View upper, int pixs) {
        this(container, upper, pixs, TypedValue.COMPLEX_UNIT_PX);
    }
    public VerticalPinGestureTracker(View container, View upper, int scrollDis, int unit) {
        mContainerView = container;
        mUperView = upper;

        if (unit == TypedValue.COMPLEX_UNIT_DIP) {
            mScrollableYDisPixs = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    Math.abs(scrollDis),
                    mContainerView.getContext().getResources().getDisplayMetrics());
        } else if (unit == TypedValue.COMPLEX_UNIT_PX) {
            mScrollableYDisPixs = Math.abs(scrollDis);
        } else {
            throw new InvalidParameterException("this unit is not supported.");
        }

    }

    View mContainerView;
    View mUperView;
    int mScrollableYDisPixs;

    @Override public void       onShowPress(MotionEvent e)      { }
    @Override public boolean    onSingleTapUp(MotionEvent e)    { return false; }
    @Override public void       onLongPress(MotionEvent e)      { }

    @Override public boolean    onDown(MotionEvent e)           { return false; }

    @Override public boolean    onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
/*
        final int dir = GestureUtils.getScrollDir(e1, e2);
        int deltaYDisPixs = (int) distanceY;
        int absDeltaYDisPixs = Math.abs(deltaYDisPixs);
        int sign = deltaYDisPixs >= 0 ? 1 : -1;

        int scrollY = mContainerView.getScrollY();

        int downable_dis  = scrollY ;
        int upable_dis   = mScrollableYDisPixs - scrollY;

        float alpha = ((float) upable_dis)/mScrollableYDisPixs;
        ViewUtils.setAlpha(mUperView, alpha);

        switch (dir) {
            case GestureUtils.DIR_DOWN: {
                absDeltaYDisPixs = absDeltaYDisPixs >= downable_dis ? downable_dis : absDeltaYDisPixs;
                deltaYDisPixs = absDeltaYDisPixs * sign;
                ViewGroup.LayoutParams layout = mContainerView.getLayoutParams();
                layout.height = mContainerView.getHeight() + deltaYDisPixs;
                mContainerView.setLayoutParams(layout);
                mContainerView.scrollBy(0, deltaYDisPixs);
                break;
            }
            case GestureUtils.DIR_UP: {
                absDeltaYDisPixs = absDeltaYDisPixs >= upable_dis ? upable_dis : absDeltaYDisPixs;
                deltaYDisPixs = absDeltaYDisPixs * sign;
                ViewGroup.LayoutParams layout = mContainerView.getLayoutParams();
                layout.height = mContainerView.getHeight() + deltaYDisPixs;
                mContainerView.setLayoutParams(layout);
                mContainerView.scrollBy(0, deltaYDisPixs);
                break;
            }
            default:
                break;
        }
        return false;
*/
    }

    @Override public boolean    onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int dir = GestureUtils.getScrollDir(e1, e2);

        int scrollY = mContainerView.getScrollY();

        int downable_dis  = scrollY ;
        int upable_dis   = mScrollableYDisPixs - scrollY;

        float duration = 500;

        switch (dir) {
            case GestureUtils.DIR_DOWN:
                duration = Math.abs(duration * downable_dis / mScrollableYDisPixs);
                AnimationUtils.animateScroll(mContainerView,
                        mContainerView.getScrollX(), mContainerView.getScrollX(),
                        mContainerView.getScrollY(), 0,
                        mUperView,
                        0f,                          1f,
                        (int) duration, null);
                break;
            case GestureUtils.DIR_UP:
                duration = Math.abs(duration * upable_dis / mScrollableYDisPixs);
                AnimationUtils.animateScroll(mContainerView,
                        mContainerView.getScrollX(), mContainerView.getScrollX(),
                        mContainerView.getScrollY(), mScrollableYDisPixs,
                        mUperView,
                        1f,                          0f,
                        (int) duration, null);
                break;
            default:
                break;
        }

        return false;
    }
}
