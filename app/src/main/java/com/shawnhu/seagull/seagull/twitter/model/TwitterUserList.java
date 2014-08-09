package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;
import com.shawnhu.seagull.utils.ParseUtils;

import twitter4j.User;

public class TwitterUserList implements Parcelable, JSONParcelable, Comparable<TwitterUserList> {

    public static final Parcelable.Creator<TwitterUserList> CREATOR = new Parcelable.Creator<TwitterUserList>() {
        @Override
        public TwitterUserList createFromParcel(final Parcel in) {
            return new TwitterUserList(in);
        }

        @Override
        public TwitterUserList[] newArray(final int size) {
            return new TwitterUserList[size];
        }
    };

    public static final JSONParcelable.Creator<TwitterUserList> JSON_CREATOR = new JSONParcelable.Creator<TwitterUserList>() {
        @Override
        public TwitterUserList createFromParcel(final JSONParcel in) {
            return new TwitterUserList(in);
        }

        @Override
        public TwitterUserList[] newArray(final int size) {
            return new TwitterUserList[size];
        }
    };

    public final int members_count, subscribers_count;

    public final long account_id, id, user_id, position;

    public final boolean is_public, is_following;

    public final String description, name, user_screen_name, user_name, user_profile_image_url;

    public TwitterUserList(final JSONParcel in) {
        position = in.readLong("position");
        account_id = in.readLong("account_id");
        id = in.readLong("list_id");
        is_public = in.readBoolean("is_public");
        is_following = in.readBoolean("is_following");
        name = in.readString("name");
        description = in.readString("description");
        user_id = in.readLong("user_id");
        user_name = in.readString("user_name");
        user_screen_name = in.readString("user_screen_name");
        user_profile_image_url = in.readString("user_profile_image_url");
        members_count = in.readInt("members_count");
        subscribers_count = in.readInt("subscribers_count");
    }

    public TwitterUserList(final Parcel in) {
        position = in.readLong();
        account_id = in.readLong();
        id = in.readLong();
        is_public = in.readInt() == 1;
        is_following = in.readInt() == 1;
        name = in.readString();
        description = in.readString();
        user_id = in.readLong();
        user_name = in.readString();
        user_screen_name = in.readString();
        user_profile_image_url = in.readString();
        members_count = in.readInt();
        subscribers_count = in.readInt();
    }

    public TwitterUserList(final twitter4j.UserList list, final long account_id) {
        this(list, account_id, 0);
    }

    public TwitterUserList(final twitter4j.UserList list, final long account_id, final long position) {
        this(list, account_id, position, list.isFollowing());
    }

    public TwitterUserList(final twitter4j.UserList list, final long account_id, final long position,
                           final boolean is_following) {
        final User user = list.getUser();
        this.position = position;
        this.account_id = account_id;
        id = list.getId();
        is_public = list.isPublic();
        this.is_following = is_following;
        name = list.getName();
        description = list.getDescription();
        user_id = user.getId();
        user_name = user.getName();
        user_screen_name = user.getScreenName();
        user_profile_image_url = ParseUtils.parseString(user.getProfileImageUrlHttps());
        members_count = list.getMemberCount();
        subscribers_count = list.getSubscriberCount();
    }

    @Override
    public int compareTo(final TwitterUserList another) {
        if (another == null) return 0;
        final long diff = position - another.position;
        if (diff > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (diff < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) diff;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof TwitterUserList)) return false;
        final TwitterUserList other = (TwitterUserList) obj;
        if (account_id != other.account_id) return false;
        if (id != other.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (account_id ^ account_id >>> 32);
        result = prime * result + (int) (id ^ id >>> 32);
        return result;
    }

    @Override
    public String toString() {
        return "TwitterUserList{members_count=" + members_count + ", subscribers_count=" + subscribers_count
                + ", account_id=" + account_id + ", id=" + id + ", user_id=" + user_id + ", position=" + position
                + ", is_public=" + is_public + ", is_following=" + is_following + ", description=" + description
                + ", name=" + name + ", user_screen_name=" + user_screen_name + ", user_name=" + user_name
                + ", user_profile_image_url=" + user_profile_image_url + "}";
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeLong("position", position);
        out.writeLong("account_id", account_id);
        out.writeLong("list_id", id);
        out.writeBoolean("is_public", is_public);
        out.writeBoolean("is_following", is_following);
        out.writeString("name", name);
        out.writeString("description", description);
        out.writeLong("user_id", user_id);
        out.writeString("user_name", user_name);
        out.writeString("user_screen_name", user_screen_name);
        out.writeString("user_profile_image_url", user_profile_image_url);
        out.writeInt("members_count", members_count);
        out.writeInt("subscribers_count", subscribers_count);
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(position);
        out.writeLong(account_id);
        out.writeLong(id);
        out.writeInt(is_public ? 1 : 0);
        out.writeInt(is_following ? 1 : 0);
        out.writeString(name);
        out.writeString(description);
        out.writeLong(user_id);
        out.writeString(user_name);
        out.writeString(user_screen_name);
        out.writeString(user_profile_image_url);
        out.writeInt(members_count);
        out.writeInt(subscribers_count);
    }

}
