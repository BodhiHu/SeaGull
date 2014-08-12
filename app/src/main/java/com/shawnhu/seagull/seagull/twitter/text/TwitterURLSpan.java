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

package com.shawnhu.seagull.seagull.twitter.text;

import android.content.Context;
import android.text.style.URLSpan;
import android.view.View;

public class TwitterURLSpan extends URLSpan {

    private final int                               type;
    private final long                              accountId;
    private final String                            url;
    private final String                            orig;
    private final boolean                           sensitive;
    private final OnLinkClickListener               listener;

    public TwitterURLSpan(final String              url,
                          final long                accountId,
                          final int                 type,
                          final boolean             sensitive,
                          final OnLinkClickListener listener) {

        this(url, null, accountId, type, sensitive, listener);
    }

    public TwitterURLSpan(final String              url,
                          final String              orig,
                          final long                accountId,
                          final int                 type,
                          final boolean             sensitive,
                          final OnLinkClickListener listener) {
        super(url);
        this.url = url;
        this.orig = orig;
        this.accountId = accountId;
        this.type = type;
        this.sensitive = sensitive;
        this.listener = listener;
    }

    @Override
    public void onClick(final View widget) {
        if (listener != null) {
            listener.onLinkClick(widget.getContext(),
                                 url, orig, accountId, type, sensitive);
        }
    }

    public interface OnLinkClickListener {
        public void onLinkClick(Context context,
                                String link, String orig, long account_id, int type,
                                boolean sensitive);
    }
}
