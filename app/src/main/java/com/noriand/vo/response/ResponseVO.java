package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseVO {
	protected final String TAG_SUCCESS = "Y";
	protected final String TAG_FAIL = "N";

	public boolean isConfirm = false;
	
	public ResponseVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("isConfirm")) {
			String temp = jsonObject.getString("isConfirm");
			isConfirm = TAG_SUCCESS.equals(temp) ? true : false;
		}
	}
}
