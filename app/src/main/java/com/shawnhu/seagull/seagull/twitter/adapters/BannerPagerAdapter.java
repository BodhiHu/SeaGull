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
                fillProfilePage(v);
                break;
            case SELFIE_PAGE:
                v = layoutInflater.inflate(R.layout.banner_selfie, null);
                fillSelfiePage(v);
                break;
            default:
                break;
        }

        return v;
    }

    protected void fillProfilePage(View v) {
        if (v != null && mUser != null) {
            ImageView profileImage = (ImageView) v.findViewById(R.id.profileImage);
            TextView  screenName   = (TextView)  v.findViewById(R.id.screenName);
            TextView  name         = (TextView)  v.findViewById(R.id.name);

            ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();
            if (profileImage != null && imageLoaderWrapper != null) {
                imageLoaderWrapper.displayProfileImage(profileImage, mUser.getProfileImageURL().toString());
            }
            if (screenName != null) {
                screenName.setText(mUser.getScreenName());
            }
            if (name != null) {
                name.setText("@" + mUser.getName());
            }
        }
    }
    protected void fillSelfiePage(View v) {
        if (v != null && mUser != null) {
            TextView selfie     = (TextView) v.findViewById(R.id.selfie);
            TextView location   = (TextView) v.findViewById(R.id.location);
            TextView webSite    = (TextView) v.findViewById(R.id.webSite);

            if (selfie != null) {
                String sf = mUser.getDescription();
                if (sf != null && sf.length() != 0) {
                    selfie.setText(mUser.getDescription());
                } else {
                    selfie.setText("Lazy guy, doesn't have a selfie.\n" +
                                   "And now All you can see is...\n" +
                                   "#$%*(&(*#&@$(*%&)(!@*$(");
                }
            }
            if (location != null) {
                String lo = mUser.getLocation();
                if (lo != null && lo.length() != 0) {
                    location.setText(mUser.getLocation());
                } else {
                    location.setText("Lives at St.Mars");
                }
            }
            if (webSite != null) {
                if (mUser.getURL() != null && mUser.getURL().toString().length() != 0) {
                    webSite.setText(mUser.getURL().toString());
                } else {
                    webSite.setText("Another member of nullsite dot com");
                }
            }
        }
    }
}
