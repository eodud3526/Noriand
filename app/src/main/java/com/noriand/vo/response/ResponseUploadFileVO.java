package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseUploadFileVO extends ResponseVO {
	public String fileUrl = "";
	public String fileName = "";
	public boolean isSuccessUpdate = false;

	public ResponseUploadFileVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		super.parseJSONObject(jsonObject);

		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("fileUrl")) {
			fileUrl = jsonObject.getString("fileUrl");
		}
		if(!jsonObject.isNull("fileName")) {
			fileName = jsonObject.getString("fileName");
		}
		if(!jsonObject.isNull("isSuccessUpdate")) {
			String temp = jsonObject.getString("isSuccessUpdate");
			isSuccessUpdate = "Y".equals(temp) ? true : false;
		}
	}
}
