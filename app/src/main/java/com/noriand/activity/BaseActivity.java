package com.noriand.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.noriand.R;
import com.noriand.constant.ServerConstant;
import com.noriand.network.ApiController;
import com.noriand.util.DisplayUtils;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.view.dialog.CommonSelectDialog;
import com.noriand.view.dialog.CustomProgressDialog;
import com.noriand.vo.CommonSelectItemVO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

public class BaseActivity extends Activity {
	//--------------------------------------------------
	// Common
	protected final int REQUEST_CODE_CAMERA = 8111;
	protected final int REQUEST_CODE_GALLERY = 8112;

	public BaseActivity mActivity = this;

	public CommonDialog mCommonDialog = null;

	public ApiController mApiController = null;

	//--------------------------------------------------
	// View
	private CustomProgressDialog mProgressDialog = null;

	//--------------------------------------------------
	// Data

	protected int mPictureCode = 0;
	
	//--------------------------------------------------


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBase();
		setStatusBar(Color.WHITE);
	}

	public void setStatusBar(int color) {
		int sdkVersion = Build.VERSION.SDK_INT;
		if(sdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(color);
			if(sdkVersion >= Build.VERSION_CODES.M) {
				if(color == Color.WHITE) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				} else {
					window.getDecorView().setSystemUiVisibility(0);
				}
			}
		}
	}

	private void setBase() {
		mApiController = new ApiController();
	}

	public void startProgress() {
		if(mProgressDialog == null) {
			mProgressDialog = new CustomProgressDialog(mActivity);
		}

		mProgressDialog.show();
	}

	public void endProgress() {
		if(mProgressDialog != null) {
	    	mProgressDialog.dismiss();
		} else {
			mProgressDialog = new CustomProgressDialog(mActivity);
		}
	}

	public void showDialogOneButton(String message) {
		if(mCommonDialog != null && mCommonDialog.isShowing()) {
			mCommonDialog.cancel();
		}
		mCommonDialog = new CommonDialog(mActivity, null);
		mCommonDialog.showDialogOneButton(message);
	}

	public void showDialogTwoButton(String message) {
		if(mCommonDialog != null && mCommonDialog.isShowing()) {
			mCommonDialog.cancel();
		}
		mCommonDialog = new CommonDialog(mActivity, null);
		mCommonDialog.showDialogTwoButton(message, null, null);
	}

	public void showDialogOneButton(String message, CommonDialog.DialogConfirmListener listener) {
		if(mCommonDialog != null && mCommonDialog.isShowing()) {
			mCommonDialog.cancel();
		}
		mCommonDialog = new CommonDialog(mActivity, listener);
		mCommonDialog.showDialogOneButton(message);
	}

	public void showDialogTwoButton(String message, CommonDialog.DialogConfirmListener listener) {
		if(mCommonDialog != null && mCommonDialog.isShowing()) {
			mCommonDialog.cancel();
		}
		mCommonDialog = new CommonDialog(mActivity, listener);
		mCommonDialog.showDialogTwoButton(message, null, null);
	}

	public void showDialogTwoButton(String message, String cancelButtonName, String confirmButtonName, CommonDialog.DialogConfirmListener listener) {
		if(mCommonDialog != null && mCommonDialog.isShowing()) {
			mCommonDialog.cancel();
		}
		mCommonDialog = new CommonDialog(mActivity, listener);
		mCommonDialog.showDialogTwoButton(message, cancelButtonName, confirmButtonName);
	}

	public void showRetryDialogOneButton(CommonDialog.DialogConfirmListener listener) {
		String message = getResources().getString(R.string.please_retry_network);
		showDialogOneButton(message, listener);
	}

	public void showRetryDialogTwoButton(CommonDialog.DialogConfirmListener listener) {
		String message = getResources().getString(R.string.retry_network);
		showDialogTwoButton(message, listener);
	}

	public void showCommonSelectDialog(ArrayList<CommonSelectItemVO> list, CommonSelectDialog.DialogCommonSelectListener listener) {
		CommonSelectDialog dialog = new CommonSelectDialog(mActivity, listener);
		dialog.showCommonSelectDialog(list);
	}


	protected void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_GALLERY);
	}
	protected void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}



	public void cancelImage(ImageView iv) {
		ImageLoader.getInstance().cancelDisplayTask(iv);
	}

	public void setImage(Activity activity, ImageView iv, String imageUrl, final String filePath) {
		if(TextUtils.isEmpty(imageUrl) || iv == null) {
			return;
		}

		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

		String temp = ServerConstant.getMediaUrl(imageUrl);
		imageUrl = temp;

		final DisplayImageOptions options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.build();

		ImageLoader.getInstance().displayImage(imageUrl, iv, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if(view == null) {
					return;
				}
				ImageView imageView = (ImageView)view;
				if(TextUtils.isEmpty(filePath)) {
					return;
				}
				File file = new File(filePath);
				if (file.exists() && file.isFile()) {
					ImageLoader.getInstance().displayImage("file://" + file, imageView, options);
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	public void setImageForRadius(Activity activity, ImageView iv, String imageUrl, final String filePath, int baseDp) {
		if(TextUtils.isEmpty(imageUrl) || iv == null) {
			return;
		}

		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

		String temp = ServerConstant.getMediaUrl(imageUrl);
		imageUrl = temp;

		int radius = (int)(DisplayUtils.getPX(activity, baseDp));
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(radius))
				.bitmapConfig(Bitmap.Config.RGB_565)
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.build();

		ImageLoader.getInstance().displayImage(imageUrl, iv, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if(view == null) {
					return;
				}
				ImageView imageView = (ImageView)view;
				if(TextUtils.isEmpty(filePath)) {
					return;
				}
				File file = new File(filePath);
				if (file.exists() && file.isFile()) {
					ImageLoader.getInstance().displayImage("file://" + file, imageView, options);
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}
}
