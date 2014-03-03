package com.example.smartguideplus.maker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionView_GuideMaker implements OnTouchListener {
	public class Pos {
		float x;
		float y;
		public Pos(){}
		public Pos(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private Context context;
	private Handler mHandler;
	
	
	private LinearLayout layout_instructionBar;
	private LinearLayout layout_instruction;
	private Button btn_transMode;
	private Button btn_exit;
	private Button btn_addScene;
	private Button btn_saveGuide;
	
	private WindowManager.LayoutParams mParams_instructionBar;
	private WindowManager.LayoutParams mParams_instruction;
	private WindowManager mWindowManager;
	
	private TextView txtV_SceneInfo;
	
	private Pos bef_Pos;
	private Pos curr_Pos;
	
	public ActionView_GuideMaker(Context context, WindowManager WindowManager, Handler Handler) {
		this.context = context;
		this.mWindowManager = WindowManager;
		this.mHandler = Handler;
		initData();
	}
	
	private void initData() {
		// �ʱ�ȭ
		layout_instruction = new LinearLayout(this.context);
		layout_instructionBar = new LinearLayout(this.context);
		
		btn_transMode = new Button(this.context);
		btn_exit = new Button(this.context);
		btn_addScene = new Button(this.context);
		btn_saveGuide = new Button(this.context);
		
		layout_instruction.setOnTouchListener(this);
		btn_transMode.setOnTouchListener(this);
		btn_exit.setOnTouchListener(this);
		btn_addScene.setOnTouchListener(this);
		btn_saveGuide.setOnTouchListener(this);
		
		btn_transMode.setOnClickListener(btn_transMode_ClickListener);
		btn_exit.setOnClickListener(btn_exit_ClickListener);
		btn_addScene.setOnClickListener(btn_addScene_ClickListener);
		btn_saveGuide.setOnClickListener(btn_saveGuide_ClickListener);
		// layout ��ġ
		mParams_instruction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams_instruction.gravity = Gravity.LEFT | Gravity.TOP;
		
		mParams_instructionBar = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams_instructionBar.gravity = Gravity.LEFT | Gravity.TOP;
			
		
		mWindowManager.addView(layout_instruction, mParams_instruction);
		layout_instruction.setOrientation(LinearLayout.VERTICAL);
		///////////////////////////////////////////////////////////////////
		txtV_SceneInfo = new TextView(this.context);
		txtV_SceneInfo.setText("Action!");
		txtV_SceneInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		txtV_SceneInfo.setGravity(Gravity.CENTER);
		txtV_SceneInfo.setTextColor(Color.WHITE);
		txtV_SceneInfo.setBackgroundColor(Color.argb(200, 200, 200, 200));
		txtV_SceneInfo.setShadowLayer(3, 0, 0, Color.BLACK);
		///////////////////////////////////////////////////////////////////
		layout_instruction.addView(txtV_SceneInfo);
		layout_instruction.addView(layout_instructionBar, mParams_instructionBar);
		
		btn_transMode.setText("[전환]");
		btn_transMode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_transMode.setTextColor(Color.WHITE);
		btn_transMode.setBackgroundColor(Color.BLACK);
		
		btn_addScene.setText("[씬 추가]");
		btn_addScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_addScene.setTextColor(Color.WHITE);
		btn_addScene.setBackgroundColor(Color.BLACK);
		
		btn_saveGuide.setText("[가이드 저장]");
		btn_saveGuide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_saveGuide.setTextColor(Color.WHITE);
		btn_saveGuide.setBackgroundColor(Color.BLACK);
		
		btn_exit.setText("[X]");
		btn_exit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_exit.setTextColor(Color.WHITE);
		btn_exit.setBackgroundColor(Color.BLACK);
		
		layout_instructionBar.addView(btn_transMode);
		layout_instructionBar.addView(btn_addScene);
		layout_instructionBar.addView(btn_saveGuide);
		layout_instructionBar.addView(btn_exit);
		
		bef_Pos = new Pos();
		curr_Pos = new Pos();
	}
	
	public void setVisibility(int visibility) {
		if(visibility == View.GONE) {
			layout_instruction.setVisibility(View.GONE);
		}
		else if(visibility == View.VISIBLE) {
			layout_instruction.setVisibility(View.VISIBLE);
		}
	}

	
	public void setInstructionViewPos(Pos pos) {
		mParams_instruction.x += (int) pos.x;
		mParams_instruction.y += (int) pos.y;
		mWindowManager.updateViewLayout(layout_instruction, mParams_instruction);
	}
	
	
	long st_time;
	long end_time;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		curr_Pos.x = event.getRawX();
		curr_Pos.y = event.getRawY();
		
		v.setClickable(true);
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			st_time = System.currentTimeMillis();
			bef_Pos.x = curr_Pos.x;
			bef_Pos.y = curr_Pos.y;
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaX = curr_Pos.x-bef_Pos.x;
			float deltaY = curr_Pos.y-bef_Pos.y;
			setInstructionViewPos(new Pos(deltaX, deltaY));
			bef_Pos.x = curr_Pos.x;
			bef_Pos.y = curr_Pos.y;
			break;	
		case MotionEvent.ACTION_UP:
			end_time = System.currentTimeMillis();
			if(end_time - st_time > 100) v.setClickable(false);
			break;	
		}
		// TODO Auto-generated method stub
		return false;
	}

	View.OnClickListener btn_transMode_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_Guide);
			mHandler.sendMessage(msg);
			// �ڵ鷯�޽���
		}};

	View.OnClickListener btn_exit_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// ���̵� �� �����ϰ� ��Ƽ��Ƽ�� ���� �޽���
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_StopService);
			mHandler.sendMessage(msg);
		}};
		
	View.OnClickListener btn_addScene_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_AddScene);
			mHandler.sendMessage(msg);
		}};
		
	View.OnClickListener btn_saveGuide_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_SaveGuide);
			mHandler.sendMessage(msg);
		}};

	public void Remove() {
		// TODO Auto-generated method stub
		 if(layout_instruction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
	        {
	            mWindowManager.removeView(layout_instruction);
	            layout_instruction = null;
	        }
	}
		
}