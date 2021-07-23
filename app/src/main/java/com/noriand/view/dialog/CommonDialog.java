package com.noriand.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;

public class CommonDialog extends Dialog {
	private DialogConfirmListener mListener = null;
	private boolean isConfirm = false;
	
	public CommonDialog(Activity activity, DialogConfirmListener listener) {
		super(activity);

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);
		
		mListener = listener;

		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mListener != null) {
					if(isConfirm) {
						mListener.onConfirm();
					} else {
						mListener.onCancel();
					}
				}
			}
		});
	}

	public void showDialogOneButton(String msg) {
		RelativeLayout rlOneButton = (RelativeLayout) findViewById(R.id.rl_dialog_onebutton);
		rlOneButton.setVisibility(View.VISIBLE);
		
		TextView tv = (TextView) findViewById(R.id.tv_dialog_onebutton);
		tv.setText(msg);
		
		Button btConfirm = (Button) findViewById(R.id.btn_dialog_onebutton_confirm);
		btConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				isConfirm = true;
				dismiss();
			}
		});

		show();
	}
	
	public void showDialogTwoButton(String msg, String cancelButtonName, String confirmButtonName) {
		RelativeLayout rlTwoButton = (RelativeLayout) findViewById(R.id.rl_dialog_twobutton);
		rlTwoButton.setVisibility(View.VISIBLE);

		TextView tv = (TextView) findViewById(R.id.tv_dialog_twobutton);
		tv.setText(msg);

		Button btConfirm = (Button) findViewById(R.id.btn_dialog_twobutton_confirm);
		btConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				isConfirm = true;
				dismiss();
			}
		});
	
		View.OnClickListener closeListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		};
	
		Button btCancel = (Button) findViewById(R.id.btn_dialog_twobutton_cancel);
		btCancel.setOnClickListener(closeListener);
	
		if(cancelButtonName != null) {
			btCancel.setText(cancelButtonName);
		}
		if(confirmButtonName != null) {
			btConfirm.setText(confirmButtonName);
		}

		show();
	}
	
	
	public void showDialogUpdate(String msg, String leftButtonName, String rightButtonName) {
		RelativeLayout rlTwoButton = (RelativeLayout) findViewById(R.id.rl_dialog_twobutton);
		rlTwoButton.setVisibility(View.VISIBLE);
		
		TextView tv = (TextView) findViewById(R.id.tv_dialog_twobutton);
		tv.setText(msg);
	
		View.OnClickListener closeListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		};
		
		Button btConfirm = (Button) findViewById(R.id.btn_dialog_twobutton_confirm);
		btConfirm.setText(rightButtonName);
		btConfirm.setOnClickListener(closeListener);

		Button btCancel = (Button) findViewById(R.id.btn_dialog_twobutton_cancel);
		btCancel.setText(leftButtonName);
		btCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				isConfirm = true;
				dismiss();
			}
		});

		show();
	}

	public interface DialogConfirmListener {
		public void onConfirm();
		public void onCancel();
	}
}
