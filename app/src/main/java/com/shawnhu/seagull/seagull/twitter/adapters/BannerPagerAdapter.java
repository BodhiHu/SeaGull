package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.widgets.ViewPagerAdapter;

import twitter4j.User;

public class BannerPagerAdapter extends ViewPagerAdapter {

    public BannerPagerAdapter(Context context, User user) {
        mContext = context;
        mUser = user;
    }

    static final public int PROFILE_PAGE    = 0;
    static final public int SELFIE_PAGE     = 1;
    static final public int COUNT           = 2;

    protected User mUser;
    protected Context mContext;
    public void setUser(User u) {
        mUser = u;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public View getView(int pos) {
        if (mContext == null) {
            return null;
        }

        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (pos) {
            case PROFILE_PAGE:
                v = layoutInflater.inflate(R.layout.banner_profile, null);
                UserViewBuilder.buildProfileView(v, mUser);
                break;
            case SELFIE_PAGE:
                v = layoutInflater.inflate(R.layout.banner_selfie, null);
                UserViewBuilder.buildSelfieView(v, mUser);
                break;
            default:
                break;
        }

        return v;
    }
}
