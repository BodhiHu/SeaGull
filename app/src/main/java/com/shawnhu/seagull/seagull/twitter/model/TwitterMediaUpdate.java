package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;
import com.shawnhu.seagull.utils.JSON.JSONSerializer;

import org.json.JSONArray;
import org.json.JSONException;

public class TwitterMediaUpdate implements Parcelable, JSONParcelable {

    public static final Parcelable.Creator<TwitterMediaUpdate> CREATOR = new Parcelable.Creator<TwitterMediaUpdate>() {
        @Override
        public TwitterMediaUpdate createFromParcel(final Parcel in) {
            return new TwitterMediaUpdate(in);
        }

        @Override
        public TwitterMediaUpdate[] newArray(final int size) {
            return new TwitterMediaUpdate[size];
        }
    };

    public static final JSONParcelable.Creator<TwitterMediaUpdate> JSON_CREATOR = new JSONParcelable.Creator<TwitterMediaUpdate>() {
        @Override
        public TwitterMediaUpdate createFromParcel(final JSONParcel in) {
            return new TwitterMediaUpdate(in);
        }

        @Override
        public TwitterMediaUpdate[] newArray(final int size) {
            return new TwitterMediaUpdate[size];
        }
    };

    public final String uri;
    public final int type;

    public TwitterMediaUpdate(final JSONParcel in) {
        uri = in.readString("uri");
        type = in.readInt("type");
    }

    public TwitterMediaUpdate(final Parcel in) {
        uri = in.readString();
        type = in.readInt();
    }

    public TwitterMediaUpdate(final String uri, final int type) {
        this.uri = uri;
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "TwitterMediaUpdate{uri=" + uri + ", type=" + type + "}";
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeString("uri", uri);
        out.writeInt("type", type);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(uri);
        dest.writeInt(type);
    }

    public static TwitterMediaUpdate[] fromJSONString(final String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return JSONSerializer.createArray(JSON_CREATOR, new JSONArray(json));
        } catch (final JSONException e) {
            return null;
        }
    }

}