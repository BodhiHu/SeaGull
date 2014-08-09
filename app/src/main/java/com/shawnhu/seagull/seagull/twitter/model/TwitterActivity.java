package com.shawnhu.seagull.seagull.twitter.model;

import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;

import java.util.Arrays;
import java.util.Date;

public class TwitterActivity implements Comparable<TwitterActivity>, JSONParcelable {

    public static final JSONParcelable.Creator<TwitterActivity> JSON_CREATOR = new JSONParcelable.Creator<TwitterActivity>() {
        @Override
        public TwitterActivity createFromParcel(final JSONParcel in) {
            return new TwitterActivity(in);
        }

        @Override
        public TwitterActivity[] newArray(final int size) {
            return new TwitterActivity[size];
        }
    };

    public final static int ACTION_FAVORITE = twitter4j.Activity.Action.ACTION_FAVORITE;
    public final static int ACTION_FOLLOW = twitter4j.Activity.Action.ACTION_FOLLOW;
    public final static int ACTION_MENTION = twitter4j.Activity.Action.ACTION_MENTION;
    public final static int ACTION_REPLY = twitter4j.Activity.Action.ACTION_REPLY;
    public final static int ACTION_RETWEET = twitter4j.Activity.Action.ACTION_RETWEET;
    public final static int ACTION_LIST_MEMBER_ADDED = twitter4j.Activity.Action.ACTION_LIST_MEMBER_ADDED;
    public final static int ACTION_LIST_CREATED = twitter4j.Activity.Action.ACTION_LIST_CREATED;

    public final long account_id, activity_timestamp, max_position, min_position;
    public final int action;

    public final TwitterUser[] sources;
    public final TwitterUser[] target_users;
    public final TwitterStatus[] target_statuses;
    public final TwitterUserList[] target_user_lists;

    public final TwitterUserList[] target_object_user_lists;
    public final TwitterStatus[] target_object_statuses;

    public TwitterActivity(final twitter4j.Activity activity, final long account_id) {
        this.account_id = account_id;
        activity_timestamp = getTime(activity.getCreatedAt());
        action = activity.getAction().getActionId();
        max_position = activity.getMaxPosition();
        min_position = activity.getMinPosition();
        final int sources_size = activity.getSourcesSize();
        sources = new TwitterUser[sources_size];
        for (int i = 0; i < sources_size; i++) {
            sources[i] = new TwitterUser(activity.getSources()[i], account_id);
        }
        final int targets_size = activity.getTargetsSize();
        if (action == ACTION_FOLLOW || action == ACTION_MENTION || action == ACTION_LIST_MEMBER_ADDED) {
            target_users = new TwitterUser[targets_size];
            target_statuses = null;
            target_user_lists = null;
            for (int i = 0; i < targets_size; i++) {
                target_users[i] = new TwitterUser(activity.getTargetUsers()[i], account_id);
            }
        } else if (action == ACTION_LIST_CREATED) {
            target_user_lists = new TwitterUserList[targets_size];
            target_statuses = null;
            target_users = null;
            for (int i = 0; i < targets_size; i++) {
                target_user_lists[i] = new TwitterUserList(activity.getTargetUserLists()[i], account_id);
            }
        } else {
            target_statuses = new TwitterStatus[targets_size];
            target_users = null;
            target_user_lists = null;
            for (int i = 0; i < targets_size; i++) {
                target_statuses[i] = new TwitterStatus(activity.getTargetStatuses()[i], account_id, false);
            }
        }
        final int target_objects_size = activity.getTargetObjectsSize();
        if (action == ACTION_LIST_MEMBER_ADDED) {
            target_object_user_lists = new TwitterUserList[target_objects_size];
            target_object_statuses = null;
            for (int i = 0; i < target_objects_size; i++) {
                target_object_user_lists[i] = new TwitterUserList(activity.getTargetObjectUserLists()[i], account_id);
            }
        } else if (action == ACTION_LIST_CREATED) {
            target_object_user_lists = null;
            target_object_statuses = null;
        } else {
            target_object_statuses = new TwitterStatus[target_objects_size];
            target_object_user_lists = null;
            for (int i = 0; i < target_objects_size; i++) {
                target_object_statuses[i] = new TwitterStatus(activity.getTargetObjectStatuses()[i], account_id,
                        false);
            }
        }
    }

    public TwitterActivity(final JSONParcel in) {
        account_id = in.readLong("account_id");
        activity_timestamp = in.readLong("activity_timestamp");
        max_position = in.readLong("max_position");
        min_position = in.readLong("min_position");
        action = in.readInt("action");
        sources = in.readParcelableArray("sources", TwitterUser.JSON_CREATOR);
        target_users = in.readParcelableArray("target_users", TwitterUser.JSON_CREATOR);
        target_statuses = in.readParcelableArray("target_statuses", TwitterStatus.JSON_CREATOR);
        target_user_lists = in.readParcelableArray("target_user_lists", TwitterUserList.JSON_CREATOR);
        target_object_user_lists = in.readParcelableArray("target_object_user_lists", TwitterUserList.JSON_CREATOR);
        target_object_statuses = in.readParcelableArray("target_object_statuses", TwitterStatus.JSON_CREATOR);
    }

    @Override
    public int compareTo(final TwitterActivity another) {
        if (another == null) return 0;
        final long delta = another.activity_timestamp - activity_timestamp;
        if (delta < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        if (delta > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) delta;
    }

    @Override
    public boolean equals(final Object that) {
        if (!(that instanceof TwitterActivity)) return false;
        final TwitterActivity activity = (TwitterActivity) that;
        return max_position == activity.max_position && min_position == activity.min_position;
    }

    @Override
    public String toString() {
        return "TwitterActivity{account_id=" + account_id + ", activity_timestamp=" + activity_timestamp
                + ", max_position=" + max_position + ", min_position=" + min_position + ", action=" + action
                + ", sources=" + Arrays.toString(sources) + ", target_users=" + Arrays.toString(target_users)
                + ", target_statuses=" + Arrays.toString(target_statuses) + ", target_user_lists="
                + Arrays.toString(target_user_lists) + ", target_object_user_lists="
                + Arrays.toString(target_object_user_lists) + ", target_object_statuses="
                + Arrays.toString(target_object_statuses) + "}";
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeLong("account_id", account_id);
        out.writeLong("activity_timestamp", activity_timestamp);
        out.writeLong("max_position", max_position);
        out.writeLong("min_position", min_position);
        out.writeInt("action", action);
        out.writeParcelableArray("sources", sources);
        out.writeParcelableArray("target_users", target_users);
        out.writeParcelableArray("target_statuses", target_statuses);
        out.writeParcelableArray("target_user_lists", target_user_lists);
        out.writeParcelableArray("target_object_user_lists", target_object_user_lists);
        out.writeParcelableArray("target_object_statuses", target_object_statuses);
    }

    private static long getTime(final Date date) {
        return date != null ? date.getTime() : 0;
    }

}
