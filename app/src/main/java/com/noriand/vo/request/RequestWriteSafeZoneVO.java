package com.noriand.vo.request;

public class RequestWriteSafeZoneVO extends RequestVO {
	public int no = 0;
	public int userNo = 0;
	public int deviceNo = 0;
	public String name = "";
	public int boundary = 0;
	public String isIn = "";
	public String isOut = "";
	public String x = "";
	public String y = "";
	public String zoneCode = "";
	public String address = "";
	public String addressDetail = "";

	public RequestWriteSafeZoneVO() {
		kind = "writeSafeZone";
	}

	public void changeUpdateMode(int no) {
		kind = "updateSafeZone";
		this.no = no;
	}

	public String getParameter() {
		StringBuffer sb = new StringBuffer();
		sb.append("kind").append("=").append(kind).append("&");
		sb.append("no").append("=").append(no).append("&");
		sb.append("userNo").append("=").append(userNo).append("&");
		sb.append("deviceNo").append("=").append(deviceNo).append("&");
		sb.append("name").append("=").append(name).append("&");
		sb.append("boundary").append("=").append(boundary).append("&");
		sb.append("isIn").append("=").append(isIn).append("&");
		sb.append("isOut").append("=").append(isOut).append("&");
		sb.append("x").append("=").append(x).append("&");
		sb.append("y").append("=").append(y).append("&");
		sb.append("zoneCode").append("=").append(zoneCode).append("&");
		sb.append("address").append("=").append(address).append("&");
		sb.append("addressDetail").append("=").append(addressDetail).append("&");
		return sb.toString();
	}
}