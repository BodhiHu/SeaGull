package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;

import twitter4j.User;

public class UsersArrayAdapter extends CapacityArrayAdapter<User> {
    protected int   mResource;
    protected long  mAccountId = -1;

    public UsersArrayAdapter(Context context) {
        super(context, R.layout.user_banner);
        //mResource = ;
    }

    public UsersArrayAdapter(Context context, List<User> users) {
        super(context, R.layout.user_banner, users);
        mResource =    R.layout.user_banner;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mResource, parent, false);
        }

        convertView.setBackgroundColor((new Random()).nextInt());

        User user = getItem(position);
        if (user != null && convertView != null && getContext() != null) {
            ViewPager viewPager = (ViewPager) convertView.findViewById(R.id.bannerPager);
            CirclePageIndicator circlePageIndicator =
                    (CirclePageIndicator) convertView.findViewById(R.id.bannerIndicator);

            BannerPagerAdapter adapter = new BannerPagerAdapter(getContext(), user);
            viewPager.setAdapter(adapter);
            circlePageIndicator.setViewPager(viewPager);
        }

        return convertView;
    }
}
