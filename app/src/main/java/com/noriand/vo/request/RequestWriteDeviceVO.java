package com.noriand.vo.request;

public class RequestWriteDeviceVO extends RequestVO {
	public int userNo = 0;
	public int deviceNo = 0;
	public String ltid = "";
	public String name = "";

	public RequestWriteDeviceVO() {
		kind = "writeDevice";
	}

	public void changeUpdateMode() {
		kind = "updateDevice";
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("userNo").append("=").append(userNo).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		sb.append("ltid").append("=").append(ltid).append("&");
		sb.append("name").append("=").append(name).append("&");
		return sb.toString();
	}
}