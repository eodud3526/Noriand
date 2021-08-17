package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.constant.ServerConstant;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.DeviceItemVO;
import com.noriand.vo.SafeZoneItemVO;
import com.noriand.vo.TraceItemVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestGetDeviceLocationVO;
import com.noriand.vo.request.RequestGetNowLocationVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseGetDeviceLocationVO;
import com.noriand.vo.response.ResponseGetNowLocationVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseActivity {
// --------------------------------------------------
    // Common
    private final int REQUEST_CODE_ALARM = 212;
    private final int REQUEST_CODE_SAFE_ZONE = 213;
    private final int REQUEST_CODE_DEVICE_UPDATE = 214;

    private final int REFRESH_TIME = 30000;

    private DrawerLayout.DrawerListener mDrawerListener = null;

    private CalloutBalloonAdapter mCalloutBalloonListener = null;
    private MapView.POIItemEventListener mPoiItemEventListener = null;
    private MapView.MapViewEventListener mMapViewEventListener = null;

    private Timer mTimer = null;
    private MapPOIItem mMarker = null;

    // --------------------------------------------------
    // View
    private Button mbtnMenu = null;
    private TextView mtvTitle = null;

    private RelativeLayout mrlMap = null;
    private MapView mmv = null;


    private DrawerLayout mdl = null;
    private RelativeLayout mrlSubMenuArea = null;

    private TextView mtvSubName = null;
    private ImageView mivSub = null;
    private RelativeLayout mrlSubPencil = null;

    private RelativeLayout mrlSubDeviceNumber = null;
    private RelativeLayout mrlShareFriend = null;
    private RelativeLayout mrlAlarm = null;
    private RelativeLayout mrlDeviceSetting = null;
    private RelativeLayout mrlVoucher = null;
    private RelativeLayout mrlSafeZone = null;
    private RelativeLayout mrlActionHistory = null;

    private RelativeLayout mrlRefresh = null;
    private RelativeLayout mrlWhat = null;
    private RelativeLayout mrlTrace = null;
    private RelativeLayout mrlCctv = null;
    private RelativeLayout mrlSiren = null;
    private TextView mtvSosCount = null;

    private TextView mtvToday = null;
    private TextView mtvAddress = null;

    // --------------------------------------------------
    // Data
    private DeviceItemVO mItem = null;

    private ArrayList<TraceItemVO> mTraceList = null;
    private String mToday = "";
    private String mLastTime = "";

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setBase();
        setLayout();
        setListener();

    }

    private void setBase() {
        mItem = new DeviceItemVO();
        mTraceList = new ArrayList<TraceItemVO>();
    }

    private void setLayout() {
        mbtnMenu = (Button)findViewById(R.id.btn_main_submenu);
        mtvTitle = (TextView)findViewById(R.id.tv_main_title);

        mrlMap = (RelativeLayout) findViewById(R.id.rl_map);
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

        mdl = (DrawerLayout)findViewById(R.id.dl_main);
        mrlSubMenuArea = (RelativeLayout)findViewById(R.id.rl_main_sub_menu);

        mtvSubName = (TextView)findViewById(R.id.tv_main_submenu_name);
        mivSub = (ImageView)findViewById(R.id.iv_main_submenu_picture);
        mrlSubPencil = (RelativeLayout)findViewById(R.id.rl_main_submenu_pencil);

        mrlSubDeviceNumber = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_number);
        mrlShareFriend = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_share_friend);
        mrlAlarm = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_alarm);
        mrlDeviceSetting = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_device_setting);
        mrlVoucher = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_voucher);
        mrlSafeZone = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_safe_zone);
        mrlActionHistory = (RelativeLayout)findViewById(R.id.rl_main_sub_menu_action_history);

        mtvToday = (TextView)findViewById(R.id.tv_main_today);
        mtvAddress = (TextView)findViewById(R.id.tv_main_address);

        mrlRefresh = (RelativeLayout)findViewById(R.id.rl_main_refresh);
        mrlWhat = (RelativeLayout)findViewById(R.id.rl_main_what);
        mrlTrace = (RelativeLayout)findViewById(R.id.rl_main_trace);
        mrlCctv = (RelativeLayout)findViewById(R.id.rl_main_cctv);
        mrlSiren = (RelativeLayout)findViewById(R.id.rl_main_siren);

        mtvSosCount = (TextView)findViewById(R.id.tv_main_sos_count);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(mTimer == null) {
                        return;
                    }
                    int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                    RequestGetDeviceLocationVO requestItem = new RequestGetDeviceLocationVO();
                    requestItem.deviceNo = deviceNo;
                    requestItem.isSilent = true;
                    networkGetDeviceLocation(requestItem);
                }
            }, REFRESH_TIME, REFRESH_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTimer != null) {
            mTimer.cancel();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setData();

        String isNew = CommonPreferences.getString(mActivity, CommonPreferences.TAG_IS_NEW);
        if("Y".equals(isNew)) {
            CommonPreferences.putString(mActivity, CommonPreferences.TAG_IS_NEW, "");
            return;
        }
        refresh();


        if(mTimer != null) {

        }
    }

    private void setListener() {
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
                if(tag == 999) {
                    mtvToday.setText(mToday);
                } else {
                    int size = mTraceList.size();
                    if(size > 0 && tag < size) {
                        TraceItemVO traceItem = mTraceList.get(tag);
                        mtvToday.setText(traceItem.insertTime);
                    }
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

        mDrawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View v, float x) {
                mdl.bringChildToFront(v);
                mdl.requestLayout();
            }

            @Override
            public void onDrawerOpened(View arg0) {
                mbtnMenu.setBackgroundResource(R.drawable.selector_btn_close); // 주석확인
            }

            @Override
            public void onDrawerClosed(View arg0) {
                mbtnMenu.setBackgroundResource(R.drawable.selector_btn_menu); // 주석확인
            }
        };
        mdl.addDrawerListener(mDrawerListener);

        mbtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mdl.isDrawerOpen(mrlSubMenuArea)) {
                    mdl.closeDrawers();
                } else {
                    mdl.openDrawer(mrlSubMenuArea);
                }
            }
        });

        mrlSubPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDeviceUpdateActivity(mItem);
            }
        });

        mrlSubDeviceNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveDeviceNumberActivity(mItem);
            }
        });
        mrlShareFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                kakaolink();
                //showDialogOneButton("기능 준비중입니다.");
                //ShareScreenShot();
            }
        });
        mrlAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveAlarmActivity();
            }
        });
        mrlDeviceSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveDeviceSettingActivity(mItem);
            }
        });
        mrlVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOneButton("기능 준비중입니다.");
            }
        });
        mrlSafeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveSafeZoneListActivity();
            }
        });
        mrlActionHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mdl.closeDrawers();
                moveActionHistoryActivity();
            }
        });

        mrlRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        mrlWhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOneButton("기능 준비중입니다.");
            }
        });
        mrlTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
                RequestGetTraceArrayVO requestItem = new RequestGetTraceArrayVO();
                requestItem.deviceNo = mItem.no;
                requestItem.userNo = userNo;
                requestItem.ltid = ltid;
                networkGetTodayTraceArray(requestItem);
            }
        });
        mrlCctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogOneButton("기능 준비중입니다.");
                moveCctvRoadViewActivity(mTraceList.get(0));
            }
        });
        mrlSiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveAlarmActivity();
            }
        });
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
    }

    private void drawMarker() {
        mtvTitle.setText(mItem.name);
        mtvSubName.setText(mItem.name);

        String pictureUrl = mItem.pictureUrl;
        if(!StringUtil.isEmpty(pictureUrl)) {
            setImage(mActivity, mivSub, pictureUrl, null);
        }

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
        RequestGetNowLocationVO requestItem = new RequestGetNowLocationVO();
        requestItem.isSilent = false;
        requestItem.userNo = userNo;
        requestItem.deviceNo = mItem.no;
        requestItem.ltid = mItem.ltid;
        networkGetNowLocation(requestItem);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mdl.isDrawerOpen(mrlSubMenuArea)) {
                mdl.closeDrawers();
                return false;
            }

            moveDeviceSelectActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkPush(intent);
    }

    private void checkPush(Intent intent) {
        String isNew = CommonPreferences.getString(mActivity, CommonPreferences.TAG_IS_NEW);
        Log.d("mytest2", "checkPush) " + isNew);
        if("Y".equals(isNew)) {
            mtvSosCount.setVisibility(View.VISIBLE);
            String sosContent = CommonPreferences.getString(mActivity, CommonPreferences.TAG_SOS_CONTENT);
            if(sosContent == null) {
                sosContent = "";
            }
            showDialogTwoButton(sosContent, new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    moveAlarmActivity();
                }
                @Override
                public void onCancel() {
                }
            });
        } else {
            mtvSosCount.setVisibility(View.GONE);
        }
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


    private void networkGetNowLocation(final RequestGetNowLocationVO requestItem) {
        mApiController.getNowLocation(mActivity, requestItem, new ApiController.ApiGetNowLocationListener() {
            @Override
            public void onSuccess(ResponseGetNowLocationVO item) {
                if(!"Y".equals(item.isLora)) {
                //    showDialogTwoButton("이 기기로 위치 조회가 실패했습니다. 올바른 기기 고유번호인지 확인해 주세요. 장치 정보로 이동하시겠습니까?", new CommonDialog.DialogConfirmListener() {
                //        @Override
                //        public void onConfirm() {
                //            moveDeviceUpdateActivity(mItem);
                //        }
                //        @Override
                //        public void onCancel() {
                //        }
                //    });
                    showDialogOneButton("현재 기기 전원이 꺼져 있습니다.");
                    int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                    String ltid = CommonPreferences.getString(mActivity, CommonPreferences.TAG_DEVICE_LTID);
                    RequestGetTraceArrayVO requestItem = new RequestGetTraceArrayVO();
                    requestItem.deviceNo = mItem.no;
                    requestItem.userNo = userNo;
                    requestItem.ltid = ltid;
                    networkGetLastMarker(requestItem);
                    return;
                }

                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(mmv != null && item.isConfirm) {
                    String today = item.today; // today = lora ct
                    mToday = today;
                    int batteryCount = item.batteryCount;
                    String xTemp = item.x;
                    String yTemp = item.y;

                    mmv.removeAllCircles();
                    SafeZoneItemVO[] safeZoneArray = item.safeZoneArray;
                    if (safeZoneArray != null) {
                        int safeZoneArraySize = safeZoneArray.length;
                        for (int i = 0; i < safeZoneArraySize; i++) {
                            SafeZoneItemVO safeZoneItem = safeZoneArray[i];
                            String rowXTemp = safeZoneItem.x;
                            String rowYTemp = safeZoneItem.y;
                            if (!StringUtil.isEmpty(rowXTemp) && !StringUtil.isEmpty(rowYTemp)) {
                                double dX = Double.parseDouble(rowXTemp);
                                double dY = Double.parseDouble(rowYTemp);
                                int rowBoundary = safeZoneItem.boundary;
                                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                                MapCircle mapCircle = new MapCircle(mapPoint, rowBoundary, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                                mmv.addCircle(mapCircle);
                            }
                        }
                    }
                    mtvToday.setText("마지막으로 통신한 시간 : " + today);

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
                checkPush(getIntent());
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

    private void networkGetDeviceLocation(final RequestGetDeviceLocationVO requestItem) {
        mApiController.getDeviceLocation(mActivity, requestItem, new ApiController.ApiGetDeviceLocationListener() {
            @Override
            public void onSuccess(ResponseGetDeviceLocationVO item) {
                if (item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }
                String strLastX = item.lastX;
                String strLastY = item.lastY;
                mItem.lastX = strLastX;
                mItem.lastY = strLastY;
                if (!StringUtil.isEmpty(strLastX) && !StringUtil.isEmpty(strLastY)) {
                    double lastX = Double.parseDouble(strLastX);
                    double lastY = Double.parseDouble(strLastY);
                    if (mmv != null && lastX > 0 && lastY > 0) {
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lastY, lastX);
                        mmv.setMapCenterPoint(mapPoint, true);

                        if(mMarker != null) {
                            mmv.removePOIItem(mMarker);
                            mMarker = new MapPOIItem();
                            mMarker.setItemName(mItem.name);
                            mMarker.setTag(999);
                            mMarker.setMapPoint(mapPoint);
                            mMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                            mMarker.setCustomImageResourceId(R.drawable.ico_pin_red_01); // 마커 이미지.
                            mMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                            mMarker.setCustomImageAnchor(0.5f, 0.5f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                            mmv.addPOIItem(mMarker);
                        }
                    }
                }
            }

            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetDeviceLocation(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private void networkGetTodayTraceArray(final RequestGetTraceArrayVO requestItem) {
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
                mTraceList.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());
                for(int i=0; i<length; i++) {
                    if (traceArray[i].insertTime.substring(0,10).equals(date)){
                        mTraceList.add(traceArray[i]);
                    }
                }
                if(mTraceList.size() == 0){
                    showDialogOneButton("오늘 경로 기록이 존재하지 않습니다.");
                    return;
                }
                drawTraceArray();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetTodayTraceArray(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
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

    public void moveDeviceNumberActivity(DeviceItemVO item) {
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }

        Intent intent = new Intent(mActivity, DeviceNumberActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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

    public void moveAlarmActivity() {
        Intent intent = new Intent(mActivity, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_ALARM);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveDeviceSelectActivity() {
        Intent intent = new Intent(mActivity, DeviceSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    public void moveSafeZoneListActivity() {
        Intent intent = new Intent(mActivity, SafeZoneListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_SAFE_ZONE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void moveActionHistoryActivity() {
        Intent intent = new Intent(mActivity, ActionHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CODE_ALARM || requestCode == REQUEST_CODE_SAFE_ZONE || requestCode == REQUEST_CODE_DEVICE_UPDATE) {
            if(requestCode == REQUEST_CODE_DEVICE_UPDATE) {
                if(resultCode == RESULT_OK) {
                    String isDelete = intent.getStringExtra("isDelete");
                    if ("Y".equals(isDelete)) {
                        moveDeviceSelectActivity();
                        return;
                    } else {
                        String name = intent.getStringExtra("name");
                        String ltid = intent.getStringExtra("ltid");
                        if(ltid != null) {
                            Log.d("mytest2", "ltid: " + ltid);
                            mItem.name = name;
                            mItem.ltid = ltid;
                            try {
                                getIntent().putExtra("strItem", mItem.getJSONObject().toString());
                            } catch(JSONException e) {
                            }
                        }
                        return;
                    }
                }
                return;
            }

            mtvTitle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetMapView();

                    int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                    int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                    RequestGetDeviceArrayVO requestItem = new RequestGetDeviceArrayVO();
                    requestItem.onChangeDeviceArrayForDevice(deviceNo);
                    requestItem.userNo = userNo;
                    requestItem.isSilent = false;
                    networkGetDeviceArray(requestItem);
                }
            }, 300);
        }
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

    public void kakaolink() {
        String title = mItem.name;
        String descrption = "위치 표시";

        // 검색할 주소
        TraceItemVO item = mTraceList.get(0);
        String address = item.y + ", " + item.x;

        // 이미지가 없으면  비어서 보여진다. null은 안됨
        String imageUrl = "https://ifh.cc/g/n8ymoW.jpg"; // 기기 이미지
        //String imageUrl = "https://ifh.cc/g/DyxiDr.png"; // 아이콘 이미지
        // 링크 화면에 보여줄 이미지 url, 이미지 링크 만료일 : 2022-04-18

        LocationTemplate params = LocationTemplate.newBuilder(
                address, // 보여줄 주소
                ContentObject.newBuilder(title,imageUrl,
                        // ListTemplate
                        LinkObject.newBuilder() // 링크 오브젝트
                                //.setWebUrl("https://developers.kakao.com")
                                //.setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(descrption)
                        .build())
                //위치 보기시 하단에 나타나는 타이틀
                .setAddressTitle("주소")
                // 버튼 1
                .addButton(new ButtonObject("앱 설치 링크", LinkObject.newBuilder()
                        //.setWebUrl("'https://developers.kakao.com")
                        //.setMobileWebUrl("'https://developers.kakao.com")
                        // 앱 설치x -> 오픈마켓 링크, 앱 설치o -> 앱 실행
                        .setAndroidExecutionParams("key1=value1") // JSON -> P-> DID 주소 뒤에 담김.
                        .setIosExecutionParams("key1=value1")
                        .build())
                ).build();
        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }
            @Override
            public void onSuccess(KakaoLinkResponse result) {
                System.out.println(result.getTemplateMsg());
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }

    private void networkGetLastMarker(final RequestGetTraceArrayVO requestItem) {
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
                mTraceList.clear();

                TraceItemVO lastItem = traceArray[0];
                mTraceList.add(lastItem);
                mLastTime = lastItem.insertTime;
                mtvToday.setText("마지막으로 통신한 시간 : " + mLastTime);
                String strLastX = lastItem.x;
                String strLastY = lastItem.y;
                if (!StringUtil.isEmpty(strLastX) && !StringUtil.isEmpty(strLastY)) {
                    double lastX = Double.parseDouble(strLastX);
                    double lastY = Double.parseDouble(strLastY);
                    if (mmv != null && lastX > 0 && lastY > 0) {
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lastY, lastX);
                        mmv.setMapCenterPoint(mapPoint, true);
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(mLastTime);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                        marker.setCustomImageResourceId(R.drawable.ico_pin_red_01); // 마커 이미지.
                        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                        marker.setCustomImageAnchor(0.5f, 0.5f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                        mmv.addPOIItem(marker);
                    }
                }

            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkGetLastMarker(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    public void moveCctvRoadViewActivity(TraceItemVO item){
        String strItem = "";
        try {
            JSONObject jsonObject = item.getJSONObject();
            if(jsonObject != null) {
                strItem = jsonObject.toString();
            }
        } catch (JSONException e) {
        }
        Intent intent = new Intent(mActivity, CctvRoadViewActivity.class);
        intent.putExtra("strItem", strItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_DEVICE_UPDATE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }
}