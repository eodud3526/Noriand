package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.common.CommonTag;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.VerticalSwipeRefreshLayout;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.AlarmItemVO;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.request.RequestGetAlarmArrayVO;
import com.noriand.vo.response.ResponseGetAlarmArrayVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AlarmActivity extends BaseActivity {
    //--------------------------------------------------
    // Common
    private LayoutInflater mLayoutInflater = null;

    private AlarmAdapter mAdapter = null;

    //--------------------------------------------------
    // View
    private RelativeLayout mrlPrev = null;

    private VerticalSwipeRefreshLayout msrl = null;
    private ListView mlv = null;
    private RelativeLayout mrlEmpty = null;

    private Button mbtAutoScrollTop = null;

    //--------------------------------------------------
    // Data
    private ArrayList<AlarmItemVO> mList = null;

    private AlarmItemVO mSelectedItem = null;
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
        setData();
    }

    private void refresh(boolean isSilent) {
        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
        RequestGetAlarmArrayVO requestItem = new RequestGetAlarmArrayVO();
        requestItem.userNo = userNo;
        requestItem.deviceNo = deviceNo;
        requestItem.isSilent = isSilent;
        networkGetAlarmArray(requestItem);
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh(false);
    }

    private void setBase() {
        mLayoutInflater = mActivity.getLayoutInflater();
        mList = new ArrayList<AlarmItemVO>();
        mSelectedItem = new AlarmItemVO();
    }
    private void setData() {
        CommonPreferences.putString(mActivity, CommonPreferences.TAG_IS_NEW, "");
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            DeviceItemVO item = new DeviceItemVO();
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                item.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }
        }
    }

    private void setLayout() {
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_alarm_prev);
        msrl = (VerticalSwipeRefreshLayout)findViewById(R.id.srl_alarm);
        mlv = (ListView) findViewById(R.id.lv_alarm);
        mrlEmpty = (RelativeLayout)findViewById(R.id.rl_alarm_empty);

        mbtAutoScrollTop = (Button)findViewById(R.id.btn_alarm_auto_scroll);
    }

    private void setListener() {
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

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
    }

    private void setAdapter() {
        mAdapter = new AlarmAdapter();
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

    private void networkGetAlarmArray(final RequestGetAlarmArrayVO requestItem) {
        mApiController.getAlarmArray(mActivity, requestItem, new ApiController.ApiGetAlarmArrayListener() {
            @Override
            public void onSuccess(ResponseGetAlarmArrayVO item) {
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
                AlarmItemVO[] alarmArray = item.alarmArray;
                if(alarmArray != null) {
                    int alarmArraySize = alarmArray.length;
                    for (int i = 0; i < alarmArraySize; i++) {
                        AlarmItemVO alarmItem = alarmArray[i];
                        mList.add(alarmItem);
                    }
                }

                updateList();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetAlarmArray(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private class AlarmAdapter extends BaseAdapter {
        public AlarmAdapter() {
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public AlarmItemVO getItem(int position) {
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
                convertView = mLayoutInflater.inflate(R.layout.row_alarm, parent, false);
                holder.rl = (RelativeLayout)convertView.findViewById(R.id.rl_row_alarm);
                holder.vSiren = (View) convertView.findViewById(R.id.v_row_alarm_siren);
                holder.vSafeZone = (View) convertView.findViewById(R.id.v_row_alarm_safe_zone);
                holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_row_alarm_title);
                holder.tvContent = (TextView)convertView.findViewById(R.id.tv_row_alarm_content);
                convertView.setTag(holder);
            }

            final int nowPosition = position;

            AlarmItemVO item = mList.get(position);

            int alarmType = item.alarmType;
            if(alarmType == CommonTag.ALARM_TYPE_SOS) {
                holder.vSiren.setVisibility(View.VISIBLE);
                holder.vSafeZone.setVisibility(View.GONE);
            } else if(alarmType == CommonTag.ALARM_TYPE_SAFE_ZONE_IN) {
                holder.vSiren.setVisibility(View.GONE);
                holder.vSafeZone.setVisibility(View.VISIBLE);
            } else if(alarmType == CommonTag.ALARM_TYPE_SAFE_ZONE_OUT) {
                holder.vSiren.setVisibility(View.GONE);
                holder.vSafeZone.setVisibility(View.VISIBLE);
            } else {
                holder.vSiren.setVisibility(View.GONE);
                holder.vSafeZone.setVisibility(View.GONE);
            }

            holder.tvTitle.setText(item.title);
            holder.tvContent.setText(item.content);

            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmItemVO item = mList.get(nowPosition);
                    mSelectedItem = item;
                    moveAlarmMarkerActivity(mSelectedItem);
                    System.out.println(mSelectedItem.toString());
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        private RelativeLayout rl = null;
        private View vSiren = null;
        private View vSafeZone = null;
        private TextView tvTitle = null;
        private TextView tvContent = null;
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

    public void moveAlarmMarkerActivity(AlarmItemVO item){
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }
        Intent intent = new Intent(mActivity, AlarmMarkerActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }
}