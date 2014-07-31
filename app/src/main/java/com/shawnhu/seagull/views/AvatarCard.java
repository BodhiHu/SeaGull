package com.shawnhu.seagull.views;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.AnyViewArrayAdapterItem;

/**
 * Created by shawn on 14-7-25.
 */
public class AvatarCard implements AnyViewArrayAdapterItem.ItemViewInterface {
    Drawable mAvatar;
    String   mName;
    String   mAtName;

    public AvatarCard() {

    }

    public AvatarCard(Drawable d, String n, String aT) {
        mAvatar = d;
        mName = n;
        mAtName = aT;
    }

    public void setUpCard(Drawable d, String n, String aT) {
        mAvatar = d;
        mName = n;
        mAtName = aT;
    }

    @Override
    public View getView(LayoutInflater lI, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = lI.inflate(R.layout.layout_avatar, parent, false);
        } else {
            v = convertView;
        }

        ImageView iv = (ImageView) v.findViewById(R.id.avatarImage);
        TextView  nameText = (TextView) v.findViewById(R.id.nameText);
        TextView  atNameText = (TextView) v.findViewById(R.id.atNameText);

        if (mAvatar != null) {
            iv.setImageDrawable(mAvatar);
        }
        if (mName != null) {
            nameText.setText(mName);
        }
        if (mAtName != null) {
            atNameText.setText(mAtName);
        }

        return v;
    }
}
