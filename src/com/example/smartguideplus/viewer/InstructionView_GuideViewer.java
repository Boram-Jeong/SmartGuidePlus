package com.example.smartguideplus.viewer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartguideplus.R;
import com.example.smartguideplus.clientmodel.GuideFileHelper;
import com.example.smartguideplus.util.Animations;
import com.example.smartguideplus.util.RecordHelper;


public class InstructionView_GuideViewer implements OnTouchListener{
	
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
	private int gidx;
	
	private ImageView imgV_Background;
	private ImageView[] imgV_Action;
	private LinearLayout layout_Instruction;
	private LinearLayout layout_ImageBackground;
	private LinearLayout layout_ImageAction;
	private LinearLayout layout_tvAction;
	private LinearLayout[] layout_tvNote;
	
	private TextView tv_currentScene;
	private TextView[] tv_Action;
	private TextView[] tv_Note;
	private Button btn_preScene;
	private Button btn_nxtScene;
	private Button btn_transMode;
	
	
	private WindowManager.LayoutParams mParams_Instruction;
	private WindowManager.LayoutParams mParams_Image;
	private WindowManager.LayoutParams mParams_ImageAction;
	private WindowManager.LayoutParams mParams_ImageAction1;
	private WindowManager.LayoutParams mParams_tvAction;
	private WindowManager.LayoutParams[] mParams_tvNote;
	private WindowManager mWindowManager;
	
	private int deviceWidth;
	private int deviceHeight;
	
	private Pos bef_Pos;
	private Pos curr_Pos;

	private GuideFileHelper GFH;
	
	
	private BitmapFactory.Options options;
	private Bitmap mBitmap;
	
	RecordHelper recorder;
	
	Animations anim;
	
	@SuppressWarnings("deprecation")
	public InstructionView_GuideViewer(Context context, WindowManager WindowManager, int gidx, Handler Handler) {
		this.context = context;
		this.mWindowManager = WindowManager;
		this.gidx = gidx;
		this.mHandler = Handler;
		
		Display display = mWindowManager.getDefaultDisplay();
		deviceWidth = display.getWidth();
		deviceHeight = display.getHeight();
		Log.d("display_", "w : "+deviceWidth);
		Log.d("display_", "h : "+deviceHeight);
		/*
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager.getDefaultDisplay().getMetrics(displayMetrics);
		deviceWidth = displayMetrics.getClass();
		deviceHeight = displayMetrics.heightPixels;*/
			
		initGuide();
		initAudio();
		initData();
		if(GFH.getSceneSize() != 0)
			PlayGuide(2);
	
	}
	
	private void initData() {
		// �ʱ�ȭ
		imgV_Background = new ImageView(this.context);
		imgV_Action = new ImageView[5];
		for(int i = 0; i < imgV_Action.length; i++) {
			imgV_Action[i] = new ImageView(this.context);
			imgV_Action[i].setTag("imgV_Action");
		}

		
		tv_Action = new TextView[5];
		tv_Note = new TextView[3];
		for(int i = 0; i < tv_Action.length; i++){
			tv_Action[i] = new TextView(this.context);
			tv_Action[i].setTag("tv_Action");
		}for(int i = 0; i < tv_Note.length; i++) {
			tv_Note[i] = new TextView(this.context);
			tv_Action[i].setTag("tv_Note"+i);
		}
		layout_ImageBackground = new LinearLayout(this.context);
		layout_ImageBackground.setBackgroundColor(Color.WHITE);
		layout_ImageBackground.setTag("layout_Image");
		
		layout_ImageAction = new LinearLayout(this.context);
		layout_ImageAction.setTag("layout_ImageAction");
		layout_tvAction = new LinearLayout(this.context);
		layout_tvAction.setTag("layout_tvAction");
		
		layout_tvNote = new LinearLayout[3];
		for(int i = 0; i < layout_tvNote.length; i++) {
			layout_tvNote[i] = new LinearLayout(this.context);
			layout_tvNote[i].setTag("layout_tvNote");
		}
		layout_Instruction = new LinearLayout(this.context);

		tv_currentScene = new TextView(this.context);
		tv_currentScene.setText("Guide Maker!  Scene#1");
		tv_currentScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
		tv_currentScene.setGravity(Gravity.CENTER);
		tv_currentScene.setTextColor(Color.WHITE);
		tv_currentScene.setBackgroundColor(Color.argb(200, 200, 200, 200));
		tv_currentScene.setShadowLayer(3, 0, 0, Color.BLACK);
		
		
		btn_preScene = new Button(this.context);
		btn_nxtScene = new Button(this.context);
		btn_transMode = new Button(this.context);
		btn_preScene.setTag("btn_preScene");
		btn_nxtScene.setTag("btn_nxtScene");
		btn_transMode.setTag("btn_transMode");
		
		
		layout_ImageAction.setOnTouchListener(this);
		layout_tvAction.setOnTouchListener(this);
		layout_ImageBackground.setOnTouchListener(this);
		layout_Instruction.setOnTouchListener(this);
		tv_currentScene.setOnTouchListener(this);
		for(int i = 0; i< tv_Note.length; i++)
			tv_Note[i].setOnTouchListener(this);
		btn_preScene.setOnTouchListener(this);
		btn_nxtScene.setOnTouchListener(this);
		btn_transMode.setOnTouchListener(this);
		for(int i = 0; i< imgV_Action.length; i++) {
			imgV_Action[i].setOnTouchListener(this);
			tv_Action[i].setOnTouchListener(this);
		}
		for(int i = 0; i< layout_tvNote.length; i++){
			layout_tvNote[i].setOnTouchListener(this);
			layout_tvNote[i].setOnTouchListener(this);
		}
		btn_preScene.setOnClickListener(btn_preScene_ClickListener);
		btn_nxtScene.setOnClickListener(btn_nxtScene_ClickListener);
		btn_transMode.setOnClickListener(btn_transMode_ClickListener);
		
		// layout ��ġ
		layout_ImageBackground.setBackgroundColor(Color.argb(150, 80, 80, 80));
		mParams_Image = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_Image.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(layout_ImageBackground, mParams_Image);
		
		
		mParams_ImageAction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_ImageAction.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(layout_ImageAction, mParams_ImageAction);
		
		mParams_ImageAction1 = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_ImageAction1.gravity = Gravity.CENTER | Gravity.CENTER;
		
		mParams_tvAction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_tvAction.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(layout_tvAction, mParams_tvAction);

		mParams_tvNote = new WindowManager.LayoutParams[3];
		for(int i  = 0; i<mParams_tvNote.length; i++){
			mParams_tvNote[i] = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.RGBA_8888);
			mParams_tvNote[i].gravity = Gravity.CENTER | Gravity.CENTER;
			mWindowManager.addView(layout_tvNote[i], mParams_tvNote[i]);
		}

		mParams_Instruction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mParams_Instruction.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(layout_Instruction, mParams_Instruction);
		
		////////////////////////////////////////////////////////////////////////////////////////////
		
		imgV_Background.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		options = new BitmapFactory.Options();
		

		/*tv_Action[0].setText("Touch!");
		tv_Action[1].setText("Long Touch!");
		tv_Action[2].setText("Slide!");
		tv_Action[3].setText("Menu!");
		tv_Action[4].setText("Back!");*/
		
		for(int i = 0; i< tv_Action.length; i++) {
			if(i < tv_Note.length)
			{
				tv_Note[i].setText("");
				tv_Note[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				tv_Note[i].setGravity(Gravity.CENTER);
				tv_Note[i].setTextColor(Color.BLACK);
				tv_Note[i].setBackgroundColor(Color.argb(180, 255, 255, 0));
				tv_Note[i].setShadowLayer(3, 0, 0, Color.WHITE);
				tv_Note[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
			}
			tv_Action[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
			tv_Action[i].setGravity(Gravity.CENTER);
			tv_Action[i].setTextColor(Color.BLACK);
			tv_Action[i].setShadowLayer(3, 0, 0, Color.WHITE);
			tv_Action[i].setBackgroundColor(Color.argb(130, 200, 200, 200));
			tv_Action[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}

		initAnimation();
		initAction();
		initNote();
		
		tv_currentScene.setText(Integer.toString(GFH.getCurrentSceneNum()+1));
		tv_currentScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		tv_currentScene.setGravity(Gravity.CENTER);
		tv_currentScene.setTextColor(Color.YELLOW);
		tv_currentScene.setBackgroundColor(Color.DKGRAY);
		tv_currentScene.setShadowLayer(3, 0, 0, Color.WHITE);
		
		btn_transMode.setText("[전환]");
		btn_transMode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_transMode.setTextColor(Color.WHITE);
		btn_transMode.setBackgroundColor(Color.BLACK);
		btn_transMode.setShadowLayer(3, 0, 0, Color.WHITE);
		
		btn_preScene.setText("<-");
		btn_preScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_preScene.setTextColor(Color.WHITE);
		btn_preScene.setBackgroundColor(Color.BLACK);
		btn_preScene.setShadowLayer(3, 0, 0, Color.WHITE);
		
		btn_nxtScene.setText("->");
		btn_nxtScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_nxtScene.setTextColor(Color.WHITE);
		btn_nxtScene.setBackgroundColor(Color.BLACK);
		btn_nxtScene.setShadowLayer(3, 0, 0, Color.WHITE);
		
		layout_ImageBackground.addView(imgV_Background);
		layout_Instruction.addView(tv_currentScene);
		layout_Instruction.addView(btn_transMode);
		layout_Instruction.addView(btn_preScene);
		layout_Instruction.addView(btn_nxtScene);
		
		
		
		bef_Pos = new Pos();
		curr_Pos = new Pos();
	}
	
	private void initGuide()
	{
		GFH = new GuideFileHelper();
		GFH.load(gidx);
		// load������ ���Ƿ� �ۼ��ϰ���.
	}

	private void initAudio() {
		recorder = new RecordHelper();
	}
	
	private void initNote() {
		for(int i = 0; i < tv_Note.length; i++) {
			tv_Note[i].setVisibility(View.GONE);
		}
		layout_tvAction.setGravity(Gravity.CENTER);
		for(int i = 0; i < tv_Note.length; i++) {
			layout_tvNote[i].addView(tv_Note[i]);
		}
	}
	
	private void initAction() {
		for(int i = 0; i < tv_Action.length; i++) {
			tv_Action[i].setVisibility(View.GONE);
			imgV_Action[i].setScaleType(ImageView.ScaleType.FIT_XY);
			imgV_Action[i].setVisibility(View.GONE);
		}
		imgV_Action[0].setBackgroundResource(R.drawable.touch);
		imgV_Action[1].setBackgroundResource(R.drawable.longtouch);
		imgV_Action[2].setBackgroundResource(R.drawable.slide_1);
		imgV_Action[3].setBackgroundResource(R.drawable.menu);
		imgV_Action[4].setBackgroundResource(R.drawable.back);

		layout_tvAction.setGravity(Gravity.CENTER);
		for(int i = 0; i < tv_Action.length; i++) {
			layout_tvAction.addView(tv_Action[i]);
			layout_ImageAction.addView(imgV_Action[i]);
		}
	}

	private void initAnimation() {
		anim = new Animations(this.context);
	}
	
	
	// 0 pre // 1 nxt // 2 start
	private void PlayGuide(int type) {
		if(type == 2) {
			setSceneNum();
			setBitmap(1);
			startAudio();
			startMemo();
			startAction();
		}else {
			if(GFH.isTransformable(type)) {
				stopAudio();
				GFH.setCurrentSceneNum(type);
				setSceneNum();
				setBitmap(type);
				startAudio();
				startMemo();
				startAction();
			}
		}
	}
	
	private void setSceneNum() {
		tv_currentScene.setText(Integer.toString(GFH.getCurrentSceneNum()+1));
	}
	
	private void startMemo() {
		
		for(int i = 0; i < 3; i++) {
			tv_Note[i].setVisibility(View.GONE);
		}
		
		for(int i = 0; i < 3; i++) {
			if(GFH.isDataInfo(i+2)) {
				tv_Note[i].setVisibility(View.VISIBLE);
				tv_Note[i].setText(GFH.getDataInfo(i+2).getText());
				mParams_tvNote[i].width = WindowManager.LayoutParams.WRAP_CONTENT ;
				mParams_tvNote[i].height = WindowManager.LayoutParams.WRAP_CONTENT;
				mParams_tvNote[i].x = GFH.getDataInfo(i+2).getRect().left;
				mParams_tvNote[i].y = GFH.getDataInfo(i+2).getRect().top;
				mWindowManager.updateViewLayout(layout_tvNote[i], mParams_tvNote[i]);		
			}
		}
	}
	private void startAction() {
		if(GFH.isActionMode()) {
			Rect rect = new Rect();

			for(int i = 0; i < 5; i++) {
				tv_Action[i].setVisibility(View.GONE);
				imgV_Action[i].setVisibility(View.GONE);
				anim.stopAnim_ImgV_action(imgV_Action[i]);
			}
			
			
			if(GFH.getAction().getType() == 0) {
				// touch
				tv_Action[0].setVisibility(View.VISIBLE);
				imgV_Action[0].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[0], 0);
				rect = GFH.getAction().getRect();
			}else if(GFH.getAction().getType() == 1) {
				// long touch
				tv_Action[1].setVisibility(View.VISIBLE);
				imgV_Action[1].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[1], 1);
				rect = GFH.getAction().getRect();
			}
			else if(GFH.getAction().getType() == 2) {
				// slide
				tv_Action[2].setVisibility(View.VISIBLE);
				imgV_Action[2].setVisibility(View.VISIBLE);
				Log.d("orient", ""+GFH.getAction().getOrientation());
				if(GFH.getAction().getOrientation() == 0) imgV_Action[2].setBackgroundResource(R.drawable.slide_1);
				else if(GFH.getAction().getOrientation() == 1) imgV_Action[2].setBackgroundResource(R.drawable.slide_2);
				else if(GFH.getAction().getOrientation() == 2) imgV_Action[2].setBackgroundResource(R.drawable.slide_3);
				else if(GFH.getAction().getOrientation() == 3) imgV_Action[2].setBackgroundResource(R.drawable.slide_4);
				
				int animNum = 2+GFH.getAction().getOrientation();
				anim.startAnim_ImgV_action_start(imgV_Action[2], animNum);
				rect = new Rect( 3*(deviceWidth/10), 3*(deviceHeight/10), 7*(deviceWidth/10), 7*(deviceHeight/10));
				
				mWindowManager.updateViewLayout(layout_ImageAction, mParams_ImageAction1);
			}
			else if(GFH.getAction().getType() == 3) {
				// menu
				tv_Action[3].setVisibility(View.VISIBLE);
				imgV_Action[3].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[3], 6);
				rect = new Rect( 1*(deviceWidth/10), 8*(deviceHeight/10), 2*(deviceWidth/10), 9*(deviceHeight/10));
			}
			else if(GFH.getAction().getType() == 4) {
				// back
				tv_Action[4].setVisibility(View.VISIBLE);
				imgV_Action[4].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[4], 7);
				rect = new Rect(8*(deviceWidth/10), 8*(deviceHeight/10), 9*(deviceWidth/10), 9*(deviceHeight/10));
			}


			 if(GFH.getAction().getType() != 2) {
				mParams_ImageAction.width = rect.width();
				mParams_ImageAction.height = rect.height();
				
				mParams_ImageAction.x = rect.left;
				mParams_ImageAction.y = rect.top;
				mWindowManager.updateViewLayout(layout_ImageAction, mParams_ImageAction);
				mParams_tvAction.width = WindowManager.LayoutParams.WRAP_CONTENT;
				mParams_tvAction.height = WindowManager.LayoutParams.WRAP_CONTENT;
				mParams_tvAction.x = rect.left;
				mParams_tvAction.y = rect.top;
				mWindowManager.updateViewLayout(layout_tvAction, mParams_tvAction);
			 }
		}
	}
	
	@SuppressLint("SdCardPath")
	private void setBitmap(int trans) {
		if(GFH.isDataInfo(0)) {
			String filePath = "/sdcard/SmartGuidePlus/";
			filePath = filePath + Integer.toString(gidx) + "/" + Integer.toString(gidx) + "/Image/"+GFH.getDataInfo(0).getFileName()+".png";
			Log.d("ImagePath", filePath);
			mBitmap = BitmapFactory.decodeFile(filePath, options);
			imgV_Background.setImageBitmap(mBitmap);
		}
		
		if(trans == 1) anim.startAnim_ImgV_background_start(imgV_Background);
		else if(trans == 0)anim.startAnim_ImgV_background_end(imgV_Background);
	}
	
	@SuppressLint("SdCardPath")
	private void startAudio() {
		if(GFH.isDataInfo(1)) {
			recorder.playAudio(GFH.getDataInfo(1).getFileName(), gidx);
		}
	}
	
	private void stopAudio() {
		if(GFH.isDataInfo(1)) {
			recorder.stopAudio();
		}
	}
	
	public void setVisibility(int visibility) {
		if(visibility == View.GONE) {
			layout_ImageBackground.setVisibility(View.GONE);
			imgV_Background.setVisibility(View.GONE);
			layout_Instruction.setVisibility(View.GONE);
			layout_ImageAction.setVisibility(View.GONE);
			layout_tvAction.setVisibility(View.GONE);
			for(int i = 0; i< layout_tvNote.length; i++)
				layout_tvNote[i].setVisibility(View.GONE);
		}else if(visibility == View.VISIBLE) {
			layout_ImageBackground.setVisibility(View.VISIBLE);
			imgV_Background.setVisibility(View.VISIBLE);
			layout_Instruction.setVisibility(View.VISIBLE);
			layout_ImageAction.setVisibility(View.VISIBLE);
			layout_tvAction.setVisibility(View.VISIBLE);
			for(int i = 0; i< layout_tvNote.length; i++)
				layout_tvNote[i].setVisibility(View.VISIBLE);
		}
	}
	
	public void setInstructionViewPos(Pos pos) {
		mParams_Instruction.x += (int) pos.x;
		mParams_Instruction.y += (int) pos.y;
		mWindowManager.updateViewLayout(layout_Instruction, mParams_Instruction);
	}
	

	
	long st_time;
	long end_time;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {


		if( ( 
				("btn_preScene".equals( v.getTag())) ||
				("btn_transMode".equals( v.getTag())) ||
				("btn_nxtScene".equals( v.getTag())) ||
				("btn_exitScene".equals( v.getTag()))
				)) 
		{
			Log.d("testtouch", ":"+v.getTag());
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
		}else {
			curr_Pos.x = event.getRawX();
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				bef_Pos.x = curr_Pos.x;
				break;
			case MotionEvent.ACTION_UP:
				float deltaX = curr_Pos.x-bef_Pos.x;
				if(deltaX < -200) { // next
					PlayGuide(1);
				}else if(deltaX > 200){ // before
					// TODO Auto-generated method stub
					PlayGuide(0);
				}
				break;
			}
		}
		// TODO Auto-generated method stub
		return false;
	}

	View.OnClickListener btn_preScene_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			PlayGuide(0);
		}};

	View.OnClickListener btn_nxtScene_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			PlayGuide(1);
		}};

	View.OnClickListener btn_transMode_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage(GuideViewerService.MSG_Action);
			mHandler.sendMessage(msg);
			// 
		}};

	View.OnClickListener btn_exit_ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// ���̵� �� �����ϰ� ��Ƽ��Ƽ�� ���� �޽���
	}};

	public void Remove() {
		// TODO Auto-generated method stub
		if(layout_Instruction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_Instruction);
            layout_Instruction = null;
        }Log.d("testdd", "testdd");
		if(layout_ImageBackground != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_ImageBackground);
            layout_ImageBackground = null;
        }Log.d("testdd", "testdd");
		if(layout_ImageAction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_ImageAction);
            layout_ImageAction = null;
        }Log.d("testdd", "testdd");
		if(layout_tvAction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_tvAction);
            layout_tvAction = null;
        }Log.d("testdd", "testdd");
		
		for(int i = 0; i < layout_tvNote.length; i++)
			if(layout_tvNote[i] != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
	        {
	            mWindowManager.removeView(layout_tvNote[i]);
	            layout_tvNote[i] = null;
	        }
		Log.d("testdd", "testdd");
	}
}


