package com.shawnhu.seagull.seagull.twitter.fragments;


import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.model.TwitterAccount;
import com.shawnhu.seagull.seagull.twitter.model.TwitterMediaUpdate;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusUpdate;
import com.shawnhu.seagull.seagull.twitter.services.BackgroundIntentService;
import com.shawnhu.seagull.utils.ImageUtils;

import java.io.FileNotFoundException;

import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_STATUS_UPDATED;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.INTENT_ACTION_UPDATE_STATUS;

public class ComposeFragment extends Fragment {

    public static ComposeFragment newInstance(Bundle args) {
        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public ComposeFragment() {
    }

    EditText        mTweetTxt;
    ImageView       mTweetImage;
    TextView        mTextCount;
    ImageButton     mSendBtn;
    ImageButton     mPickPicBtn;
    ImageButton     mTakePhotoBtn;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == BROADCAST_STATUS_UPDATED) {
                int resCode = intent.getIntExtra(BackgroundIntentService.STATUS_UPDATE_RESULT, -1);
                if (resCode == BackgroundIntentService.STATUS_UPDATE_SUCCESS) {
                    Bitmap   largeIcon  = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                    Notification notification = new NotificationCompat.Builder(getActivity())
                            .setSmallIcon(R.drawable.ic_send_128)
                            .setLargeIcon(largeIcon)
                            .setTicker(getActivity().getString(R.string.tweet_sent))
                            .setContentTitle(getActivity().getString(R.string.tweet_sent))
                            .setContentText(getActivity().getString(R.string.tweet_sent))
                            .build();
                    NotificationManagerCompat.from(getActivity()).notify(0, notification);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTweetTxt.getWindowToken(), 0);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LocalBroadcastManager.getInstance(activity).registerReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_STATUS_UPDATED));
    }
    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);

        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_compose, container, false);

        mTweetTxt       = (EditText) v.findViewById(R.id.text);
        mTweetImage     = (ImageView) v.findViewById(R.id.image);
        mTextCount      = (TextView) v.findViewById(R.id.textCount);
        mSendBtn        = (ImageButton) v.findViewById(R.id.sendTweetBtn);
        mPickPicBtn     = (ImageButton) v.findViewById(R.id.pickPictureBtn);
        mTakePhotoBtn   = (ImageButton) v.findViewById(R.id.takePhotoBtn);

        setUpComposeView();

        return v;
    }
    private static final int SELECT_PHOTO = 100;
    private static final int TAKE_A_PHOTO = 200;
    private String new_photo_name;
    private Uri    mImageUri;

    private void setUpComposeView() {
        mTweetTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { mTextCount.setText(String.valueOf(s.length())); }
        });

        mPickPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtils.pickImage(ComposeFragment.this, SELECT_PHOTO, true, null);
            }
        });
        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_photo_name = getActivity().getPackageName() + System.currentTimeMillis() + ".jpg";
                ImageUtils.pickImage(ComposeFragment.this, TAKE_A_PHOTO, false, new_photo_name);
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterStatusUpdate status;
                TwitterMediaUpdate media = null;
                if (mImageUri != null) {
                    media = new TwitterMediaUpdate(mImageUri.toString(), 0);
                    mImageUri = null;
                }
                String text = mTweetTxt.getText().toString();
                TwitterAccount[] accounts =
                        TwitterAccount.getAccounts(getActivity(),
                                new long[] {Seagull.sCurrentAccount.sAccountId});
                status = (new TwitterStatusUpdate.Builder())
                        .text(text)
                        .medias(media)
                        .accounts(accounts)
                        .build();

                Intent i = new Intent(getActivity(), BackgroundIntentService.class);
                i.putExtra(SeagullTwitterConstants.EXTRA_STATUS, status);
                i.setAction(INTENT_ACTION_UPDATE_STATUS);
                getActivity().startService(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bmp = null;
            Uri tmp_uri = null;
            int size = mTweetImage.getWidth();
            try {
                Object[] rets = ImageUtils.onPickPhotoResult(getActivity(), requestCode, data,
                        TAKE_A_PHOTO, SELECT_PHOTO, new_photo_name, size);
                tmp_uri = (Uri) (rets[0]);
                bmp = (Bitmap) (rets[1]);
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }

            if (bmp != null) {
                mTweetImage.setImageBitmap(bmp);
                mImageUri = tmp_uri;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
