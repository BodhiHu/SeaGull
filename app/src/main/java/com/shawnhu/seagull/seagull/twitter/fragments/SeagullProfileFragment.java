package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.BannerPagerAdapter;
import com.shawnhu.seagull.seagull.twitter.adapters.GraphPagerAdapter;
import com.shawnhu.seagull.seagull.twitter.adapters.UserViewBuilder;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.tasks.GetUserProfileTask;
import com.shawnhu.seagull.utils.ImageUtils;
import com.shawnhu.seagull.utils.gestures.VerticalPinGestureListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import twitter4j.User;

public class SeagullProfileFragment extends Fragment {

    private static final String TAG = "SeagullProfileFragment";

    static public SeagullProfileFragment newInstance(Bundle args) {
        SeagullProfileFragment fragment = new SeagullProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public SeagullProfileFragment() {}

    protected long mAccountId = -1;
    protected long mUserId = -1;
    protected User mUser = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mAccountId = args.getLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, -1);
            mUserId    = args.getLong(SeagullTwitterConstants.EXTRA_USER_ID);
        }

        if (mAccountId < 0 || mUserId < 0) {
            Log.e(TAG, "account OR user id is not valid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        View upperView = v.findViewById(R.id.upperView);

        final GestureDetectorCompat mGestureListener =
                new GestureDetectorCompat(getActivity(), new VerticalPinGestureListener(upperView));
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureListener.onTouchEvent(event);
            }
        });

        final ViewPager           bannerPager     = (ViewPager) v.findViewById(R.id.bannerPager);
        final CirclePageIndicator bannerIndicator = (CirclePageIndicator) v.findViewById(R.id.bannerIndicator);
        final ViewPager           graphPager      = (ViewPager) v.findViewById(R.id.graphPager);
        final TitlePageIndicator  graphTabInd     = (TitlePageIndicator) v.findViewById(R.id.graphTabIndicator);

        bannerPager.setAdapter(new BannerPagerAdapter(getActivity(), null, getActivity()));
        bannerIndicator.setViewPager(bannerPager);
        graphPager.setAdapter(new GraphPagerAdapter(getActivity(), mAccountId, null));
        graphTabInd.setViewPager(graphPager);

        loadViewAsync(bannerPager, graphPager);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void loadViewAsync(final ViewPager viewPager, final ViewPager graphPager) {
        GetUserProfileTask getUserProfileTask =
        new GetUserProfileTask(getActivity(), mAccountId, mUserId) {
            BitmapDrawable mBanner = null;
            @Override
            protected Response<twitter4j.User> doInBackground(final Void... params) {
                Response<twitter4j.User> response = super.doInBackground(params);

                if (response.hasData()) {
                /** load banner*/
                    User user = response.getData();

                    //String url = user.getProfileBannerImageUrl();
                    String url = user.getProfileBackgroundImageUrl();
                    if (url != null && url != "") {
                        mBanner = UserViewBuilder.loadUserBannerImageSync(user);
                    }
                }

                return response;
            }
            @Override
            protected void onPostExecuteSafe(final Response<User> result) {
                //on UI thread
                if (result.hasData()) {
                    mUser = result.getData();
                } else {
                    mUser = null;
                }
                //FIXME: seems call notifyDatasetChanged won't work
                BannerPagerAdapter bannerAdapter = (BannerPagerAdapter) viewPager.getAdapter();
                if (bannerAdapter != null) {
                    bannerAdapter.setUser(mUser);
                    viewPager.setAdapter(bannerAdapter);
                    if (mBanner != null) {
                        Bitmap bmp = mBanner.getBitmap();
                        if (bmp != null) {
                            bmp = ImageUtils.getCenterCropedBitmap(bmp, viewPager.getWidth(), viewPager.getHeight());
                        }
                        viewPager.setBackgroundDrawable(new BitmapDrawable(bmp));
                    }
                }
                GraphPagerAdapter  graphAdapter  = (GraphPagerAdapter)  graphPager.getAdapter();
                if (graphAdapter != null) {
                    graphAdapter.setUser(mUser);
                    graphPager.setAdapter(graphAdapter);
                }

                if (getActivity() != null) {
                    ActionBar actionBar = getActivity().getActionBar();
                    if (actionBar != null) {
                        actionBar.setSubtitle(mUser.getScreenName());
                    }
                }
            }
        };

        getUserProfileTask.setHost(this);
        getUserProfileTask.execute();
    }

    public String getUserScreenName() {
        return mUser != null ? mUser.getScreenName() : "";
    }
}
