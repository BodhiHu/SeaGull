package com.shawnhu.seagull.widgets;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;

/**
 *  FIXME: Not thread safe
 */
public abstract class CapacityArrayAdapter<T> extends ArrayAdapter<T> {
    public CapacityArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CapacityArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CapacityArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);

        if (objects != null && objects.length > mCapacity) {
            throw new InvalidParameterException("The number of items exceeded capacity " + String.valueOf(mCapacity));
        }
    }

    public CapacityArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);

        if (objects != null && objects.length > mCapacity) {
            throw new InvalidParameterException("The number of items exceeded capacity " + String.valueOf(mCapacity));
        }
    }

    public CapacityArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);

        if (objects != null && objects.size() > mCapacity) {
            throw new InvalidParameterException("The number of items exceeded capacity " + String.valueOf(mCapacity));
        }
    }

    public CapacityArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);


        if (objects != null && objects.size() > mCapacity) {
            throw new InvalidParameterException("The number of items exceeded capacity " + String.valueOf(mCapacity));
        }
    }

    static private final int DEFAULT_CAPACITY = 42;
    protected int mCapacity = DEFAULT_CAPACITY;
    public void setCapacity(int c) {
        mCapacity = c > 0 ? c : DEFAULT_CAPACITY;
    }

    @Override
    public void add(T object) {
        //insert at end
        ensureRoomForItems(false, 1);

        super.add(object);
    }

    @Override
    public void addAll(Collection<? extends T> collection) {
        if (collection != null) {
            T[] arr = (T[]) collection.toArray();
            addAll(arr);
        }
    }

    @Override
    public void addAll(T ... items) {
        if (items != null) {
            ensureRoomForItems(false, items.length);

            if (items.length <= mCapacity) {
                super.addAll(items);
            } else {
                for (int i = 0; i < mCapacity; i++) {
                    add(items[i]);
                }
            }
        }
    }

    @Override
    public void insert(T object, int index) {
        ensureRoomForItems(index == 0, 1);

        super.insert(object, index);
    }

    /**
     * Remove items at tail/head, then return the available number of slots.
     *
     * @param insertAtStart
     * @param count The count of items to add
     * @return Number of available slots
     */
    protected int ensureRoomForItems(boolean insertAtStart, int count) {
        if (getCount() > mCapacity) {
            Log.e(this.getClass().getSimpleName(), "Its data holder array's capacity has reached out of capacity");
            //safe fallback, remove the tail items
            while (getCount() > mCapacity) {
                remove(getItem(getCount()-1));
            }
        }


        if (count > 0 && count < mCapacity) {
            int available_space = mCapacity - getCount();
            while (available_space < count) {
                if (insertAtStart) {
                    //insert at start, remove last item
                    remove(getItem(getCount() - 1));
                } else {
                    //insert at end,   remove first item
                    remove(getItem(0));
                }

                available_space = mCapacity - getCount();
            }
            return available_space;
        } else if (count >= mCapacity) {
            clear();
            return mCapacity;
        } else {
            return (mCapacity - getCount());
        }
    }
}
