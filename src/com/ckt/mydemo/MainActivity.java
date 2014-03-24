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

	//����log�����¼��ַ�ʽ��
	//1.��logcat������filter����By log tagͳһ���ո������е�TAG��д�����ɹ�����Ӧtag��log
	//2.��By Application name ��дΪ�������ܹ��˳����и��ð���ص�log
	//3.logcat������filter�÷������Ϸ�������
	//4.�ڳ�������дlogʱ�����ڿ�ͷ�����������ֲ���ͳһ�ĸ�ʽ���硰--> ������logcat�����������롰--> ��������������Ӧlog
	//5.ͨ��log�������
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
				//�����ť�����µ��߳�
				new Thread(new MyThread()).start();
			}

		});

		//����APP������Service
		Intent intent = new Intent(MainActivity.this, MyService.class);
		bindService(intent, MainActivity.this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//���ö�̬����ע��㲥
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
		//Service���������sendCount��ÿ��5��ͨ��JNI��ȡһ���ַ���
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
		//�����ť���EditText�л�ȡ�û������ַ�����Ϊ�βδ��뱾�ط�����JNI���յ��ַ����󷢻�"native_set_to_jni success"��ͨ��Toast��ʾ����
		String mStr = NativeMethod.setParamsToJNI(getContent());
		if (mStr == null) {
			mStr = "JNI failed";
		}
		//���ø÷�������log�д�ӡ��JAVA�Ķ�ջ��Ϣ
		Log.d(TAG, Log.getStackTraceString(new Throwable()));
		Toast.makeText(content, mStr, Toast.LENGTH_SHORT).show();
	}

	/******************************************************************************************/

	/************************************* define inner class ***********************************/
	//�Զ����߳���ͨ��handler������Ϣ
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

	//�Զ���handler���˳���Ϣ��Я���Ĳ�������ʶ�𣬲������㲥
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
