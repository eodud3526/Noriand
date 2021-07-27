package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionHistoryItemVO {

    public int userNo = 0;
    public int deviceNo = 0;
    public String fromDt = "";
    public String toDt = "";
    public String viewDate = "";
    public String dist = "";

    public void parseJSONObject(JSONObject jsonObject) throws JSONException {
        if(jsonObject == null) {
            return;
        }
        if(!jsonObject.isNull("viewDate")) {
            viewDate = jsonObject.getString("viewDate");
        }
        if(!jsonObject.isNull("dist")) {
            dist = jsonObject.getString("dist");
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userNo", userNo);
        jsonObject.put("deviceNo", deviceNo);
        jsonObject.put("fromDt", fromDt);
        jsonObject.put("toDt", toDt);
        return jsonObject;
    }

    public String toString() {return "viewDate : " + viewDate + " dist : " + dist;}
}
