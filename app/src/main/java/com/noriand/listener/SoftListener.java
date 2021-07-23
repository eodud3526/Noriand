package com.noriand.listener;

import android.os.Message;

public interface SoftListener {
	public void handleMessage(Message msg, int what);
}