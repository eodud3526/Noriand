package com.noriand.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.util.DisplayUtils;
import com.noriand.vo.CommonSelectItemVO;

import java.util.ArrayList;

public class CommonSelectDialog extends Dialog {
	private Activity mActivity = null;
	private int mIndex = -1;
	ArrayList<CommonSelectItemVO> mList = null;

	private DialogCommonSelectListener mListener = null;

	public CommonSelectDialog(Activity activity, DialogCommonSelectListener listener) {
		super(activity);

		mActivity = activity;

		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_common_select);

		mListener = listener;
	
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mListener != null) {
					mListener.onSelect(mIndex, mList);
				}
			}
		});
	}

	public void showCommonSelectDialog(ArrayList<CommonSelectItemVO> list) {
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll_dialog_common_select);
		ll.removeAllViews();

		mList = new ArrayList<CommonSelectItemVO>();
		mList.addAll(list);

		if(list != null) {
			int size = list.size();
			if(size > 0) {
				int grayColor = Color.parseColor("#eeeeee");
				float dp1 = DisplayUtils.getPX(mActivity, 1);
				int dp14 = (int)(dp1 * 14);
				int dp60 = (int)(dp1 * 60);
				float scrollContainerHeight = 0;
				if(size > 6) {
					 scrollContainerHeight = dp1 * 408;
				} else {
					scrollContainerHeight = (dp60 * size) + (dp1 * (size - 1) + (dp1 * 8));
				}
				ScrollView sv = (ScrollView)findViewById(R.id.sv_dialog_common_select);

				RelativeLayout.LayoutParams lpScrollContainer = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)scrollContainerHeight);
				lpScrollContainer.addRule(RelativeLayout.CENTER_VERTICAL);
				lpScrollContainer.leftMargin = dp14;
				lpScrollContainer.rightMargin = dp14;
				sv.setLayoutParams(lpScrollContainer);

				LinearLayout.LayoutParams lpLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)dp1);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp60);
				for(int i=0; i<size; i++) {
					TextView tv = new TextView(mActivity);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					tv.setTextColor(Color.BLACK);
					tv.setGravity(Gravity.CENTER);
					tv.setMaxLines(3);
					tv.setEllipsize(TextUtils.TruncateAt.END);
					tv.setLayoutParams(lp);

					String str = list.get(i).text;
					tv.setText(str);

					tv.setBackgroundResource(R.drawable.selector_bgr_white_to_gray);
					final int position = i;
					tv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mIndex = position;
							dismiss();
						}
					});
					ll.addView(tv);

					if(size == 1) {
						tv.setBackgroundResource(R.drawable.selector_bgr_white_to_gray_corner);

						View vLine = new View(mActivity);
						vLine.setLayoutParams(lpLine);
						vLine.setBackgroundColor(grayColor);
						ll.addView(vLine);
					} else if(i == 0) {
						tv.setBackgroundResource(R.drawable.selector_bgr_white_to_gray_corner_top);

						View vLine = new View(mActivity);
						vLine.setLayoutParams(lpLine);
						vLine.setBackgroundColor(grayColor);
						ll.addView(vLine);
					} else if(i == size - 1) {
						tv.setBackgroundResource(R.drawable.selector_bgr_white_to_gray_corner_bottom);
					} else {
						tv.setBackgroundResource(R.drawable.selector_bgr_white_to_gray);

						View vLine = new View(mActivity);
						vLine.setLayoutParams(lpLine);
						vLine.setBackgroundColor(grayColor);
						ll.addView(vLine);
					}


				}
			}
		}

		View.OnClickListener cancelListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIndex = -1;
				dismiss();
			}
		};
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_dialog_common_select);
		rl.setOnClickListener(cancelListener);

		show();
	}

	public interface DialogCommonSelectListener {
		public void onSelect(int index, ArrayList<CommonSelectItemVO> list);
		public void onCancel();
	}
}
