package com.shawnhu.seagull.seagull;


import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbstractPreferenceActivity;
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
import com.shawnhu.seagull.views.AvatarCard;

/**
 * Created by shawn on 14-7-23.
 */
public class Seagull {
    public static final String DRAWER_MENU_HOME = "Home";
    public static final String DRAWER_MENU_NOTI = "Notifications";
    public static final String DRAWER_MENU_SRCH = "Search";
    public static final String DRAWER_MENU_TWTS = "Tweets";
    public static final String DRAWER_MENU_FLIN = "Followings";
    public static final String DRAWER_MENU_FLRS = "Followers";
    public static final String DRAWER_MENU_DRFS = "Drafts";
    public static final String DRAWER_MENU_STNS = "Settins";

    static AvatarCard aC = new AvatarCard(null, "Seagull", "@Seagull");
    public static AnyViewArrayAdapterItem mSeagullDrawerItems[] = {
            new AnyViewArrayAdapterItem(R.layout.layout_avatar, aC, aC, ProfileFragment.class),

            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_HOME), HomeFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_NOTI), NotificationsFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_search,       DRAWER_MENU_SRCH), SearchFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_TWTS), TweetsFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_FLIN), FollowingsFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_FLRS), FollowersFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_edit,         DRAWER_MENU_DRFS), DraftsFragment.class),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_preferences,  DRAWER_MENU_STNS), AbstractPreferenceActivity.class),
    };


}
