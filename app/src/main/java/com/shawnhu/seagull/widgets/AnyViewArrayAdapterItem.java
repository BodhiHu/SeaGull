package com.shawnhu.seagull.widgets;

import android.os.Bundle;
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
    public Bundle  mActionArgs;
    public Integer mResource;
    public Object  mTarget;
    public ItemViewInterface mInterface;
    public String mName;

    public interface ItemViewInterface {
        View getView(LayoutInflater lI, View convertView, ViewGroup parent);
    }

    public AnyViewArrayAdapterItem(Object target, String name) {
        this(null, target, null, null, name);
    }
    public AnyViewArrayAdapterItem(Object target, Class actionClass, String name) {
        this(null, target, null, actionClass, name);
    }
    public AnyViewArrayAdapterItem(Integer resource, Object target, ItemViewInterface i, Class actionClass, String name) {
        if (target == null ||
                (resource != null && i == null)) {
             throw new InvalidParameterException();
         }
        this.mResource = resource;
        this.mTarget = target;
        this.mInterface = i;
        this.mActionClass = actionClass;
        this.mName = name;
    }
}
