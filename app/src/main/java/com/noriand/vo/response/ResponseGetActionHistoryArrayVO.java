package com.noriand.vo.response;

import com.noriand.vo.ActionHistoryItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetActionHistoryArrayVO extends ResponseVO {
    public ActionHistoryItemVO[] actionHistoryArray = null;

    public ResponseGetActionHistoryArrayVO() {
    }

    public void parseJSONObject(JSONObject jsonObject) throws JSONException {
        super.parseJSONObject(jsonObject);
        if(jsonObject == null) {
            return;
        }

        if(!jsonObject.isNull("actionHistoryArray")) {
            JSONArray jsonArray = jsonObject.getJSONArray("actionHistoryArray");
            if(jsonArray != null) {
                int size = jsonArray.length();
                if(size > 0) {
                    actionHistoryArray = new ActionHistoryItemVO[size];
                    for(int i=0; i<size; i++) {
                        ActionHistoryItemVO item = new ActionHistoryItemVO();
                        item.parseJSONObject(jsonArray.getJSONObject(i));
                        actionHistoryArray[i] = item;
                    }
                }
            }
        }
    }
}
