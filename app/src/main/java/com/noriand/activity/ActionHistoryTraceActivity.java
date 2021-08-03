package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.constant.ServerConstant;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestGetNowLocationVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseGetNowLocationVO;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionHistoryTraceActivity extends BaseActivity {
    private final int REQUEST_CODE_DEVICE_UPDATE = 214;

    private RelativeLayout mrlPrev = null;
    private RelativeLayout mrlMap = null;
    private MapView mmv = null;
    private DeviceItemVO mItem = null;

    private TextView mtvTitle = null;
    private TextView mtvSubName = null;
    private TextView mtvAddress = null;

    private MapPOIItem mMarker = null;
    private CalloutBalloonAdapter mCalloutBalloonListener = null;
    private MapView.POIItemEventListener mPoiItemEventListener = null;
    private MapView.MapViewEventListener mMapViewEventListener = null;

    private int refresh_interval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_history_trace);
        setStatusBar(Color.WHITE);
        setBase();
        setLayout();
        setListener();
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public void setListener(){
        mMapViewEventListener = new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {
            }

            @Override
            public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewZoomLevelChanged(MapView mapView, int i) {

            }

            @Override
            public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

            }
        };
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mCalloutBalloonListener = new CalloutBalloonAdapter() {
            @Override
            public View getCalloutBalloon(MapPOIItem mapPOIItem) {
                return null;
            }

            @Override
            public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
                RelativeLayout rl = (RelativeLayout)getLayoutInflater().inflate(R.layout.row_callout, null);
                TextView tv = (TextView)rl.findViewById(R.id.tv_row_callout_name);
                tv.setText(mapPOIItem.getItemName());
                return rl;
            }
        };
        mPoiItemEventListener = new MapView.POIItemEventListener() {
            @Override
            public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
                MapPoint mapPoint = mapPOIItem.getMapPoint();
                mmv.setMapCenterPoint(mapPoint, true);

                MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(ServerConstant.KAKAO_API_KEY, mapPoint, new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
                    @Override
                    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                        mtvAddress.setText(s);
                    }
                    @Override
                    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                    }
                }, mActivity);
                mapReverseGeoCoder.startFindingAddress();
            }
            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            }
            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            }
            @Override
            public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
            }
        };
    }

    public void setBase(){
        mItem = new DeviceItemVO();
    }


    public void setLayout(){
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_action_history_prev);
        mrlMap = (RelativeLayout) findViewById(R.id.rl_map);
        mtvTitle = (TextView)findViewById(R.id.tv_main_title);

        try {
            mmv = new MapView(mActivity);
            mrlMap.addView(mmv);
        } catch(UnsatisfiedLinkError e) {
            mrlMap.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetMapView();
                    setData();
                    refresh();
                }
            }, 3000);
        }
        mtvSubName = (TextView)findViewById(R.id.tv_main_submenu_name);
        mtvAddress = (TextView)findViewById(R.id.tv_main_address);
    }

    private void resetMapView() {
        if(mmv == null) {
            return;
        }
        mrlMap.removeAllViews();
        mmv = new MapView(mActivity);
        mmv.removeAllCircles();
        mmv.removeAllPOIItems();
        mrlMap.addView(mmv);
    }

    private void setData() {
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                mItem.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }
        }

        int rowNo = mItem.no;
        if(rowNo == 0) {
            int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
            int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
            RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
            requestItem.onChangeDeviceArrayForDevice(deviceNo);
            requestItem.userNo = userNo;
            requestItem.isSilent = false;
            networkGetDeviceArray(requestItem);
        } else {
            drawMarker();
        }
        refresh_interval = mItem.refreshInterval;
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

                DeviceItemVO[] deviceArray = item.deviceArray;
                int size = deviceArray.length;
                if(size == 0) {
                    return;
                }
                mItem = deviceArray[0];

                if(mItem != null) {
                    try {
                        JSONObject jsonObject = mItem.getJSONObject();

                        Intent intent = getIntent();
                        intent.putExtra("strItem",jsonObject.toString());
                    } catch (JSONException e) {
                    }
                }
                drawMarker();

                refresh();
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

    private void refresh() {
        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        if(userNo == 0 || mItem == null || mItem.no == 0) {
            return;
        }
        refresh_interval = mItem.refreshInterval;
        RequestGetNowLocationVO requestItem = new RequestGetNowLocationVO();
        requestItem.isSilent = false;
        requestItem.userNo = userNo;
        requestItem.deviceNo = mItem.no;
        requestItem.ltid = mItem.ltid;
        networkGetNowLocation(requestItem);
    }

    private void drawMarker() {
        mtvTitle.setText(mItem.name);
        mtvSubName.setText(mItem.name);

        String strLastX = mItem.lastX;
        String strLastY = mItem.lastY;
        if(!StringUtil.isEmpty(strLastX) && !StringUtil.isEmpty(strLastY)) {
            double lastX = Double.parseDouble(strLastX);
            double lastY = Double.parseDouble(strLastY);

            if(mmv != null &&lastX > 0 && lastY > 0) {
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lastY, lastX);
                mmv.setMapCenterPointAndZoomLevel(mapPoint, 2, true);

                mMarker = new MapPOIItem();
                mMarker.setItemName(mItem.name);
                mMarker.setTag(999);
                mMarker.setMapPoint(mapPoint);
                mMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                mMarker.setCustomImageResourceId(R.drawable.ico_pin_red_01); // 마커 이미지.
                mMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                mMarker.setCustomImageAnchor(0.5f, 0.5f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                mmv.addPOIItem(mMarker);

                MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(ServerConstant.KAKAO_API_KEY, mapPoint, new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
                    @Override
                    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                        mtvAddress.setText(s);
                    }
                    @Override
                    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                    }
                }, mActivity);
                mapReverseGeoCoder.startFindingAddress();
            } else {
                if(mmv != null) {
                    mmv.setZoomLevel(2, true);
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
                            moveDeviceUpdateActivity(mItem);
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

                if(mmv != null && item.isConfirm) {
                    String xTemp = item.x;
                    String yTemp = item.y;
                    if (mmv != null && !StringUtil.isEmpty(xTemp) && !StringUtil.isEmpty(yTemp)) {
                        mmv.removeAllPOIItems();
                        mmv.removeAllPolylines();
                        double x = Double.parseDouble(xTemp);
                        double y = Double.parseDouble(yTemp);
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(y, x);
                        mMarker = new MapPOIItem();
                        mMarker.setItemName(mItem.name);
                        mMarker.setTag(999);
                        mMarker.setMapPoint(mapPoint);
                        mMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                        mMarker.setCustomImageResourceId(R.drawable.ico_pin_red_01); // 마커 이미지.
                        mMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        mMarker.setCustomImageAnchor(0.5f, 0.5f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                        mmv.addPOIItem(mMarker);
                        mmv.setMapCenterPoint(mapPoint, true);
                    }

                    mmv.setCalloutBalloonAdapter(mCalloutBalloonListener);
                    mmv.setPOIItemEventListener(mPoiItemEventListener);
                    mmv.setMapViewEventListener(mMapViewEventListener);
                }
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
        startActivityForResult(intent, REQUEST_CODE_DEVICE_UPDATE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}
