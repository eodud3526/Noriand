package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.noriand.vo.request.RequestDeleteDeviceVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;
import com.noriand.vo.response.ResponseVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainSettingActivity extends BaseActivity{
    private final int REQUEST_CODE_ALARM = 212;
    private final int REQUEST_CODE_SAFE_ZONE = 213;
    private final int REQUEST_CODE_DEVICE_UPDATE = 214;

    private TextView mtvSubName = null;
    private ImageView mivSub = null;

    private RelativeLayout mrlSubDeviceNumber = null;
    private RelativeLayout mrlShareFriend = null;
    private RelativeLayout mrlAlarm = null;
    private RelativeLayout mrlDeviceSetting = null;
    private RelativeLayout mrlVoucher = null;
    private RelativeLayout mrlSafeZone = null;
    private RelativeLayout mrlActionHistory = null;

    private RelativeLayout mrlSubPencil = null;
    private RelativeLayout mrlPrev = null;
    private RelativeLayout mrlDelete = null;

    private DeviceItemVO mItem = null;
    private ArrayList<TraceItemVO> mTraceList = null;

    private int mDeviceNo = 0;


    //private RelativeLayout mrlSubMenuArea = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
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
    }

    public void setLayout(){
        mtvSubName = (TextView)findViewById(R.id.tv_main_submenu_name);
        mivSub = (ImageView)findViewById(R.id.iv_main_submenu_picture);
        mrlDelete = (RelativeLayout)findViewById(R.id.rl_device_write_delete);

        mrlSubPencil = (RelativeLayout)findViewById(R.id.rl_main_submenu_pencil);
        //mrlSubMenuArea = (RelativeLayout)findViewById(R.id.rl_main_sub_menu);
        mrlSubDeviceNumber = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_number);
        mrlShareFriend = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_share_friend);
        mrlAlarm = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_alarm);
        mrlDeviceSetting = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_setting);
        mrlVoucher = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_voucher);
        mrlSafeZone = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_safe_zone);
        mrlActionHistory = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_action_history);

        mrlPrev = (RelativeLayout)findViewById(R.id.rl_action_history_prev);

    }

    public void setListener() {
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceSelectActivity();
            }
        });
        mrlSubPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceUpdateActivity(mItem);
            }
        });
        mrlSubDeviceNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceNumberActivity(mItem);
            }
        });
        mrlShareFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
                RequestGetTraceArrayVO requestItem = new RequestGetTraceArrayVO();
                requestItem.deviceNo = mDeviceNo;
                requestItem.userNo = userNo;
                requestItem.ltid = ltid;
                networkGetTraceArrayKakaoLink(requestItem);
                //showDialogOneButton("?????? ??????????????????.");
            }
        });
        mrlAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveAlarmActivity();
            }
        });
        mrlDeviceSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceSettingActivity(mItem);
            }
        });
        mrlVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOneButton("?????? ??????????????????.");
            }
        });
        mrlSafeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSafeZoneListActivity();
            }
        });
        mrlActionHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveActionHistoryActivity();
            }
        });
        mrlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTwoButton("????????? ?????????????????????????", new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                        RequestDeleteDeviceVO requestItem = new RequestDeleteDeviceVO();
                        requestItem.deviceNo = mDeviceNo;
                        requestItem.userNo = userNo;
                        networkDeleteDevice(requestItem);
                        moveDeviceSelectActivity();
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });

        mtvSubName.setText(mItem.name);

        String pictureUrl = mItem.pictureUrl;
        if(!StringUtil.isEmpty(pictureUrl)) {
            setImage(mActivity, mivSub, pictureUrl, null);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            moveDeviceSelectActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void moveDeviceSettingActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, DeviceSettingActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveDeviceNumberActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, DeviceNumberActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveDeviceUpdateActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, DeviceWriteActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_DEVICE_UPDATE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveAlarmActivity() {
        Intent intent = new Intent(mActivity, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_ALARM);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveDeviceSelectActivity() {
        Intent intent = new Intent(mActivity, DeviceSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    public void moveSafeZoneListActivity() {
        Intent intent = new Intent(mActivity, SafeZoneListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_SAFE_ZONE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveActionHistoryActivity() {
        Intent intent = new Intent(mActivity, ActionHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private void networkDeleteDevice(final RequestDeleteDeviceVO requestItem) {
        mApiController.deleteDevice(mActivity, requestItem, new ApiController.ApiCommonListener() {
            @Override
            public void onSuccess(ResponseVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("????????? ??????????????????. ?????? ????????? ?????? ???, ?????? ????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }

                String message = "????????? ?????????????????????.";
                showDialogOneButton(message, new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        Intent intent = getIntent();
                        intent.putExtra("isDelete", "Y");
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkDeleteDevice(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
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
