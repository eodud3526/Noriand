package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.listener.SoftHandler;
import com.noriand.listener.SoftListener;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.VerticalSwipeRefreshLayout;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestGetNowLocationVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseGetNowLocationVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DeviceSelectActivity extends BaseActivity {
    //--------------------------------------------------
    // Common
    private final int REQUEST_CODE_DEVICE_WRITE = 1101;
    private LayoutInflater mLayoutInflater = null;

    private DeviceAdapter mAdapter = null;

    private SoftHandler mExitCheckHandler = null;

    //--------------------------------------------------
    // View
    private VerticalSwipeRefreshLayout msrl = null;
    private ListView mlv = null;
    private RelativeLayout mrlEmpty = null;
    private View mvEmptyAdd = null;
    private View mvFooterAdd = null;

    private Button mbtAutoScrollTop = null;
    //--------------------------------------------------
    // Data
    private ArrayList<DeviceItemVO> mList = null;
    private boolean isPressBack = false;
    //--------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
    }

    private void refresh(boolean isSilent) {
        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
        requestItem.userNo = userNo;
        requestItem.isSilent = isSilent;
        networkGetDeviceArray(requestItem);
    }

    private void onBack() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!isPressBack) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.really_exit),
                        Toast.LENGTH_SHORT).show();
                isPressBack = true;
                mExitCheckHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(false);
    }

    private void setBase() {
        mLayoutInflater = mActivity.getLayoutInflater();
        mList = new ArrayList<DeviceItemVO>();

        mExitCheckHandler = new SoftHandler(new SoftListener() {
            @Override
            public void handleMessage(Message msg, int what) {
                isPressBack = false;
            }
        }, 0);
    }

    private void setLayout() {
        msrl = (VerticalSwipeRefreshLayout)findViewById(R.id.srl_device_select);
        mlv = (ListView) findViewById(R.id.lv_device_select);
        mrlEmpty = (RelativeLayout)findViewById(R.id.rl_device_select_empty);

        mvEmptyAdd = (View)findViewById(R.id.v_device_select_empty_add);

        RelativeLayout rlFooter = (RelativeLayout)mLayoutInflater.inflate(R.layout.row_add, null);
        mvFooterAdd = (View)rlFooter.findViewById(R.id.v_row_add);
        mlv.addFooterView(rlFooter);

        mbtAutoScrollTop = (Button)findViewById(R.id.btn_device_select_auto_scroll);

    }

    private void setListener() {
        msrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msrl.setRefreshing(false);
                refresh(true);
            }
        });
        mbtAutoScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlv.smoothScrollToPosition(0);
                mbtAutoScrollTop.setVisibility(View.GONE);
            }
        });
        mlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastPosition = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && mActivity != null) {
                    if (mbtAutoScrollTop.getVisibility() == View.VISIBLE) {
                        mbtAutoScrollTop.setVisibility(View.GONE);
                    }
                } else {
                    if (mbtAutoScrollTop.getVisibility() == View.GONE) {
                        mbtAutoScrollTop.setVisibility(View.VISIBLE);
                    }
                }

                mLastPosition = firstVisibleItem;
            }
        });

        View.OnClickListener addDeviceLister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceWriteActivity();
            }
        };
        mvEmptyAdd.setOnClickListener(addDeviceLister);
        mvFooterAdd.setOnClickListener(addDeviceLister);

    }

    private void setAdapter() {
        mAdapter = new DeviceAdapter();
        mlv.setAdapter(mAdapter);
    }

    private void updateList() {
        if(mList == null) {
            msrl.setVisibility(View.GONE);
            mrlEmpty.setVisibility(View.VISIBLE);
            return;
        }

        int size = mList.size();
        if(size == 0) {
            if(mrlEmpty.getVisibility() == View.GONE) {
                msrl.setVisibility(View.GONE);
                mrlEmpty.setVisibility(View.VISIBLE);
            }
            return;
        }

        msrl.setVisibility(View.VISIBLE);
        mrlEmpty.setVisibility(View.GONE);

        if(mAdapter == null) {
            setAdapter();
            return;
        }
        mAdapter.notifyDataSetChanged();
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
                    mrlEmpty.setVisibility(View.VISIBLE);
                    msrl.setVisibility(View.GONE);
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

                updateList();
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

    private class DeviceAdapter extends BaseAdapter {
        public DeviceAdapter() {
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public DeviceItemVO getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView != null) {
                holder = (ViewHolder)convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.row_device, parent, false);
                holder.rl = (RelativeLayout)convertView.findViewById(R.id.rl_row_device);
                holder.iv = (ImageView)convertView.findViewById(R.id.iv_row_device_picture);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_row_device_name);
                holder.vBattery = (View) convertView.findViewById(R.id.v_row_device_battery);
                holder.vSafeZone = (View) convertView.findViewById(R.id.v_row_device_safe_zone);
                holder.tvPeriod = (TextView)convertView.findViewById(R.id.tv_row_device_period);
                holder.rlMap = (RelativeLayout)convertView.findViewById(R.id.rl_row_device_map);
                holder.rlStar = (RelativeLayout)convertView.findViewById(R.id.rl_row_device_star);
                holder.rlPeople = (RelativeLayout)convertView.findViewById(R.id.rl_row_device_people);
                holder.rlSetting = (RelativeLayout) convertView.findViewById(R.id.rl_row_device_setting);

                convertView.setTag(holder);
            }

            final int nowPosition = position;

            DeviceItemVO item = mList.get(position);

            String pictureUrl = item.pictureUrl;
            setImage(mActivity, holder.iv, pictureUrl, null);

            holder.tvName.setText(item.name);

            int batteryCount = item.batteryCount;
            if(batteryCount > 200) {
                holder.vBattery.setBackgroundResource(R.drawable.ico_battery_04);
            } else if(batteryCount > 150) {
                holder.vBattery.setBackgroundResource(R.drawable.ico_battery_03);
            } else if(batteryCount > 100) {
                holder.vBattery.setBackgroundResource(R.drawable.ico_battery_02);
            } else if(batteryCount > 50) {
                holder.vBattery.setBackgroundResource(R.drawable.ico_battery_01);
            }

            int refreshInterval = item.refreshInterval;
            holder.tvPeriod.setText( refreshInterval+ "분마다");

            String strSafeZoneNoArray = item.strSafeZoneNoArray;
            if(!StringUtil.isEmpty(strSafeZoneNoArray)) {
                holder.vSafeZone.setBackgroundResource(R.drawable.ico_safe_zone);
            } else {
                holder.vSafeZone.setBackgroundResource(R.drawable.ico_safe_zone_pressed);
            }

            holder.rlMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeviceItemVO item = mList.get(nowPosition);
                    CommonPreferences.putInt(mActivity, CommonPreferences.TAG_DEVICE_NO, item.no);
                    CommonPreferences.putString(mActivity, CommonPreferences.TAG_DEVICE_LTID, item.ltid);
                    moveMainActivity(item);
                }
            });
            holder.rlStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogOneButton("기능 준비중입니다.");
                }
            });
            holder.rlPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogOneButton("기능 준비중입니다.");
                }
            });
            holder.rlSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonPreferences.putInt(mActivity, CommonPreferences.TAG_DEVICE_NO, item.no);
                    CommonPreferences.putString(mActivity, CommonPreferences.TAG_DEVICE_LTID, item.ltid);
                    DeviceItemVO item = mList.get(nowPosition);
                    //moveMainSettingActivity(item);
                    moveDeviceSettingActivity(item);
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        private RelativeLayout rl = null;
        private ImageView iv = null;
        private TextView tvName = null;
        private View vBattery = null;
        private View vSafeZone = null;
        private TextView tvPeriod = null;
        private RelativeLayout rlMap = null;
        private RelativeLayout rlStar = null;
        private RelativeLayout rlPeople = null;
        private RelativeLayout rlSetting = null;
    }

    public void moveMainActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    public void moveDeviceWriteActivity() {
        Intent intent = new Intent(mActivity, DeviceWriteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_DEVICE_WRITE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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

    public void moveMainSettingActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, MainSettingActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("mytest2", "onActi, requestCode: " + requestCode);
        Log.d("mytest2", "onActi, resultCode: " + resultCode);

        if(requestCode == REQUEST_CODE_DEVICE_WRITE && resultCode == RESULT_OK) {
            String isDelete = intent.getStringExtra("isDelete");
            if ("Y".equals(isDelete)) {
                refresh(false);
                return;
            } else {
                int deviceNo = intent.getIntExtra("no", 0);
                String name = intent.getStringExtra("name");
                String ltid = intent.getStringExtra("ltid");
                Log.d("mytest2", deviceNo + ", " + name + ", " + ltid);
                if(deviceNo > 0 && ltid != null && name != null) {
                    int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                    RequestGetNowLocationVO requestItem = new RequestGetNowLocationVO();
                    requestItem.isSilent = false;
                    requestItem.userNo = userNo;
                    requestItem.deviceNo = deviceNo;
                    requestItem.ltid = ltid;
                    requestItem.name = name;
                    networkGetNowLocation(requestItem);
                }
            }
        }
    }

    private void networkGetNowLocation(final RequestGetNowLocationVO requestItem) {
        mApiController.getNowLocation(mActivity, requestItem, new ApiController.ApiGetNowLocationListener() {
            @Override
            public void onSuccess(ResponseGetNowLocationVO item) {
                if(!"Y".equals(item.isLora)) {
                    showDialogTwoButton("이 기기로 위치 조회가 실패했습니다. 올바른 기기 고유번호인지 확인해 주세요. 장치 정보로 이동하시겠습니까?", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            DeviceItemVO deviceItem = new DeviceItemVO();
                            deviceItem.no = requestItem.deviceNo;
                            deviceItem.name = requestItem.name;
                            deviceItem.ltid = requestItem.ltid;
                            moveDeviceUpdateActivity(deviceItem);
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                    return;
                }

                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                refresh(true);
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetNowLocation(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
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
        startActivityForResult(intent, REQUEST_CODE_DEVICE_WRITE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}