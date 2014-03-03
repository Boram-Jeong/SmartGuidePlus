package com.example.smartguideplus;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	// �쒕퉬�ㅼ����듭떊���꾪븳 �꾨뱶
	protected static Messenger m_writeMsger = null;

	protected boolean m_bind = false;
	private final int REGISTRATION_ID = 0;
	// 諛붿씤��愿�젴 �꾨뱶
	private Messenger m_readMsger = null;

	private Handler m_handle_AppToSvc = null;

	@Override
	protected void onError(Context context, String error) {
		// TODO Auto-generated method stub
		Log.d("@@@@", "onError : " + error);

	}

	@Override
	protected void onMessage(Context context, Intent message) {
		// TODO Auto-generated method stub
		if (message == null)
			return;
		final String sender = message.getStringExtra("sender");

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runList = am.getRunningTasks(10);
		ComponentName name = runList.get(0).topActivity;
		String className = name.getClassName();
		boolean isAppRunning = false;
		// <uses-permission android:name="android.permission.GET_TASKS" /> 二쇱뼱�쇳븿
		if (className.contains("com.example.smartguideplus")) {
			isAppRunning = true;
		}

		if (isAppRunning == true) {
			// �깆씠 �ㅽ뻾以묒씪 寃쎌슦 //���덈쑉吏�!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			Toast.makeText(getApplicationContext(), "�대� 媛�씠�쒓� �ㅽ뻾以묒엯�덈떎.",
					Toast.LENGTH_LONG).show();

		} else {
			// �깆씠 �ㅽ뻾以묒씠 �꾨땺 ��
			MainActivity.mHandler.post(new Runnable() {
				public void run() {
					showNotify(sender);
				}
			});
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		// TODO Auto-generated method stub
		Log.d("@@@@", "onRegister : " + regId);
		// prepareIPC(regId);
	}

	public void prepareIPC(final String regId) {
		Log.i("OTGIT", "�쒕퉬���듭떊 以�퉬 �쒖옉");
		m_handle_AppToSvc = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d("�ш린源뚯�", "�붿뜾");
				Message sendMsg = Message.obtain();
				sendMsg.what = REGISTRATION_ID;
				sendMsg.obj = regId;
				try {
					m_writeMsger.send(sendMsg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				super.handleMessage(msg);
			}
		};

		m_readMsger = new Messenger(m_handle_AppToSvc);
		Log.i("OTGIT", "�쒕퉬���듭떊 以�퉬 �꾨즺");
	}

	@Override
	protected void onUnregistered(Context context, String regId) {

		// TODO Auto-generated method stub
		Log.d("@@@@", "onUnregister : " + regId);
	}

	private void showNotify(String sender) {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int _ID = 0xffff0001;
		Notification notification = null;
		notification = new Notification(R.drawable.ic_launcher, "�ㅻ쭏�멸��대뱶+", System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(this, "�ㅻ쭏�멸��대뱶+", sender
				+ "�섏씠 媛�씠�쒕� �꾩넚�덉뒿�덈떎.", contentIntent);
		nm.notify(_ID, notification);
		// nm.cancel(0);
	}

}