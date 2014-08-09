package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TwitterStatusShortenResult implements Parcelable {

    public static final Parcelable.Creator<TwitterStatusShortenResult> CREATOR = new Parcelable.Creator<TwitterStatusShortenResult>() {

        @Override
        public TwitterStatusShortenResult createFromParcel(final Parcel source) {
            return new TwitterStatusShortenResult(source);
        }

        @Override
        public TwitterStatusShortenResult[] newArray(final int size) {
            return new TwitterStatusShortenResult[size];
        }
    };

    public final String shortened;
    public final int error_code;
    public final String error_message;

    public TwitterStatusShortenResult(final int errorCode, final String errorMessage) {
        if (errorCode == 0) throw new IllegalArgumentException("Error code must not be 0");
        shortened = null;
        error_code = errorCode;
        error_message = errorMessage;
    }

    public TwitterStatusShortenResult(final Parcel src) {
        shortened = src.readString();
        error_code = src.readInt();
        error_message = src.readString();
    }

    public TwitterStatusShortenResult(final String shortened) {
        if (shortened == null) throw new IllegalArgumentException("Shortened text must not be null");
        this.shortened = shortened;
        error_code = 0;
        error_message = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "TwitterStatusShortenResult{shortened=" + shortened + ", error_code=" + error_code + ", error_message="
                + error_message + "}";
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(shortened);
        dest.writeInt(error_code);
        dest.writeString(error_message);
    }
}
