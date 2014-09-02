package com.shawnhu.seagull.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.ViewPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

abstract public class AbsMediasViewActivity extends AbsFullScreenActionBarActivity {
    static final public String EXTRA_URIS = "urls";
    abstract protected void loadImage(ImageView imageView, String url);


    protected int getContentViewId() {
        return R.layout.twitter_medias_view;
    }
    protected int getFullScreenContentViewId() {
        return R.id.fullscreen_content;
    }

    protected CirclePageIndicator mPageIndicator;
    protected ViewPager mViewPager;
    protected MediaPagerAdapter mAdapter = new MediaPagerAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String[] mediaUrls;

        Intent i = getIntent();
        if (i != null) {
            mediaUrls = i.getStringArrayExtra(EXTRA_URIS);
            mAdapter.setMediaUrls(mediaUrls);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mPageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mPageIndicator.setViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MediaPagerAdapter extends ViewPagerAdapter {
        public MediaPagerAdapter(final Context context) {
            mContext = context;
        }

        Context  mContext;
        String[] mMediaUrls;
        static final int mResource = R.layout.picture;
        public void setMediaUrls(String[] urls) {
            mMediaUrls = urls;
        }

        @Override
        public View getView(int pos) {
            if (mContext != null && getCount() > 0) {
                pos %= mMediaUrls.length;

                String url = mMediaUrls[pos];
                LayoutInflater lf = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View   view = lf.inflate(mResource, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.picture);

                loadImage(imageView, url);

                return view;
            }

            return null;
        }
        @Override
        public int getCount() {
            if (mMediaUrls != null) {
                return mMediaUrls.length;
            }
            return 0;
        }
    }
}
