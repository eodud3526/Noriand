package com.noriand.view.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
	private Runnable mRunnable = null;
	private int mInitialPosition = 0;

	private int mNewCheck = 100;

	private int mRange = 0;
	private int mCount = 0;

	public interface OnScrollStoppedListener {
		void onScrollStopped();
		void onScrollChanged(int x, int y, int oldX, int oldY, int range);
	}

	private OnScrollStoppedListener mListener = null;

	public CustomScrollView(Context context) {
		super(context);
		init();
	}
	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);
		if(mListener != null) {
			if(mCount < 5) {
				int range = getScrollRange();
				if(mRange < range) {
					mRange = range;
				}
				mCount++;
			}
			mListener.onScrollChanged(x, y, oldX, oldY, mRange);
		}
	}
	
	private void init() {
		mRunnable = new Runnable() {
			public void run() {
				int newPosition = getScrollY();
				if (mInitialPosition - newPosition == 0) {
					if (mListener != null) {
						mListener.onScrollStopped();
					}
				} else {
					mInitialPosition = getScrollY();
					postDelayed(mRunnable, mNewCheck);
				}
			}
		};
	}

	public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
		mListener = listener;
	}

	public void startScrollerTask() {
		mInitialPosition = getScrollY();
		postDelayed(mRunnable, mNewCheck);
	}

	public int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
		return scrollRange;
	}
}
