package com.shawnhu.seagull.seagull.twitter.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;
import static android.text.TextUtils.isEmpty;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTwitterWrapper;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.*;

import com.shawnhu.seagull.seagull.twitter.TweetStore;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.*;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterLocation;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMediaUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterDirectMessage;
import com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator;
import com.shawnhu.seagull.seagull.twitter.utils.MessagesManager;
import com.shawnhu.seagull.seagull.twitter.utils.StatusCodeMessageUtils;
import com.shawnhu.seagull.seagull.twitter.utils.TwitterValidator;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.seagull.twitter.utils.io.ContentLengthInputStream;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.ListUtils;
import com.twitter.Extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import twitter4j.MediaUploadResponse;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeDirectMessageContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeDirectMessageDraftContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.*;

public class BackgroundOperationService extends IntentService {

    private TwitterValidator mValidator;
    private final Extractor extractor = new Extractor();

    private Handler mHandler;
    private SharedPreferences mPreferences;
    private ContentResolver mResolver;
    private NotificationManager mNotificationManager;
    private AsyncTwitterWrapper mTwitter;
    private MessagesManager mMessagesManager;

    public BackgroundOperationService() {
        super("background_operation");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final TwitterManager tw = TwitterManager.getInstance(this);
        mHandler = new Handler();
        mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mValidator = new TwitterValidator(this);
        mResolver = getContentResolver();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mTwitter = tw.getTwitterWrapper();
        mMessagesManager = tw.getMessagesManager();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void showErrorMessage(final CharSequence message, final boolean long_message) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(message, long_message);
            }
        });
    }

    public void showErrorMessage(final int action_res, final Exception e, final boolean long_message) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(action_res, e, long_message);
            }
        });
    }

    public void showErrorMessage(final int action_res, final String message, final boolean long_message) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showErrorMessage(action_res, message, long_message);
            }
        });
    }

    public void showOkMessage(final int message_res, final boolean long_message) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mMessagesManager.showOkMessage(message_res, long_message);
            }
        });
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        if (INTENT_ACTION_UPDATE_STATUS.equals(action)) {
            handleUpdateStatusIntent(intent);
        } else if (INTENT_ACTION_SEND_DIRECT_MESSAGE.equals(action)) {
            handleSendDirectMessageIntent(intent);
        }
    }

    private Notification buildNotification(final String title, final String message, final int icon,
            final Intent content_intent, final Intent delete_intent) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(message);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(icon);
        if (delete_intent != null) {
            builder.setDeleteIntent(PendingIntent.getBroadcast(this, 0, delete_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        if (content_intent != null) {
            content_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            builder.setContentIntent(PendingIntent.getActivity(this, 0, content_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        // final Uri defRingtone =
        // RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // final String path =
        // mPreferences.getString(PREFERENCE_KEY_NOTIFICATION_RINGTONE, "");
        // builder.setSound(isEmpty(path) ? defRingtone : Uri.parse(path),
        // Notification.STREAM_DEFAULT);
        // builder.setLights(HOLO_BLUE_LIGHT, 1000, 2000);
        // builder.setDefaults(Notification.DEFAULT_VIBRATE);
        return builder.build();
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
            mResolver.delete(TweetStore.DirectMessages.Outbox.CONTENT_URI, delete_where, null);
            mResolver.insert(TweetStore.DirectMessages.Outbox.CONTENT_URI, values);
            showOkMessage(R.string.direct_message_sent, false);
        } else {
            final ContentValues values = makeDirectMessageDraftContentValues(accountId, recipientId, text, imageUri);
            mResolver.insert(TweetStore.Drafts.CONTENT_URI, values);
            showErrorMessage(R.string.action_sending_direct_message, result.getException(), true);
        }
        stopForeground(false);
        mNotificationManager.cancel(NOTIFICATION_ID_SEND_DIRECT_MESSAGE);
    }

    private void handleUpdateStatusIntent(final Intent intent) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final TwitterStatusUpdate status = intent.getParcelableExtra(EXTRA_STATUS);
        final Parcelable[] status_parcelables = intent.getParcelableArrayExtra(EXTRA_STATUSES);
        final TwitterStatusUpdate[] statuses;
        if (status_parcelables != null) {
            statuses = new TwitterStatusUpdate[status_parcelables.length];
            for (int i = 0, j = status_parcelables.length; i < j; i++) {
                statuses[i] = (TwitterStatusUpdate) status_parcelables[i];
            }
        } else if (status != null) {
            statuses = new TwitterStatusUpdate[1];
            statuses[0] = status;
        } else
            return;
        startForeground(NOTIFICATION_ID_UPDATE_STATUS, updateUpdateStatusNotificaion(this, builder, 0, null));
        for (final TwitterStatusUpdate item : statuses) {
            mNotificationManager.notify(NOTIFICATION_ID_UPDATE_STATUS,
                    updateUpdateStatusNotificaion(this, builder, 0, item));
            final List<Response<TwitterStatus>> result = updateStatus(builder, item);
            boolean failed = false;
            Exception exception = null;
            final List<Long> failed_account_ids = ListUtils.fromArray(TwitterAccount.getAccountIds(item.accounts));

            for (final Response<TwitterStatus> response : result) {
                if (response.getData() == null) {
                    failed = true;
                    if (exception == null) {
                        exception = response.getException();
                    }
                } else if (response.getData().account_id > 0) {
                    failed_account_ids.remove(response.getData().account_id);
                }
            }
            if (result.isEmpty()) {
                saveDrafts(item, failed_account_ids);
                showErrorMessage(R.string.action_updating_status, getString(R.string.no_account_selected), false);
            } else if (failed) {
                // If the status is a duplicate, there's no need to save it to
                // drafts.
                if (exception instanceof TwitterException
                        && ((TwitterException) exception).getErrorCode() == StatusCodeMessageUtils.STATUS_IS_DUPLICATE) {
                    showErrorMessage(getString(R.string.status_is_duplicate), false);
                } else {
                    saveDrafts(item, failed_account_ids);
                    showErrorMessage(R.string.action_updating_status, exception, true);
                }
            } else {
                showOkMessage(R.string.status_updated, false);
                if (item.medias != null) {
                    for (final TwitterMediaUpdate media : item.medias) {
                        final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                        if (path != null) {
                            new File(path).delete();
                        }
                    }
                }
            }
            if (mPreferences.getBoolean(KEY_REFRESH_AFTER_TWEET, false)) {
                mTwitter.refreshAll();
            }
        }
        stopForeground(false);
        mNotificationManager.cancel(NOTIFICATION_ID_UPDATE_STATUS);
    }

    private void saveDrafts(final TwitterStatusUpdate status, final List<Long> account_ids) {
        final ContentValues values = ContentValuesCreator.makeStatusDraftContentValues(status,
                ArrayUtils.fromList(account_ids));
        mResolver.insert(Drafts.CONTENT_URI, values);
        final String title = getString(R.string.status_not_updated);
        final String message = getString(R.string.status_not_updated_summary);
        final Intent intent = new Intent(INTENT_ACTION_DRAFTS);
        final Notification notification = buildNotification(title, message, R.drawable.ic_stat_twitter, intent, null);
        mNotificationManager.notify(NOTIFICATION_ID_DRAFTS, notification);
    }

    private Response<TwitterDirectMessage> sendDirectMessage(final NotificationCompat.Builder builder,
            final long accountId, final long recipientId, final String text, final String imageUri) {
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
                final ContentLengthInputStream is = new ContentLengthInputStream(file);
                is.setReadListener(new MessageMediaUploadListener(this, mNotificationManager, builder, text));
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

    private void showToast(final int resId, final int duration) {
        mHandler.post(new ToastRunnable(this, resId, duration));
    }

    private List<Response<TwitterStatus>> updateStatus(final Builder builder,
            final TwitterStatusUpdate statusUpdate) {
        final ArrayList<ContentValues> hashtag_values = new ArrayList<ContentValues>();
        final Collection<String> hashtags = extractor.extractHashtags(statusUpdate.text);
        for (final String hashtag : hashtags) {
            final ContentValues values = new ContentValues();
            values.put(CachedHashtags.NAME, hashtag);
            hashtag_values.add(values);
        }
        final boolean hasEasterEggTriggerText = statusUpdate.text.contains(EASTER_EGG_TRIGGER_TEXT);
        final boolean hasEasterEggRestoreText = statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART1)
                && statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART2)
                && statusUpdate.text.contains(EASTER_EGG_RESTORE_TEXT_PART3);
        mResolver.bulkInsert(CachedHashtags.CONTENT_URI,
                hashtag_values.toArray(new ContentValues[hashtag_values.size()]));

        final List<Response<TwitterStatus>> results = new ArrayList<Response<TwitterStatus>>();

        if (statusUpdate.accounts.length == 0) return Collections.emptyList();

        try {
            final boolean hasMedia = statusUpdate.medias != null && statusUpdate.medias.length > 0;
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
            for (final TwitterAccount account : statusUpdate.accounts) {
                final Twitter twitter = getTwitterInstance(this, account.account_id, true, true);
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
                        try {
                            if (path == null) throw new FileNotFoundException();
                            BitmapFactory.decodeFile(path, o);
                            final File file = new File(path);
                            final ContentLengthInputStream is = new ContentLengthInputStream(file);
                            is.setReadListener(new StatusMediaUploadListener(this, mNotificationManager, builder,
                                    statusUpdate));
                            status.setMedia(file.getName(), is, o.outMimeType);
                        } catch (final FileNotFoundException e) {
                        }
                    } else {
                        final long[] mediaIds = new long[statusUpdate.medias.length];
                        try {
                            for (int i = 0, j = mediaIds.length; i < j; i++) {
                                final TwitterMediaUpdate media = statusUpdate.medias[i];
                                final String path = getImagePathFromUri(this, Uri.parse(media.uri));
                                if (path == null) throw new FileNotFoundException();
                                BitmapFactory.decodeFile(path, o);
                                final File file = new File(path);
                                final ContentLengthInputStream is = new ContentLengthInputStream(file);
                                is.setReadListener(new StatusMediaUploadListener(this, mNotificationManager, builder,
                                        statusUpdate));
                                final MediaUploadResponse uploadResp = twitter.uploadMedia(file.getName(), is,
                                        o.outMimeType);
                                mediaIds[i] = uploadResp.getId();
                            }
                        } catch (final FileNotFoundException e) {

                        } catch (final TwitterException e) {
                            final Response<TwitterStatus> response = new Response<TwitterStatus>(null, e);
                            results.add(response);
                            continue;
                        }
                        status.mediaIds(mediaIds);
                    }
                }
                status.setPossiblySensitive(statusUpdate.is_possibly_sensitive);

                if (twitter == null) {
                    results.add(new Response<TwitterStatus>(null, new NullPointerException()));
                    continue;
                }
                try {
                    final twitter4j.Status resultStatus = twitter.updateStatus(status);
                    final TwitterStatus result = new TwitterStatus(resultStatus, account.account_id, false);
                    results.add(new Response<TwitterStatus>(result, null));
                } catch (final TwitterException e) {
                    final Response<TwitterStatus> response = new Response<TwitterStatus>(null, e);
                    results.add(response);
                }
            }
        } catch (final Exception e) {
            final Response<TwitterStatus> response = new Response<TwitterStatus>(null, e);
            results.add(response);
        }

        return results;
    }

    private static Notification updateSendDirectMessageNotificaion(final Context context,
            final NotificationCompat.Builder builder, final int progress, final String message) {
        builder.setContentTitle(context.getString(R.string.sending_direct_message));
        if (message != null) {
            builder.setContentText(message);
        }
        builder.setSmallIcon(R.drawable.ic_stat_send);
        builder.setProgress(100, progress, progress >= 100 || progress <= 0);
        builder.setOngoing(true);
        return builder.build();
    }

    private static Notification updateUpdateStatusNotificaion(final Context context,
            final NotificationCompat.Builder builder, final int progress, final TwitterStatusUpdate status) {
        builder.setContentTitle(context.getString(R.string.updating_status_notification));
        if (status != null) {
            builder.setContentText(status.text);
        }
        builder.setSmallIcon(R.drawable.ic_stat_send);
        builder.setProgress(100, progress, progress >= 100 || progress <= 0);
        builder.setOngoing(true);
        return builder.build();
    }

    private static class ToastRunnable implements Runnable {
        private final Context context;
        private final int resId;
        private final int duration;

        public ToastRunnable(final Context context, final int resId, final int duration) {
            this.context = context;
            this.resId = resId;
            this.duration = duration;
        }

        @Override
        public void run() {
            Toast.makeText(context, resId, duration).show();

        }

    }

    static class MessageMediaUploadListener implements ContentLengthInputStream.ReadListener {
        private final Context context;
        private final NotificationManager manager;

        int percent;

        private final Builder builder;
        private final String message;

        MessageMediaUploadListener(final Context context, final NotificationManager manager,
                final NotificationCompat.Builder builder, final String message) {
            this.context = context;
            this.manager = manager;
            this.builder = builder;
            this.message = message;
        }

        @Override
        public void onRead(final long length, final long position) {
            final int percent = length > 0 ? (int) (position * 100 / length) : 0;
            if (this.percent != percent) {
                manager.notify(NOTIFICATION_ID_SEND_DIRECT_MESSAGE,
                        updateSendDirectMessageNotificaion(context, builder, percent, message));
            }
            this.percent = percent;
        }
    }

    static class StatusMediaUploadListener implements ContentLengthInputStream.ReadListener {
        private final Context context;
        private final NotificationManager manager;

        int percent;

        private final Builder builder;
        private final TwitterStatusUpdate statusUpdate;

        StatusMediaUploadListener(final Context context, final NotificationManager manager,
                final NotificationCompat.Builder builder, final TwitterStatusUpdate statusUpdate) {
            this.context = context;
            this.manager = manager;
            this.builder = builder;
            this.statusUpdate = statusUpdate;
        }

        @Override
        public void onRead(final long length, final long position) {
            final int percent = length > 0 ? (int) (position * 100 / length) : 0;
            if (this.percent != percent) {
                manager.notify(NOTIFICATION_ID_UPDATE_STATUS,
                        updateUpdateStatusNotificaion(context, builder, percent, statusUpdate));
            }
            this.percent = percent;
        }
    }
}
