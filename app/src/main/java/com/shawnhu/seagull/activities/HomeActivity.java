package com.shawnhu.seagull.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.shawnhu.seagull.adapters.AnyViewArrayAdapter;
import com.shawnhu.seagull.adapters.AnyViewArrayAdapterItem;
import com.shawnhu.seagull.fragments.NavigationDrawerFragment;
import com.shawnhu.seagull.R;

import java.lang.reflect.Method;


public abstract class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /*
     *   Subclasses MUST provide these data.
     */
    protected AnyViewArrayAdapter mDrawerListArrayAdapter;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                mDrawerListArrayAdapter);
    }
    @Override
    protected void onDestroy() {
    /*
     * NOTE: When pressing back button, activity will be destroyed, so will its fragments.
     *       But system will cache app, so, clear user data, and reload when calling onCreate.
     */
        //clear data
        mDrawerListArrayAdapter.clear();
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        AnyViewArrayAdapterItem i = mDrawerListArrayAdapter.getItem(position);
        FragmentManager fragmentManager = getSupportFragmentManager();

        Class targetClass = i.mActionClass;

        if (targetClass != null) {
            //TODO: targetClass(Fragment, Activity or whatever, might need args passed
            //TODO: which might be put in the adapter
            Class targetBaseClass = targetClass.getSuperclass();
            if (targetBaseClass == Fragment.class) {
                //Fragment, transmit to it
                try {
                    Method newFragmentInstance = targetClass.getMethod("newIntance", Bundle.class);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, (Fragment) newFragmentInstance.invoke(null, null))
                            .commit();
                } catch(Exception e) {
                    //TODO: log this error
                }

            } else if (targetBaseClass == Activity.class) {
                //Activity, start it
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
        actionBar.setTitle(mTitle);
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

}
