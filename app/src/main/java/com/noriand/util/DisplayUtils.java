package com.noriand.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayUtils {

	public static int[] getScrrenSize(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;

		int[] size = { width, height };
		return size;
	}
	
	
    public static float getPX(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
