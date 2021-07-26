package com.noriand.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.TraceItemVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;

import java.util.ArrayList;
import java.util.Calendar;

public class DateSelectActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private TextView dateTextView;
    private boolean mAutoHighlight;
    private ArrayList<TraceItemVO> mTraceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        setStatusBar(Color.WHITE);

        setLayout();
        setListener();
    }

    public void setLayout(){
        dateTextView = (TextView)findViewById(R.id.date_textview);
    }

    public void setListener(){
        Button dateButton = (Button)findViewById(R.id.date_button);


        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        DateSelectActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAutoHighlight(mAutoHighlight);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) {
            dpd.setOnDateSetListener(this);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String from_date = "" + year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        String to_date = "" + yearEnd + "-" + (++monthOfYearEnd) + "-" + dayOfMonthEnd;
        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
        dateTextView.setText(to_date);
        RequestGetTraceArrayVO requestItem = new RequestGetTraceArrayVO();
        requestItem.deviceNo = deviceNo;
        requestItem.userNo = userNo;
        networkGetActionHistory(requestItem);
    }

    private void networkGetActionHistory(final RequestGetTraceArrayVO requestItem) {
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

                for(int i=0; i<length; i++) {
                    mTraceList.add(traceArray[i]);
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
}

