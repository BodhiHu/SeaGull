package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.adapters.BannerPagerAdapter;
import com.shawnhu.seagull.seagull.twitter.adapters.GraphPagerAdapter;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.tasks.GetUserProfileTask;
import com.shawnhu.seagull.seagull.twitter.utils.ImageLoaderWrapper;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TabPageIndicator;

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

        final ViewPager           bannerPager     = (ViewPager) v.findViewById(R.id.bannerPager);
        final CirclePageIndicator bannerIndicator = (CirclePageIndicator) v.findViewById(R.id.bannerIndicator);
        final ViewPager           graphPager      = (ViewPager) v.findViewById(R.id.graphPager);
        final TabPageIndicator    graphTabInd     = (TabPageIndicator) v.findViewById(R.id.graphTabIndicator);

        bannerPager.setAdapter(new BannerPagerAdapter(getActivity(), null));
        bannerIndicator.setViewPager(bannerPager);
        graphPager.setAdapter(new GraphPagerAdapter(getActivity(), null));
        graphTabInd.setViewPager(graphPager);

        loadViewAsync(bannerPager, graphPager);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void loadViewAsync(final ViewPager viewPager, final ViewPager graphPager) {
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
                        //final String type = Utils.getBestBannerType(vp.getWidth());
                        //url = url + "/" + type;

                        ImageLoader imageLoader = TwitterManager.getInstance().getImageLoader();
                        Bitmap bitmap = imageLoader.loadImageSync(url, ImageLoaderWrapper.mBannerDisplayOptions);
                        if (bitmap != null) {
                            mBanner = new BitmapDrawable(bitmap);
                        }
                    }
                }

                return response;
            }
            @Override
            protected void onPostExecute(final Response<User> result) {
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
                        viewPager.setBackgroundDrawable(mBanner);
                    }
                }
                GraphPagerAdapter  graphAdapter  = (GraphPagerAdapter)  graphPager.getAdapter();
                if (graphAdapter != null) {
                    graphAdapter.setUser(mUser);
                    graphPager.setAdapter(graphAdapter);
                }
            }
        }.execute();
    }
}
