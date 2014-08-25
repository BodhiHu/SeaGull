package com.shawnhu.seagull.widgets;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewPagerAdapter extends PagerAdapter {
    public abstract View getView(int pos);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = getView(position);
        if (container != null && v != null) {
            container.addView(v);
        }

        return v;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (container != null) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }
}
