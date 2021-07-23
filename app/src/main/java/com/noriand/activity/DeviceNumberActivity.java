package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.util.StringUtil;
import com.noriand.vo.DeviceItemVO;

import org.json.JSONException;
import org.json.JSONObject;


public class DeviceNumberActivity extends BaseActivity {
// --------------------------------------------------
    // Common


    // --------------------------------------------------
    // View

    private TextView mtvTitle = null;
    private RelativeLayout mrlPrev = null;

    private TextView mtvDeviceNumber = null;

    // --------------------------------------------------
    // Data

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_number);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
        setData();
    }

    private void setBase() {
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
                mtvDeviceNumber.setText(item.ltid);
            }
        }
    }

    private void setLayout() {
        mtvTitle = (TextView)findViewById(R.id.rl_device_number_title);
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_device_number_prev);
        mtvDeviceNumber = (TextView) findViewById(R.id.tv_device_number);
    }

    private void setListener() {
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
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