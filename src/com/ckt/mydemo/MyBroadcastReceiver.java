package com.ckt.mydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(MainActivity.TAG, "--> MyBroadcastReceiver onReceive");
		//广播接收者直接获取JNI的字符串
		MainActivity.jniToastFunc();
	}

}
