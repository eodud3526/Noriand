package com.noriand.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;

import com.noriand.common.SLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {

	public String getAppVersionName(Activity activity) {
		String versionName = "";

		try {
			PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
		}

		return versionName;
	}

	public boolean checkOS() {
		String version = Build.VERSION.RELEASE;
		if(version.compareTo("4.1.2") < 1) {
			return false;
		}
		return true;
	}

	public String getOsVersion() {
		return Build.VERSION.RELEASE;
	}

	public String getModelName() {
		String model = Build.MODEL;
		if(model == null) {
			model = "";
		}
		return model;
	}

	public String getBrandName() {
		String model = Build.BRAND;
		if(model == null) {
			model = "";
		}
		return model;
	}

	public String getDeviceSerialNumber(Activity activity) {
		String serialNumber = Build.SERIAL;
		if(serialNumber == null) {
			serialNumber = "";
		}
		return serialNumber;
	}

	public String getNowDate() {
		return getDateTime("yyyyMMdd");
	}

	public String getNowDateTime() {
		return getDateTime("yyyyMMddHHmmss");
	}

	public String getDateTime(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.KOREA);
		Date date = new Date(System.currentTimeMillis());
		String nowDateTime = simpleDateFormat.format(date);
		return nowDateTime;
	}

	public int getMonthDayCount(int year, int month) {
		if(month > 0) {
			month--;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		int monthDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return monthDayCount;
	}

	public void getHashKey(Context context){
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
		}
		if(packageInfo == null) {
			SLog.d("mytest2", "KeyHash is null");
		}
		for(Signature signature : packageInfo.signatures) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				SLog.d("mytest2", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			} catch (NoSuchAlgorithmException e) {
				SLog.d("mytest2", "Unable to get MessageDigest. signature=" + signature + ", e: " + e.toString());
			}
		}
	}
}