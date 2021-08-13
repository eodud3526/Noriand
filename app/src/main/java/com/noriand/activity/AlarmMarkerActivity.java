package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.constant.ServerConstant;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.AlarmItemVO;
import com.noriand.vo.SafeZoneItemVO;
import com.noriand.vo.request.RequestGetSafeZoneArrayVO;
import com.noriand.vo.response.ResponseGetSafeZoneArrayVO;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static net.daum.mf.map.n.api.internal.NativeMapLocationManager.setShowCurrentLocationMarker;

public class AlarmMarkerActivity extends BaseActivity{
    private MapView mmv = null;
    private RelativeLayout mrlMap = null;
    private RelativeLayout mrlPrev = null;

    private ArrayList<AlarmItemVO> mAlarmList = null;
    private ArrayList<SafeZoneItemVO> mSafeZoneList = null;
    private AlarmItemVO mItem;

    private CalloutBalloonAdapter mCalloutBalloonListener = null;
    private MapView.POIItemEventListener mPoiItemEventListener = null;
    private MapView.MapViewEventListener mMapViewEventListener = null;

    private TextView mtvToday = null;
    private TextView mtvAddress = null;

    private final int REQUEST_CODE_ALARM = 212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_marker);
        setStatusBar(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setBase();
        setLayout();
        setData();
        setListener();
    }

    private void setBase() {
        mAlarmList = new ArrayList<AlarmItemVO>();
        mItem = new AlarmItemVO();
        mSafeZoneList = new ArrayList<SafeZoneItemVO>();
    }

    private void setLayout(){
        mtvToday = (TextView)findViewById(R.id.tv_main_today);
        mtvAddress = (TextView)findViewById(R.id.tv_main_address);
        mrlMap = (RelativeLayout) findViewById(R.id.rl_map);
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_alarm_marker_prev);
        try {
            mmv = new MapView(mActivity);
            mrlMap.addView(mmv);
        } catch(UnsatisfiedLinkError e) {
            mrlMap.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetMapView();
                    setData();
                }
            }, 3000);
        }
    }

    private void setListener(){
        mrlPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mMapViewEventListener = new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {
                setShowCurrentLocationMarker(false);
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
                int tag = mapPOIItem.getTag();
                int size = mAlarmList.size();
                if(size > 0 && tag < size) {
                    AlarmItemVO alarmItem = mAlarmList.get(tag);
                    mtvToday.setText(alarmItem.insertTime);
                }
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
    /*
    private void drawAlarmMarker() {
        if(mmv == null || mAlarmList == null) {
            return;
        }

        int size = mAlarmList.size();
        if(size == 0) {
            return;
        }
        mmv.removeAllPOIItems();
        for(int i=0; i<size; i++) {
            AlarmItemVO item = mAlarmList.get(i);
            String name = item.insertTime;
            String x = item.x;
            String y = item.y;
            if(!StringUtil.isEmpty(x) && !StringUtil.isEmpty(y)) {

                double dX = Double.parseDouble(x);
                double dY = Double.parseDouble(y);

                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                MapPOIItem marker = new MapPOIItem();

                marker.setItemName(name);
                marker.setTag(i);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                marker.setCustomImageResourceId(R.drawable.ico_pin_brown); // 마커 이미지.
                marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                marker.setCustomImageAnchor(0.5f, 0.5f);
                mmv.addPOIItem(marker);
                mmv.setMapCenterPoint(mapPoint, true);

                for(int j=0; j<mSafeZoneList.size(); j++){
                    if(item.safeZoneNo == mSafeZoneList.get(j).no){
                        double circleY = Double.parseDouble(mSafeZoneList.get(j).y);
                        double circleX = Double.parseDouble(mSafeZoneList.get(j).x);
                        int mBoundary = mSafeZoneList.get(j).boundary;
                        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(circleY, circleX);
                        MapCircle mapCircle = new MapCircle(mapPoint2, mBoundary,
                                Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                        mapCircle.setCenter(MapPoint.mapPointWithGeoCoord(circleY, circleX));
                        mmv.addCircle(mapCircle);
                    }
                }
            }
        }
    }
     */

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
    public void setData() {
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if (!StringUtil.isEmpty(strItem)) {
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                mItem.parseJSONObject(jsonObject);
            } catch (JSONException e) {
            }
        }
        mAlarmList.add(mItem);

        int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
        int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
        RequestGetSafeZoneArrayVO requestItem = new RequestGetSafeZoneArrayVO();
        requestItem.userNo = userNo;
        requestItem.deviceNo = deviceNo;
        requestItem.isSilent = false;
        networkGetAlarmMarker(requestItem);
    }
    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        moveAlarmActivity();
    }
    public void moveAlarmActivity() {
        Intent intent = new Intent(mActivity, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_ALARM);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private void networkGetAlarmMarker(final RequestGetSafeZoneArrayVO requestItem) {
        mApiController.getSafeZoneArray(mActivity, requestItem, new ApiController.ApiGetSafeZoneArrayListener() {
            @Override
            public void onSuccess(ResponseGetSafeZoneArrayVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }
                if(!item.isConfirm) {
                    return;
                }
                mSafeZoneList.clear();
                SafeZoneItemVO[] safeZoneArray = item.safeZoneArray;
                if(safeZoneArray != null) {
                    int safeZoneArraySize = safeZoneArray.length;
                    for (int i = 0; i < safeZoneArraySize; i++) {
                        SafeZoneItemVO safeZoneItem = safeZoneArray[i];
                        mSafeZoneList.add(safeZoneItem);
                    }
                }
                if(mmv == null || mAlarmList == null) {
                    return;
                }

                int size = mAlarmList.size();
                if(size == 0) {
                    return;
                }

                mmv.removeAllPOIItems();
                for(int i=0; i<size; i++) {
                    AlarmItemVO alarmItem = mAlarmList.get(i);

                    String name = alarmItem.insertTime;
                    String x = alarmItem.x;
                    String y = alarmItem.y;

                    if(!StringUtil.isEmpty(x) && !StringUtil.isEmpty(y)) {

                        double dX = Double.parseDouble(x);
                        double dY = Double.parseDouble(y);

                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                        MapPOIItem marker = new MapPOIItem();

                        marker.setItemName(name);
                        marker.setTag(i);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.setCustomImageResourceId(R.drawable.ico_pin_brown); // 마커 이미지.
                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        marker.setCustomImageAnchor(0.5f, 0.5f);
                        mmv.addPOIItem(marker);
                        mmv.setMapCenterPoint(mapPoint, true);

                        double circleY = 0, circleX = 0;
                        int mBoundary = 0;
                        for(int j=0; j<mSafeZoneList.size(); j++){
                            if(alarmItem.safeZoneNo == mSafeZoneList.get(j).no){
                                circleY = Double.parseDouble(mSafeZoneList.get(j).y);
                                circleX = Double.parseDouble(mSafeZoneList.get(j).x);
                                mBoundary = mSafeZoneList.get(j).boundary;
                            }
                        }
                        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(circleY, circleX);
                        MapCircle mapCircle = new MapCircle(mapPoint2, mBoundary, Color.argb(128, 255, 0, 0),
                                Color.argb(128, 255, 255, 0));
                        mmv.addCircle(mapCircle);
                    }
                }
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetAlarmMarker(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

}
