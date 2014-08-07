/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shawnhu.seagull.seagull.twitter.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TweetStore;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.seagull.twitter.TweetStore.Accounts;

public class AccountsAdapter extends SimpleCursorAdapter {

    private static final String TAG = "AccountsAdapter";
	private final ImageLoaderWrapper mImageLoader;

	private int mProfileImageIdx, mScreenNameIdx, mAccountIdIdx;

    public AccountsAdapter(final Context context, int layout) {
        super(context, layout, null,
                new String[] {TweetStore.Accounts.NAME},
                new int[]    {R.id.text_name});
		mImageLoader = TwitterManager.getImageLoaderWrapper();
    }

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
        if (view != null) {
            ImageView profile_image = (ImageView) view.findViewById(R.id.image_profile);
            TextView screen_name = (TextView) view.findViewById(R.id.text_screen_name);

            try {
                mImageLoader.displayProfileImage(profile_image, cursor.getString(mProfileImageIdx));
                screen_name.setText(cursor.getString(mScreenNameIdx));
            } catch(Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        }

		super.bindView(view, context, cursor);
	}

	@Override
	public long getItemId(final int position) {
		final Cursor c = getCursor();
		if (c == null || c.isClosed()) return -1;
		c.moveToPosition(position);
		return c.getLong(mAccountIdIdx);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return super.newView(context, cursor, parent);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

    @Override
	public Cursor swapCursor(final Cursor cursor) {
		if (cursor != null) {
			mAccountIdIdx    = cursor.getColumnIndex(Accounts.ACCOUNT_ID);
			mProfileImageIdx = cursor.getColumnIndex(Accounts.PROFILE_IMAGE_URL);
			mScreenNameIdx   = cursor.getColumnIndex(Accounts.SCREEN_NAME);
		}
		return super.swapCursor(cursor);
	}
}
