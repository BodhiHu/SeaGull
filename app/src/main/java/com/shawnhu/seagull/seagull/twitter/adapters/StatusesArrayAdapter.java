package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.utils.ImageLoadingHandler;
import com.shawnhu.seagull.widgets.CapacityArrayAdapter;

import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;

public class StatusesArrayAdapter extends CapacityArrayAdapter<Status> {
    protected int mResource;

    public StatusesArrayAdapter(Context context) {
        super(context, R.layout.status_item);
        mResource = R.layout.status_item;
    }

    public StatusesArrayAdapter(Context context, List<Status> statuses) {
        super(context, R.layout.status_item, statuses);
        mResource = R.layout.status_item;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(mResource, parent, false);
        }

        Status status = getItem(position);
        if (status != null && convertView != null) {
            convertView.findViewById(R.id.tweetActionLayout).setVisibility(View.GONE);

            final ImageView tweetImage = (ImageView) convertView.findViewById(R.id.tweetImage);
            final TextView  tweetText  = (TextView)   convertView.findViewById(R.id.tweetText);

            tweetText.setText(status.getText());

            tweetImage.setVisibility(View.VISIBLE);
            tweetText.setVisibility(View.VISIBLE);

            MediaEntity[] medias = status.getMediaEntities();
            if (medias != null) {
                ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();
                imageLoaderWrapper.displayPreviewImage(
                        tweetImage,
                        medias[0].getURL().toString(),
                        new ImageLoadingHandler() {
                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                super.onLoadingFailed(imageUri, view, failReason);
                                tweetImage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                super.onLoadingCancelled(imageUri, view);
                                tweetImage.setVisibility(View.GONE);
                            }
                        }
                );
            }

        }

        return convertView;
    }
}
