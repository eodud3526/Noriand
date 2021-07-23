package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;


public class DeviceItemVO {
	public int no = 0;
	public String name = "";
	public String pictureUrl = "";
	public int batteryCount = 0;
	public int refreshInterval = 0;
	public String strSafeZoneNoArray = "";
	public String lastX = "";
	public String lastY = "";
	public String insertTime = "";
	public String ltid = "";
	public String isBatteryAlarm = "";
	public int soundType = 0;

	public DeviceItemVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("no")) {
			no = jsonObject.getInt("no");
		}
		if(!jsonObject.isNull("name")) {
			name = jsonObject.getString("name");
		}
		if(!jsonObject.isNull("pictureUrl")) {
			pictureUrl = jsonObject.getString("pictureUrl");
		}
		if(!jsonObject.isNull("batteryCount")) {
			batteryCount = jsonObject.getInt("batteryCount");
		}
		if(!jsonObject.isNull("refreshInterval")) {
			refreshInterval = jsonObject.getInt("refreshInterval");
		}
		if(!jsonObject.isNull("strSafeZoneNoArray")) {
			strSafeZoneNoArray = jsonObject.getString("strSafeZoneNoArray");
		}
		if(!jsonObject.isNull("lastX")) {
			lastX = jsonObject.getString("lastX");
		}
		if(!jsonObject.isNull("lastY")) {
			lastY = jsonObject.getString("lastY");
		}
		if(!jsonObject.isNull("insertTime")) {
			String temp = jsonObject.getString("insertTime");
			if(temp != null && temp.length() > 10) {
				insertTime = temp.substring(0, 10);
			}
		}
		if(!jsonObject.isNull("ltid")) {
			ltid = jsonObject.getString("ltid");
		}

		if(!jsonObject.isNull("isBatteryAlarm")) {
			isBatteryAlarm = jsonObject.getString("isBatteryAlarm");
		}
		if(!jsonObject.isNull("soundType")) {
			soundType = jsonObject.getInt("soundType");
		}
	}

	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("no", no);
		jsonObject.put("name", name);
		jsonObject.put("pictureUrl", pictureUrl);
		jsonObject.put("batteryCount", batteryCount);
		jsonObject.put("refreshInterval", refreshInterval);
		jsonObject.put("strSafeZoneNoArray", strSafeZoneNoArray);
		jsonObject.put("lastX", lastX);
		jsonObject.put("lastY", lastY);
		jsonObject.put("insertTime", insertTime);
		jsonObject.put("ltid", ltid);
		jsonObject.put("isBatteryAlarm", isBatteryAlarm);
		jsonObject.put("soundType", soundType);
		return jsonObject;
	}
}
