package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.shawnhu.seagull.utils.TimeDateUtils;

final public class StatusesCursorAdapter extends SimpleCursorAdapter {

    static final public String[] PROJECTION  = TweetStore.Statuses.COLUMNS;
    static final public String   SELECTION   =
            "("                                                        +
                    "(" + TweetStore.Statuses.ACCOUNT_ID       + "=?)" +
            ")";
    static final public String   SORT_ORDER  = TweetStore.Statuses.STATUS_TIMESTAMP + " DESC";
    static final public Uri      CONTENT_URI = TweetStore.Statuses.CONTENT_URI;

    public StatusesCursorAdapter(Context context, Cursor c, int flags) {
        super(context,
                R.layout.status_item,
                c,
                PROJECTION,
                null,
                flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView   tweetImage = (ImageView)    view.findViewById(R.id.tweetImage);
        TextView    tweetText  = (TextView)     view.findViewById(R.id.tweetText);
        ImageView   proflImage = (ImageView)    view.findViewById(R.id.profileImage);
        TextView    screenName = (TextView)     view.findViewById(R.id.screenName);
        TextView    name       = (TextView)     view.findViewById(R.id.name);
        TextView    date       = (TextView)     view.findViewById(R.id.date);
        ImageButton followBtn  = (ImageButton)  view.findViewById(R.id.followButn);
        TextView    folwersNum = (TextView)     view.findViewById(R.id.followNum);
        ImageButton likeBtn    = (ImageButton)  view.findViewById(R.id.likeButn);
        TextView    likesNum   = (TextView)     view.findViewById(R.id.likesNum);
        ImageButton retwtBtn   = (ImageButton)  view.findViewById(R.id.retweetButn);
        TextView    retwtNum   = (TextView)     view.findViewById(R.id.retweetsNum);

        TwitterStatus status = new TwitterStatus(cursor);
        ImageLoaderWrapper imageLoaderWrapper = TwitterManager.getInstance().getImageLoaderWrapper();

        tweetText.setText(status.text_plain);
        if (!status.is_possibly_sensitive && status.first_media != null) {
            //TODO: add click flip
            imageLoaderWrapper.displayPreviewImage(tweetImage, status.first_media);
            tweetText.setVisibility(View.GONE);
        } else {
            tweetImage.setVisibility(View.GONE);
        }

        imageLoaderWrapper.displayProfileImage(proflImage, status.user_profile_image_url);

        screenName.setText(status.user_screen_name);
        name.setText(status.user_name);
        date.setText(TimeDateUtils.formatTimeStampString(mContext, status.timestamp));

        if (status.user_is_following) {
            followBtn.setImageResource(R.drawable.anchor_blue);
        }
        folwersNum.setText(String.format(mContext.getString(R.string.followers_num), 0));

        if (status.is_favorite) {
            likeBtn.setImageResource(R.drawable.heart_orange);
        }
        likesNum.setText(String.format(mContext.getString(R.string.likes_num), status.favorite_count));

        if (status.my_retweet_id != -1) {
            retwtBtn.setImageResource(R.drawable.bird_sing_blue);
        }
        retwtNum.setText(String.format(mContext.getString(R.string.retweet_num), status.retweet_count));
    }

    public interface TwitterActions {
        boolean follow(long accountId, boolean cancel);
        boolean like(long statusId, boolean cancel);
        boolean retweet(long statusId);
        boolean reply(long statusId);
    }
}
