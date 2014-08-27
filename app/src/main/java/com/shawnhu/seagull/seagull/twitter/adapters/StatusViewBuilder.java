package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.tasks.CreateFavoriteTask;
import com.shawnhu.seagull.seagull.twitter.tasks.CreateFriendshipTask;
import com.shawnhu.seagull.seagull.twitter.tasks.DestroyFavoriteTask;
import com.shawnhu.seagull.seagull.twitter.tasks.DestroyFriendshipTask;
import com.shawnhu.seagull.seagull.twitter.tasks.RetweetStatusTask;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.utils.ImageLoadingHandler;
import com.shawnhu.seagull.utils.TimeDateUtils;

import twitter4j.Status;
import twitter4j.User;

public class StatusViewBuilder {
    static public void buildStatusView(final View view, final Context context, final Cursor cursor) {
        if (cursor == null) {
            return;
        }
        final TwitterStatus status = new TwitterStatus(cursor);
        buildStatusView(view, context, status);
    }

    static public void buildStatusView(final View view, final Context context, final Status _4jstatus, long account_id, boolean is_gap) {
        if (_4jstatus == null) {
            return;
        }

        final TwitterStatus status = new TwitterStatus(_4jstatus, account_id, is_gap);
        buildStatusView(view, context, status);
    }


    static public void buildStatusView(final View view, final Context context, final TwitterStatus  status) {
        if (view == null || context == null || status == null) {
            return;
        }

        final ImageView   tweetImage = (ImageView)    view.findViewById(R.id.tweetImage);
        final TextView    tweetText  = (TextView)     view.findViewById(R.id.tweetText);
        final ImageView   proflImage = (ImageView)    view.findViewById(R.id.profileImage);
        final TextView    screenName = (TextView)     view.findViewById(R.id.screenName);
        final TextView    name       = (TextView)     view.findViewById(R.id.name);
        final TextView    date       = (TextView)     view.findViewById(R.id.date);
        final ImageButton followBtn  = (ImageButton)  view.findViewById(R.id.followButn);
        final TextView    folwersNum = (TextView)     view.findViewById(R.id.followNum);
        final ImageButton likeBtn    = (ImageButton)  view.findViewById(R.id.likeButn);
        final TextView    likesNum   = (TextView)     view.findViewById(R.id.likesNum);
        final ImageButton retwtBtn   = (ImageButton)  view.findViewById(R.id.retweetButn);
        final TextView    retwtNum   = (TextView)     view.findViewById(R.id.retweetsNum);
        final ProgressBar progressBar= (ProgressBar)  view.findViewById(R.id.progressBar);

        ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();

        tweetText.setText(status.text_plain);
        tweetText.setVisibility(View.VISIBLE);
        tweetImage.setVisibility(View.VISIBLE);
        if (!status.is_possibly_sensitive && status.first_media != null) {
            //TODO: add click flip
            imageLoaderWrapper.displayPreviewImage(
                    tweetImage,
                    status.first_media,
                    new ImageLoadingHandler() {
                        @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            tweetImage.setVisibility(View.GONE);
                        }
                        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                        }
                        @Override public void onLoadingCancelled(String imageUri, View view) {
                            super.onLoadingCancelled(imageUri, view);
                            tweetImage.setVisibility(View.GONE);
                        }
                    }
            );
        } else {
            progressBar.setVisibility(view.GONE);
            tweetImage.setVisibility(View.GONE);
        }

        imageLoaderWrapper.displayProfileImage(proflImage, status.user_profile_image_url);

        screenName.setText(status.user_screen_name);
        name.setText(status.user_name);
        date.setText(TimeDateUtils.formatTimeStampString(context, status.timestamp));

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View root = v.getRootView();
                ImageButton followBtn = (ImageButton) root.findViewById(R.id.followButn);
                TextView    followNum = (TextView)    root.findViewById(R.id.followNum);
                if (!status.user_is_following) {
                    new LocalCreateFriendshipTask(followBtn, followNum, context, status.account_id, status.user_id)
                            .execute();
                } else {
                    new LocalDestroyFriendshipTask(followBtn, followNum, context, status.account_id, status.user_id)
                            .execute();
                }
            }
        });
        if (status.user_is_following) {
            followBtn.setImageResource(R.drawable.anchor_blue);
            //followBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.anchor_blue));
        }
        folwersNum.setText(String.format(context.getString(R.string.followers_num), 0));

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View root = v.getRootView();
                ImageButton likeBtn  = (ImageButton) root.findViewById(R.id.likeButn);
                TextView    likesNum = (TextView)    root.findViewById(R.id.likesNum);
                if (!status.is_favorite) {
                    new LocalCreateFavoriteTask(likeBtn, likesNum, context, status.account_id, status.id)
                            .execute();
                } else {
                    new LocalDestroyFavoriteTask(likeBtn, likesNum, context, status.account_id, status.id)
                            .execute();
                }
            }
        });
        if (status.is_favorite) {
            likeBtn.setImageResource(R.drawable.heart_orange);
        }
        likesNum.setText(String.format(context.getString(R.string.likes_num), status.favorite_count));

        retwtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View root = v.getRootView();
                ImageButton retwBtn = (ImageButton) root.findViewById(R.id.retweetButn);
                TextView    retwNum = (TextView)    root.findViewById(R.id.retweetsNum);

                if (status.is_retweet && status.user_id != status.account_id) {
                    new LocalRetweetStatusTask(retwBtn, retwNum, context, status.account_id, status.id)
                            .execute();
                }
            }
        });
        if (status.is_retweet && status.user_id == status.account_id) {
            retwtBtn.setImageResource(R.drawable.bird_sing_blue);
        }
        retwtNum.setText(String.format(context.getString(R.string.retweet_num), status.retweet_count));
    }

    static public class LocalCreateFavoriteTask extends CreateFavoriteTask {
        protected ImageButton mImageBtn;
        protected TextView    mTextView;
        public LocalCreateFavoriteTask(ImageButton b, TextView t, Context context, final long account_id, final long status_id) {
            super(context, account_id, status_id);

            mImageBtn = b;
            mTextView = t;
        }

        @Override
        protected void onPostExecute(final Response<TwitterStatus> result) {
            if (result.hasData()) {
                TwitterStatus status = result.getData();
                if (mImageBtn != null) {
                    mImageBtn.setImageResource(R.drawable.heart_orange);
                }
                if (mTextView != null) {
                    mTextView.setText(String.format(mContext.getString(R.string.likes_num), status.favorite_count));
                }
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    static public class LocalDestroyFavoriteTask extends DestroyFavoriteTask {
        protected ImageButton imageButton;
        protected TextView    textView;
        public LocalDestroyFavoriteTask(ImageButton imageButton, TextView textView, Context context, final long account_id, final long status_id) {
            super(context, account_id, status_id);
            this.imageButton = imageButton;
            this.textView = textView;
        }

        @Override
        protected void onPostExecute(final Response<TwitterStatus> result) {
            if (result.hasData()) {
                TwitterStatus status = result.getData();
                if (imageButton != null) {
                    imageButton.setImageResource(R.drawable.heart_gray);
                }
                if (textView != null) {
                    textView.setText(String.format(mContext.getString(R.string.likes_num), status.favorite_count));
                }
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }
    static public class LocalCreateFriendshipTask extends CreateFriendshipTask {
        protected ImageButton imageButton;
        protected TextView    textView;
        public LocalCreateFriendshipTask(ImageButton imageButton, TextView textView, Context context, final long account_id, final long user_id) {
            super(context, account_id, user_id);
            this.imageButton = imageButton;
            this.textView = textView;
        }

        @Override
        protected void onPostExecute(final Response<User> result) {
            if (result.hasData()) {
                User user = result.getData();
                if (imageButton != null) {
                    imageButton.setImageResource(R.drawable.anchor_blue);
                }
                if (textView != null) {
                    textView.setText(String.format(mContext.getString(R.string.followers_num), user.getFollowersCount()));
                }
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }
    static public class LocalDestroyFriendshipTask extends DestroyFriendshipTask {
        protected ImageButton imageButton;
        protected TextView    textView;
        public LocalDestroyFriendshipTask(ImageButton imageButton, TextView textView, Context context, final long account_id, final long user_id) {
            super(context, account_id, user_id);
            this.imageButton = imageButton;
            this.textView = textView;
        }

        @Override
        protected void onPostExecute(final Response<User> result) {
            if (result.hasData()) {
                User user = result.getData();
                if (imageButton != null) {
                    imageButton.setImageResource(R.drawable.anchor_gray);
                }
                if (textView != null) {
                    textView.setText(String.format(mContext.getString(R.string.followers_num), user.getFollowersCount()));
                }
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }
    static public class LocalRetweetStatusTask extends RetweetStatusTask {
        protected ImageButton imageButton;
        protected TextView    textView;
        public LocalRetweetStatusTask(ImageButton imageButton, TextView textView, Context context, final long account_id, final long status_id) {
            super(context, account_id, status_id);
            this.imageButton = imageButton;
            this.textView = textView;
        }

        @Override
        protected void onPostExecute(final Response<twitter4j.Status> result) {
            if (result.hasData()) {
                twitter4j.Status status = result.getData();
                if (imageButton != null) {
                    imageButton.setImageResource(R.drawable.bird_sing_blue);
                }
                if (textView != null) {
                    textView.setText(String.format(mContext.getString(R.string.retweet_num), status.getRetweetCount()));
                }
            } else {
                Toast.makeText(mContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}