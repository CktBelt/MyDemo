package com.ckt.mydemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

	private static final String TAG = "my-Service";
	private static IBinder binder;
	private String sFromJNI;
	private boolean bRun;

	public class MyBinder extends Binder {
		//获取当前实例的方法
		MyService getService() {
			Log.d(TAG, "--> get service");
			return MyService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "--> service onCreate()");
		binder = new MyBinder();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bRun = false;
		Log.d(TAG, "--> service onDestroy()");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		bRun = true;
		Log.d(TAG, "--> service onBind()");
		return binder;
	}

	public void sendCount() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (bRun) {
					try {
						//定时通过JNI获取静态字符串
						sFromJNI = NativeMethod.stringFromJNI();
						Thread.sleep(5000);
						Log.d(TAG, "MyService get string from JNI: " + sFromJNI);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}).start();
	}

}
