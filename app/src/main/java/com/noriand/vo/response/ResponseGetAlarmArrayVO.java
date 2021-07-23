package com.noriand.vo.response;

import com.noriand.vo.AlarmItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetAlarmArrayVO extends ResponseVO {
	public AlarmItemVO[] alarmArray = null;

	public ResponseGetAlarmArrayVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		super.parseJSONObject(jsonObject);
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("alarmArray")) {
			JSONArray jsonArray = jsonObject.getJSONArray("alarmArray");
			if(jsonArray != null) {
				int size = jsonArray.length();
				if(size > 0) {
					alarmArray = new AlarmItemVO[size];
					for(int i=0; i<size; i++) {
						AlarmItemVO item = new AlarmItemVO();
						item.parseJSONObject(jsonArray.getJSONObject(i));
						alarmArray[i] = item;
					}
				}
			}
		}
	}
}