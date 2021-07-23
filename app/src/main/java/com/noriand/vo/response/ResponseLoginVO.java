package com.noriand.vo.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseLoginVO extends ResponseVO {
	public int userNo = 0;
	public String name = "";
	public String email = "";
	public String phoneNumber = "";
	public String joinTime = "";
	public String lastLoginTime = "";

	public ResponseLoginVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}
		super.parseJSONObject(jsonObject);

		if(!jsonObject.isNull("userNo")) {
			userNo = jsonObject.getInt("userNo");
		}
		if(!jsonObject.isNull("email")) {
			email = jsonObject.getString("email");
		}
		if(!jsonObject.isNull("name")) {
			name = jsonObject.getString("name");
		}
		if(!jsonObject.isNull("phoneNumber")) {
			phoneNumber = jsonObject.getString("phoneNumber");
		}
		if(!jsonObject.isNull("joinTime")) {
			joinTime = jsonObject.getString("joinTime");
		}
		if(!jsonObject.isNull("lastLoginTime")) {
			lastLoginTime = jsonObject.getString("lastLoginTime");
		}
	}
}