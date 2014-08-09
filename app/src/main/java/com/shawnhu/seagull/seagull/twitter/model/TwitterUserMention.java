package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;
import com.shawnhu.seagull.utils.JSON.JSONSerializer;

import org.json.JSONArray;
import org.json.JSONException;

import twitter4j.Status;
import twitter4j.UserMentionEntity;

public class TwitterUserMention implements Parcelable, JSONParcelable {

    public static final Parcelable.Creator<TwitterUserMention> CREATOR = new Parcelable.Creator<TwitterUserMention>() {
        @Override
        public TwitterUserMention createFromParcel(final Parcel in) {
            return new TwitterUserMention(in);
        }

        @Override
        public TwitterUserMention[] newArray(final int size) {
            return new TwitterUserMention[size];
        }
    };
    public static final JSONParcelable.Creator<TwitterUserMention> JSON_CREATOR = new JSONParcelable.Creator<TwitterUserMention>() {
        @Override
        public TwitterUserMention createFromParcel(final JSONParcel in) {
            return new TwitterUserMention(in);
        }

        @Override
        public TwitterUserMention[] newArray(final int size) {
            return new TwitterUserMention[size];
        }
    };
    public long id;

    public String name, screen_name;

    public TwitterUserMention(final JSONParcel in) {
        id = in.readLong("id");
        name = in.readString("name");
        screen_name = in.readString("screen_name");
    }

    public TwitterUserMention(final Parcel in) {
        id = in.readLong();
        name = in.readString();
        screen_name = in.readString();
    }

    public TwitterUserMention(final UserMentionEntity entity) {
        id = entity.getId();
        name = entity.getName();
        screen_name = entity.getScreenName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof TwitterUserMention)) return false;
        final TwitterUserMention other = (TwitterUserMention) obj;
        if (id != other.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ id >>> 32);
        return result;
    }

    @Override
    public String toString() {
        return "TwitterUserMention{id=" + id + ", name=" + name + ", screen_name=" + screen_name + "}";
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeLong("id", id);
        out.writeString("name", name);
        out.writeString("screen_name", screen_name);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(screen_name);
    }

    public static TwitterUserMention[] fromJSONString(final String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return JSONSerializer.createArray(JSON_CREATOR, new JSONArray(json));
        } catch (final JSONException e) {
            return null;
        }
    }

    public static TwitterUserMention[] fromStatus(final Status status) {
        return fromUserMentionEntities(status.getUserMentionEntities());
    }

    public static TwitterUserMention[] fromUserMentionEntities(final UserMentionEntity[] entities) {
        if (entities == null) return null;
        final TwitterUserMention[] mentions = new TwitterUserMention[entities.length];
        for (int i = 0, j = entities.length; i < j; i++) {
            mentions[i] = new TwitterUserMention(entities[i]);
        }
        return mentions;
    }

    public static boolean hasMention(final TwitterUserMention[] mentions, final long id) {
        if (mentions == null) return false;
        for (final TwitterUserMention mention : mentions) {
            if (mention.id == id) return true;
        }
        return false;
    }

    public static boolean hasMention(final String json, final long id) {
        final TwitterUserMention[] mentions = fromJSONString(json);
        if (mentions == null) return false;
        for (final TwitterUserMention mention : mentions) {
            if (mention.id == id) return true;
        }
        return false;
    }

}
