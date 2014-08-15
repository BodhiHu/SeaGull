package com.shawnhu.seagull.seagull;


import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.utils.ActivityUtils;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;
import com.shawnhu.seagull.app.AppPreferences;
import com.shawnhu.seagull.app.SeagullApplication;
import com.shawnhu.seagull.fragments.DraftsFragment;
import com.shawnhu.seagull.fragments.FollowersFragment;
import com.shawnhu.seagull.fragments.FollowingsFragment;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.fragments.NotificationsFragment;
import com.shawnhu.seagull.fragments.ProfileFragment;
import com.shawnhu.seagull.fragments.SearchFragment;
import com.shawnhu.seagull.fragments.TweetsFragment;
import com.shawnhu.seagull.misc.IconicItem;
import com.shawnhu.seagull.seagull.activities.SeagullPreferenceActivity;
import com.shawnhu.seagull.views.AvatarCard;

/**
 * Created by shawn on 14-7-23.
 */
public class Seagull extends SeagullApplication {

    /**
     * NAVIGATION MENU ITEMS
     */
    public static final String DRAWER_MENU_PROF = "Profile";
    public static final String DRAWER_MENU_HOME = "Home";
    public static final String DRAWER_MENU_NOTI = "Notifications";
    public static final String DRAWER_MENU_SRCH = "Search";
    public static final String DRAWER_MENU_TWTS = "Tweets";
    public static final String DRAWER_MENU_FLIN = "Followings";
    public static final String DRAWER_MENU_FLRS = "Followers";
    public static final String DRAWER_MENU_DRFS = "Drafts";
    public static final String DRAWER_MENU_PRFS = "Preferences";

    static AvatarCard aC = new AvatarCard(null, "Seagull", "@Seagull");
    public static AnyViewArrayAdapterItem mSeagullDrawerItems[] = {
            /*  layout,          content,  getViewInterface,           targetActionClass, actionBarTitle */
            new AnyViewArrayAdapterItem(R.layout.layout_avatar, aC, aC, ProfileFragment.class, DRAWER_MENU_PROF),

            /* ~default_layout~, content, ~default getViewInterface~,  targetActionClass, actionBarTitle */
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_HOME), HomeFragment.class,              DRAWER_MENU_HOME),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_NOTI), NotificationsFragment.class,     DRAWER_MENU_NOTI),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_search,       DRAWER_MENU_SRCH), SearchFragment.class,            DRAWER_MENU_SRCH),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_TWTS), TweetsFragment.class,            DRAWER_MENU_TWTS),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_FLIN), FollowingsFragment.class,        DRAWER_MENU_FLIN),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_info_details, DRAWER_MENU_FLRS), FollowersFragment.class,         DRAWER_MENU_FLRS),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_edit,         DRAWER_MENU_DRFS), DraftsFragment.class,            DRAWER_MENU_DRFS),
            new AnyViewArrayAdapterItem(new IconicItem(android.R.drawable.ic_menu_preferences,  DRAWER_MENU_PRFS), SeagullPreferenceActivity.class, DRAWER_MENU_PRFS),
    };

    /**
     * PREFERENCE KEYS
     */
    static public String PREF_SEAGULL_NOTIFICATION_ON;
    static public String PREF_SEAGULL_NOTIFICATION_RINGTONE;
    static public String PREF_SEAGULL_NOTIFICATION_VIRATE;

    /**
     * Seagull's themes, KEY is PREF_APP_THEME, which will be handled by wrapper
     */
    static public String SEAGULL_THEMES[] = {
            Integer.toString(R.style.Theme_Day),
            Integer.toString(R.style.Theme_Night),
    };

    @Override
    public void onCreate() {
        super.onCreate();

        AppPreferences.addPreferencesToMap(AppPreferences.PREF_APP_THEME, SEAGULL_THEMES);
        AppPreferences.mDefaultAppTheme = ActivityUtils.getTheme(getApplicationContext(), AppPreferences.mDefaultAppTheme);

        PREF_SEAGULL_NOTIFICATION_ON = getString(R.string.PREF_SEAGULL_NOTIFICATION_ON);
        PREF_SEAGULL_NOTIFICATION_RINGTONE = getString(R.string.PREF_SEAGULL_NOTIFICATION_RINGTONE);
        PREF_SEAGULL_NOTIFICATION_VIRATE = getString(R.string.PREF_SEAGULL_NOTIFICATION_VIRATE);

        TwitterManager.init(this);
    }
}
