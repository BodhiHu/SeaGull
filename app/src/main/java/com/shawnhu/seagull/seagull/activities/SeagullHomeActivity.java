package com.shawnhu.seagull.seagull.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractHomeNavDrawerActivity;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullHomeFragment;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapter;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

public class SeagullHomeActivity extends AbstractHomeNavDrawerActivity {
    static ArrayList<AnyViewArrayAdapterItem> mSeagullHomeDrawerItems = new ArrayList<AnyViewArrayAdapterItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        AnyViewArrayAdapter mSeagullDrawerListArrayAdapter =
                new AnyViewArrayAdapter(this, R.layout.layout_iconic_item, mSeagullHomeDrawerItems,
                    new AnyViewArrayAdapter.DefaultViewInterface() {
                        @Override
                        public View getDefaultView(LayoutInflater lI, AnyViewArrayAdapterItem item, int position, View convertView, ViewGroup parent) {
                            View v;

                            if (convertView == null) {
                                v = lI.inflate(R.layout.layout_iconic_item, parent, false);
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
            setFragmentArgs(i);
        }

        super.onCreate(savedInstanceState);
    }

    protected void setFragmentArgs(AnyViewArrayAdapterItem a) {
        Intent  i   = getIntent();

        if (a.mActionClass == SeagullHomeFragment.class) {

            long id = i.getLongExtra(SeagullTwitterConstants.EXTRA_USER_ID, -1);

            if (id == -1) {
                long ids[] = Utils.getAccountIds(this);
                id = ids[0];
            }

            Bundle args = new Bundle();
            args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, id);
            a.mActionArgs = args;
            Toast.makeText(this,
                    "Your id is " + id + ". Now you can reach Twitter now!", Toast.LENGTH_SHORT)
                    .show();
        } else {

        }
    }

}
