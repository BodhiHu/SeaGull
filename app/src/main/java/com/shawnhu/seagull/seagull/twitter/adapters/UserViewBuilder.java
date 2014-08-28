package com.shawnhu.seagull.seagull.twitter.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.utils.ImageUtils;

import twitter4j.User;

public class UserViewBuilder {

    static public void buildProfileView(View v, User mUser) {
        if (v != null && mUser != null) {
            ImageView profileImage  = (ImageView) v.findViewById(R.id.profileImage);
            TextView screenName     = (TextView)  v.findViewById(R.id.screenName);
            TextView  name          = (TextView)  v.findViewById(R.id.name);

            ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();
            if (profileImage != null && imageLoaderWrapper != null) {
                imageLoaderWrapper.displayProfileImage(profileImage, mUser.getProfileImageURL().toString());
            }
            if (screenName != null) {
                screenName.setText(mUser.getScreenName());
            }
            if (name != null) {
                name.setText("@" + mUser.getName());
            }
        }
    }
    static public void buildSelfieView(View v, User mUser) {
        if (v != null && mUser != null) {
            TextView selfie     = (TextView) v.findViewById(R.id.selfie);
            TextView location   = (TextView) v.findViewById(R.id.location);
            TextView webSite    = (TextView) v.findViewById(R.id.webSite);

            if (selfie != null) {
                String sf = mUser.getDescription();
                if (sf != null && sf.length() != 0) {
                    selfie.setText(mUser.getDescription());
                } else {
                    selfie.setText("Lazy guy, doesn't have a selfie.\n" +
                                   "And now All you can see is...\n" +
                                   "#$%*(&(*#&@$(*%&)(!@*$(");
                }
            }
            if (location != null) {
                String lo = mUser.getLocation();
                if (lo != null && lo.length() != 0) {
                    location.setText(mUser.getLocation());
                } else {
                    location.setText("Lives at St.Mars");
                }
            }
            if (webSite != null) {
                if (mUser.getURL() != null && mUser.getURL().toString().length() != 0) {
                    webSite.setText(mUser.getURL().toString());
                } else {
                    webSite.setText("Another member of nullsite dot com");
                }
            }
        }
    }

    static public BitmapDrawable loadUserBannerImageSync(User user) {
        if (user != null) {
            String url = user.getProfileBackgroundImageUrl();
            if (url != null && url != "") {

                ImageLoader imageLoader = TwitterManager.getInstance().getImageLoader();
                Bitmap bitmap = imageLoader.loadImageSync(url, ImageLoaderWrapper.mBannerDisplayOptions);
                if (bitmap != null) {
                    return new BitmapDrawable(bitmap);
                }
            }
        }

        return null;
    }

    public class LoadBannerImageTask extends AsyncTask<Void, Void, BitmapDrawable> {
        //FIXME: should cancel when frag/acti goes dead

        protected User mUser;
        protected int mWidth;
        protected int mHeight;
        public LoadBannerImageTask(User user, int width, int height) {
            mUser = user;
            mWidth = width;
            mHeight = height;

        }
        @Override
        protected BitmapDrawable doInBackground(Void... params) {
            if (mUser != null && mWidth > 0 && mHeight > 0) {
                BitmapDrawable bmpDrawable = UserViewBuilder.loadUserBannerImageSync(mUser);
                if (bmpDrawable != null) {
                    Bitmap bmp = ImageUtils.getCenterCropedBitmap(bmpDrawable.getBitmap(), mWidth, mHeight);
                    if (bmp != null) {
                        return new BitmapDrawable(bmp);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {
        }
    }
}
