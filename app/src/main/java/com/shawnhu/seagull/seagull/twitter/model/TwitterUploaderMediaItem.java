package com.shawnhu.seagull.seagull.twitter.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import java.io.File;
import java.io.FileNotFoundException;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getImagePathFromUri;


public class TwitterUploaderMediaItem implements Parcelable {

	public static final Parcelable.Creator<TwitterUploaderMediaItem> CREATOR = new Parcelable.Creator<TwitterUploaderMediaItem>() {

		@Override
		public TwitterUploaderMediaItem createFromParcel(final Parcel source) {
			return new TwitterUploaderMediaItem(source);
		}

		@Override
		public TwitterUploaderMediaItem[] newArray(final int size) {
			return new TwitterUploaderMediaItem[size];
		}
	};

	public final String path;
	public final ParcelFileDescriptor fd;
	public final long size;

	public TwitterUploaderMediaItem(final Context context, final TwitterMediaUpdate media) throws FileNotFoundException {
		path = getImagePathFromUri(context, Uri.parse(media.uri));
		final File file = new File(path);
		fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		size = file.length();
	}

	public TwitterUploaderMediaItem(final Parcel src) {
		path = src.readString();
		fd = src.readParcelable(ParcelFileDescriptor.class.getClassLoader());
		size = src.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		return "MediaUpload{path=" + path + ", fd=" + fd + ", size=" + size + "}";
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(path);
		dest.writeParcelable(fd, flags);
		dest.writeLong(size);
	}

	public static TwitterUploaderMediaItem[] getFromStatusUpdate(final Context context, final TwitterStatusUpdate status)
			throws FileNotFoundException {
		if (status.medias == null) return null;
		final TwitterUploaderMediaItem[] medias = new TwitterUploaderMediaItem[status.medias.length];
		for (int i = 0, j = medias.length; i < j; i++) {
			medias[i] = new TwitterUploaderMediaItem(context, status.medias[i]);
		}
		return medias;
	}

}