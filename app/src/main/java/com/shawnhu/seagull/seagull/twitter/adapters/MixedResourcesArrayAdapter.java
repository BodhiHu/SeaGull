package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.shawnhu.seagull.R;

import java.util.List;

import twitter4j.Status;
import twitter4j.User;

public class MixedResourcesArrayAdapter extends ArrayAdapter<Object> {
    public MixedResourcesArrayAdapter(Context context) {
        super(context, 0);
    }

    public MixedResourcesArrayAdapter(Context context, List<Object> resources) {
        super(context, 0, resources);
    }

    static final protected int mUserLayout = R.layout.user_profile;
    static final protected int mStatusLayout = R.layout.status_item;

    protected long mAccountId = -1;
    protected OnShowUser mOnShowUser;

    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        if (item != null) {
            if (item instanceof User) {
                return getUserView(position, convertView, parent);
            } else if (item instanceof Status) {
                return getStatusView(position, convertView, parent);
            }
        }

        return null;
    }

    public View getUserView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mUserLayout, parent, false);
        }

        final User user = (User) getItem(position);
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

    public View getStatusView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mStatusLayout, parent, false);
        }

        Status status = (Status) getItem(position);
        StatusViewBuilder.buildStatusView(convertView, getContext(), status, mAccountId, false);

        return convertView;
    }

    public void registerShowUserListener(OnShowUser l) {
        mOnShowUser = l;
    }
}
