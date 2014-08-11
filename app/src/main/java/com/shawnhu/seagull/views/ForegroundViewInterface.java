package com.shawnhu.seagull.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public interface ForegroundViewInterface {

	/**
	 * Returns the drawable used as the foreground of this FrameLayout. The
	 * foreground drawable, if non-null, is always drawn on top of the children.
	 * 
	 * @return A Drawable or null if no foreground was set.
	 */
	public Drawable getForeground();

	/**
	 * Supply a Drawable that is to be rendered on top of all of the child views
	 * in the frame layout. Any padding in the Drawable will be taken into
	 * account by ensuring that the children are inset to be placed inside of
	 * the padding area.
	 * 
	 * @param drawable The Drawable to be drawn on top of the children.
	 * 
	 * @attr ref android.R.attr#foreground
	 */
	public void setForeground(final Drawable drawable);

	/**
	 * Describes how the foreground is positioned. Defaults to START and TOP.
	 * 
	 * @param foregroundGravity See {@link android.view.Gravity}
	 * 
	 * @attr ref android.R.attr#foregroundGravity
	 */
	public void setForegroundGravity(int foregroundGravity);

	public static class ForegroundViewHelper {

		private final View mView;

		private final Rect mSelfBounds = new Rect();
		private final Rect mOverlayBounds = new Rect();

		private Drawable mForeground;

		private int mForegroundGravity = Gravity.FILL;
		private boolean mForegroundInPadding = true;
		private boolean mForegroundBoundsChanged = false;

		public ForegroundViewHelper(final View view, final Context context, final AttributeSet attrs, final int defStyle) {
			mView = view;
			final TypedArray a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.foreground,
					android.R.attr.foregroundGravity }, defStyle, 0);

			mForegroundGravity = a.getInt(1, mForegroundGravity);

			final Drawable d = a.getDrawable(0);
			if (d != null) {
				setForeground(d);
			}

			mForegroundInPadding = true;

			a.recycle();
		}

		public void dispatchOnDraw(final Canvas canvas) {
			if (mForeground != null) {
				final Drawable foreground = mForeground;

				if (mForegroundBoundsChanged) {
					mForegroundBoundsChanged = false;
					final Rect selfBounds = mSelfBounds;
					final Rect overlayBounds = mOverlayBounds;

					final int w = mView.getRight() - mView.getLeft();
					final int h = mView.getBottom() - mView.getTop();

					if (mForegroundInPadding) {
						selfBounds.set(0, 0, w, h);
					} else {
						selfBounds.set(mView.getPaddingLeft(), mView.getPaddingTop(), w - mView.getPaddingRight(), h
								- mView.getPaddingBottom());
					}

					final int layoutDirection = ViewCompat.getLayoutDirection(mView);
					GravityCompat.apply(mForegroundGravity, foreground.getIntrinsicWidth(),
                            foreground.getIntrinsicHeight(), selfBounds, overlayBounds, layoutDirection);
					foreground.setBounds(overlayBounds);
				}

				foreground.draw(canvas);
			}
		}

		public void dispatchOnLayout(final boolean changed, final int left, final int top, final int right,
				final int bottom) {
			mForegroundBoundsChanged = true;
		}

		public void dispatchOnSizeChanged(final int w, final int h, final int oldw, final int oldh) {
			mForegroundBoundsChanged = true;
		}

		public void draw(final Canvas canvas) {
			if (mForeground != null) {
				final Drawable foreground = mForeground;

				if (mForegroundBoundsChanged) {
					mForegroundBoundsChanged = false;
					final Rect selfBounds = mSelfBounds;
					final Rect overlayBounds = mOverlayBounds;

					final int w = mView.getRight() - mView.getLeft();
					final int h = mView.getBottom() - mView.getTop();

					if (mForegroundInPadding) {
						selfBounds.set(0, 0, w, h);
					} else {
						selfBounds.set(mView.getPaddingLeft(), mView.getPaddingTop(), w - mView.getPaddingRight(), h
								- mView.getPaddingBottom());
					}

					final int layoutDirection = ViewCompat.getLayoutDirection(mView);
					GravityCompat.apply(mForegroundGravity, foreground.getIntrinsicWidth(),
                            foreground.getIntrinsicHeight(), selfBounds, overlayBounds, layoutDirection);
					foreground.setBounds(overlayBounds);
				}

				foreground.draw(canvas);
			}
		}

		public void drawableStateChanged() {
			if (mForeground != null && mForeground.isStateful()) {
				mForeground.setState(mView.getDrawableState());
			}
		}

		public Drawable getForeground() {
			return mForeground;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void jumpDrawablesToCurrentState() {
			if (mForeground != null) {
				mForeground.jumpToCurrentState();
			}
		}

		public void setForeground(final Drawable drawable) {
			if (mForeground != drawable) {
				if (mForeground != null) {
					mForeground.setCallback(null);
					mView.unscheduleDrawable(mForeground);
				}

				mForeground = drawable;

				if (drawable != null) {
					drawable.setCallback(mView);
					if (drawable.isStateful()) {
						drawable.setState(mView.getDrawableState());
					}
					if (mForegroundGravity == Gravity.FILL) {
						final Rect padding = new Rect();
						if (drawable.getPadding(padding)) {
						}
					}
				}
				mView.requestLayout();
				mView.invalidate();
			}
		}

		public void setForegroundGravity(int foregroundGravity) {
			if (mForegroundGravity != foregroundGravity) {
				if ((foregroundGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
					foregroundGravity |= Gravity.START;
				}

				if ((foregroundGravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
					foregroundGravity |= Gravity.TOP;
				}

				mForegroundGravity = foregroundGravity;

				if (mForegroundGravity == Gravity.FILL && mForeground != null) {
					final Rect padding = new Rect();
					if (mForeground.getPadding(padding)) {
					}
				} else {
				}

				mView.requestLayout();
			}
		}

		public boolean verifyDrawable(final Drawable who) {
			return who == mForeground;
		}
	}
}