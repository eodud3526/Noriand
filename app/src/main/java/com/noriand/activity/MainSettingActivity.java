package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import com.noriand.R;
import com.noriand.vo.DeviceItemVO;

import org.json.JSONException;
import org.json.JSONObject;

public class MainSettingActivity extends BaseActivity{
    private final int REQUEST_CODE_ALARM = 212;
    private final int REQUEST_CODE_SAFE_ZONE = 213;
    private final int REQUEST_CODE_DEVICE_UPDATE = 214;

    private RelativeLayout mrlSubDeviceNumber = null;
    private RelativeLayout mrlShareFriend = null;
    private RelativeLayout mrlAlarm = null;
    private RelativeLayout mrlDeviceSetting = null;
    private RelativeLayout mrlVoucher = null;
    private RelativeLayout mrlSafeZone = null;
    private RelativeLayout mrlActionHistory = null;

    private RelativeLayout mrlSubPencil = null;

    private DeviceItemVO mItem = null;


    private Button mbtnMenu = null;

    private DrawerLayout mdl = null;
    private RelativeLayout mrlSubMenuArea = null;
    private DrawerLayout.DrawerListener mDrawerListener = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar(Color.WHITE);

        setLayout();
        setListener();
    }

    public void setLayout(){
        mbtnMenu = (Button)findViewById(R.id.btn_main_submenu);
        mdl = (DrawerLayout)findViewById(R.id.dl_main);
        mrlSubPencil = (RelativeLayout)findViewById(R.id.rl_main_submenu_pencil);
        mrlSubMenuArea = (RelativeLayout)findViewById(R.id.rl_main_sub_menu);
        mrlSubDeviceNumber = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_number);
        mrlShareFriend = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_share_friend);
        mrlAlarm = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_alarm);
        mrlDeviceSetting = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_setting);
        mrlVoucher = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_voucher);
        mrlSafeZone = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_safe_zone);
        mrlActionHistory = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_action_history);
    }

    public void setListener() {
        mDrawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View v, float x) {
                mdl.bringChildToFront(v);
                mdl.requestLayout();
            }

            @Override
            public void onDrawerOpened(View arg0) {
                mbtnMenu.setBackgroundResource(R.drawable.selector_btn_close); // 주석확인
            }

            @Override
            public void onDrawerClosed(View arg0) {
                mbtnMenu.setBackgroundResource(R.drawable.selector_btn_menu); // 주석확인
            }
        };
        mdl.addDrawerListener(mDrawerListener);
        mbtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdl.isDrawerOpen(mrlSubMenuArea)) {
                    mdl.closeDrawers();
                } else {
                    mdl.openDrawer(mrlSubMenuArea);
                }
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
                mdl.closeDrawers();
                moveDeviceNumberActivity(mItem);
            }
        });
        mrlShareFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                showDialogOneButton("기능 준비중입니다.");
            }
        });
        mrlAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveAlarmActivity();
            }
        });
        mrlDeviceSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveDeviceSettingActivity(mItem);
            }
        });
        mrlVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOneButton("기능 준비중입니다.");
            }
        });
        mrlSafeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveSafeZoneListActivity();
            }
        });
        mrlActionHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveActionHistoryActivity();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mdl.isDrawerOpen(mrlSubMenuArea)) {
                mdl.closeDrawers();
                return false;
            }

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
}
