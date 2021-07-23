package com.noriand.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.noriand.R;

public class CustomProgressDialog extends Dialog {
private Activity mActivity = null;
	
	private AnimationDrawable mAnimationDrawable = null;
	private ImageView miv = null;
	
	public CustomProgressDialog(Activity activity) {
		super(activity);

		mActivity = activity;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setCancelable(false);
		
		setContentView(R.layout.dialog_progress);
		miv = (ImageView) findViewById(R.id.iv_progress);
		miv.setBackgroundResource(R.drawable.progress_dialog);
	}


	@Override
	public void show() {
		if(mActivity == null || miv == null) {
			if(mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
				mAnimationDrawable.stop();
			}
			return;
		}
		
		if(mAnimationDrawable == null) {
			mAnimationDrawable = (AnimationDrawable)miv.getBackground();
		}
		mAnimationDrawable.start();

		if(mActivity != null && !mActivity.isFinishing()) {
			super.show();
		}
	}


	@Override
	public void dismiss() {
		if(mActivity == null || miv == null) {
			if(mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
				mAnimationDrawable.stop();
			}
			return;
		}
		
		if(mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
			mAnimationDrawable.stop();
		}

		if(mActivity != null && !mActivity.isFinishing()) {
			super.dismiss();
		}
	}
}
