package com.shawnhu.seagull.seagull.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.HomeActivity;
import com.shawnhu.seagull.adapters.GeneralAdapter;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * Created by shawn on 14-7-24.
 */
public class SeagullHomeActivity extends HomeActivity {
    static ArrayList<GeneralAdapter.Item> mHomeDrawerItems = new ArrayList<GeneralAdapter.Item>();
    GeneralAdapter mHomeDrawerMenusAdapter =
            new GeneralAdapter(getApplicationContext(), R.layout.layout_drawer_item, mHomeDrawerItems,
                    new GeneralAdapter.DefaultViewInterface() {
                        @Override
                        public View getDefaultView(GeneralAdapter.Item item, int position, View convertView, ViewGroup parent) {
                            View v;
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            if (convertView == null) {
                                v = layoutInflater.inflate(R.layout.layout_drawer_item, parent, false);
                            } else {
                                v = convertView;
                            }

                            Object target = item.mTarget;
                            if (target instanceof DrawerItem) {
                                ImageView iv = (ImageView) v.findViewById(R.id.imageView);
                                TextView tv  = (TextView)  v.findViewById(R.id.textView);
                                iv.setImageResource(((DrawerItem) target).iconResId);
                                tv.setText(((DrawerItem) target).name);
                            } else {
                                try {
                                    throw new InvalidObjectException("required DrawerItem instance");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            return v;
                        }
                    }
            );




    class DrawerItem {
        DrawerItem(int id, String s) {
            iconResId = id;
            name = s;
        }
        int iconResId;
        String   name;
    }
}
