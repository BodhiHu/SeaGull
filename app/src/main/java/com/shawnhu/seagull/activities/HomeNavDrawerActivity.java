package com.shawnhu.seagull.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.shawnhu.seagull.widgets.AnyViewArrayAdapter;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;
import com.shawnhu.seagull.fragments.NavigationDrawerFragment;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.app.AppPreferences;
import com.shawnhu.seagull.utils.ActivityUtils;

import java.lang.reflect.Method;


/**
 * TODO: base home activity, which should handle:
 *        1. navigation;
 *        2. theme change
 *        3. actionbar title
 *        ?. any other stuff
 */
public abstract class HomeNavDrawerActivity
        extends     ActionBarActivity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks
{

    /**
     * Subclasses MUST provide these data.
     */
    protected AnyViewArrayAdapter mDrawerListArrayAdapter;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private int mLastFragmentPosition = 0;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mAppTitle;
    private CharSequence mSubTitle;

    /**
     * currentTheme
     */
    private int mCurrentTheme = AppPreferences.mDefaultAppTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkInstanceData();

        mCurrentTheme = ActivityUtils.getTheme(this, mCurrentTheme);
        setTheme(mCurrentTheme);

        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mAppTitle = getTitle();
        mSubTitle = getSupportActionBar().getSubtitle();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                mDrawerListArrayAdapter);
    }

    @Override
    protected void onResume() {
        if (mCurrentTheme !=
                ActivityUtils.getTheme(this, mCurrentTheme)) {
            ActivityUtils.applyTheme(this);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
    /**
     * NOTE: When pressing back button, activity will be destroyed, so will its fragments.
     *       But system will cache app, so, clear init data.
     */
        mDrawerListArrayAdapter.clear();
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        AnyViewArrayAdapterItem i = mDrawerListArrayAdapter.getItem(position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        Class targetClass = i.mActionClass;

        if (targetClass != null) {
            //TODO: Fragment, Activity or whatever, might need args passed which might be put in the adapter
            if (Fragment.class.isAssignableFrom(targetClass)) {
                //Fragment, transform to it
                try {
                    Method newFragmentInstance = targetClass.getMethod("newInstance", Bundle.class);
                    //TODO: might need args
                    Bundle args = new Bundle();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, (Fragment) newFragmentInstance.invoke(null, args))
                            .commit();

                    if (i.mName != null && i.mName != "") {
                        mSubTitle = i.mName;
                        getSupportActionBar().setSubtitle(mSubTitle);
                    }
                    mLastFragmentPosition = position;
                } catch(Exception e) {
                    Log.e(HomeNavDrawerActivity.class.getSimpleName(), e.toString());
                    //TODO: log this error
                }

            } else if (Activity.class.isAssignableFrom(targetClass)) {
                //Activity, start it
                mNavigationDrawerFragment.setCurrentPosition(mLastFragmentPosition);
                startActivity(new Intent(this, targetClass));
            } else {
                //Other stuff, TODO
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mAppTitle);
        actionBar.setSubtitle(mSubTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkInstanceData() {
         if (mDrawerListArrayAdapter == null) {
            throw new NullPointerException();
        }
    }

}
