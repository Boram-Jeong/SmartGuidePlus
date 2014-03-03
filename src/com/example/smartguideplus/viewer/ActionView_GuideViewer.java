package com.example.smartguideplus.viewer;



import com.example.smartguideplus.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class ActionView_GuideViewer implements OnTouchListener{

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
	
	private LinearLayout layout;
	private Button btn_transMode;
	private Button btn_exit;
	
	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;
	
	private Pos bef_Pos;
	private Pos curr_Pos;
	
	public ActionView_GuideViewer(Context context, WindowManager WindowManager, Handler Handler) {
		this.context = context;
		this.mWindowManager = WindowManager;
		this.mHandler = Handler;
		initData();
	}
	
	private void initData() {
		// �ʱ�ȭ
		layout = new LinearLayout(this.context);
	
		
		btn_transMode = new Button(this.context);
		btn_exit = new Button(this.context);
		
		layout.setOnTouchListener(this);
		btn_transMode.setOnTouchListener(this);
		btn_exit.setOnTouchListener(this);
		
		
		btn_transMode.setOnClickListener(btn_transMode_ClickListener);
		btn_exit.setOnClickListener(btn_exit_ClickListener);
		// layout ��ġ
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.LEFT | Gravity.TOP;
			
		mWindowManager.addView(layout, mParams);
		
		
		btn_transMode.setText("[전환]");
		btn_transMode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_transMode.setTextColor(Color.WHITE);
		btn_transMode.setBackgroundColor(Color.BLACK);
		btn_transMode.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_exit.setText("[X]");
		btn_exit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_exit.setTextColor(Color.WHITE);
		btn_exit.setBackgroundColor(Color.BLACK);
		btn_exit.setShadowLayer(3, 0, 0, Color.WHITE);
		
		layout.addView(btn_transMode);
		layout.addView(btn_exit);
		
		bef_Pos = new Pos();
		curr_Pos = new Pos();
	}
	
	public void setVisibility(int visibility) {
		if(visibility == View.GONE) {
			layout.setVisibility(View.GONE);
			btn_transMode.setVisibility(View.GONE);
			btn_exit.setVisibility(View.GONE);
		}
		else if(visibility == View.VISIBLE) {
			layout.setVisibility(View.VISIBLE);
			btn_transMode.setVisibility(View.VISIBLE);
			btn_exit.setVisibility(View.VISIBLE);
		}
	}

	
	public void setInstructionViewPos(Pos pos) {
		mParams.x += (int) pos.x;
		mParams.y += (int) pos.y;
		mWindowManager.updateViewLayout(layout, mParams);
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
			Message msg = mHandler.obtainMessage(GuideViewerService.MSG_Guide);
			mHandler.sendMessage(msg);
			// �ڵ鷯�޽���
		}};

	View.OnClickListener btn_exit_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Message msg = mHandler.obtainMessage(GuideViewerService.MSG_StopService);
			mHandler.sendMessage(msg);
		}};

	public void Remove() {
		// TODO Auto-generated method stub
		if(layout != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout);
            layout = null;
        }
	}
}
