package com.shawnhu.seagull.seagull.activities;

import android.widget.ImageView;

import com.shawnhu.seagull.activities.AbsMediasViewActivity;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.utils.ImageLoadingHandler;

public class TwitterMediasViewActivity extends AbsMediasViewActivity {
    protected void loadImage(ImageView imageView, String url) {
        ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();
        imageLoaderWrapper.displayPreviewImage(imageView, url, new ImageLoadingHandler());
    }
}
