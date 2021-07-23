package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;


public class AlarmItemVO {
	public int no = 0;
	public int deviceNo = 0;
	public int alarmType = 0;
	public String title = "";
	public String content = "";
	public String x = "";
	public String y = "";
	public int safeZoneNo = 0;
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
		if(!jsonObject.isNull("alarmType")) {
			alarmType = jsonObject.getInt("alarmType");
		}
		if(!jsonObject.isNull("title")) {
			title = jsonObject.getString("title");
		}
		if(!jsonObject.isNull("content")) {
			content = jsonObject.getString("content");
		}
		if(!jsonObject.isNull("x")) {
			x = jsonObject.getString("x");
		}
		if(!jsonObject.isNull("y")) {
			y = jsonObject.getString("y");
		}
		if(!jsonObject.isNull("safeZoneNo")) {
			safeZoneNo = jsonObject.getInt("safeZoneNo");
		}
		if(!jsonObject.isNull("insertTime")) {
			insertTime = jsonObject.getString("insertTime");
		}
	}

	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("no", no);
		jsonObject.put("deviceNo", deviceNo);
		jsonObject.put("alarmType", alarmType);
		jsonObject.put("title", title);
		jsonObject.put("content", content);
		jsonObject.put("x", x);
		jsonObject.put("y", y);
		jsonObject.put("safeZoneNo", safeZoneNo);
		jsonObject.put("insertTime", insertTime);
		return jsonObject;
	}
}
