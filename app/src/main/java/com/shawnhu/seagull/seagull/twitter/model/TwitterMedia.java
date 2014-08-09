package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.shawnhu.seagull.seagull.twitter.utils.MediaPreviewUtils;
import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;
import com.shawnhu.seagull.utils.JSON.JSONSerializer;
import com.shawnhu.seagull.utils.ParseUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.EntitySupport;
import twitter4j.MediaEntity;
import twitter4j.URLEntity;

public class TwitterMedia implements Parcelable, JSONParcelable {

    public static final int TYPE_IMAGE = 1;

    public static final Parcelable.Creator<TwitterMedia> CREATOR = new Parcelable.Creator<TwitterMedia>() {
        @Override
        public TwitterMedia createFromParcel(final Parcel in) {
            return new TwitterMedia(in);
        }

        @Override
        public TwitterMedia[] newArray(final int size) {
            return new TwitterMedia[size];
        }
    };

    public static final JSONParcelable.Creator<TwitterMedia> JSON_CREATOR = new JSONParcelable.Creator<TwitterMedia>() {
        @Override
        public TwitterMedia createFromParcel(final JSONParcel in) {
            return new TwitterMedia(in);
        }

        @Override
        public TwitterMedia[] newArray(final int size) {
            return new TwitterMedia[size];
        }
    };

    public final String url, media_url;
    public final int start, end, type;

    public TwitterMedia(final JSONParcel in) {
        url = in.readString("url");
        media_url = in.readString("media_url");
        start = in.readInt("start");
        end = in.readInt("end");
        type = in.readInt("type");
    }

    public TwitterMedia(final MediaEntity entity) {
        url = ParseUtils.parseString(entity.getMediaURL());
        media_url = ParseUtils.parseString(entity.getMediaURL());
        start = entity.getStart();
        end = entity.getEnd();
        type = TYPE_IMAGE;
    }

    public TwitterMedia(final Parcel in) {
        url = in.readString();
        media_url = in.readString();
        start = in.readInt();
        end = in.readInt();
        type = in.readInt();
    }

    private TwitterMedia(final String url, final String media_url, final int start, final int end, final int type) {
        this.url = url;
        this.media_url = media_url;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final JSONParcel out) {
        out.writeString("url", url);
        out.writeString("media_url", media_url);
        out.writeInt("start", start);
        out.writeInt("end", end);
        out.writeInt("type", type);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(url);
        dest.writeString(media_url);
        dest.writeInt(start);
        dest.writeInt(end);
        dest.writeInt(type);
    }

    public static TwitterMedia[] fromEntities(final EntitySupport entities) {
        final List<TwitterMedia> list = new ArrayList<TwitterMedia>();
        final MediaEntity[] medias = entities.getMediaEntities();
        if (medias != null) {
            for (final MediaEntity media : medias) {
                final URL media_url = media.getMediaURL();
                if (media_url != null) {
                    list.add(new TwitterMedia(media));
                }
            }
        }
        final URLEntity[] urls = entities.getURLEntities();
        if (urls != null) {
            for (final URLEntity url : urls) {
                final String expanded = ParseUtils.parseString(url.getExpandedURL());
                final String media_url = MediaPreviewUtils.getSupportedLink(expanded);
                if (expanded != null && media_url != null) {
                    list.add(new TwitterMedia(expanded, media_url, url.getStart(), url.getEnd(), TYPE_IMAGE));
                }
            }
        }
        if (list.isEmpty()) return null;
        return list.toArray(new TwitterMedia[list.size()]);
    }

    public static TwitterMedia[] fromJSONString(final String json) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return JSONSerializer.createArray(JSON_CREATOR, new JSONArray(json));
        } catch (final JSONException e) {
            return null;
        }
    }

    public static TwitterMedia newImage(final String media_url, final String url) {
        return new TwitterMedia(url, media_url, 0, 0, TYPE_IMAGE);
    }

}
