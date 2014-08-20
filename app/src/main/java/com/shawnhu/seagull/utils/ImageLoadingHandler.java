package com.shawnhu.seagull.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.shawnhu.seagull.R;

import java.util.HashMap;
import java.util.Map;

public class ImageLoadingHandler implements ImageLoadingListener, ImageLoadingProgressListener {

    private final Map<View, String> mLoadingUris = new HashMap<View, String>();
    private final int[] mProgressBarIds;

    public ImageLoadingHandler() {
        this(R.id.progressBar);
    }

    public ImageLoadingHandler(final int... progressBarIds) {
        mProgressBarIds = progressBarIds;
    }

    public String getLoadingUri(final View view) {
        return mLoadingUris.get(view);
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view) {
        if (view == null || imageUri == null || imageUri.equals(mLoadingUris.get(view))) return;
        mLoadingUris.remove(view);
        final ProgressBar progress = findProgressBar(view.getParent());
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap bitmap) {
        if (view == null) return;
        mLoadingUris.remove(view);
        final ProgressBar progress = findProgressBar(view.getParent());
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason reason) {
        if (view == null) return;
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
            view.setBackgroundResource(R.drawable.image_preview_refresh);
        }
        mLoadingUris.remove(view);
        final View parent = (View) view.getParent();
        final View progress = parent.findViewById(R.id.image_preview_progress);
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadingStarted(final String imageUri, final View view) {
        if (view == null || imageUri == null || imageUri.equals(mLoadingUris.get(view))) return;
        mLoadingUris.put(view, imageUri);
        final ProgressBar progress = findProgressBar(view.getParent());
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
            progress.setIndeterminate(true);
            progress.setMax(100);
        }
    }

    @Override
    public void onProgressUpdate(final String imageUri, final View view, final int current, final int total) {
        if (total == 0 || view == null) return;
        final ProgressBar progress = findProgressBar(view.getParent());
        if (progress != null) {
            progress.setIndeterminate(false);
            progress.setProgress(100 * current / total);
        }
    }

    private ProgressBar findProgressBar(final ViewParent viewParent) {
        if (mProgressBarIds == null || !(viewParent instanceof View)) return null;
        final View parent = (View) viewParent;
        for (final int id : mProgressBarIds) {
            final View progress = parent.findViewById(id);
            if (progress instanceof ProgressBar) return (ProgressBar) progress;
        }
        return null;
    }
}
