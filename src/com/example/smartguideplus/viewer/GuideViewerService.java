package com.example.smartguideplus.viewer;

import com.example.smartguideplus.guidemaker.addGuideInfoActivity;
import com.example.smartguideplus.maker.GuideMakerService;
import com.example.smartguideplus.mylist.DownloadGuideDetailActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class GuideViewerService extends Service{

	public static final int MSG_Guide = 1;
    public static final int MSG_Action = 2;
    public static final int MSG_StopService = 3;
    
	private WindowManager mWindowManager;

	private InstructionView_GuideViewer IV;
	private ActionView_GuideViewer AV;

	// ��� ����
	public enum Mode{ Guide, Action };
	static Mode currentMode;

	int gidx;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub


		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		gidx = intent.getIntExtra("gidx", 0);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		// InstructionView ��
		IV = new InstructionView_GuideViewer(getApplicationContext(), this.mWindowManager, gidx, mHandler);
		// ActionView ��

		AV = new ActionView_GuideViewer(getApplicationContext(), this.mWindowManager, mHandler);

		currentMode = Mode.Guide;
		ModeSet();		
		return START_NOT_STICKY ;
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_Guide:
				currentMode = Mode.Guide;
				ModeSet();
				break;
			case MSG_Action:
				currentMode = Mode.Action;
				ModeSet();
				break;
			case MSG_StopService:
				startApp();
				break;
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void StopGuide() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	private void startApp() {
		Log.d("testup2", ""+gidx);
		Intent intent = new Intent(GuideViewerService.this, DownloadGuideDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
	private void ModeSet() {
		if(currentMode == Mode.Guide) {
			IV.setVisibility(View.VISIBLE);
			AV.setVisibility(View.GONE);
		}else if(currentMode == Mode.Action) {
			IV.setVisibility(View.GONE);
			AV.setVisibility(View.VISIBLE);
		}
	}
	 @Override
	    public void onDestroy() {
	        super.onDestroy();
	        /*
	        IV.Remove();
	        AV.Remove();*/
	    }
}
