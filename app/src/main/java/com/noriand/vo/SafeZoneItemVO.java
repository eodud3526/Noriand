package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class SafeZoneItemVO {
	public int no = 0;
	public int deviceNo = 0;
	public String name = "";
	public String x = "";
	public String y = "";
	public String isIn = "";
	public String isOut = "";
	public int boundary = 0;
	public String zoneCode = "";
	public String address = "";
	public String addressDetail = "";
	public String insertTime = "";

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("no")) {
			no = jsonObject.getInt("no");
		}
		if(!jsonObject.isNull("deviceNo")) {
			deviceNo = jsonObject.getInt("deviceNo");
		}
		if(!jsonObject.isNull("name")) {
			name = jsonObject.getString("name");
		}
		if(!jsonObject.isNull("x")) {
			x = jsonObject.getString("x");
		}
		if(!jsonObject.isNull("y")) {
			y = jsonObject.getString("y");
		}
		if(!jsonObject.isNull("isIn")) {
			isIn = jsonObject.getString("isIn");
		}
		if(!jsonObject.isNull("isOut")) {
			isOut = jsonObject.getString("isOut");
		}
		if(!jsonObject.isNull("boundary")) {
			boundary = jsonObject.getInt("boundary");
		}
		if(!jsonObject.isNull("zoneCode")) {
			zoneCode = jsonObject.getString("zoneCode");
		}
		if(!jsonObject.isNull("address")) {
			address = jsonObject.getString("address");
		}
		if(!jsonObject.isNull("addressDetail")) {
			addressDetail = jsonObject.getString("addressDetail");
		}
		if(!jsonObject.isNull("insertTime")) {
			insertTime = jsonObject.getString("insertTime");
		}
	}

	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("no", no);
		jsonObject.put("deviceNo", deviceNo);
		jsonObject.put("name", name);
		jsonObject.put("x", x);
		jsonObject.put("y", y);
		jsonObject.put("isIn", isIn);
		jsonObject.put("isOut", isOut);
		jsonObject.put("boundary", boundary);
		jsonObject.put("zoneCode", zoneCode);
		jsonObject.put("address", address);
		jsonObject.put("addressDetail", addressDetail);
		jsonObject.put("insertTime", insertTime);
		return jsonObject;
	}
}
