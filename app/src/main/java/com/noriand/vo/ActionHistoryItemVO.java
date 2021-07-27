package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionHistoryItemVO {
    public int no = 0;
    public int deviceNo = 0;
    public String fromDt = "";
    public String toDt = "";

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
        if(!jsonObject.isNull("fromDt")) {
            fromDt = jsonObject.getString("fromDt");
        }
        if(!jsonObject.isNull("toDt")) {
            toDt = jsonObject.getString("toDt");
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("no", no);
        jsonObject.put("deviceNo", deviceNo);
        jsonObject.put("fromDt", fromDt);
        jsonObject.put("toDt", toDt);
        return jsonObject;
    }
}
