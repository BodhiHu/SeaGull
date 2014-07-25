package com.shawnhu.seagull.seagull.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.HomeActivity;
import com.shawnhu.seagull.activities.SettingsActivity;
import com.shawnhu.seagull.adapters.AnyViewArrayAdapter;
import com.shawnhu.seagull.adapters.AnyViewArrayAdapterItem;
import com.shawnhu.seagull.fragments.DraftsFragment;
import com.shawnhu.seagull.fragments.FollowersFragment;
import com.shawnhu.seagull.fragments.FollowingsFragment;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.fragments.NotificationsFragment;
import com.shawnhu.seagull.fragments.ProfileFragment;
import com.shawnhu.seagull.fragments.SearchFragment;
import com.shawnhu.seagull.fragments.TweetsFragment;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.views.AvatarCard;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by shawn on 14-7-24.
 */
public class SeagullHomeActivity extends HomeActivity {
    static ArrayList<AnyViewArrayAdapterItem> mSeagullHomeDrawerItems = new ArrayList<AnyViewArrayAdapterItem>();
    AnyViewArrayAdapter mSeagullDrawerListArrayAdapter =
            new AnyViewArrayAdapter(getApplicationContext(), R.layout.layout_iconic_item, mSeagullHomeDrawerItems,
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
                            if (target instanceof IconicItem) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //provide app data, super will do the rest.
        mDrawerListArrayAdapter = mSeagullDrawerListArrayAdapter;
        for (AnyViewArrayAdapterItem i: Seagull.mSeagullDrawerItems) {
            mDrawerListArrayAdapter.add(i);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        AnyViewArrayAdapterItem i = mDrawerListArrayAdapter.getItem(position);
        Class fragmentClass = null;
        Class activityClass = null;
        if (i.mTarget instanceof AvatarCard) {
            fragmentClass = ProfileFragment.class;
        } else if (i.mTarget instanceof IconicItem) {
            IconicItem iI = (IconicItem) i.mTarget;
            if (iI != null) {
                String it = iI.name;
                if        (it == Seagull.DRAWER_MENU_HOME) {
                    fragmentClass = HomeFragment.class;
                } else if (it == Seagull.DRAWER_MENU_NOTI) {
                    fragmentClass = NotificationsFragment.class;
                } else if (it == Seagull.DRAWER_MENU_TWTS) {
                    fragmentClass = TweetsFragment.class;
                } else if (it == Seagull.DRAWER_MENU_SRCH) {
                    fragmentClass = SearchFragment.class;
                } else if (it == Seagull.DRAWER_MENU_FLIN) {
                    fragmentClass = FollowingsFragment.class;
                } else if (it == Seagull.DRAWER_MENU_FLRS) {
                    fragmentClass = FollowersFragment.class;
                } else if (it == Seagull.DRAWER_MENU_DRFS) {
                    fragmentClass = DraftsFragment.class;
                } else if (it == Seagull.DRAWER_MENU_STNS) {
                    activityClass = SettingsActivity.class;
                } else {
                    //TODO: log error
                }
            }
        } else {
            //TODO: here should log this error
        }

        if (fragmentClass != null) {
            Method fragmentNewInstanceMethod;
            Fragment newFragment;
            try {
                fragmentNewInstanceMethod = fragmentClass.getMethod("newInstance", String.class, String.class);
                //TODO: Fragments pending to be implemented
                newFragment = (Fragment) fragmentNewInstanceMethod.invoke(null, "TO", "DO");

                fragmentManager.beginTransaction()
                        .replace(R.id.container, newFragment)
                        .commit();

            } catch(NoSuchMethodException e) {
                //TODO: log this exception
            } catch(InvocationTargetException e) {
                //TODO: log
            } catch(IllegalAccessException e) {
                //TODO: log
            }
        } else if (activityClass != null) {
            startActivity(new Intent(this, activityClass));
        }
    }
}
