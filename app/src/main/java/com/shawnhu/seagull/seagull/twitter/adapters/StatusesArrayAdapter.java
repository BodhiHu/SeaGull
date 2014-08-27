package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;

import java.util.List;

import twitter4j.Status;

public class StatusesArrayAdapter extends CapacityArrayAdapter<Status> {
    protected int   mResource;
    protected long  mAccountId = -1;

    public StatusesArrayAdapter(Context context) {
        super(context, R.layout.status_item);
        mResource = R.layout.status_item;
    }

    public StatusesArrayAdapter(Context context, List<Status> statuses) {
        super(context, R.layout.status_item, statuses);
        mResource = R.layout.status_item;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mResource, parent, false);
        }

        Status status = getItem(position);
        StatusViewBuilder.buildStatusView(convertView, getContext(), status, mAccountId, false);

        return convertView;
    }
}
