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
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.TraceItemVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActionHistoryTraceActivity extends BaseActivity {
    private MapView mmv = null;
    private RelativeLayout mrlMap = null;
    private RelativeLayout mrlPrev = null;

    private ArrayList<TraceItemVO> mTraceList = null;
    private DeviceItemVO mItem;
    private String date = "";

    private CalloutBalloonAdapter mCalloutBalloonListener = null;
    private MapView.POIItemEventListener mPoiItemEventListener = null;
    private MapView.MapViewEventListener mMapViewEventListener = null;

    private TextView mtvToday = null;
    private TextView mtvAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_history_trace);
        setStatusBar(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setBase();
        setLayout();
        setData();
        setListener();
    }

    private void setBase() {
        mTraceList = new ArrayList<TraceItemVO>();
        mItem = new DeviceItemVO();

    }

    private void setLayout(){
        mtvToday = (TextView)findViewById(R.id.tv_main_today);
        mtvAddress = (TextView)findViewById(R.id.tv_main_address);
        mrlMap = (RelativeLayout) findViewById(R.id.rl_map);
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_action_history_prev);
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
                int size = mTraceList.size();
                if(size > 0 && tag < size) {
                    TraceItemVO traceItem = mTraceList.get(tag);
                    mtvToday.setText(traceItem.insertTime);
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

    private void drawTraceArray() {
        if(mmv == null || mTraceList == null) {
            return;
        }

        int size = mTraceList.size();
        if(size == 0) {
            return;
        }

        mmv.removeAllPOIItems();
        MapPolyline mpl = new MapPolyline();

        for(int i=0; i<size; i++) {
            TraceItemVO item = mTraceList.get(i);

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

                mpl.setTag(i); /* polyline Tag 번호 지정 */
                mpl.setLineColor(Color.argb(128, 255, 51, 0)); /* polyline 색 지정 */
                mpl.addPoint(mapPoint);

                mmv.addPolyline(mpl);
            }
        }
        MapPointBounds mpb = new MapPointBounds(mpl.getMapPoints());
        int padding = 100;
        mmv.moveCamera(CameraUpdateFactory.newMapPointBounds(mpb, padding));
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

    private void networkGetTraceArray(final RequestGetTraceArrayVO requestItem) {
        mApiController.getTraceArray(mActivity, requestItem, new ApiController.ApiGetTraceArrayListener() {
            @Override
            public void onSuccess(ResponseGetTraceArrayVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    System.out.println("11111111111111111111111111");
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                TraceItemVO[] traceArray = item.traceArray;
                if(item.traceArray == null) {
                    System.out.println("2222222222222222222222222222");
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                int length = traceArray.length;
                if(length == 0) {
                    System.out.println("3333333333333333333333333333");
                    showDialogOneButton("최근 기록이 없습니다.");
                    return;
                }

                for(int i=0; i<length; i++) {
                    if (traceArray[i].insertTime.substring(0,10).equals(date)){
                        mTraceList.add(traceArray[i]);
                        System.out.println("xxxxxxxxxxxxxxxxxxx");
                        System.out.println(traceArray[i]);
                        System.out.println("yyyyyyyyyyyyyyyyyyy");
                    }
                }
                drawTraceArray();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetTraceArray(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
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
    public void setData(){
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        int userNo = 0;
        int deviceNo = 0;
        String ltid = "";
        int rowNo = mItem.no;
        if(rowNo == 0) {
            userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
            deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
            ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
            RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
            requestItem.onChangeDeviceArrayForDevice(deviceNo);
            requestItem.userNo = userNo;
            requestItem.isSilent = false;
            networkGetDeviceArray(requestItem);
        }
        RequestGetTraceArrayVO RequestItem = new RequestGetTraceArrayVO();
        RequestItem.deviceNo = deviceNo;
        RequestItem.userNo = userNo;
        RequestItem.ltid = ltid;
        networkGetTraceArray(RequestItem);
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
