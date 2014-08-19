package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;

import com.shawnhu.seagull.adapters.PersistentArrayAdapter;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;

import java.util.List;

/**
 * Created by shawn on 14-8-19.
 */
public class StatusesArrayAdapter extends PersistentArrayAdapter<TwitterStatus> {
    public StatusesArrayAdapter(Context context, int resource) {
        super(context, resource);
    }
    public StatusesArrayAdapter(Context context, int resource, List<TwitterStatus> objects) {
        super(context, resource, 0, objects);
    }

}
