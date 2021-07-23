package com.noriand.vo.request;


public class RequestGetDeviceLocationVO extends RequestVO {
	public int deviceNo = 0;

	public RequestGetDeviceLocationVO() {
		kind = "getDeviceLocation";
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		return sb.toString();
	}
}