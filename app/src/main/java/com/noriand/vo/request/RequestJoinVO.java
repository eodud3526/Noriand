package com.noriand.vo.request;


import com.noriand.activity.BaseActivity;
import com.noriand.common.CommonPreferences;
import com.noriand.common.CommonTag;

public class RequestJoinVO extends RequestVO {
	public String email = "";
	public String password = "";
	public String phoneNumber = "";
	public String pushToken = "";
	public int osType = 0;

	public RequestJoinVO(BaseActivity baseActivity) {
		kind = "join";
		this.pushToken = CommonPreferences.getString(baseActivity, CommonPreferences.TAG_PUSH_TOKEN);
		this.osType = CommonTag.OS_TYPE_ANDROID;
	}

	public void setUserInformation(String email, String password, String phoneNumber) {
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("email").append("=").append(email).append("&");
		sb.append("password").append("=").append(password).append("&");
		sb.append("phoneNumber").append("=").append(phoneNumber).append("&");
		sb.append("pushToken").append("=").append(pushToken).append("&");
		sb.append("osType").append("=").append(osType).append("&");
		return sb.toString();
	}
}