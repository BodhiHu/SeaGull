package com.shawnhu.seagull.seagull.twitter.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class TwitterStatusUpdate implements Parcelable {

    public static final Parcelable.Creator<TwitterStatusUpdate> CREATOR = new Parcelable.Creator<TwitterStatusUpdate>() {
        @Override
        public TwitterStatusUpdate createFromParcel(final Parcel in) {
            return new TwitterStatusUpdate(in);
        }

        @Override
        public TwitterStatusUpdate[] newArray(final int size) {
            return new TwitterStatusUpdate[size];
        }
    };

    //TODO: drop array
    public final TwitterAccount[] accounts;
    public final TwitterMediaUpdate[] medias;
    public final String text;
    public final TwitterLocation location;
    public final long in_reply_to_status_id;
    public final boolean is_possibly_sensitive;

    /**
     * @deprecated It has too much arguments to call, use
     *             <b>TwitterStatusUpdate.Builder</b> instead.
     */
    @Deprecated
    public TwitterStatusUpdate(final TwitterAccount[] accounts, final String text, final TwitterLocation location,
                               final TwitterMediaUpdate[] medias, final long in_reply_to_status_id, final boolean is_possibly_sensitive) {
        this.accounts = accounts;
        this.text = text;
        this.location = location;
        this.medias = medias;
        this.in_reply_to_status_id = in_reply_to_status_id;
        this.is_possibly_sensitive = is_possibly_sensitive;
    }

    public TwitterStatusUpdate(final Context context, final TwitterDraftItem draft) {
        accounts = TwitterAccount.getAccounts(context, draft.account_ids);
        text = draft.text;
        location = draft.location;
        medias = draft.medias;
        in_reply_to_status_id = draft.in_reply_to_status_id;
        is_possibly_sensitive = draft.is_possibly_sensitive;
    }

    public TwitterStatusUpdate(final Parcel in) {
        accounts = in.createTypedArray(TwitterAccount.CREATOR);
        text = in.readString();
        location = in.readParcelable(TwitterLocation.class.getClassLoader());
        medias = in.createTypedArray(TwitterMediaUpdate.CREATOR);
        in_reply_to_status_id = in.readLong();
        is_possibly_sensitive = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "TwitterStatusUpdate{accounts=" + Arrays.toString(accounts) + ", medias=" + Arrays.toString(medias)
                + ", text=" + text + ", location=" + location + ", in_reply_to_status_id=" + in_reply_to_status_id
                + ", is_possibly_sensitive=" + is_possibly_sensitive + "}";
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedArray(accounts, flags);
        dest.writeString(text);
        dest.writeParcelable(location, flags);
        dest.writeTypedArray(medias, flags);
        dest.writeLong(in_reply_to_status_id);
        dest.writeInt(is_possibly_sensitive ? 1 : 0);
    }

    public static final class Builder {

        private TwitterAccount[] accounts;
        private String text;
        private TwitterLocation location;
        private TwitterMediaUpdate[] medias;
        private long in_reply_to_status_id;
        private boolean is_possibly_sensitive;

        public Builder() {

        }

        public Builder(final TwitterStatusUpdate base) {
            accounts(base.accounts);
            text(base.text);
            medias(base.medias);
            location(base.location);
            inReplyToStatusId(base.in_reply_to_status_id);
            isPossiblySensitive(base.is_possibly_sensitive);
        }

        public Builder accounts(final TwitterAccount[] accounts) {
            this.accounts = accounts;
            return this;
        }

        public TwitterStatusUpdate build() {
            return new TwitterStatusUpdate(accounts, text, location, medias, in_reply_to_status_id,
                    is_possibly_sensitive);
        }

        public Builder inReplyToStatusId(final long in_reply_to_status_id) {
            this.in_reply_to_status_id = in_reply_to_status_id;
            return this;
        }

        public Builder isPossiblySensitive(final boolean is_possibly_sensitive) {
            this.is_possibly_sensitive = is_possibly_sensitive;
            return this;
        }

        public Builder location(final TwitterLocation location) {
            this.location = location;
            return this;
        }

        public Builder medias(final TwitterMediaUpdate... medias) {
            this.medias = medias;
            return this;
        }

        public Builder text(final String text) {
            this.text = text;
            return this;
        }
    }

}
