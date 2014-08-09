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

package com.shawnhu.seagull.seagull.twitter.model;

import com.shawnhu.seagull.utils.JSON.JSONParcel;
import com.shawnhu.seagull.utils.JSON.JSONParcelable;

public class TwitterUnreadItem implements JSONParcelable {

	public static final JSONParcelable.Creator<TwitterUnreadItem> JSON_CREATOR = new JSONParcelable.Creator<TwitterUnreadItem>() {
		@Override
		public TwitterUnreadItem createFromParcel(final JSONParcel in) {
			return new TwitterUnreadItem(in);
		}

		@Override
		public TwitterUnreadItem[] newArray(final int size) {
			return new TwitterUnreadItem[size];
		}
	};

	public final long id, account_id;

	public TwitterUnreadItem(final JSONParcel in) {
		id = in.readLong("id");
		account_id = in.readLong("account_id");
	}

	public TwitterUnreadItem(final long id, final long account_id) {
		this.id = id;
		this.account_id = account_id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof TwitterUnreadItem)) return false;
		final TwitterUnreadItem other = (TwitterUnreadItem) obj;
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
		return "TwitterUnreadItem{id=" + id + ", account_id=" + account_id + "}";
	}

	@Override
	public void writeToParcel(final JSONParcel out) {
		out.writeLong("id", id);
		out.writeLong("account_id", account_id);
	}
}
