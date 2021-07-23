package com.noriand.common;

import android.util.Log;

public class SLog {
	private static boolean isDebugState = true;
	
	public static void d(String tag, String text) {
		if(!isDebugState) {
			return;
		}

		Log.d(tag, text);
	}
}
