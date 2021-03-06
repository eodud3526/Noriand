package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.TraceItemVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KakaoLinkActivity extends BaseActivity{
    private DeviceItemVO mItem = null;
    private int mDeviceNo = 0;
    private ArrayList<TraceItemVO> mTraceList = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_setting);
        setStatusBar(Color.WHITE);

        setBase();
    }

    private void setBase() {
        mItem = new DeviceItemVO();
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                mItem.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }
        }
        mDeviceNo = mItem.no;
        mTraceList = new ArrayList<TraceItemVO>();

        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
        RequestGetTraceArrayVO requestItem = new RequestGetTraceArrayVO();
        requestItem.deviceNo = mDeviceNo;
        requestItem.userNo = userNo;
        requestItem.ltid = ltid;
        networkGetTraceArrayKakaoLink(requestItem);
    }

    public void kakaolink() {
        String title = mItem.name;
        String descrption = "?????? ??????";

        // ????????? ??????
        TraceItemVO item = mTraceList.get(0);
        String address = item.y + ", " + item.x;

        // ???????????? ?????????  ????????? ????????????. null??? ??????
        String imageUrl = "https://ifh.cc/g/n8ymoW.jpg"; // ?????? ?????????
        //String imageUrl = "https://ifh.cc/g/DyxiDr.png"; // ????????? ?????????
        // ?????? ????????? ????????? ????????? url, ????????? ?????? ????????? : 2022-04-18

        LocationTemplate params = LocationTemplate.newBuilder(
                address, // ????????? ??????
                ContentObject.newBuilder(title,imageUrl,
                        // ListTemplate
                        LinkObject.newBuilder() // ?????? ????????????
                                //.setWebUrl("https://developers.kakao.com")
                                //.setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(descrption)
                        .build())
                //?????? ????????? ????????? ???????????? ?????????
                .setAddressTitle("??????")
                // ?????? 1
                .addButton(new ButtonObject("??? ?????? ??????", LinkObject.newBuilder()
                        //.setWebUrl("'https://developers.kakao.com")
                        //.setMobileWebUrl("'https://developers.kakao.com")
                        // ??? ??????x -> ???????????? ??????, ??? ??????o -> ??? ??????
                        .setAndroidExecutionParams("key1=value1") // JSON -> P-> DID ?????? ?????? ??????.
                        .setIosExecutionParams("key1=value1")
                        .build())
                ).build();
        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }
            @Override
            public void onSuccess(KakaoLinkResponse result) {
                System.out.println(result.getTemplateMsg());
                // ????????? ?????????????????? ?????? ????????? ??????????????? ??????. ????????? ??????????????? ??????????????? ????????? ??? ??? ??????. ?????? ?????? ????????? ???????????? ????????? ??????????????? ??????.
            }
        });
    }

    private void networkGetTraceArrayKakaoLink(final RequestGetTraceArrayVO requestItem) {
        mApiController.getTraceArray(mActivity, requestItem, new ApiController.ApiGetTraceArrayListener() {
            @Override
            public void onSuccess(ResponseGetTraceArrayVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("?????? ????????? ????????????.");
                    return;
                }

                TraceItemVO[] traceArray = item.traceArray;
                if(item.traceArray == null) {
                    showDialogOneButton("?????? ????????? ????????????.");
                    return;
                }

                int length = traceArray.length;
                if(length == 0) {
                    showDialogOneButton("?????? ????????? ????????????.");
                    return;
                }
                for(int i=0; i<length; i++){
                    mTraceList.add(traceArray[i]);
                }
                kakaolink();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetTraceArrayKakaoLink(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }
}
