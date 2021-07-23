package com.noriand.listener;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SoftHandler extends Handler {
	private WeakReference<SoftListener> handlerListener = null;
	private int what = 0;

	public SoftHandler(SoftListener lisener, int what) {
		this.what = what;
		this.handlerListener = null;
		this.handlerListener = new WeakReference<SoftListener>(lisener);
	}
	
	public void setListener(SoftListener listener, int what) {
		this.what = what;
		this.handlerListener = null;
		this.handlerListener = new WeakReference<SoftListener>(listener);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		SoftListener listener = (SoftListener)handlerListener.get();
		if (listener == null) {
			return;
		}
		listener.handleMessage(msg, what);
	}

}
