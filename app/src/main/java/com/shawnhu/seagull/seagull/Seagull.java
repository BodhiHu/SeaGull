package com.shawnhu.seagull.seagull;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.adapters.GeneralAdapter;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.seagull.activities.SeagullHomeActivity;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * Created by shawn on 14-7-23.
 */
public class Seagull {
    GeneralAdapter.Item mDrawerItems[] = {
            new GeneralAdapter.Item(new SeagullHomeActivity.DrawerItem(android.R.drawable.ic_menu_info_details, "Home")),
            /*
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_info_details, "Home")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_info_details, "Notifications")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_search,       "Search")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_info_details, "Tweets")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_info_details, "Following")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_info_details, "Followers")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_edit,         "Drafts")),
            mHomeDrawerMenusAdapter.new Item(new DrawerItem(android.R.drawable.ic_menu_preferences,  "Settings")),
            */
    };


}
