package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestUpdateDeviceSettingVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseVO;

import org.json.JSONException;
import org.json.JSONObject;


public class DeviceSettingActivity extends BaseActivity {
// --------------------------------------------------
    // Common


    // --------------------------------------------------
    // View

    private TextView mtvTitle = null;
    private RelativeLayout mrlPrev = null;

    private Button mbtSave = null;

    private RelativeLayout mrlSoundTypeSound = null;
    private View mvSoundTypeSound = null;
    private RelativeLayout mrlSoundTypeNone = null;
    private View mvSoundTypeNone = null;

    private RelativeLayout mrlBatteryAlarmPercentOn = null;
    private View mvBatteryAlarmPercentOn = null;
    private RelativeLayout mrlBatteryAlarmPercentOff = null;
    private View mvBatteryAlarmPercentOff = null;

    private RelativeLayout mrlRefreshInterval1 = null;
    private View mvRefreshInterval1 = null;
    private RelativeLayout mrlRefreshInterval2 = null;
    private View mvRefreshInterval2 = null;
    private RelativeLayout mrlRefreshInterval5 = null;
    private View mvRefreshInterval5 = null;
    private RelativeLayout mrlRefreshInterval10 = null;
    private View mvRefreshInterval10 = null;



    // --------------------------------------------------
    // Data
    private int mSoundType = 0;
    private String isBatteryAlarm = "";
    private int mRefreshInterval = 0;

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
        RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
        requestItem.onChangeDeviceArrayForDevice(deviceNo);
        requestItem.userNo = userNo;
        requestItem.isSilent = false;
        networkGetDeviceArray(requestItem);
    }

    private void networkGetDeviceArray(final RequestGetDeviceArrayVO requestItem) {
        mApiController.getDeviceArray(mActivity, requestItem, new ApiController.ApiGetDeviceArrayListener() {
            @Override
            public void onSuccess(ResponseGetDeviceArrayVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("통신에 실패했습니다. 기기 정보를 확인 후, 다시 시도해 주세요.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            onBack();
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }

                DeviceItemVO[] deviceArray = item.deviceArray;
                int size = deviceArray.length;
                if(size == 0) {
                    return;
                }

                DeviceItemVO deviceItem = deviceArray[0];
                mRefreshInterval = deviceItem.refreshInterval;
                mSoundType = deviceItem.soundType;
                isBatteryAlarm = deviceItem.isBatteryAlarm;

                drawCheckAll();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetDeviceArray(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private void setBase() {
        mSoundType = 1;
        isBatteryAlarm = "Y";
        mRefreshInterval = 1 * 60000;
    }

    private void setData() {
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            DeviceItemVO item = new DeviceItemVO();
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                item.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }

            if(item != null) {
                mtvTitle.setText(item.name);
                mSoundType = item.soundType;
                isBatteryAlarm = item.isBatteryAlarm;
                mRefreshInterval = item.refreshInterval;
            }
        }

        drawCheckAll();
    }

    private void drawCheckAll() {
        if(mSoundType == 1) {
            mvSoundTypeSound.setBackgroundResource(R.drawable.btn_check_pressed);
            mvSoundTypeNone.setBackgroundResource(R.drawable.btn_check);
        } else {
            mvSoundTypeSound.setBackgroundResource(R.drawable.btn_check);
            mvSoundTypeNone.setBackgroundResource(R.drawable.btn_check_pressed);
        }

        if("Y".equals(isBatteryAlarm)) {
            mvBatteryAlarmPercentOn.setBackgroundResource(R.drawable.btn_check_pressed);
            mvBatteryAlarmPercentOff.setBackgroundResource(R.drawable.btn_check);
        } else {
            mvBatteryAlarmPercentOn.setBackgroundResource(R.drawable.btn_check);
            mvBatteryAlarmPercentOff.setBackgroundResource(R.drawable.btn_check_pressed);
        }

        if(mRefreshInterval == 1) {
            mvRefreshInterval1.setBackgroundResource(R.drawable.btn_check_pressed);
            mvRefreshInterval2.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval5.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval10.setBackgroundResource(R.drawable.btn_check);
        } else if(mRefreshInterval == 2) {
            mvRefreshInterval1.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval2.setBackgroundResource(R.drawable.btn_check_pressed);
            mvRefreshInterval5.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval10.setBackgroundResource(R.drawable.btn_check);
        } else if(mRefreshInterval == 5) {
            mvRefreshInterval1.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval2.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval5.setBackgroundResource(R.drawable.btn_check_pressed);
            mvRefreshInterval10.setBackgroundResource(R.drawable.btn_check);
        } else if(mRefreshInterval == 10) {
            mvRefreshInterval1.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval2.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval5.setBackgroundResource(R.drawable.btn_check);
            mvRefreshInterval10.setBackgroundResource(R.drawable.btn_check_pressed);
        }

        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mbtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
                RequestUpdateDeviceSettingVO requestItem = new RequestUpdateDeviceSettingVO();
                requestItem.userNo = userNo;
                requestItem.deviceNo = deviceNo;
                requestItem.soundType = mSoundType;
                requestItem.isBatteryAlarm = isBatteryAlarm;
                requestItem.refreshInterval = mRefreshInterval;
                requestItem.ltid = ltid;
                networkUpdateDeviceSetting(requestItem);
            }
        });
    }

    private void networkUpdateDeviceSetting(final RequestUpdateDeviceSettingVO requestItem) {
        mApiController.updateDeviceSetting(mActivity, requestItem, new ApiController.ApiCommonListener() {
            @Override
            public void onSuccess(ResponseVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("장치의 설정이 변경되지 못했습니다. 값을 확인해 주세요.");
                    return;
                }

                showDialogOneButton("장치의 설정이 변경 되었습니다. 적용까지 시간이 소요될 수 있습니다.", new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        onBack();
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
                        networkUpdateDeviceSetting(requestItem);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }


    private void setLayout() {
        mtvTitle = (TextView)findViewById(R.id.tv_device_setting_title);
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_device_setting_prev);

        mbtSave = (Button)findViewById(R.id.btn_device_setting_save);

        mrlSoundTypeSound = (RelativeLayout)findViewById(R.id.rl_device_setting_sound_type_sound);
        mvSoundTypeSound = (View)findViewById(R.id.v_device_setting_sound_type_sound);
        mrlSoundTypeNone = (RelativeLayout)findViewById(R.id.rl_device_setting_sound_type_none);
        mvSoundTypeNone = (View)findViewById(R.id.v_device_setting_sound_type_none);

        mrlBatteryAlarmPercentOn = (RelativeLayout)findViewById(R.id.rl_device_setting_battery_alarm_percent_on);
        mvBatteryAlarmPercentOn = (View)findViewById(R.id.v_device_setting_battery_alarm_percent_on);
        mrlBatteryAlarmPercentOff = (RelativeLayout)findViewById(R.id.rl_device_setting_battery_alarm_percent_off);
        mvBatteryAlarmPercentOff = (View)findViewById(R.id.v_device_setting_battery_alarm_percent_off);

        mrlRefreshInterval1 = (RelativeLayout)findViewById(R.id.rl_device_setting_refresh_interval_1);
        mvRefreshInterval1 = (View)findViewById(R.id.v_device_setting_refresh_interval_1);
        mrlRefreshInterval2 = (RelativeLayout)findViewById(R.id.rl_device_setting_refresh_interval_2);
        mvRefreshInterval2 = (View)findViewById(R.id.v_device_setting_refresh_interval_2);
        mrlRefreshInterval5 = (RelativeLayout)findViewById(R.id.rl_device_setting_refresh_interval_5);
        mvRefreshInterval5 = (View)findViewById(R.id.v_device_setting_refresh_interval_5);
        mrlRefreshInterval10 = (RelativeLayout)findViewById(R.id.rl_device_setting_refresh_interval_10);
        mvRefreshInterval10 = (View)findViewById(R.id.v_device_setting_refresh_interval_10);
    }

    private void setListener() {
        mrlSoundTypeSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundType = 1;
                drawCheckAll();
            }
        });
        mrlSoundTypeNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundType = 0;
                drawCheckAll();
            }
        });

        mrlBatteryAlarmPercentOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBatteryAlarm = "Y";
                drawCheckAll();
            }
        });
        mrlBatteryAlarmPercentOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBatteryAlarm = "";
                drawCheckAll();
            }
        });

        mrlRefreshInterval1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshInterval = 1;
                drawCheckAll();
            }
        });
        mrlRefreshInterval2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshInterval = 2;
                //mRefreshInterval = 5;
                drawCheckAll();
            }
        });
        mrlRefreshInterval5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshInterval = 5;
                //mRefreshInterval = 30;
                drawCheckAll();
            }
        });
        mrlRefreshInterval10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshInterval = 10;
                //mRefreshInterval = 60;
                drawCheckAll();
            }
        });
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}