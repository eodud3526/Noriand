package com.noriand.vo.request;

public class RequestDeleteSafeZoneVO extends RequestVO {
	public int no = 0;
	public int userNo = 0;
	public int deviceNo = 0;

	public RequestDeleteSafeZoneVO() {
		kind = "deleteSafeZone";
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("no").append("=").append(no).append("&");
		sb.append("userNo").append("=").append(userNo).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		return sb.toString();
	}
}