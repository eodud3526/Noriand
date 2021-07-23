package com.noriand.vo.response;

import com.noriand.vo.SafeZoneItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetSafeZoneArrayVO extends ResponseVO {
	public SafeZoneItemVO[] safeZoneArray = null;

	public ResponseGetSafeZoneArrayVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		super.parseJSONObject(jsonObject);
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("safeZoneArray")) {
			JSONArray jsonArray = jsonObject.getJSONArray("safeZoneArray");
			if(jsonArray != null) {
				int size = jsonArray.length();
				if(size > 0) {
					safeZoneArray = new SafeZoneItemVO[size];
					for(int i=0; i<size; i++) {
						SafeZoneItemVO item = new SafeZoneItemVO();
						item.parseJSONObject(jsonArray.getJSONObject(i));
						safeZoneArray[i] = item;
					}
				}
			}
		}
	}
}