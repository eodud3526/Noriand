package com.noriand.vo.request;

public class RequestUpdateDeviceSettingVO extends RequestVO {
	public int userNo = 0;
	public int deviceNo = 0;
	public int soundType = 0;
	public String isBatteryAlarm = "";
	public int refreshInterval = 0;
	public String ltid = "";

	public RequestUpdateDeviceSettingVO() {
		kind = "updateDeviceSetting";
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("userNo").append("=").append(userNo).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		sb.append("soundType").append("=").append(soundType).append("&");
		sb.append("isBatteryAlarm").append("=").append(isBatteryAlarm).append("&");
		sb.append("refreshInterval").append("=").append(refreshInterval).append("&");
		sb.append("ltid").append("=").append(ltid).append("&");
		return sb.toString();
	}
}