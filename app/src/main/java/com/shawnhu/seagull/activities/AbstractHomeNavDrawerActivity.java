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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * TODO: base home activity, which should handle:
 *        1. navigation;
 *        2. theme change
 *        3. actionbar title
 *        ?. other stuff
 */
public abstract class AbstractHomeNavDrawerActivity
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
    protected NavigationDrawerFragment mNavigationDrawerFragment;
    private int mLastFragmentPosition = 0;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mAppTitle;
    private CharSequence mSubTitle;

    protected String PRIVATE_PREFERENCE_NAME = (((Object) this).getClass().getName()) + "_private preference";
    static final protected String PREF_LAST_POS = "PREF_LAST_POS";

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

        mLastFragmentPosition = getSharedPreferences(PRIVATE_PREFERENCE_NAME, MODE_PRIVATE).getInt(PREF_LAST_POS, 0);
        saveCurrentPosition(mLastFragmentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        getSharedPreferences(PRIVATE_PREFERENCE_NAME, MODE_PRIVATE).edit()
                .putInt(PREF_LAST_POS, mLastFragmentPosition)
                .apply();

        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        AnyViewArrayAdapterItem i = mDrawerListArrayAdapter.getItem(position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        Class targetClass = i.mActionClass;

        if (targetClass != null) {
            //TODO: Fragment, TwitterActivity or whatever, might need args passed which might be put in the adapter
            if (Fragment.class.isAssignableFrom(targetClass)) {
                //Fragment, transform to it
                try {
                    Method newFragmentInstance = targetClass.getMethod("newInstance", Bundle.class);
                    Fragment fragment = (Fragment) newFragmentInstance.invoke(null, i.mActionArgs);

                    if (fragment != null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();

                        if (i.mName != null && i.mName != "") {
                            mSubTitle = i.mName;
                            getSupportActionBar().setSubtitle(mSubTitle);
                        }
                        mLastFragmentPosition = position;
                    }
                } catch(Exception e) {
                    Log.e(AbstractHomeNavDrawerActivity.class.getSimpleName(), e.toString());
                    e.printStackTrace();

                    if (e instanceof InvocationTargetException) {
                        InvocationTargetException ie = (InvocationTargetException) e;
                        Throwable th = ie.getCause();
                        if (th != null) {
                            Log.e(AbstractHomeNavDrawerActivity.class.getSimpleName(), "InvocationTargetException caused by:");
                            Log.e(AbstractHomeNavDrawerActivity.class.getSimpleName(), th.toString());
                            th.printStackTrace();
                        }
                    }
                }

            } else if (Activity.class.isAssignableFrom(targetClass)) {
                //TwitterActivity, start it
                mNavigationDrawerFragment.saveCurrentPosition(mLastFragmentPosition);
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
        super.onCreateOptionsMenu(menu);

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            DrawerLayout mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
            if (mNavigationDrawerFragment.isDrawerOpen()) {
                mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
            } else {
                mDrawerLayout.openDrawer(findViewById(R.id.navigation_drawer));
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkInstanceData() {
         if (mDrawerListArrayAdapter == null) {
            throw new NullPointerException();
        }
    }

    protected void saveCurrentPosition(int pos) {
        mNavigationDrawerFragment.saveCurrentPosition(pos);
    }
    protected void setCurrentPosition(int pos) {
        mNavigationDrawerFragment.setPosition(pos);
    }

}
