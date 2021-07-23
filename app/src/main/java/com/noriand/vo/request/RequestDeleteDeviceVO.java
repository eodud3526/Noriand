package com.noriand.vo.request;

public class RequestDeleteDeviceVO extends RequestVO {
	public int userNo = 0;
	public int deviceNo = 0;

	public RequestDeleteDeviceVO() {
		kind = "deleteDevice";
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("userNo").append("=").append(userNo).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		return sb.toString();
	}
}