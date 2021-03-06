package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.request.RequestDeleteDeviceVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestWriteDeviceVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseVO;
import com.noriand.vo.response.ResponseWriteDeviceVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DeviceWriteActivity extends BaseActivity {
// --------------------------------------------------
    // Common
    private String BASE_PICTURE_URL = "bg_base.jpg";

    // --------------------------------------------------
    // View

    private RelativeLayout mrlPrev = null;

    private TextView mtvTitle = null;
    private ImageView miv = null;
    private EditText metName = null;
    private EditText metLtid = null;

    private Button mbtSave = null;

    private RelativeLayout mrlDelete = null;

    // --------------------------------------------------
    // Data
    private boolean isEditMode = false;
    private int mDeviceNo = 0;
    private DeviceItemVO mItem = null;
    private ArrayList<DeviceItemVO> mList = null;
    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_write);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setBase() {
        mList = new ArrayList<>();
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

            isEditMode = true;
            mtvTitle.setText("?????? ??????");
            mrlDelete.setVisibility(View.VISIBLE);
            StringUtil.selectionLast(metName);

            if(item != null) {
                mDeviceNo = item.no;
                metName.setText(item.name);
                metLtid.setText(item.ltid);
            }
            setImage(mActivity, miv, BASE_PICTURE_URL, null);
        } else {
            setImage(mActivity, miv, BASE_PICTURE_URL, null);
        }
        RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
        requestItem.userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        networkGetDeviceArray(requestItem);
        System.out.println();
    }

    private void setLayout() {
        mtvTitle = (TextView)findViewById(R.id.tv_device_write_title);
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_device_write_prev);

        miv = (ImageView)findViewById(R.id.iv_device_write);

        metName = (EditText)findViewById(R.id.et_device_write_name);
        metLtid = (EditText)findViewById(R.id.et_device_write_ltid);

        mrlDelete = (RelativeLayout)findViewById(R.id.rl_device_write_delete);

        mbtSave = (Button)findViewById(R.id.btn_device_write_save);
    }

    private void setListener() {
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mbtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = metName.getText().toString();
                if (StringUtil.isEmpty(name)) {
                    showDialogOneButton("????????? ????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metName);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }

                String ltid = metLtid.getText().toString();
                if (StringUtil.isEmpty(ltid)) {
                    showDialogOneButton("?????? ??????????????? ????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }
                if (CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID).equals(ltid)){
                    showDialogOneButton("?????? ?????? ??????????????? ????????? ????????? ???????????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }
                for(int i=0; i<mList.size(); i++){
                    if(ltid.equals(mList.get(i).ltid)){
                        showDialogOneButton("?????? ?????? ??????????????? ????????? ????????? ???????????????.", new CommonDialog.DialogConfirmListener() {
                            @Override
                            public void onConfirm() {
                                StringUtil.selectionLast(metLtid);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        return;
                    }
                }
                if (!StringUtil.isValidString(ltid)) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }
                if (ltid.length() != 24) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }
                if (!ltid.startsWith("00000908")) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                RequestWriteDeviceVO requestItem = new RequestWriteDeviceVO();
                if (isEditMode) {
                    requestItem.changeUpdateMode();
                }
                requestItem.userNo = userNo;
                requestItem.deviceNo = mDeviceNo;
                requestItem.name = name;
                requestItem.ltid = ltid;
                networkWriteDevice(requestItem);
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
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void networkWriteDevice(final RequestWriteDeviceVO requestItem) {
        mApiController.writeDevice(mActivity, requestItem, new ApiController.ApiWriteDeviceListener() {
            @Override
            public void onSuccess(ResponseWriteDeviceVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }
                if(requestItem.ltid.equals(CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID))){
                    CommonPreferences.resetPreferences(getApplicationContext());
                    showDialogOneButton("?????? ?????? ??????????????? ????????? ????????? ?????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }
                for(int i=0; i<mList.size(); i++){
                    if(requestItem.ltid.equals(mList.get(i).ltid)){
                        showDialogOneButton("?????? ?????? ??????????????? ????????? ????????? ?????? ?????????.", new CommonDialog.DialogConfirmListener() {
                            @Override
                            public void onConfirm() {
                                StringUtil.selectionLast(metLtid);
                            }
                            @Override
                            public void onCancel() {

                            }
                        });
                        return;
                    }
                }
                if(requestItem.ltid.length() != 24) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }
                if(!requestItem.ltid.startsWith("00000908")) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }
                if(!StringUtil.isValidString(requestItem.ltid)) {
                    showDialogOneButton("????????? ???????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }
                if(!item.isConfirm) {
                    showDialogOneButton("????????? ??????????????????. ?????? ????????? ?????? ???, ?????? ????????? ?????????.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metLtid);
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }


                String message = "????????? ?????????????????????.";
                if(isEditMode) {
                    message = "????????? ?????????????????????.";
                }

                showDialogOneButton(message, new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        Intent intent = getIntent();
                        intent.putExtra("isDelete", "N");
                        intent.putExtra("no", item.no);
                        intent.putExtra("name", requestItem.name);
                        intent.putExtra("ltid", requestItem.ltid);
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
                        networkWriteDevice(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
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
                            StringUtil.selectionLast(metLtid);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
                    return;
                }
                mList.clear();
                DeviceItemVO[] deviceArray = item.deviceArray;
                if(deviceArray != null) {
                    int deviceArraySize = deviceArray.length;
                    for (int i = 0; i < deviceArraySize; i++) {
                        DeviceItemVO deviceItem = deviceArray[i];
                        mList.add(deviceItem);
                    }
                }
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
}