package com.shawnhu.seagull.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableFrameLayout extends FrameLayout {
    public TouchableFrameLayout(Context context) {
        super(context);
    }

    public TouchableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public TouchableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    OnTouchListener mOnTouchListener;
    public void registerOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mOnTouchListener != null) {
            return mOnTouchListener.onTouch(this, event);
        }

        return super.onInterceptTouchEvent(event);
    }
}
