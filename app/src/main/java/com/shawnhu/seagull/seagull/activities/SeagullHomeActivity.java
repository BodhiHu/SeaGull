package com.shawnhu.seagull.seagull.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractHomeNavDrawerActivity;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.fragments.ComposeFragment;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullHomeFragment;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullProfileFragment;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.tasks.GetUserProfileTask;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapter;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

import twitter4j.User;

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
                                ImageView iv = (ImageView) v.findViewById(R.id.item_icon);
                                TextView tv  = (TextView)  v.findViewById(R.id.item_name);
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

        mGetUserProfileTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            onCreateHomeMenu(this, menu, getMenuInflater());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (onHomeItemSelected(this, item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    public static void onCreateHomeMenu(Activity activity, Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home, menu);
        SearchManager searchManager =
                (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(new ComponentName(activity, SeagullSearchActivity.class)));
        }
    }
    public static boolean onHomeItemSelected(SeagullHomeActivity activity, MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_compose) {
            ComposeFragment composeFragment = ComposeFragment.newInstance(null);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, composeFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            return true;
        } else if (id == R.id.action_settings) {
            activity.setCurrentPosition(Seagull.NAV_PREFRENCE);
            return true;
        }

        return false;
    }

    GetUserProfileTask mGetUserProfileTask =
        new GetUserProfileTask(this, Seagull.sCurrentAccount.sAccountId, Seagull.sCurrentAccount.sAccountId) {
            User mUser;
            Bitmap mProfileBmp = null;
            @Override
            protected Response<twitter4j.User> doInBackground(final Void... params) {
                Response<User> response = super.doInBackground(params);
                if (response != null && response.hasData()) {
                    mUser = response.getData();

                    ImageLoader imageLoader = TwitterManager.getInstance().getImageLoaderWrapper().getImageLoader();
                    mProfileBmp = imageLoader.loadImageSync(mUser.getProfileImageURL().toString());
                }

                return response;
            }
            @Override
            protected void onPostExecuteSafe(final Response<User> result) {
                //on UI thread
                if (result.hasData()) {
                    BitmapDrawable profileDrawable = null;
                    if (mProfileBmp != null) {
                        profileDrawable = new BitmapDrawable(mProfileBmp);
                    }

                    Seagull.aC.setUpCard(profileDrawable, mUser.getScreenName(), "@" + mUser.getName());
                    mDrawerListArrayAdapter.notifyDataSetChanged();
                }
            }
        };
}
