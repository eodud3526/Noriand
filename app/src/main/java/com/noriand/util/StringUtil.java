package com.noriand.util;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static boolean isEmpty(String str) {
		if(str == null || str.length() == 0) {
			return true;
		}
		return false;
	}
	
	public static void selectionLast(EditText et) {
		Editable e = et.getText();
        Selection.setSelection(e, et.length());
        et.requestFocus();
	}

	public static void showKeyPad(Activity activity, EditText et) {
		InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(et, 0);
	}

	public static void hideKeyPad(Activity activity) {
		View v = (View)activity.getCurrentFocus();
		if(v != null) {
			InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
	
	public static void hideKeyPad(Activity activity, EditText et) {
		InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	public static boolean isValidString(String keyword) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣]*$");
		Matcher matcher = pattern.matcher(keyword);
		return matcher.matches();
	}

	public static String toNumFormat(int number) {
		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		return decimalFormat.format(number);
	}

	public static String getOnlyDigit(String str) {
		if(str == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int length = str.length();
		if(length == 0) {
			return "";
		}
		for (int i = 0; i < length; i++) {
			char charAt = str.charAt(i);
			if (Character.isDigit(charAt)) {
				sb.append(charAt);
			}
		}
	    return sb.toString();
	}
	
	public static boolean isOnlyDigit(String str) {
		boolean isDigit = true;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			char charAt = str.charAt(i);
			if (!Character.isDigit(charAt)) {
				isDigit = false;
				break;
			}
		}
	    return isDigit;
	}

	public static String replaceNull(String str) {
		if(str == null || str.length() == 0) {
			str = "";
		}
		return str;
	}

	public static int getStringNumber(String str) {
		int number = 0;
		if(str != null && str.length() > 0) {
			String temp = getOnlyDigit(str);
			number = Integer.parseInt(temp);
		}
		return number;
	}
	
	public static String getPriceFormat(int price) {
		DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
		return decimalFormat.format(price);
	}

	public static boolean isCheckOnlyHangul(String str) {
		int size = str.length();
		if(size == 0) {
			return false;
		}

		boolean result = true;
		for(int i=0; i<size; i++) {
			char c = str.charAt(i);
			if((c >= 'ㄱ' && c <= 'ㅎ') || c >= 'ㅏ' && c <= 'ㅣ') {
				result = false;
				break;
			}
		}
		return result;
	}

	public static boolean checkOnlyInitialOrString(String str) {
		if(str == null) {
			return false;
		}

		int size = str.length();
		if(size == 0) {
			return false;
		}

		boolean result = true;
		for(int i=0; i<size; i++) {
			char c = str.charAt(i);
			if(c < 'ㄱ' || c > 'ㅎ') {
				result = false;
				break;
			}
		}

		return result;
	}

	public static boolean checkOnlyInitialString(String str) {
		if(str == null) {
			return false;
		}

		int size = str.length();
		if(size == 0) {
			return false;
		}

		boolean result = true;
		for(int i=0; i<size; i++) {
			char c = str.charAt(i);
			if(c < 'ㄱ' || c > 'ㅎ') {
				result = false;
				break;
			}
		}
		
		return result;
	}

	public static boolean isCheckKorea(char c) {
		if(c >= '가' && c <= '힣') {
			return true;
		}
		return false;
	}

	public static boolean isCheckEnglish(char c) {
		//65 122
		if(c >= 'A' && c <= 'z') {
			return true;
		}
		return false;
	}
	
	public static String getInitialString(String str) {
		String[] chosungArray = {"ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};
		
		if(str == null) {
			return "";
		}

		int size = str.length();
		if(size == 0) {
			return "";
		}
		String result = "";
		for(int i=0; i<size; i++) {
			char c = str.charAt(i);
			if(c >= '가' && c <= '힣') {
				if(c >= 'ㄱ' && c <= 'ㅎ') {
					result += c;
				} else {
					char k = (char)(c - 44032);
					char chosung = (char)(((k - (k % 28))/28)/21);
					result += chosungArray[chosung];
				}
			} else {
				result += c;
			}
		}
		
		return result;
	}


	public static boolean isPhoneNumber(String str) {
		if(isEmpty(str)) {
			return false;
		}

		boolean isDigit = true;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			char charAt = str.charAt(i);
			if(charAt == '-') {
				continue;
			}
			if (!Character.isDigit(charAt)) {
				isDigit = false;
				break;
			}
		}
		return isDigit;
	}

	public static boolean isValidEmail(String str) {
		boolean isValid = false;
		if(str.contains("@")) {
			String remain = str.substring(str.indexOf("@") + 1, str.length());
			System.out.println(remain);
			if(remain.contains("@")) {
				isValid = false;
			} else {
				isValid = Pattern.matches("^.*(?=.{8,})[\\w.]+@[\\w.-]+[.][a-zA-Z0-9]+$", str);
			}
		} else {
			isValid = false;
		}
		return isValid;
	}

	public static String getStringNumberFor10(int i) {
		if(i < 10) {
			return ("0" + i);
		}
		return String.valueOf(i);
	}
}
