package com.noriand.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionHistoryItemVO {

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
        jsonObject.put("viewDate", viewDate);
        jsonObject.put("dist", dist);
        return jsonObject;
    }
}
