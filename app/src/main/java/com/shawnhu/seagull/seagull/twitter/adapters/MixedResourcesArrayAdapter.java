package com.shawnhu.seagull.seagull.twitter.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.shawnhu.seagull.R;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;

public class MixedResourcesArrayAdapter extends ArrayAdapter<Object> {

    public MixedResourcesArrayAdapter(Context context, List<Object> resources, Fragment fragment) {
        super(context, 0, resources);

        if (fragment == null) {
            throw new NullPointerException("Hosting fragment can not be null");
        }

        mHostFragment = fragment;
    }

    public MixedResourcesArrayAdapter(Context context, List<Object> resources, Activity activity) {
        super(context, 0, resources);

        if (activity == null) {
            throw new NullPointerException("Hosting activity can not be null");
        }

        mHostActivity = activity;
    }

    static final protected int mUserLayout = R.layout.user_profile;
    static final protected int mStatusLayout = R.layout.status_item;
    static final private String TAG_USER = "TAG_USER";
    static final private String TAG_STATUS = "TAG_STATUS";

    protected long mAccountId = -1;
    protected OnShowUser mOnShowUser;

    protected Fragment mHostFragment;
    protected Activity mHostActivity;
    public void setHost(Fragment fragment) {
        mHostFragment = fragment;
    }
    public void setHost(Activity activity) {
        mHostActivity = activity;
    }

    @Override
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
        if (convertView == null || convertView.getTag() == null || convertView.getTag().toString() != TAG_USER) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mUserLayout, parent, false);
            convertView.setTag(TAG_USER);
        }

        final User user = (User) getItem(position);
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

    public View getStatusView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag() == null || convertView.getTag().toString() != TAG_STATUS) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mStatusLayout, parent, false);
            convertView.setTag(TAG_STATUS);
        }

        Status status = (Status) getItem(position);
        StatusViewBuilder viewBuilder;
        if (mHostFragment != null) {
            viewBuilder = new StatusViewBuilder(mHostFragment);
        } else if (mHostActivity != null) {
            viewBuilder = new StatusViewBuilder(mHostActivity);
        } else {
            throw new NullPointerException("You must set host activity or fragment");
        }
        viewBuilder.buildStatusView(convertView, status, mAccountId, false);

        return convertView;
    }

    public void registerShowUserListener(OnShowUser l) {
        mOnShowUser = l;
    }
}
