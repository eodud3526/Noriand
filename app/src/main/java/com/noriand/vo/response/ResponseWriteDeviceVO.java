package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseWriteDeviceVO extends ResponseVO {
	public int no = 0;
	public boolean isExistDevice = false;

	public ResponseWriteDeviceVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}
		super.parseJSONObject(jsonObject);

		if(!jsonObject.isNull("no")) {
			no = jsonObject.getInt("no");
		}

		if(!jsonObject.isNull("isExistDevice")) {
			String temp = jsonObject.getString("isExistDevice");
			isExistDevice = "Y".equals(temp);
		}
	}
}