package com.noriand.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.ActionHistoryItemVO;
import com.noriand.vo.request.RequestGetActionHistoryVO;
import com.noriand.vo.response.ResponseGetActionHistoryArrayVO;

import java.util.ArrayList;
import java.util.Calendar;

public class ActionHistoryActivity extends BaseActivity {
    private ArrayList<ActionHistoryItemVO> mActionHistoryList;
    private ListView mlv = null;
    Button startDate, endDate = null;
    Button searchButton = null;
    private int year=0, monthOfYear=0, dayOfMonth=0, yearEnd=0, monthOfYearEnd=0, dayOfMonthEnd=0;
    private String fromDt, toDt;
    RequestGetActionHistoryVO requestItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_history);
        setStatusBar(Color.WHITE);
        setBase();
        setListener();
        setLayout();
    }

    public void setBase(){
        mActionHistoryList = new ArrayList<>();
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        searchButton = findViewById(R.id.searchButton);
        requestItem = new RequestGetActionHistoryVO();
    }

    public void setListener(){
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                monthOfYear = c.get(Calendar.MONTH);
                dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(ActionHistoryActivity.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        startDate.setText(year+ " - " + getMonth(monthOfYear) + " - " + getDay(dayOfMonth));
                        fromDt = "" + year + getMonth(monthOfYear) + getDay(dayOfMonth);
                    }
                }, year, monthOfYear, dayOfMonth);
                dpd.show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == endDate){
                    Calendar c = Calendar.getInstance();
                    yearEnd = c.get(Calendar.YEAR);
                    monthOfYearEnd = c.get(Calendar.MONTH);
                    dayOfMonthEnd = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dpd = new DatePickerDialog(ActionHistoryActivity.this, new DatePickerDialog.OnDateSetListener(){
                        @Override
                        public void onDateSet(DatePicker view, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd){
                            endDate.setText(yearEnd+ " - " + getMonth(monthOfYearEnd) + " - " + getDay(dayOfMonthEnd));
                            toDt = "" + yearEnd + getMonth(monthOfYearEnd) + getDay(dayOfMonthEnd);
                        }
                    }, yearEnd, monthOfYearEnd, dayOfMonthEnd);
                    dpd.show();
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                requestItem.userNo = userNo;
                requestItem.deviceNo = deviceNo;
                requestItem.fromDt = fromDt;
                requestItem.toDt = toDt;
                networkGetActionHistory(requestItem);
            }
        });
    }

    public void setLayout(){
        mlv = (ListView) findViewById(R.id.lv_action_history);
    }

    private void networkGetActionHistory(final RequestGetActionHistoryVO requestItem) {
        mApiController.getActionHistory(mActivity, requestItem, new ApiController.ApiGetActionHistoryListener() {
            @Override
            public void onSuccess(ResponseGetActionHistoryArrayVO item) {
                ArrayList<String> viewDateList;
                ArrayList<String> dist;

                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                ActionHistoryItemVO[] actionHistoryArray = item.actionHistoryArray;
                if(item.actionHistoryArray == null) {
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                int length = actionHistoryArray.length;
                if(length == 0) {
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                for(int i=0; i<length; i++) {
                    mActionHistoryList.add(actionHistoryArray[i]);
                }

            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetActionHistory(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }
    public static String getMonth(int month){
        if(month < 10){
            return "0"+String.valueOf(month+1);
        }
        else{
            return String.valueOf(month+1);
        }
    }
    public static String getDay(int day){
        if(day < 10){
            return "0"+String.valueOf(day);
        }
        else{
            return String.valueOf(day);
        }
    }

}

