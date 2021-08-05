package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionHistoryItemVO {

    public int userNo = 0;
    public int deviceNo = 0;
    public String fromDt = "";
    public String toDt = "";
    public String ltid = "";
    public String viewDate = "";
    public String dist = "";

    public void parseJSONObject(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return;
        }
        if (!jsonObject.isNull("viewDate")) {
            viewDate = jsonObject.getString("viewDate");
        }
        if (!jsonObject.isNull("dist")) {
            dist = jsonObject.getString("dist");
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userNo", userNo);
        jsonObject.put("deviceNo", deviceNo);
        jsonObject.put("ltid", ltid);
        jsonObject.put("fromDt", fromDt);
        jsonObject.put("toDt", toDt);
        return jsonObject;
    }

    public String toString() {
        return addDash(viewDate) + "                                                     " + String.format("%,d", Integer.parseInt(dist)) + "m";
    }

    public String addDash(String s){
        s = s.substring(0,4) + "-" + s.substring(4,6) + "-" + s.substring(6);
        return s;
    }
}
