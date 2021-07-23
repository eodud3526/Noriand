package com.noriand.vo.response;

import com.noriand.vo.SafeZoneItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetNowLocationVO extends ResponseVO {
	public String today = "";
	public String x = "";
	public String y = "";
	public int batteryCount = 0;
	public SafeZoneItemVO[] safeZoneArray = null;
	public String isLora = "";

	public ResponseGetNowLocationVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}
		super.parseJSONObject(jsonObject);

		if(!jsonObject.isNull("isLora")) {
			isLora = jsonObject.getString("isLora");
		}
		if(!jsonObject.isNull("today")) {
			today = jsonObject.getString("today");
		}
		if(!jsonObject.isNull("x")) {
			x = jsonObject.getString("x");
		}
		if(!jsonObject.isNull("y")) {
			y = jsonObject.getString("y");
		}
		if(!jsonObject.isNull("batteryCount")) {
			batteryCount = jsonObject.getInt("batteryCount");
		}

		if(!jsonObject.isNull("safeZoneArray")) {
			JSONArray jsonArray = jsonObject.getJSONArray("safeZoneArray");
			if(jsonArray != null) {
				int size = jsonArray.length();
				if(size > 0) {
					safeZoneArray = new SafeZoneItemVO[size];
					for(int i=0; i<size; i++) {
						SafeZoneItemVO item = new SafeZoneItemVO();
						item.parseJSONObject(jsonArray.getJSONObject(i));
						safeZoneArray[i] = item;
					}
				}
			}
		}
	}
}