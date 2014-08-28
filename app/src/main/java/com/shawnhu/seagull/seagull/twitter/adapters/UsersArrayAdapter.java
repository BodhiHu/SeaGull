package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Random;

import twitter4j.User;

public class UsersArrayAdapter extends CapacityArrayAdapter<User> {
    protected int   mResource;
    protected long  mAccountId = -1;

    public UsersArrayAdapter(Context context) {
        super(context, R.layout.user_profile);
        //mResource = ;
    }

    public UsersArrayAdapter(Context context, List<User> users) {
        super(context, R.layout.user_profile, users);
        mResource =    R.layout.user_profile;
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
            UserViewBuilder.buildProfileView(convertView, user);
            UserViewBuilder.buildSelfieView(convertView, user);
        }

        return convertView;
    }
}
