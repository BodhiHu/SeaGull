package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
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

    public UsersArrayAdapter(Context context) {
        super(context, R.layout.user_profile);
        mResource = R.layout.user_profile;
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

        final User user = getItem(position);
        if (user != null && convertView != null && getContext() != null) {
            UserViewBuilder.buildProfileView(convertView, user);
            UserViewBuilder.buildSelfieView(convertView, user);

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
