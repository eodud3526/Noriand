package com.noriand.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.noriand.R;


public class PictureSelectDialog extends Dialog {
	public final int MODE_NONE = 0;
	public final int MODE_CAMERA = 1;
	public final int MODE_GALLERY = 2;

	private int mMode = MODE_NONE;

	private DialogPictureSelectListener mListener = null;

	public PictureSelectDialog(Activity activity, DialogPictureSelectListener listener) {
		super(activity);
	
		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_picture_select);
	
		mListener = listener;
	
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mListener != null) {
					if(mMode == MODE_NONE) {
						mListener.onCancel();
					} else if(mMode == MODE_CAMERA) {
						mListener.onCamera();
					} else if(mMode == MODE_GALLERY) {
						mListener.onGallery();
					}
				}
			}
		});
	}

	public void showPictureSelectDialog() {
		RelativeLayout rlCamera = (RelativeLayout)findViewById(R.id.rl_dialog_picture_select_camera);
		rlCamera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMode = MODE_CAMERA;
				dismiss();
			}
		});

		RelativeLayout rlGallery = (RelativeLayout)findViewById(R.id.rl_dialog_picture_select_gallery);
		rlGallery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMode = MODE_GALLERY;
				dismiss();
			}
		});

		View.OnClickListener cancelListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMode = MODE_NONE;
				dismiss();
			}
		};

		RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_dialog_picture_select);
		rl.setOnClickListener(cancelListener);

		show();
	}

	public interface DialogPictureSelectListener {
		public void onCamera();
		public void onGallery();
		public void onCancel();
	}
}
