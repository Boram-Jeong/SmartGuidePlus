package com.example.smartguideplus.maker;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.smartguideplus.guidemaker.addGuideInfoActivity;
import com.example.smartguideplus.util.Data;
import com.example.smartguideplus.util.ZipUtils;



public class GuideMakerService extends Service {
	public static final int MSG_Guide = 1;
    public static final int MSG_Action = 2;
    public static final int MSG_StopService = 3;
    public static final int MSG_AddScene = 4;
    public static final int MSG_SaveGuide = 5;
    public static final int MSG_CAPTURE = 6;
    public static final int MSG_VIEWER = 7;
    public static final int MSG_DELETE = 8;
    
	private WindowManager mWindowManager;

	private InstructionView_GuideMaker IV;
	private ActionView_GuideMaker AV;
	private Viewer_GuideMaker VG;

	// ��� ����
	public enum Mode{ Guide, Action, Viewer };
	static Mode currentMode;

	int gidx;
	
	String name;
	String model;
	String os;
	String desc;
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		gidx = (int)Data.gidx;
		//gidx=1;
		//
		//request부분
		//
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		Data.activity.finish();
		// InstructionView ��
		IV = new InstructionView_GuideMaker(getApplicationContext(), this.mWindowManager, gidx, mHandler);
		// ActionView ��

		AV = new ActionView_GuideMaker(getApplicationContext(), this.mWindowManager, mHandler);
		VG = new Viewer_GuideMaker(getApplicationContext(), mWindowManager, gidx, mHandler);
		currentMode = Mode.Action;
		ModeSet();	
		
		return START_NOT_STICKY ;
	}

	
	
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler()  {
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_VIEWER :

				VG = new Viewer_GuideMaker(getApplicationContext(), mWindowManager, gidx, mHandler);
				currentMode = Mode.Viewer;
				ModeSet();
				break;
			case MSG_DELETE:
				int sceneNum = VG.getCurrentSceneNum();
				IV.deleteScene(VG.getCurrentSceneNum());
				//VG.setScene(sceneNum);
				currentMode = Mode.Guide;
				ModeSet();
				
				break;
			case MSG_Guide:
				if( IV.getCurrentSceneNum() == -1) {
					Toast.makeText(getApplicationContext(),"씬 등록을 먼저 하세요.",Toast.LENGTH_SHORT).show();
				}else{
					currentMode = Mode.Guide;
					ModeSet();
				}
				break;
			case MSG_Action:
				currentMode = Mode.Action;
				ModeSet();
				break;
			case MSG_StopService:
				StopGuide();
				break;
			case MSG_CAPTURE:
				currentMode = Mode.Guide;
				ModeSet();
				break;
			case MSG_AddScene:
				// ó�� ���̵带 ���� ���̵带 ���ϰ� �������� �ʴ´�.
				if( IV.getCurrentSceneNum() == -1) {
					AV.setVisibility(View.GONE);
					AddScene();
					currentMode = Mode.Guide;
					ModeSet();
				}
				// ���� ���̵���ʹ� ���� ���̵带 �����ϰ�, ���ο� ���̵带 ���Ѵ�.
				else {
					
					if(IV.isGuideCaptureInput()) {
						if(IV.isGuideDataInput()) {
							IV.setViewerAvaliable();
							SaveGuide();
							AV.setVisibility(View.GONE);
							AddScene();
							currentMode = Mode.Guide;
							ModeSet();
						}else {
							
							Toast.makeText(getApplicationContext(),"등록한 씬을 먼저 완성하세요.",Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(getApplicationContext(),"캡쳐는 필수항목입니다.",Toast.LENGTH_SHORT).show();
					}
				}
				break;
				
			case MSG_SaveGuide:
				if( IV.getCurrentSceneNum() != -1 && IV.isGuideCaptureInput()) {
					SaveGuide();
					zip();
					startApp();
				}else
					Toast.makeText(getApplicationContext(),"등록한 씬을 먼저 완성하세요.",Toast.LENGTH_SHORT).show();
				break;
			}
				
		}
	};
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("maker", "oncreate");
	}

	private void startApp() {
		Log.d("testup2", ""+gidx);
		Intent intent = new Intent(GuideMakerService.this, addGuideInfoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
	
	
	private void ModeSet() {
		if(currentMode == Mode.Guide) {
			IV.setVisibility(View.VISIBLE);
			AV.setVisibility(View.GONE);
			VG.setVisibility(View.GONE);
		}else if(currentMode == Mode.Action) {
			IV.setVisibility(View.GONE);
			AV.setVisibility(View.VISIBLE);
			VG.setVisibility(View.GONE);
		}else if(currentMode == Mode.Viewer) {
			IV.setVisibility(View.GONE);
			AV.setVisibility(View.GONE);
			VG.setVisibility(View.VISIBLE);
		}
	}
	
	private void StopGuide() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	private void AddScene() {
		IV.addScene();
	}
	
	private void SaveGuide() {
		IV.saveGuide(0);
		
	}
	
	
	
	private void zip(){
		String sourcePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx;
		String fileName = "/sdcard/SmartGuidePlus/"+gidx +"/"+gidx+".zip";
		
		try {
			ZipUtils.zip(sourcePath, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 @Override
    public void onDestroy() {
        super.onDestroy();
        IV.Remove();
        AV.Remove();
        VG.Remove();
    }
}