package com.noriand.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CommonPreferences {
	private static final String TAG_PREFERENCES = "noriandTest";

	public static final String TAG_USER_NO = "userNo"; //int
	public static final String TAG_DEVICE_NO = "deviceNo"; //int
	public static final String TAG_DEVICE_LTID = "deviceLtid"; //String
	public static final String TAG_LAST_X = "lastX"; //String
	public static final String TAG_LAST_Y = "lastY"; //String

	public static final String TAG_EMAIL = "email"; //String
	public static final String TAG_PHONE_NUMBER = "phoneNumber"; //String
	public static final String TAG_JOIN_TIME = "joinTime"; //String
	public static final String TAG_LAST_LOGIN_TIME = "lastLoginTime"; //String

	public static final String TAG_PUSH_TOKEN = "pushToken"; //String

	public static final String TAG_IS_NEW = "isNew"; //String
	public static final String TAG_SOS_CONTENT = "sosContent"; //String

	public static final String TAG_NOTIFY_COUNT = "notifyCount"; //int
	private static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(TAG_PREFERENCES, Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	public static void resetPreferences(Context context) {
		putInt(context, TAG_USER_NO, 0);
		putInt(context, TAG_DEVICE_NO, 0);
		putString(context, TAG_PHONE_NUMBER, "");
		putString(context, TAG_JOIN_TIME, "");
		putString(context, TAG_LAST_LOGIN_TIME, "");
	}

	public static void putDouble(Context context, String key, double value) {
		SharedPreferences pref = getSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putString(key, String.valueOf(value));
		editor.apply();
	}

	public static double getDouble(Context context, String key) {
		SharedPreferences pref = getSharedPreferences(context);
		String value = pref.getString(key, "0");
		return Double.parseDouble(value);
	}

	public static void putInt(Context context, String key, int value) {
		SharedPreferences pref = getSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences pref = getSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.apply();
	}


	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences pref = getSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public static void putLong(Context context, String key, long value) {
		SharedPreferences pref = getSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putLong(key, value);
		editor.apply();
	}

	public static int getInt(Context context, String key) {
		SharedPreferences pref = getSharedPreferences(context);
		return pref.getInt(key, 0);
	}

	public static String getString(Context context, String key) {
		SharedPreferences pref = getSharedPreferences(context);
		return pref.getString(key, "");
	}

	public static boolean getBoolean(Context context, String key) {
		SharedPreferences pref = getSharedPreferences(context);
		return pref.getBoolean(key, false);
	}

	public static long getLong(Context context, String key) {
		SharedPreferences pref = getSharedPreferences(context);
		return pref.getLong(key, 0);
	}
}
