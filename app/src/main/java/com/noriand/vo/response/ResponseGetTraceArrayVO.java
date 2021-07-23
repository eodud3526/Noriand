package com.noriand.vo.response;

import com.noriand.vo.TraceItemVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseGetTraceArrayVO extends ResponseVO {
	public TraceItemVO[] traceArray = null;

	public ResponseGetTraceArrayVO() {
	}

	public void parseJSONObject(JSONObject jsonObject) throws JSONException {
		super.parseJSONObject(jsonObject);
		if(jsonObject == null) {
			return;
		}

		if(!jsonObject.isNull("traceArray")) {
			JSONArray jsonArray = jsonObject.getJSONArray("traceArray");
			if(jsonArray != null) {
				int size = jsonArray.length();
				if(size > 0) {
					traceArray = new TraceItemVO[size];
					for(int i=0; i<size; i++) {
						TraceItemVO item = new TraceItemVO();
						item.parseJSONObject(jsonArray.getJSONObject(i));
						traceArray[i] = item;
					}
				}
			}
		}
	}
}