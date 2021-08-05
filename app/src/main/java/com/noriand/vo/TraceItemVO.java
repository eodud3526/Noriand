package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;


public class TraceItemVO {
	public int no = 0;
	public int deviceNo = 0;
	public String x = "";
	public String y = "";
	public String insertTime = "";
	public String ltid = "";

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("no")) {
			no = jsonObject.getInt("no");
		}
		if(!jsonObject.isNull("deviceNo")) {
			deviceNo = jsonObject.getInt("deviceNo");
		}
		if(!jsonObject.isNull("x")) {
			x = jsonObject.getString("x");
		}
		if(!jsonObject.isNull("y")) {
			y = jsonObject.getString("y");
		}
		if(!jsonObject.isNull("insertTime")) {
			insertTime = jsonObject.getString("insertTime");
		}
		if(!jsonObject.isNull("ltid")) {
			ltid = jsonObject.getString("ltid");
		}
	}

	public JSONObject getJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("no", no);
		jsonObject.put("deviceNo", deviceNo);
		jsonObject.put("x", x);
		jsonObject.put("y", y);
		jsonObject.put("insertTime", insertTime);
		jsonObject.put("ltid", ltid);
		return jsonObject;
	}

	public String toString(){
		return "insertTime = " + insertTime;
	}
}
