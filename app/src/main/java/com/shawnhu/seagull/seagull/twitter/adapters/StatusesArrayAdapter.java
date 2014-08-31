package com.shawnhu.seagull.seagull.twitter.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;

import twitter4j.Status;

public class StatusesArrayAdapter extends CapacityArrayAdapter<Status> {
    protected int   mResource;
    protected long  mAccountId = -1;

    protected Fragment mHostFragment;
    protected Activity mHostActivity;

    public StatusesArrayAdapter(Context context, Fragment fragment) {
        this(context);

        if (fragment == null) {
            throw new NullPointerException("Host Fragment must not be null");
        }
        mHostFragment = fragment;
    }
    public StatusesArrayAdapter(Context context, Activity activity) {
        this(context);

        if (activity == null) {
            throw new NullPointerException("Host Activity must not be null");
        }
        mHostActivity = activity;
    }

    private StatusesArrayAdapter(Context context) {
        super(context, R.layout.status_item);
        mResource = R.layout.status_item;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mResource, parent, false);
        }

        Status status = getItem(position);
        StatusViewBuilder viewBuilder;
        if (mHostFragment != null) {
            viewBuilder = new StatusViewBuilder(mHostFragment);
        } else {
            viewBuilder = new StatusViewBuilder(mHostActivity);
        }

        viewBuilder.buildStatusView(convertView, status, mAccountId, false);

        return convertView;
    }
}
