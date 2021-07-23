package com.noriand.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noriand.R;
import com.noriand.activity.MainActivity;
import com.noriand.activity.SplashActivity;
import com.noriand.common.CommonPreferences;
import com.noriand.common.CommonTag;
import com.noriand.util.StringUtil;

import java.util.List;
import java.util.Map;

public class FirebaseMessageService extends FirebaseMessagingService {
	private final int FOREGROUND_KEY = 8161;

	@Override
	@WorkerThread
	public void onMessageReceived(RemoteMessage arg0) {
		super.onMessageReceived(arg0);

		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		@SuppressLint("InvalidWakeLockTag")
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "noriand_WakeLock");
		wakeLock.acquire();

		Map<String, String> clsData = arg0.getData();

		int pushType = 0;
		String title = "";
		String content = "";
		int alarmType = 0;
		for (Map.Entry<String, String> it : clsData.entrySet()) {
			String key = it.getKey();
			if("pushType".equals(key)) {
				String value = it.getValue();
				if(StringUtil.isOnlyDigit(value)) {
					pushType = Integer.parseInt(value);
				}
			} else if("title".equals(key)) {
				title = it.getValue();
			} else if("content".equals(key)) {
				content = it.getValue();
			} else if("alarmType".equals(key)) {
				String value = it.getValue();
				if(StringUtil.isOnlyDigit(value)) {
					alarmType = Integer.parseInt(value);
				}
			}
		}

		Context context = getApplicationContext();
		int userNo = CommonPreferences.getInt(context, CommonPreferences.TAG_USER_NO);
		int deviceNo = CommonPreferences.getInt(context, CommonPreferences.TAG_DEVICE_NO);
		Log.d("mytest2", userNo + ", " + deviceNo);
		if(userNo == 0 || deviceNo == 0) {
			return;
		}

		if (alarmType == CommonTag.ALARM_TYPE_SOS) {
			CommonPreferences.putString(getApplicationContext(), CommonPreferences.TAG_IS_NEW, "Y");
			CommonPreferences.putString(getApplicationContext(), CommonPreferences.TAG_SOS_CONTENT, title + "\n" + content);
		}

		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("pushType", pushType);
		intent.putExtra("alarmType", alarmType);
		intent.setClass(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		String ticker = context.getResources().getString(R.string.app_name);
		int number = 0;
		sendNotification(context, ticker, number, title, content, pendingIntent);

		if (isRunningActivity(context, MainActivity.class.getName())) {
			try {
				pendingIntent.send();
			} catch (PendingIntent.CanceledException e) {
			}
		}
	}

	private boolean isRunningApp(Context context) {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
		int size = list.size();
		String packageName = context.getPackageName();
		for(int i=0; i<size; i++) {
			if(packageName.equals(list.get(i).processName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRunningActivity(Context context, String activityName) {
		boolean isExist = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
		for(ActivityManager.RunningTaskInfo task : tasks) {
			String rowName = task.topActivity.getClassName();
			if(rowName.endsWith(activityName)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

	@Override
	public void onNewToken(@NonNull String token) {
		super.onNewToken(token);

		Log.d("mytest2", "(onNewToken) token: " + token);
		CommonPreferences.putString(getApplicationContext(), CommonPreferences.TAG_PUSH_TOKEN, token);
		Intent intent = new Intent();
		intent.setAction(CommonTag.ACTION_FCM_REGISTRATION);
		intent.putExtra(CommonPreferences.TAG_PUSH_TOKEN, token);
		getApplicationContext().sendBroadcast(intent);
	}

	public void sendNotification(Context context, String ticker, int number, String title, String content, PendingIntent pendingIntent) {
		String NOTIFICATION_CHANNEL_ID = "57915";

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ico_launcher));
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		builder.setContentIntent(pendingIntent);
		builder.setAutoCancel(true);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setSmallIcon(R.mipmap.ic_launcher_round);
			String appName = getResources().getString(R.string.app_name);
			CharSequence charSequence = appName;
			String description = appName;
			NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, charSequence, NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.setDescription(description);

			assert notificationManager != null;
			notificationManager.createNotificationChannel(notificationChannel);
		} else {
			builder.setSmallIcon(R.mipmap.ic_launcher_round);
		}
		builder.setVibrate(new long[]{100, 0, 100, 0});
		builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

		int notifyCount = CommonPreferences.getInt(context, CommonPreferences.TAG_NOTIFY_COUNT);
		notifyCount++;
		CommonPreferences.putInt(context, CommonPreferences.TAG_NOTIFY_COUNT, notifyCount);
		assert notificationManager != null;
		notificationManager.notify(notifyCount, builder.build());
	}
}