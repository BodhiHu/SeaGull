package com.shawnhu.seagull.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.Enumeration;

/**
 * Created by shawn on 14-7-25.
 */
public class AnyViewArrayAdapterItem {
    public Class   mActionClass;
    public Integer mResource;
    public Object  mTarget;
    public ItemViewInterface mInterface;

    public interface ItemViewInterface {
        View getView(LayoutInflater lI, View convertView, ViewGroup parent);
    }

    public AnyViewArrayAdapterItem(Object target) {
        this(null, target, null, null);
    }
    public AnyViewArrayAdapterItem(Object target, Class actionClass) {
        this(null, target, null, actionClass);
    }
    public AnyViewArrayAdapterItem(Integer resource, Object target, ItemViewInterface i, Class actionClass) {
        if (target == null ||
                (resource != null && i == null)) {
             throw new InvalidParameterException();
         }
        this.mResource = resource;
        this.mTarget = target;
        this.mInterface = i;
        this.mActionClass = actionClass;
    }
}
