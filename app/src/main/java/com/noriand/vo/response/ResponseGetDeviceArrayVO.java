package com.noriand.vo.response;

import com.noriand.vo.DeviceItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetDeviceArrayVO extends ResponseVO {
	public DeviceItemVO[] deviceArray = null;
	public String today = "";
	public String state = "";

	public ResponseGetDeviceArrayVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		super.parseJSONObject(jsonObject);
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("deviceArray")) {
			JSONArray jsonArray = jsonObject.getJSONArray("deviceArray");
			if(jsonArray != null) {
				int size = jsonArray.length();
				if(size > 0) {
					deviceArray = new DeviceItemVO[size];
					for(int i=0; i<size; i++) {
						DeviceItemVO item = new DeviceItemVO();
						item.parseJSONObject(jsonArray.getJSONObject(i));
						deviceArray[i] = item;
					}
				}
			}
		}

		if(!jsonObject.isNull("today")) {
			today = jsonObject.getString("today");
		}
		if(!jsonObject.isNull("state")) {
			state = jsonObject.getString("state");
		}
	}
}