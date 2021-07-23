package com.noriand.vo.request;

import com.noriand.activity.BaseActivity;
import com.noriand.common.CommonPreferences;
import com.noriand.common.CommonTag;

public class RequestLoginVO extends RequestVO {
	public int no = 0;
	public String email = "";
	public String password = "";
	public String pushToken = "";
	public int osType = 0;

	public RequestLoginVO(BaseActivity baseActivity) {
		kind = "login";
		this.pushToken = CommonPreferences.getString(baseActivity, CommonPreferences.TAG_PUSH_TOKEN);
		this.osType = CommonTag.OS_TYPE_ANDROID;
	}

	public void setLogin(String email, String password) {
		this.kind = "login";
		this.email = email;
		this.password = password;
	}

	public void setLoginForAuto(int no, String email) {
		this.kind = "loginForAuto";
		this.no = no;
		this.email = email;
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("no").append("=").append(no).append("&");
		sb.append("email").append("=").append(email).append("&");
		sb.append("password").append("=").append(password).append("&");
		sb.append("pushToken").append("=").append(pushToken).append("&");
		sb.append("osType").append("=").append(osType).append("&");
		return sb.toString();
	}
}