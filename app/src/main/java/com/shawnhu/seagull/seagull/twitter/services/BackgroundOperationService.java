package com.shawnhu.seagull.seagull.twitter.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterDirectMessage;
import com.shawnhu.seagull.seagull.twitter.model.TwitterLocation;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMediaUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusUpdate;
import com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator;
import com.shawnhu.seagull.seagull.twitter.utils.MessagesManager;
import com.shawnhu.seagull.seagull.twitter.utils.StatusCodeMessageUtils;
import com.shawnhu.seagull.seagull.twitter.utils.TwitterValidator;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.ListUtils;
import com.twitter.Extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import twitter4j.MediaUploadResponse;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static android.text.TextUtils.isEmpty;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_DIRECT_MESSAGE_SENT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_STATUS_UPDATED;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EASTER_EGG_RESTORE_TEXT_PART1;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EASTER_EGG_RESTORE_TEXT_PART2;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EASTER_EGG_RESTORE_TEXT_PART3;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EASTER_EGG_TRIGGER_TEXT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_ACCOUNT_ID;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_IMAGE_URI;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_RECIPIENT_ID;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_STATUS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.EXTRA_TEXT;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.INTENT_ACTION_DRAFTS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.INTENT_ACTION_SEND_DIRECT_MESSAGE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.INTENT_ACTION_UPDATE_STATUS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.NOTIFICATION_ID_DRAFTS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.NOTIFICATION_ID_SEND_DIRECT_MESSAGE;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.NOTIFICATION_ID_UPDATE_STATUS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.CachedHashtags;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.Drafts;
import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeDirectMessageContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeDirectMessageDraftContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getImagePathFromUri;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class BackgroundOperationService extends IntentService {

    static final String         TAG = "BackgroundOperationService";
    private TwitterValidator    mValidator;
    private final Extractor     extractor = new Extractor();

    private Handler             mHandler;
    private ContentResolver     mResolver;
    private NotificationManager mNotificationManager;
    private MessagesManager     mMessagesManager;

    public BackgroundOperationService() {
        super("background_operation");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler                = new Handler();
        mValidator              = new TwitterValidator(this);
        mResolver               = getContentResolver();
        mNotificationManager    = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mMessagesManager        = TwitterManager.getInstance(this).getMessagesManager();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        int retRes;
        if (INTENT_ACTION_UPDATE_STATUS.equals(action)) {
            retRes = handleUpdateStatusIntent(intent);
            Intent i = new Intent(BROADCAST_STATUS_UPDATED);
            LocalBroadcastManager.getInstance(this).sendBroadcast();
        }
        if (INTENT_ACTION_SEND_DIRECT_MESSAGE.equals(action)) {
            handleSendDirectMessageIntent(intent);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_DIRECT_MESSAGE_SENT));
        }
    }

    private void handleSendDirectMessageIntent(final Intent intent) {
        final long accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, -1);
        final long recipientId = intent.getLongExtra(EXTRA_RECIPIENT_ID, -1);
        final String imageUri = intent.getStringExtra(EXTRA_IMAGE_URI);
        final String text = intent.getStringExtra(EXTRA_TEXT);
        if (accountId <= 0 || recipientId <= 0 || isEmpty(text)) return;
        final String title = getString(R.string.sending_direct_message);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_send);
        builder.setProgress(100, 0, true);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setOngoing(true);
        final Notification notification = builder.build();
        startForeground(NOTIFICATION_ID_SEND_DIRECT_MESSAGE, notification);
        final Response<TwitterDirectMessage> result = sendDirectMessage(builder, accountId, recipientId, text,
                imageUri);
        if (result.getData() != null && result.getData().id > 0) {
            final ContentValues values = makeDirectMessageContentValues(result.getData());
            final String delete_where = TweetStore.DirectMessages.ACCOUNT_ID + " = " + accountId + " AND "
                    + TweetStore.DirectMessages.MESSAGE_ID + " = " + result.getData().id;
            mResolver.delete(TweetStore.DirectMessages.CONTENT_URI, delete_where, null);
            mResolver.insert(TweetStore.DirectMessages.CONTENT_URI, values);
            showOkMessage(R.string.direct_message_sent, false);
        } else {
            final ContentValues values = makeDirectMessageDraftContentValues(accountId, recipientId, text, imageUri);
            mResolver.insert(TweetStore.Drafts.CONTENT_URI, values);
            showErrorMessage(R.string.action_sending_direct_message, result.getException(), true);
        }
        stopForeground(false);
        mNotificationManager.cancel(NOTIFICATION_ID_SEND_DIRECT_MESSAGE);
    }

    /**
     * @param intent
     * @return: 0: update OK; others: error string res id;
     */
    private int handleUpdateStatusIntent(final Intent intent) {
        int ret = 0;

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        final TwitterStatusUpdate status =
                intent.getParcelableExtra(EXTRA_STATUS);

        if (status == null) {
            return ret;
        }

        startForeground(NOTIFICATION_ID_UPDATE_STATUS,
                updateUpdateStatusNotificaion(this, builder, 0, null));

        mNotificationManager.notify(NOTIFICATION_ID_UPDATE_STATUS,
                updateUpdateStatusNotificaion(this, builder, 0, status));

        //update status
        final Response<TwitterStatus> result = updateStatus(builder, status);

        //process responses
        boolean          failed = false;
        Exception        exception = null;
        final List<Long> failed_account_ids =
                ListUtils.fromArray(TwitterAccount.getAccountIds(status.accounts));

        if (result == null || result.getData() == null) {
            failed = true;
            exception = result.getException();
        }

        if (failed) {
            // If the status is a duplicate, there's no need to save it to
            // drafts.
            if (exception instanceof TwitterException
                    && ((TwitterException) exception).getErrorCode() == StatusCodeMessageUtils.STATUS_IS_DUPLICATE) {
                ret = R.string.status_is_duplicate;
            } else {
                saveDrafts(status, failed_account_ids);
                Log.e(TAG, exception.toString());
                exception.printStackTrace();
                ret = R.string.status_not_updated;
            }
        } else {
            if (status.medias != null) {
                for (final TwitterMediaUpdate media : status.medias) {
                    final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                    if (path != null) {
                        new File(path).delete();
                    }
                }
            }
        }

        stopForeground(false);
        mNotificationManager.cancel(NOTIFICATION_ID_UPDATE_STATUS);
        return ret;
    }

    private void saveDrafts(final TwitterStatusUpdate   status,
                            final List<Long>            account_ids) {
        final ContentValues values =
                ContentValuesCreator.makeStatusDraftContentValues(
                        status, ArrayUtils.fromList(account_ids));
        mResolver.insert(Drafts.CONTENT_URI, values);
        final String title      = getString(R.string.status_not_updated);
        final String message    = getString(R.string.status_not_updated_summary);
        final Intent intent     = new Intent(INTENT_ACTION_DRAFTS);
        final Notification notification =
                buildNotification(
                        title, message,
                        R.drawable.ic_stat_twitter,
                        intent, null);
        mNotificationManager.notify(NOTIFICATION_ID_DRAFTS, notification);
    }

    private Response<TwitterDirectMessage> sendDirectMessage(
            final NotificationCompat.Builder builder,
            final long accountId, final long recipientId,
            final String text, final String imageUri) {
        final Twitter twitter = getTwitterInstance(this, accountId, true, true);
        try {
            final TwitterDirectMessage directMessage;
            if (imageUri != null) {
                final String path = getImagePathFromUri(this, Uri.parse(imageUri));
                if (path == null) throw new FileNotFoundException();
                final BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, o);
                final File file = new File(path);
                Utils.downscaleImageIfNeeded(file, 100);
                final FileInputStream is = new FileInputStream(file);
                final MediaUploadResponse uploadResp = twitter.uploadMedia(file.getName(), is, o.outMimeType);
                directMessage = new TwitterDirectMessage(twitter.sendDirectMessage(recipientId, text,
                        uploadResp.getId()), accountId, true);
                file.delete();
            } else {
                directMessage = new TwitterDirectMessage(twitter.sendDirectMessage(recipientId, text), accountId,
                        true);
            }
            return new Response<TwitterDirectMessage>(directMessage, null);
        } catch (final IOException e) {
            return new Response<TwitterDirectMessage>(null, e);
        } catch (final TwitterException e) {
            return new Response<TwitterDirectMessage>(null, e);
        }
    }

    private Response<TwitterStatus> updateStatus(
            final Builder builder,
            final TwitterStatusUpdate statusUpdate) {

        if (statusUpdate.accounts.length == 0) {
            return null;
        }

        //TODO: Easter Eggs
        final boolean hasEasterEggTriggerText =
                statusUpdate.text.contains(EASTER_EGG_TRIGGER_TEXT);
        final boolean hasEasterEggRestoreText =
                statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART1) &&
                        statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART2) &&
                        statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART3);

        final ArrayList<ContentValues> hashtag_values = new ArrayList<ContentValues>();
        final Collection<String> hashtags = extractor.extractHashtags(statusUpdate.text);
        for (final String hashtag : hashtags) {
            final ContentValues values = new ContentValues();
            values.put(CachedHashtags.NAME, hashtag);
            hashtag_values.add(values);
        }
        mResolver.bulkInsert(CachedHashtags.CONTENT_URI,
                hashtag_values.toArray(new ContentValues[hashtag_values.size()]));

        try {
            final boolean hasMedia =
                    statusUpdate.medias != null &&
                            statusUpdate.medias.length > 0;
            final String unshortenedText = statusUpdate.text;

            if (statusUpdate.medias != null) {
                for (final TwitterMediaUpdate media : statusUpdate.medias) {
                    final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                    final File file = path != null ? new File(path) : null;
                    if (file != null && file.exists()) {
                        Utils.downscaleImageIfNeeded(file, 95);
                    }
                }
            }

            final TwitterAccount account = statusUpdate.accounts[0];
            final Twitter        twitter = getTwitterInstance(this, account.account_id, true, true);

            final twitter4j.StatusUpdate status = new twitter4j.StatusUpdate(unshortenedText);
            status.setInReplyToStatusId(statusUpdate.in_reply_to_status_id);
            if (statusUpdate.location != null) {
                status.setLocation(TwitterLocation.toGeoLocation(statusUpdate.location));
            }

            if (hasMedia) {
                final BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                if (statusUpdate.medias.length == 1) {
                    final TwitterMediaUpdate media = statusUpdate.medias[0];
                    final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                    BitmapFactory.decodeFile(path, o);
                    final File file = new File(path);
                    final FileInputStream is = new FileInputStream(file);

                    status.setMedia(file.getName(), is, o.outMimeType);
                } else {
                    final long[] mediaIds = new long[statusUpdate.medias.length];
                    for (int i = 0, j = mediaIds.length; i < j; i++) {
                        final TwitterMediaUpdate media = statusUpdate.medias[i];
                        final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                        BitmapFactory.decodeFile(path, o);
                        final File file = new File(path);
                        final FileInputStream is = new FileInputStream(file);
                        final MediaUploadResponse uploadResp = twitter.uploadMedia(file.getName(), is,
                                o.outMimeType);
                        mediaIds[i] = uploadResp.getId();
                    }

                    status.mediaIds(mediaIds);
                }
            }

            status.setPossiblySensitive(statusUpdate.is_possibly_sensitive);

            final twitter4j.Status resultStatus = twitter.updateStatus(status);
            final TwitterStatus result =
                    new TwitterStatus(resultStatus, account.account_id, false);

            return new Response<TwitterStatus>(result, null);
        } catch (final Exception e) {
            return new Response<TwitterStatus>(null, e);
        }
    }

    private Notification buildNotification(
            final String title,
            final String message,
            final int    icon,
            final Intent content_intent,
            final Intent delete_intent) {
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setTicker(message)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(icon);

        if (delete_intent != null) {
            builder.setDeleteIntent(
                    PendingIntent.getBroadcast(
                            this, 0, delete_intent, PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }
        if (content_intent != null) {
            content_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            builder.setContentIntent(
                    PendingIntent.getActivity(
                            this, 0, content_intent, PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }

        return builder.build();
    }

    private static Notification updateUpdateStatusNotificaion(
            final Context context,  final NotificationCompat.Builder builder,
            final int progress,     final TwitterStatusUpdate status) {
        builder.setContentTitle(context.getString(R.string.updating_status_notification));
        if (status != null) {
            builder.setContentText(status.text);
        }
        builder.setSmallIcon(R.drawable.ic_stat_send);
        builder.setProgress(100, progress, progress >= 100 || progress <= 0);
        builder.setOngoing(true);
        return builder.build();
    }


    protected void showErrorMessage(final CharSequence message,
                                    final boolean long_message) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(message, long_message);
            }
        });
    }

    protected void showErrorMessage(final int action_res, final Exception e,
                                    final boolean long_message) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(action_res, e, long_message);
            }
        });
    }

    protected void showErrorMessage(final int action_res, final String message,
                                    final boolean long_message) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(action_res, message, long_message);
            }
        });
    }

    protected void showOkMessage(final int message_res, final boolean long_message) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showOkMessage(message_res, long_message);
            }
        });
    }
}
