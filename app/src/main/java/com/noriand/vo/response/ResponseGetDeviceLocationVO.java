package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetDeviceLocationVO extends ResponseVO {
	public String lastX = "";
	public String lastY = "";
	public String today = "";

	public ResponseGetDeviceLocationVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}
		super.parseJSONObject(jsonObject);
        if(!jsonObject.isNull("lastX")) {
            lastX = jsonObject.getString("lastX");
        }
		if(!jsonObject.isNull("lastY")) {
			lastY = jsonObject.getString("lastY");
		}
		if(!jsonObject.isNull("today")) {
			today = jsonObject.getString("today");
		}
	}
}