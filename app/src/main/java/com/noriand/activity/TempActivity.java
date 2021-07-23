package com.noriand.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

import com.noriand.R;


public class TempActivity extends BaseActivity {
// --------------------------------------------------
    // Common


    // --------------------------------------------------
    // View

    // --------------------------------------------------
    // Data


    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar(Color.WHITE);

        setLayout();
        setListener();
    }

    private void setLayout() {

    }

    private void setListener() {

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