package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.VerticalSwipeRefreshLayout;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.SafeZoneItemVO;
import com.noriand.vo.request.RequestDeleteSafeZoneVO;
import com.noriand.vo.request.RequestGetSafeZoneArrayVO;
import com.noriand.vo.response.ResponseGetSafeZoneArrayVO;
import com.noriand.vo.response.ResponseVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SafeZoneListActivity extends BaseActivity {
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
    private View mvEmptyAdd = null;
    private View mvFooterAdd = null;

    private Button mbtAutoScrollTop = null;

    //--------------------------------------------------
    // Data
    private ArrayList<SafeZoneItemVO> mList = null;
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_zone_list);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
        setData();
    }

    private void refresh(boolean isSilent) {
        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
        RequestGetSafeZoneArrayVO requestItem = new RequestGetSafeZoneArrayVO();
        requestItem.userNo = userNo;
        requestItem.deviceNo = deviceNo;
        requestItem.isSilent = isSilent;
        networkGetSafeZoneArray(requestItem);
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

    @Override
    public void onResume() {
        super.onResume();
        refresh(false);
    }

    private void setBase() {
        mLayoutInflater = mActivity.getLayoutInflater();
        mList = new ArrayList<SafeZoneItemVO>();
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
        }
    }

    private void setLayout() {
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_safe_zone_list_prev);

        msrl = (VerticalSwipeRefreshLayout)findViewById(R.id.srl_safe_zone_list);
        mlv = (ListView) findViewById(R.id.lv_safe_zone_list);
        mrlEmpty = (RelativeLayout)findViewById(R.id.rl_safe_zone_list_empty);

        mvEmptyAdd = (View)findViewById(R.id.v_safe_zone_list_empty_add);

        RelativeLayout rlFooter = (RelativeLayout)mLayoutInflater.inflate(R.layout.row_add, null);
        mvFooterAdd = (View)rlFooter.findViewById(R.id.v_row_add);
        mlv.addFooterView(rlFooter);

        mbtAutoScrollTop = (Button)findViewById(R.id.btn_safe_zone_list_auto_scroll);
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

        View.OnClickListener addLister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mList.size();
                if(size > 4) {
                    return;
                }

                moveSafeZoneWriteActivity(null);
            }
        };
        mvEmptyAdd.setOnClickListener(addLister);
        mvFooterAdd.setOnClickListener(addLister);
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

    private void networkGetSafeZoneArray(final RequestGetSafeZoneArrayVO requestItem) {
        mApiController.getSafeZoneArray(mActivity, requestItem, new ApiController.ApiGetSafeZoneArrayListener() {
            @Override
            public void onSuccess(ResponseGetSafeZoneArrayVO item) {
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
                SafeZoneItemVO[] safeZoneArray = item.safeZoneArray;
                if(safeZoneArray != null) {
                    int safeZoneArraySize = safeZoneArray.length;
                    for (int i = 0; i < safeZoneArraySize; i++) {
                        SafeZoneItemVO safeZoneItem = safeZoneArray[i];
                        mList.add(safeZoneItem);
                    }
                }

                updateList();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetSafeZoneArray(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private void networkDeleteSafeZone(final RequestDeleteSafeZoneVO requestItem) {
        mApiController.deleteSafeZone(mActivity, requestItem, new ApiController.ApiCommonListener() {
            @Override
            public void onSuccess(ResponseVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    return;
                }
                refresh(false);
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkDeleteSafeZone(requestItem);
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
        public SafeZoneItemVO getItem(int position) {
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
                convertView = mLayoutInflater.inflate(R.layout.row_safe_zone, parent, false);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_row_safe_zone_title);
                holder.rlPencil = (RelativeLayout)convertView.findViewById(R.id.rl_row_safe_zone_pencil);
                holder.rlTrash = (RelativeLayout)convertView.findViewById(R.id.rl_row_safe_zone_trash);
                convertView.setTag(holder);
            }

            final int nowPosition = position;
            SafeZoneItemVO item = mList.get(position);
            holder.tvName.setText(item.name);

            holder.rlPencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SafeZoneItemVO item = mList.get(nowPosition);
                    moveSafeZoneWriteActivity(item);
                }
            });


            holder.rlTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SafeZoneItemVO item = mList.get(nowPosition);
                    showDialogTwoButton(item.name + " 안심존을 삭제하시겠습니까?", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                            RequestDeleteSafeZoneVO requestItem = new RequestDeleteSafeZoneVO();
                            requestItem.no = item.no;
                            requestItem.userNo = userNo;
                            requestItem.deviceNo = item.deviceNo;
                            networkDeleteSafeZone(requestItem);
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tvName = null;
        private RelativeLayout rlPencil = null;
        private RelativeLayout rlTrash = null;
    }


    public void moveSafeZoneWriteActivity(SafeZoneItemVO item) {
        String strItem = "";
        if(item != null) {
            try {
                JSONObject jsonObject = item.getJSONObject();
                if (jsonObject != null) {
                    strItem = jsonObject.toString();
                }
            } catch (JSONException e) {
            }
        }
        Intent intent = new Intent(mActivity, SafeZoneWriteActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}