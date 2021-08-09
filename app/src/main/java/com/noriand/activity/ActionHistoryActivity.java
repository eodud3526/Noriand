package com.noriand.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.ActionHistoryItemVO;
import com.noriand.vo.TraceItemVO;
import com.noriand.vo.request.RequestGetActionHistoryVO;
import com.noriand.vo.response.ResponseGetActionHistoryArrayVO;

import java.util.ArrayList;
import java.util.Calendar;

public class ActionHistoryActivity extends BaseActivity {
    private ArrayList<ActionHistoryItemVO> mActionHistoryList;

    private ListView mlv = null;
    ArrayAdapter<ActionHistoryItemVO> mAdapter;
    private ArrayList<TraceItemVO> mTraceList = null;

    Button startDate, endDate = null;
    Button searchButton = null;
    EditText sum = null;
    private RelativeLayout mrlPrev = null;

    private int year=0, monthOfYear=0, dayOfMonth=0, yearEnd=0, monthOfYearEnd=0, dayOfMonthEnd=0;
    private int sum_of_dist = 0;
    private String fromDt, toDt;
    RequestGetActionHistoryVO requestItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_history);
        setStatusBar(Color.WHITE);
        setBase();
        setListener();
    }

    public void setBase(){
        mActionHistoryList = new ArrayList<>();
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        searchButton = findViewById(R.id.searchButton);
        requestItem = new RequestGetActionHistoryVO();
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_action_history_prev);
        mTraceList = new ArrayList<TraceItemVO>();
    }

    public void setListener(){
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
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
                c.add(Calendar.YEAR, -1);
                dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, 1);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
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
                    c.add(Calendar.YEAR, -1);
                    dpd.getDatePicker().setMinDate(c.getTimeInMillis());
                    c.add(Calendar.YEAR, 1);
                    dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                    dpd.show();
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);

                requestItem.userNo = userNo;
                requestItem.deviceNo = deviceNo;
                requestItem.ltid = ltid;
                requestItem.fromDt = fromDt;
                requestItem.toDt = toDt;
                networkGetActionHistory(requestItem);
                clear();
            }
        });

    }

    private void clear() {
        mActionHistoryList.clear();
        sum_of_dist = 0;
    }

    private void networkGetActionHistory(final RequestGetActionHistoryVO requestItem) {
        mApiController.getActionHistory(mActivity, requestItem, new ApiController.ApiGetActionHistoryListener() {
            @Override
            public void onSuccess(ResponseGetActionHistoryArrayVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }
                if(!item.isConfirm) {
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }
                ActionHistoryItemVO[] actionHistoryArray = item.actionHistoryArray;
                if(actionHistoryArray != null) {
                    int size = actionHistoryArray.length;
                    for (int i = 0; i < size; i++) {
                        ActionHistoryItemVO ActionHistoryItem = actionHistoryArray[i];
                        mActionHistoryList.add(ActionHistoryItem);
                        sum_of_dist += Integer.parseInt(actionHistoryArray[i].dist);
                    }
                }
                sum = (EditText)findViewById(R.id.sum_of_dist);
                sum.setText(String.format("%,d", Integer.parseInt(String.valueOf(sum_of_dist))) + "m");
                mlv = (ListView) findViewById(R.id.lv_action_history);
                mAdapter = new ArrayAdapter<ActionHistoryItemVO>(ActionHistoryActivity.this,
                        android.R.layout.simple_list_item_1, mActionHistoryList);
                mlv.setAdapter(mAdapter);
                mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String date = mlv.getAdapter().getItem(position).toString().substring(0,10);
                        moveActionHistoryTraceActivity(date);
                    }
                });
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
    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public void moveActionHistoryTraceActivity(String date) {
        Intent intent = new Intent(mActivity, ActionHistoryTraceActivity.class);
        intent.putExtra("date", date);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}

