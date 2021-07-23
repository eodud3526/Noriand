package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.constant.ServerConstant;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.view.dialog.CommonSelectDialog;
import com.noriand.vo.CommonSelectItemVO;
import com.noriand.vo.SafeZoneItemVO;
import com.noriand.vo.request.RequestWriteSafeZoneVO;
import com.noriand.vo.response.ResponseVO;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SafeZoneWriteActivity extends BaseActivity {
    // --------------------------------------------------
    // Common
    private int REQUEST_CODE_ADDRESS = 181;

    // --------------------------------------------------
    // View
    private RelativeLayout mrlMap = null;

    private RelativeLayout mrlPrev = null;
    private TextView mtvAddress = null;
    private EditText metName = null;
    private TextView mtvBoundary = null;

    private RelativeLayout mrlCheckType1 = null;
    private View mvCheckType1 = null;

    private RelativeLayout mrlCheckType2 = null;
    private View mvCheckType2 = null;

    private Button mbtSave = null;

    private MapView mmv = null;
    private MapView.MapViewEventListener mMapViewEventListener = null;
    // --------------------------------------------------
    // Data

    private int mBoundary = 0;
    private String mX = "";
    private String mY = "";
    private String mZoneCode = "";

    private String isIn = "Y";
    private String isOut = "Y";

    private int mNo = 0;

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_safe_zone);
        setStatusBar(Color.WHITE);

        setLayout();
        setListener();
        setData();
        drawMap();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mmv != null) {
            mmv.setMapViewEventListener(mMapViewEventListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setLayout() {
        mrlMap = (RelativeLayout)findViewById(R.id.rl_write_safe_zone_map);
        try {
            mmv = new MapView(mActivity);
            mrlMap.addView(mmv);
        } catch(UnsatisfiedLinkError e) {
            mrlMap.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawMap();
                }
            }, 3000);
        }

        mrlPrev = (RelativeLayout)findViewById(R.id.rl_write_safe_zone_prev);
        mtvAddress = (TextView)findViewById(R.id.tv_write_safe_zone_address);
        metName = (EditText)findViewById(R.id.et_write_safe_zone_name);
        mtvBoundary = (TextView)findViewById(R.id.tv_write_safe_zone_boundary);

        mrlCheckType1 = (RelativeLayout)findViewById(R.id.rl_write_safe_zone_check_type_1);
        mvCheckType1 = (View)findViewById(R.id.v_write_safe_zone_check_type_1);

        mrlCheckType2 = (RelativeLayout)findViewById(R.id.rl_write_safe_zone_check_type_2);
        mvCheckType2 = (View)findViewById(R.id.v_write_safe_zone_check_type_2);

        mbtSave = (Button)findViewById(R.id.btn_write_safe_zone_save);
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
                mmv.setMapCenterPoint(mapPoint, true);

                final double x = mapPoint.getMapPointGeoCoord().longitude;
                final double y = mapPoint.getMapPointGeoCoord().latitude;
                MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(ServerConstant.KAKAO_API_KEY, mapPoint, new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
                    @Override
                    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                        mtvAddress.setText(s);
                        mX = String.valueOf(x);
                        mY = String.valueOf(y);
                        mmv.removeAllCircles();
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(y, x);
                        MapCircle mapCircle = new MapCircle(mapPoint, mBoundary, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                        mmv.addCircle(mapCircle);

                        mmv.setMapCenterPoint(mapPoint, true);
                    }
                    @Override
                    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                    }
                }, mActivity);
                mapReverseGeoCoder.startFindingAddress();

//
//                if(!StringUtil.isEmpty(x) && !StringUtil.isEmpty(y)) {
//                    double dX = Double.parseDouble(mX);
//                    double dY = Double.parseDouble(mY);
//
//
//                }
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

        mtvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mtvAddress.getText().toString();
                moveAddressActivity(address);
            }
        });

        mrlCheckType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("Y".equals(isIn)) {
                    isIn = "";
                    mvCheckType1.setBackgroundResource(R.drawable.btn_check);
                } else {
                    isIn = "Y";
                    mvCheckType1.setBackgroundResource(R.drawable.btn_check_pressed);
                }
            }
        });

        mrlCheckType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("Y".equals(isOut)) {
                    isOut = "";
                    mvCheckType2.setBackgroundResource(R.drawable.btn_check);
                } else {
                    isOut = "Y";
                    mvCheckType2.setBackgroundResource(R.drawable.btn_check_pressed);
                }
            }
        });

        mtvBoundary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CommonSelectItemVO> list = new ArrayList<CommonSelectItemVO>();
                CommonSelectItemVO item1 = new CommonSelectItemVO();
                item1.text = "100m";
                item1.count = 100;
                list.add(item1);

                CommonSelectItemVO item2 = new CommonSelectItemVO();
                item2.text = "200m";
                item2.count = 200;
                list.add(item2);

                CommonSelectItemVO item3 = new CommonSelectItemVO();
                item3.text = "500m";
                item3.count = 500;
                list.add(item3);

                CommonSelectItemVO item4 = new CommonSelectItemVO();
                item4.text = "1000m";
                item4.count = 1000;
                list.add(item4);

                CommonSelectItemVO item5 = new CommonSelectItemVO();
                item5.text = "2000m";
                item5.count = 2000;
                list.add(item5);

                showCommonSelectDialog(list, new CommonSelectDialog.DialogCommonSelectListener() {
                    @Override
                    public void onSelect(int index, ArrayList<CommonSelectItemVO> list) {
                        if(index == -1) {
                            return;
                        }
                        CommonSelectItemVO item = list.get(index);
                        mtvBoundary.setText(item.text);
                        mBoundary = item.count;



                        if(!StringUtil.isEmpty(mX) && !StringUtil.isEmpty(mY)) {
                            double dX = Double.parseDouble(mX);
                            double dY = Double.parseDouble(mY);

                            mmv.removeAllCircles();
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                            MapCircle mapCircle = new MapCircle(mapPoint, mBoundary, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                            mmv.addCircle(mapCircle);

                            mmv.setMapCenterPoint(mapPoint, true);
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        });

        mbtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = metName.getText().toString();
                if(StringUtil.isEmpty(name)) {
                    showDialogOneButton("이름을 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
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

                if(mBoundary == 0) {
                    showDialogOneButton("반경(범위)를 선택해 주세요.");
                    return;
                }

                String address = mtvAddress.getText().toString();
                if(StringUtil.isEmpty(mX) || StringUtil.isEmpty(mY) || StringUtil.isEmpty(address)) {
                    showDialogOneButton("주소를 선택해 주세요.");
                    return;
                }

                int deviceNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_DEVICE_NO);
                int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
                RequestWriteSafeZoneVO requestItem = new RequestWriteSafeZoneVO();
                if(mNo > 0) {
                    requestItem.changeUpdateMode(mNo);
                }
                requestItem.userNo = userNo;
                requestItem.deviceNo = deviceNo;
                requestItem.x = mX;
                requestItem.y = mY;
                requestItem.zoneCode = mZoneCode;
                requestItem.name = name;
                requestItem.boundary = mBoundary;
                requestItem.address = address;
                requestItem.addressDetail = "";
                requestItem.isIn = isIn;
                requestItem.isOut = isOut;
                networkWriteSafeZone(requestItem);
            }
        });
    }

    private void setData() {
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            SafeZoneItemVO item = new SafeZoneItemVO();
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                item.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }

            if(item != null) {
                mBoundary = item.boundary;
                mX = item.x;
                mY = item.y;
                mZoneCode = item.zoneCode;
                isIn = item.isIn;
                isOut = item.isOut;
                metName.setText(item.name);
                mtvAddress.setText(item.address);
                mZoneCode = item.zoneCode;
                mNo = item.no;
            }
        }

        if(mBoundary == 0) {
            mBoundary = 100;
        }
        mtvBoundary.setText(mBoundary + "m");

        if("Y".equals(isIn)) {
            mvCheckType1.setBackgroundResource(R.drawable.btn_check_pressed);
        } else {
            mvCheckType1.setBackgroundResource(R.drawable.btn_check);
        }

        if("Y".equals(isOut)) {
            mvCheckType2.setBackgroundResource(R.drawable.btn_check_pressed);
        } else {
            mvCheckType2.setBackgroundResource(R.drawable.btn_check);
        }
    }


    private void drawMap() {
        if(mmv != null) {
            String tempX = CommonPreferences.getString(mActivity, CommonPreferences.TAG_LAST_X);
            String tempY = CommonPreferences.getString(mActivity, CommonPreferences.TAG_LAST_Y);
            if(!StringUtil.isEmpty(tempX) && !StringUtil.isEmpty(tempY)) {
                double lastY = Double.parseDouble(tempY);
                double lastX = Double.parseDouble(tempX);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lastY, lastX);
                mmv.setMapCenterPointAndZoomLevel(mapPoint, 2, true);
            } else {
                mmv.setZoomLevel(2, true);
            }

            mmv.zoomIn(true);
            mmv.zoomOut(true);

            if(mNo > 0) {
                if(!StringUtil.isEmpty(mX) && !StringUtil.isEmpty(mY)) {
                    double dX = Double.parseDouble(mX);
                    double dY = Double.parseDouble(mY);

                    mmv.removeAllCircles();
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                    MapCircle mapCircle = new MapCircle(mapPoint, mBoundary, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                    mmv.addCircle(mapCircle);

                    mmv.setMapCenterPoint(mapPoint, true);
                }
            }
        }
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

    public void moveAddressActivity(String address) {
        Intent intent = new Intent(mActivity, DaumAddressActivity.class);
        intent.putExtra("address", address);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_ADDRESS) {
            String zoneCode = intent.getStringExtra("zoneCode");
            String address = intent.getStringExtra("address");
            String x = intent.getStringExtra("x");
            String y = intent.getStringExtra("y");

            if(zoneCode == null) {
                zoneCode = "";
            }
            if(address == null) {
                address = "";
            }
            if(x == null) {
                x = "";
            }
            if(y == null) {
                y = "";
            }

            mX = x;
            mY = y;
            mZoneCode = zoneCode;
            mtvAddress.setText(address);

            if(!StringUtil.isEmpty(x) && !StringUtil.isEmpty(y)) {
                double dX = Double.parseDouble(mX);
                double dY = Double.parseDouble(mY);

                mmv.removeAllCircles();
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(dY, dX);
                MapCircle mapCircle = new MapCircle(mapPoint, mBoundary, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 255, 0));
                mmv.addCircle(mapCircle);

                mmv.setMapCenterPoint(mapPoint, true);
            }
        }
    }



    private void networkWriteSafeZone(final RequestWriteSafeZoneVO requestItem) {
        mApiController.writeSafeZone(mActivity, requestItem, new ApiController.ApiCommonListener() {
            @Override
            public void onSuccess(ResponseVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm) {
                    showDialogOneButton("등록에 실패했습니다. 값을 확인 후 다시 시도해 주세요.");
                    return;
                }

                setResult(RESULT_OK);
                finish();
            }
            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkWriteSafeZone(requestItem);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }
}