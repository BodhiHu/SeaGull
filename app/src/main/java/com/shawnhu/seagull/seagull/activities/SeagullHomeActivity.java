package com.shawnhu.seagull.seagull.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.HomeNavDrawerActivity;
import com.shawnhu.seagull.adapters.AnyViewArrayAdapter;
import com.shawnhu.seagull.adapters.AnyViewArrayAdapterItem;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.Seagull;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * Created by shawn on 14-7-24.
 */
public class SeagullHomeActivity extends HomeNavDrawerActivity {
    static ArrayList<AnyViewArrayAdapterItem> mSeagullHomeDrawerItems = new ArrayList<AnyViewArrayAdapterItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AnyViewArrayAdapter mSeagullDrawerListArrayAdapter =
                new AnyViewArrayAdapter(this, R.layout.layout_iconic_item, mSeagullHomeDrawerItems,
                    new AnyViewArrayAdapter.DefaultViewInterface() {
                        @Override
                        public View getDefaultView(LayoutInflater lI, AnyViewArrayAdapterItem item, int position, View convertView, ViewGroup parent) {
                            View v;
                            LayoutInflater layoutInflater = lI;

                            if (convertView == null) {
                                v = layoutInflater.inflate(R.layout.layout_iconic_item, parent, false);
                            } else {
                                v = convertView;
                            }

                            Object target = item.mTarget;
                            if (target != null && target instanceof IconicItem) {
                                ImageView iv = (ImageView) v.findViewById(R.id.imageView);
                                TextView tv  = (TextView)  v.findViewById(R.id.textView);
                                iv.setImageResource(((IconicItem) target).iconResId);
                                tv.setText(((IconicItem) target).name);
                            } else {
                                try {
                                    throw new InvalidObjectException("required " + IconicItem.class + " instance");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            return v;
                        }
                    }
                );

        //provide app data, super will do the rest.
        mDrawerListArrayAdapter = mSeagullDrawerListArrayAdapter;

        for (AnyViewArrayAdapterItem i : Seagull.mSeagullDrawerItems) {
            mDrawerListArrayAdapter.add(i);
        }

        super.onCreate(savedInstanceState);
    }

}
