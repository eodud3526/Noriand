package com.noriand.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.common.CommonTag;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.request.RequestLoginVO;
import com.noriand.vo.response.ResponseLoginVO;


public class SplashActivity extends BaseActivity {
    private final int REQUEST_CODE_EXTERNAL_STORAGE = 5901;
    private View mvTextLogo = null;

    private BroadcastReceiver mBroadcastReceiver = null;

    private boolean isGetToken = false;
    private boolean isAnimation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBar(Color.WHITE);

        mvTextLogo = (View)findViewById(R.id.v_splash_text_logo);

        setFCM();
        checkPermission();

//        new CommonUtil().getHashKey(getApplicationContext());
    }

    private void setFCM() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(CommonTag.ACTION_FCM_REGISTRATION.equals(action)) {
                    String token = intent.getStringExtra(CommonPreferences.TAG_PUSH_TOKEN);
                    Log.d("mytest2", "FCM Regist, token: " + token);
                    if(!TextUtils.isEmpty(token)) {
                        CommonPreferences.putString(context, CommonPreferences.TAG_PUSH_TOKEN, token);
                        isGetToken = true;
                        checkMoveMainActivity();
                        return;
                    }
                }

                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        tryFCM();
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
                });
            }
        };
        registerReceiver(mBroadcastReceiver, new IntentFilter(CommonTag.ACTION_FCM_REGISTRATION));
        tryFCM();
    }

    private void tryFCM() {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("news");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("mytest2", "FCM try, token: " + token);
        createNotificationChannel();

        if(!StringUtil.isEmpty(token)) {
            CommonPreferences.putString(getApplicationContext(), CommonPreferences.TAG_PUSH_TOKEN, token);
            isGetToken = true;
            checkMoveMainActivity();
        } else {
            token = FirebaseInstanceId.getInstance().getToken();
            if(!StringUtil.isEmpty(token)) {
                CommonPreferences.putString(getApplicationContext(), CommonPreferences.TAG_PUSH_TOKEN, token);
                isGetToken = true;
                checkMoveMainActivity();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = getResources().getString(R.string.app_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(appName);

            if(notificationChannel == null) {
                CharSequence name = appName;
                String description = appName;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(appName, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int isWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (isWrite == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_EXTERNAL_STORAGE);
                return;
            }
        }

        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha_up);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = true;
                checkMoveMainActivity();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mvTextLogo.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkMoveMainActivity() {
        if(isGetToken && isAnimation) {
            int userNo = CommonPreferences.getInt(mActivity, CommonPreferences.TAG_USER_NO);
            if(userNo > 0) {
                String email = CommonPreferences.getString(mActivity, CommonPreferences.TAG_EMAIL);

                RequestLoginVO requestItem = new RequestLoginVO(mActivity);
                requestItem.setLoginForAuto(userNo, email);
                networkLoginForAuto(requestItem);
            } else {
                moveLoginActivity();
            }
        }
    }

    private void networkLoginForAuto(final RequestLoginVO requestItem) {
        mApiController.login(mActivity, requestItem, new ApiController.ApiLoginListener() {
            @Override
            public void onSuccess(ResponseLoginVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm || item.userNo == 0) {
                    CommonPreferences.resetPreferences(getApplicationContext());
                    moveLoginActivity();
                    return;
                }

                Context context = getApplicationContext();
                CommonPreferences.putInt(context, CommonPreferences.TAG_USER_NO, item.userNo);
                CommonPreferences.putString(context, CommonPreferences.TAG_EMAIL, requestItem.email);
                CommonPreferences.putString(context, CommonPreferences.TAG_PHONE_NUMBER, item.phoneNumber);
                CommonPreferences.putString(context, CommonPreferences.TAG_JOIN_TIME, item.joinTime);
                CommonPreferences.putString(context, CommonPreferences.TAG_LAST_LOGIN_TIME, item.lastLoginTime);

                moveDeviceSelectActivity();
            }

            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkLoginForAuto(requestItem);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    public void moveDeviceSelectActivity() {
        Intent intent = new Intent(mActivity, DeviceSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    public void moveMainActivity() {
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    public void moveLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_EXTERNAL_STORAGE) {
            if(grantResults[0] == 0) {
                checkPermission();
            } else {
                showDialogTwoButton("해당 기능을 사용하려면 앱의 저장소 권한을 허용으로 변경해주세요.", new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        moveSystemApplicationSetting(REQUEST_CODE_EXTERNAL_STORAGE);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        }
    }

    public void moveSystemApplicationSetting(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        checkPermission();
    }
}