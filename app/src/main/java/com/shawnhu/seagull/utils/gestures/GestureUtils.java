package com.shawnhu.seagull.utils.gestures;

import android.view.MotionEvent;

public class GestureUtils {

    static final public int DIR_RIGHT = 0;
    static final public int DIR_LEFT  = 1;
    static final public int DIR_DOWN  = 2;
    static final public int DIR_UP    = 3;


    static public int getScrollDir(MotionEvent e1, MotionEvent e2) {
        float start_x = e1.getX();
        float start_y = e1.getY();
        float end_x   = e2.getX();
        float end_y   = e2.getY();

        float vec_x = end_x - start_x;
        float vec_y = end_y - start_y;
        if (Math.abs(vec_x) > Math.abs(vec_y)) {
            //horizontal
            if (vec_x > 0) {
                return DIR_RIGHT;
            } else {
                return DIR_LEFT;
            }

        } else {
            //vertical
            if (vec_y > 0) {
                return DIR_DOWN;
            } else {
                return DIR_UP;
            }
        }
    }
    static public int getFlingDir(MotionEvent e1, MotionEvent e2) {
        return getScrollDir(e1, e2);
    }
}
