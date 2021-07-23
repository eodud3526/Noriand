package com.noriand.util;

import android.content.Context;
import android.content.Intent;

public class BadgeCountUtil {
	public void updateBadgeCount(Context context, int badgeCount) {
		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badgeCount);
        intent.putExtra("badge_count_package_name", "com.onsol.brbr");
        intent.putExtra("badge_count_class_name", "com.onsol.brbr.activity.SplashActivity");
        context.sendBroadcast(intent);
	}
}
