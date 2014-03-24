package com.ckt.mydemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity implements ServiceConnection {

	private final int MESSAGE_ID_SEND_STRING_TO_JNI = 0x01;
	private final String INTERNAL_ACTION_1 = "com.ckt.mydemo.broad1";

	//过滤log有以下几种方式：
	//1.在logcat中设置filter，将By log tag统一按照各个类中的TAG填写，即可过滤响应tag的log
	//2.将By Application name 填写为包名则能过滤出所有跟该包相关的log
	//3.logcat中其他filter用法与以上方法类似
	//4.在程序中填写log时尽量在开头加入容易区分并且统一的格式，如“--> ”，在logcat搜索栏中填入“--> ”即可搜索出相应log
	//5.通过log级别过滤
	public static final String TAG = "my-java";

	private static TextView tv_title;
	private static EditText ev_input;
	private Button btn_send;

	private static Context content;
	private MyBroadcastReceiver mBCR;
	private MyHandler myHandler;
	private MyService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBCR = new MyBroadcastReceiver();
		myHandler = new MyHandler();
		content = getApplicationContext();

		tv_title = (TextView) findViewById(R.id.tv_title);
		ev_input = (EditText) findViewById(R.id.ev_input);
		btn_send = (Button) findViewById(R.id.btn_send);

		tv_title.setText(NativeMethod.stringFromJNI());
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//点击按钮后开启新的线程
				new Thread(new MyThread()).start();
			}

		});

		//进入APP则启动Service
		Intent intent = new Intent(MainActivity.this, MyService.class);
		bindService(intent, MainActivity.this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//采用动态方法注册广播
		IntentFilter intentfilter = new IntentFilter(INTERNAL_ACTION_1);
		registerReceiver(mBCR, intentfilter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mBCR);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		//Service启动后调用sendCount，每隔5秒通过JNI获取一次字符串
		mService = ((MyService.MyBinder) (service)).getService();
		mService.sendCount();
		Log.d(TAG, "--> onServiceConnected sendCount");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		mService = null;
		Log.d(TAG, "--> onServiceDisconnected");
	}

	/*********************************** define static function *********************************/
	private static String getContent() {
		String str = ev_input.getText().toString();
		str = str.trim().length() > 0 ? str : "default value";
		return str;
	}

	public static void jniToastFunc() {
		Log.d(TAG,
				"--> jniToastFunc -- msg = MainActivity.MESSAGE_ID_SEND_STRING_TO_JNI");
		//点击按钮后从EditText中获取用户输入字符串作为形参传入本地方法，JNI接收到字符串后发回"native_set_to_jni success"并通过Toast显示出来
		String mStr = NativeMethod.setParamsToJNI(getContent());
		if (mStr == null) {
			mStr = "JNI failed";
		}
		//调用该方法后在log中打印出JAVA的堆栈信息
		Log.d(TAG, Log.getStackTraceString(new Throwable()));
		Toast.makeText(content, mStr, Toast.LENGTH_SHORT).show();
	}

	/******************************************************************************************/

	/************************************* define inner class ***********************************/
	//自定义线程中通过handler发送消息
	public class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msg = Message.obtain();
			msg.what = MESSAGE_ID_SEND_STRING_TO_JNI;

			Log.d(TAG,
					"--> MyThread sendMessage msg.what = MainActivity.MESSAGE_ID_SEND_STRING_TO_JNI");
			myHandler.sendMessage(msg);
		}
	}

	//自定义handler过滤出消息中携带的参数进行识别，并发出广播
	public class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_ID_SEND_STRING_TO_JNI:
				Intent intent = new Intent(INTERNAL_ACTION_1);
				sendBroadcast(intent);
				break;
			}
			super.handleMessage(msg);
		}

	}
	/******************************************************************************************/
}
