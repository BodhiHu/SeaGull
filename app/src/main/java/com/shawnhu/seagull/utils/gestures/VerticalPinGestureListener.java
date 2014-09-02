package com.shawnhu.seagull.utils.gestures;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class VerticalPinGestureListener implements GestureDetector.OnGestureListener {
    public VerticalPinGestureListener(View target) {
        mTargetView = target;
    }

    View mTargetView;

    @Override public void       onShowPress(MotionEvent e)      { }
    @Override public boolean    onSingleTapUp(MotionEvent e)    { return false; }
    @Override public void       onLongPress(MotionEvent e)      { }

    @Override public boolean    onDown(MotionEvent e)           { return false; }

    @Override public boolean    onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int dir = GestureUtils.getScrollDir(e1, e2);
        float ydis = Math.abs(distanceY);
        float downable_dis = Math.abs(mTargetView.getTop());
        float uppable_dis = mTargetView.getBottom();
        switch (dir) {
            case GestureUtils.DIR_DOWN:
                ydis = ydis <= downable_dis ? ydis : downable_dis;
                mTargetView.scrollBy(0, (int) ydis);
                //mTargetView.offsetTopAndBottom((int) ydis);
                return true;
            case GestureUtils.DIR_UP:
                ydis = ydis <= uppable_dis ? ydis : uppable_dis;
                ydis *= -1;
                mTargetView.scrollBy(0, (int) ydis);
                //mTargetView.offsetTopAndBottom((int) ydis);
                return true;
            default:
                return false;
        }
    }

    @Override public boolean    onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int dir = GestureUtils.getFlingDir(e1, e2);
        float downable_dis = Math.abs(mTargetView.getTop());
        float uppable_dis = mTargetView.getBottom();

        switch (dir) {
            case GestureUtils.DIR_DOWN:
                mTargetView.offsetTopAndBottom((int) downable_dis);
                return true;
            case GestureUtils.DIR_UP:
                uppable_dis *= -1;
                mTargetView.offsetTopAndBottom((int) uppable_dis);
                return true;
            default:
                return false;
        }
    }
}
