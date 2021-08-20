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
        String descrption = "위치 표시";

        // 검색할 주소
        TraceItemVO item = mTraceList.get(0);
        String address = item.y + ", " + item.x;

        // 이미지가 없으면  비어서 보여진다. null은 안됨
        String imageUrl = "https://ifh.cc/g/n8ymoW.jpg"; // 기기 이미지
        //String imageUrl = "https://ifh.cc/g/DyxiDr.png"; // 아이콘 이미지
        // 링크 화면에 보여줄 이미지 url, 이미지 링크 만료일 : 2022-04-18

        LocationTemplate params = LocationTemplate.newBuilder(
                address, // 보여줄 주소
                ContentObject.newBuilder(title,imageUrl,
                        // ListTemplate
                        LinkObject.newBuilder() // 링크 오브젝트
                                //.setWebUrl("https://developers.kakao.com")
                                //.setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(descrption)
                        .build())
                //위치 보기시 하단에 나타나는 타이틀
                .setAddressTitle("주소")
                // 버튼 1
                .addButton(new ButtonObject("앱 설치 링크", LinkObject.newBuilder()
                        //.setWebUrl("'https://developers.kakao.com")
                        //.setMobileWebUrl("'https://developers.kakao.com")
                        // 앱 설치x -> 오픈마켓 링크, 앱 설치o -> 앱 실행
                        .setAndroidExecutionParams("key1=value1") // JSON -> P-> DID 주소 뒤에 담김.
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
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
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
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                TraceItemVO[] traceArray = item.traceArray;
                if(item.traceArray == null) {
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                int length = traceArray.length;
                if(length == 0) {
                    showDialogOneButton("최근 기록이 없습니다.");
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
