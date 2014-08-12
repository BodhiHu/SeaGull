package com.shawnhu.seagull.seagull.twitter.utils;

import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUser;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.collections.NoDuplicatesArrayList;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectManager {

	private final NoDuplicatesArrayList<Long>       mSelectedStatusIds  =
                                                        new NoDuplicatesArrayList<Long>();
	private final NoDuplicatesArrayList<Long>       mSelectedUserIds    =
                                                        new NoDuplicatesArrayList<Long>();
	private final NoDuplicatesArrayList<Callback>   mCallbacks          =
                                                        new NoDuplicatesArrayList<Callback>();
	private final ItemsList                         mSelectedItems      =
                                                        new ItemsList(this);
	private long                                    mAccountId;

	public void clearSelectedItems() {
		mSelectedItems.clear();
	}

	public long getAccountId() {
		if (mAccountId <= 0) return getFirstSelectAccountId(mSelectedItems);
		return mAccountId;
	}

	public int getCount() {
		return mSelectedItems.size();
	}

	public long getFirstSelectAccountId() {
		return getFirstSelectAccountId(mSelectedItems);
	}

	public List<Object> getSelectedItems() {
		return mSelectedItems;
	}

	public boolean isActive() {
		return !mSelectedItems.isEmpty();
	}

	public boolean isSelected(final Object object) {
		return mSelectedItems.contains(object);
	}

	public boolean isStatusSelected(final long status_id) {
		return mSelectedStatusIds.contains(status_id);
	}

	public boolean isUserSelected(final long user_id) {
		return mSelectedUserIds.contains(user_id);
	}

	public void registerCallback(final Callback callback) {
		if (callback == null) return;
		mCallbacks.add(callback);
	}

	public boolean selectItem(final Object item) {
		return mSelectedItems.add(item);
	}

	public void setAccountId(final long accountId) {
		mAccountId = accountId;
	}

	public void unregisterCallback(final Callback callback) {
		mCallbacks.remove(callback);
	}

	public boolean unselectItem(final Object item) {
		return mSelectedItems.remove(item);
	}

	private void onItemsCleared() {
		for (final Callback callback : mCallbacks) {
			callback.onItemsCleared();
		}
		mAccountId = -1;
	}

	private void onItemSelected(final Object object) {
		for (final Callback callback : mCallbacks) {
			callback.onItemSelected(object);
		}
	}

	private void onItemUnselected(final Object object) {
		for (final Callback callback : mCallbacks) {
			callback.onItemUnselected(object);
		}
	}

	public static long getFirstSelectAccountId(final List<Object> selected_items) {
		final Object obj = selected_items.get(0);
		if (obj instanceof TwitterUser)
			return ((TwitterUser) obj).account_id;
		else if (obj instanceof TwitterStatus) return ((TwitterStatus) obj).account_id;
		return -1;
	}

	public static long[] getSelectedUserIds(final List<Object> selected_items) {
		final ArrayList<Long> ids_list = new ArrayList<Long>();
		for (final Object item : selected_items) {
			if (item instanceof TwitterUser) {
				ids_list.add(((TwitterUser) item).id);
			} else if (item instanceof TwitterStatus) {
				ids_list.add(((TwitterStatus) item).user_id);
			}
		}
		return ArrayUtils.fromList(ids_list);
	}

	public static interface Callback {

		public void onItemsCleared();

		public void onItemSelected(Object item);

		public void onItemUnselected(Object item);

	}

	@SuppressWarnings("serial")
	static class ItemsList extends NoDuplicatesArrayList<Object> {

		private final MultiSelectManager manager;

		ItemsList(final MultiSelectManager manager) {
			this.manager = manager;
		}

		@Override
		public boolean add(final Object object) {
			if (object instanceof TwitterStatus) {
				manager.mSelectedStatusIds.add(((TwitterStatus) object).id);
			} else if (object instanceof TwitterUser) {
				manager.mSelectedUserIds.add(((TwitterUser) object).id);
			} else
				return false;
			final boolean ret = super.add(object);
			manager.onItemSelected(object);
			return ret;
		}

		@Override
		public void clear() {
			super.clear();
			manager.mSelectedStatusIds.clear();
			manager.mSelectedUserIds.clear();
			manager.onItemsCleared();
		}

		@Override
		public boolean remove(final Object object) {
			final boolean ret = super.remove(object);
			if (object instanceof TwitterStatus) {
				manager.mSelectedStatusIds.remove(((TwitterStatus) object).id);
			} else if (object instanceof TwitterUser) {
				manager.mSelectedUserIds.remove(((TwitterUser) object).id);
			}
			if (ret) {
				if (isEmpty()) {
					manager.onItemsCleared();
				} else {
					manager.onItemUnselected(object);
				}
			}
			return ret;
		}

	}
}
