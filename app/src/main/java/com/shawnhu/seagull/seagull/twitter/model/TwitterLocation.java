package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;
import com.shawnhu.seagull.utils.ParseUtils;

import java.io.Serializable;

import twitter4j.GeoLocation;

public class TwitterLocation implements Serializable, Parcelable, JSONParcelable {

    private static final long serialVersionUID = -1690848439775407442L;

    public final double latitude, longitude;

    public static final Parcelable.Creator<TwitterLocation> CREATOR = new Parcelable.Creator<TwitterLocation>() {
        @Override
        public TwitterLocation createFromParcel(final Parcel in) {
            return new TwitterLocation(in);
        }

        @Override
        public TwitterLocation[] newArray(final int size) {
            return new TwitterLocation[size];
        }
    };

    public static final JSONParcelable.Creator<TwitterLocation> JSON_CREATOR = new JSONParcelable.Creator<TwitterLocation>() {
        @Override
        public TwitterLocation createFromParcel(final JSONParcel in) {
            return new TwitterLocation(in);
        }

        @Override
        public TwitterLocation[] newArray(final int size) {
            return new TwitterLocation[size];
        }
    };

    public TwitterLocation(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TwitterLocation(final GeoLocation location) {
        latitude = location != null ? location.getLatitude() : -1;
        longitude = location != null ? location.getLongitude() : -1;
    }

    public TwitterLocation(final JSONParcel in) {
        latitude = in.readDouble("latitude", -1);
        longitude = in.readDouble("longutude", -1);
    }

    public TwitterLocation(final android.location.Location location) {
        latitude = location != null ? location.getLatitude() : -1;
        longitude = location != null ? location.getLongitude() : -1;
    }

    public TwitterLocation(final Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public TwitterLocation(final String location_string) {
        if (location_string == null) {
            latitude = -1;
            longitude = -1;
            return;
        }
        final String[] longlat = location_string.split(",");
        if (longlat == null || longlat.length != 2) {
            latitude = -1;
            longitude = -1;
        } else {
            latitude = ParseUtils.parseDouble(longlat[0]);
            longitude = ParseUtils.parseDouble(longlat[1]);
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof TwitterLocation)) return false;
        final TwitterLocation other = (TwitterLocation) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    public boolean isValid() {
        return latitude >= 0 || longitude >= 0;
    }

    public GeoLocation toGeoLocation() {
        return isValid() ? new GeoLocation(latitude, longitude) : null;
    }

    @Override
    public String toString() {
        return "TwitterLocation{latitude=" + latitude + ", longitude=" + longitude + "}";
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeDouble("latitude", latitude);
        out.writeDouble("longitude", longitude);
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    public static TwitterLocation fromString(final String string) {
        final TwitterLocation location = new TwitterLocation(string);
        if (TwitterLocation.isValidLocation(location)) return location;
        return null;
    }

    public static boolean isValidLocation(final TwitterLocation location) {
        return location != null && location.isValid();
    }

    public static GeoLocation toGeoLocation(final TwitterLocation location) {
        return isValidLocation(location) ? location.toGeoLocation() : null;
    }

    public static String toString(final TwitterLocation location) {
        if (location == null) return null;
        return location.latitude + "," + location.longitude;
    }
}
