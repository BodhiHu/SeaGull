package com.shawnhu.seagull.seagull.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractHomeNavDrawerActivity;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullHomeFragment;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullProfileFragment;
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
                                TextView tv  = (TextView)  v.findViewById(R.id.screenName);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    protected void setFragmentArgs(AnyViewArrayAdapterItem a) {
        Intent i = getIntent();

        Seagull.sCurrentAccount.sAccountId = getCurrentAccountId(i);

        /** PUT FRAGMENT'S ARGS HERE */
        if (a.mActionClass == SeagullHomeFragment.class) {
            Bundle args = new Bundle();
            args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, Seagull.sCurrentAccount.sAccountId);
            a.mActionArgs = args;
        } else if (a.mActionClass == SeagullProfileFragment.class) {
            Bundle args = new Bundle();
            args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, Seagull.sCurrentAccount.sAccountId);
            args.putLong(SeagullTwitterConstants.EXTRA_USER_ID,    Seagull.sCurrentAccount.sAccountId);
            a.mActionArgs = args;
        }
    }

    protected long getCurrentAccountId(Intent i) {
        long id = -1;

        if (i != null) {
            id = i.getLongExtra(SeagullTwitterConstants.EXTRA_USER_ID, -1);
        }

        if (id == -1) {
            long ids[] = Utils.getAccountIds(this);
            id = ids[0];
        }
        return id;
    }

}
