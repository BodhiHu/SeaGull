package com.shawnhu.seagull.views;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.utils.ImageUtils;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;

/**
 * Created by shawn on 14-7-25.
 */
public class AvatarCard implements AnyViewArrayAdapterItem.ItemViewInterface {
    BitmapDrawable mAvatar;
    String mScreenName;
    String mName;

    public AvatarCard() {

    }

    public AvatarCard(BitmapDrawable d, String n, String aT) {
        mAvatar = d;
        mScreenName = n;
        mName = aT;
    }

    public void setUpCard(BitmapDrawable d, String screenName, String name) {
        Bitmap roundCornerBmp;
        if (d != null && d.getBitmap() != null) {
            roundCornerBmp = ImageUtils.roundCornerBitmap(d.getBitmap(), 5);
            mAvatar = new BitmapDrawable(roundCornerBmp);
        }
        mScreenName = screenName;
        mName = name;
    }

    @Override
    public View getView(LayoutInflater lI, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = lI.inflate(R.layout.layout_avatar, parent, false);
        } else {
            v = convertView;
        }

        ImageView iv = (ImageView) v.findViewById(R.id.profileImage);
        TextView  nameText = (TextView) v.findViewById(R.id.item_name);
        TextView  atNameText = (TextView) v.findViewById(R.id.name);

        if (mAvatar != null) {
            iv.setImageDrawable(mAvatar);
        }
        if (mScreenName != null) {
            nameText.setText(mScreenName);
        }
        if (mName != null) {
            atNameText.setText(mName);
        }

        return v;
    }
}
