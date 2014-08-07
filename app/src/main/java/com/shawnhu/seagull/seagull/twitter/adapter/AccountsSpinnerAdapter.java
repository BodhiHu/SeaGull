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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.model.Account;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;

import java.util.Collection;

public class AccountsSpinnerAdapter extends ArrayAdapter<Account> {

	private ImageLoaderWrapper mImageLoader;

	public AccountsSpinnerAdapter(final Context context, int layout, int drop_down_layout) {
		super(context, layout);
		setDropDownViewResource(drop_down_layout);

		mImageLoader = TwitterManager.getInstance(context).getImageLoaderWrapper();
	}

	public AccountsSpinnerAdapter(final Context context, int layout, int drop_down_layout,
                                  final Collection<Account> accounts) {
		this(context, layout, drop_down_layout);
		addAll(accounts);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		final View view = super.getDropDownView(position, convertView, parent);
		bindView(view, getItem(position));
		return view;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		bindView(view, getItem(position));
		return view;
	}

	private void bindView(final View view, final Account item) {
		final TextView name = (TextView) view.findViewById(R.id.text_name);
		final TextView screen_name = (TextView) view.findViewById(R.id.text_screen_name);
		final ImageView profile_image = (ImageView) view.findViewById(R.id.image_profile);
		screen_name.setVisibility(item.is_dummy ? View.GONE : View.VISIBLE);
		profile_image.setVisibility(item.is_dummy ? View.GONE : View.VISIBLE);
		if (!item.is_dummy) {
			name.setText(item.name);
			screen_name.setText("@" + item.screen_name);
            mImageLoader.displayProfileImage(profile_image, item.profile_image_url);
		} else {
			name.setText(R.string.none);
		}
	}

}
