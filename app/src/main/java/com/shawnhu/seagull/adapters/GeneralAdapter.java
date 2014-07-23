package com.shawnhu.seagull.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by shawn on 14-7-23.
 */
public class GeneralAdapter extends ArrayAdapter<GeneralAdapter.Item> {
    private DefaultViewInterface mInterface;

    public GeneralAdapter(Context context, int resource, List<Item> items, DefaultViewInterface i) {
        super(context, resource, items);
        if (i == null) {
            throw new InvalidParameterException();
        }
        mInterface = i;
    }

    public interface DefaultViewInterface {
        View getDefaultView(int position, View convertView, ViewGroup parent);
    }
    public interface ItemViewInterface {
        View getView(View convertView, ViewGroup parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item i = getItem(position);
        if (i.mResource == null) {
            return mInterface.getDefaultView(position, convertView, parent);
        }
        return i.mInterface.getView(convertView, parent);
    }

    public class Item {
        private Integer mResource;
        private Object  mTarget;
        private ItemViewInterface mInterface;

        public Item(Object target) {
            this.mTarget = target;
        }
        public Item(Integer resource, Object target, ItemViewInterface i) {
            if (target == null ||
                    (resource != null && i == null)) {
                throw new InvalidParameterException();
            }
            this.mResource = resource;
            this.mTarget = target;
            this.mInterface = i;
        }
    }
}
