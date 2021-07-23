package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;


public class ReviewItemVO {
	public int no = 0;
	public String name = "";
	public String content = "";
	public double ratingCount = 0;
	public String insertTime = "";

	public ReviewItemVO() {
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
		if(!jsonObject.isNull("content")) {
			content = jsonObject.getString("content");
		}
		if(!jsonObject.isNull("ratingCount")) {
			ratingCount = jsonObject.getDouble("ratingCount");
		}
		if(!jsonObject.isNull("insertTime")) {
			insertTime = jsonObject.getString("insertTime");
		}
	}
}
