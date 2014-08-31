package com.shawnhu.seagull.seagull.twitter.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;

import java.util.List;

import twitter4j.User;

public class UsersArrayAdapter extends CapacityArrayAdapter<User> {
    protected int   mResource;
    protected long  mAccountId = -1;
    protected OnShowUser mOnShowUser;

    protected Fragment mHostFragment;
    protected Activity mHostActivity;

    public UsersArrayAdapter(Context context, List<User> users, Fragment fragment) {
        super(context, R.layout.user_profile, users);
        mResource =    R.layout.user_profile;

        if (fragment == null) {
            throw new NullPointerException("Hosting Fragment must not be null");
        }

        mHostFragment = fragment;
    }
    public UsersArrayAdapter(Context context, List<User> users, Activity activity) {
        super(context, R.layout.user_profile, users);
        mResource =    R.layout.user_profile;

        if (activity == null) {
            throw new NullPointerException("Hosting Activity must not be null");
        }

        mHostActivity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mResource, parent, false);
        }

        final User user = getItem(position);
        UserViewBuilder viewBuilder;
        if (mHostFragment != null) {
            viewBuilder = new UserViewBuilder(mHostFragment);
        } else {
            viewBuilder = new UserViewBuilder(mHostActivity);
        }

        if (user != null && convertView != null && getContext() != null) {
            viewBuilder.buildProfileView(convertView, user);
            viewBuilder.buildSelfieView(convertView, user);

            ImageView profileImage  = (ImageView) convertView.findViewById(R.id.profileImage);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnShowUser != null) {
                        mOnShowUser.onShowUser(user);
                    }
                }
            });
        }

        return convertView;
    }

    public void registerShowUserListener(OnShowUser l) {
        mOnShowUser = l;
    }
}
