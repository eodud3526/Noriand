package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseJoinVO extends ResponseVO {
	public boolean isExistUser = false;
	public int userNo = 0;
	public String joinTime = "";
	public String lastLoginTime = "";

	public ResponseJoinVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}
		super.parseJSONObject(jsonObject);

		if(!jsonObject.isNull("userNo")) {
			userNo = jsonObject.getInt("userNo");
		}

		if(!jsonObject.isNull("isExistUser")) {
			String temp = jsonObject.getString("isExistUser");
			isExistUser = "Y".equals(temp);
		}

		if(!jsonObject.isNull("joinTime")) {
			joinTime = jsonObject.getString("joinTime");
		}
		if(!jsonObject.isNull("lastLoginTime")) {
			lastLoginTime = jsonObject.getString("lastLoginTime");
		}
	}
}