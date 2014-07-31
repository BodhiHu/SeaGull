package com.shawnhu.seagull.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.List;

import android.widget.ArrayAdapter;


/**
 * Created by shawn on 14-7-23.
 */
public class AnyViewArrayAdapter extends ArrayAdapter<AnyViewArrayAdapterItem> {
    private DefaultViewInterface mInterface;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public AnyViewArrayAdapter(Context context, int resource, List<AnyViewArrayAdapterItem> items, DefaultViewInterface i) {
        super(context, resource, items);
        if (i == null) {
            throw new InvalidParameterException();
        }
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInterface = i;
    }

    public interface DefaultViewInterface {
        View getDefaultView(LayoutInflater li, AnyViewArrayAdapterItem item, int position, View convertView, ViewGroup parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnyViewArrayAdapterItem i = getItem(position);
        if (i.mResource == null) {
            return mInterface.getDefaultView(mInflater, this.getItem(position), position, convertView, parent);
        }
        return i.mInterface.getView(mInflater, convertView, parent);
    }


}
