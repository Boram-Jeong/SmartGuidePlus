package com.example.smartguideplus.maker;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartguideplus.R;
import com.example.smartguideplus.clientmodel.GuideFileHelper;
import com.example.smartguideplus.util.Animations;
import com.example.smartguideplus.util.RecordHelper;


public class Viewer_GuideMaker implements OnTouchListener{
	
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
	private TextView[] tv_Note;
	
	
	private WindowManager.LayoutParams mParams_Instruction;
	private WindowManager.LayoutParams mParams_Image;
	private WindowManager.LayoutParams mParams_ImageAction;
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
	public Viewer_GuideMaker(Context context, WindowManager WindowManager, int gidx, Handler Handler) {
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
			
		initViewer();
	}
	
	public void initViewer() {
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

		
		tv_Note = new TextView[3];
		for(int i = 0; i < tv_Note.length; i++) {
			tv_Note[i] = new TextView(this.context);
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
		
		
		
		layout_ImageAction.setOnTouchListener(this);
		layout_tvAction.setOnTouchListener(this);
		layout_ImageBackground.setOnTouchListener(this);
		layout_Instruction.setOnTouchListener(this);
		tv_currentScene.setOnTouchListener(this);
		for(int i = 0; i< tv_Note.length; i++)
			tv_Note[i].setOnTouchListener(this);
		for(int i = 0; i< imgV_Action.length; i++) {
			imgV_Action[i].setOnTouchListener(this);
		}
		for(int i = 0; i< layout_tvNote.length; i++){
			layout_tvNote[i].setOnTouchListener(this);
			layout_tvNote[i].setOnTouchListener(this);
		}
		
		// layout ��ġ
		mParams_Image = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_Image.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowManager.addView(layout_ImageBackground, mParams_Image);
		
		
		mParams_ImageAction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_ImageAction.gravity = Gravity.CENTER | Gravity.CENTER;
		mWindowManager.addView(layout_ImageAction, mParams_ImageAction);


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
		
		
		
		for(int i = 0; i< tv_Note.length; i++) {
			tv_Note[i].setText("");
			tv_Note[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			tv_Note[i].setGravity(Gravity.CENTER);
			tv_Note[i].setTextColor(Color.BLACK);
			tv_Note[i].setBackgroundColor(Color.argb(180, 255, 255, 0));
			tv_Note[i].setShadowLayer(3, 0, 0, Color.WHITE);
			tv_Note[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
		}

		initAnimation();
		initAction();
		initNote();
		
		tv_currentScene.setText(Integer.toString(GFH.getCurrentSceneNum()+1));
		tv_currentScene.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
		tv_currentScene.setGravity(Gravity.CENTER);
		tv_currentScene.setTextColor(Color.YELLOW);
		tv_currentScene.setBackgroundColor(Color.DKGRAY);
		tv_currentScene.setShadowLayer(3, 0, 0, Color.WHITE);
		
		layout_ImageBackground.addView(imgV_Background);
		layout_Instruction.addView(tv_currentScene);
		
		
		
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
		for(int i = 0; i < imgV_Action.length; i++) {
			imgV_Action[i].setScaleType(ImageView.ScaleType.FIT_XY);
			imgV_Action[i].setVisibility(View.GONE);
		}
		imgV_Action[0].setBackgroundResource(R.drawable.touch);
		imgV_Action[1].setBackgroundResource(R.drawable.longtouch);
		imgV_Action[2].setBackgroundResource(R.drawable.slide_1);
		imgV_Action[3].setBackgroundResource(R.drawable.menu);
		imgV_Action[4].setBackgroundResource(R.drawable.back);

		layout_tvAction.setGravity(Gravity.CENTER);
		for(int i = 0; i < imgV_Action.length; i++) {
			layout_ImageAction.addView(imgV_Action[i]);
		}
	}

	private void initAnimation() {
		anim = new Animations(this.context);
	}
	
	
	// 0 pre // 1 nxt // 2 start // 3 current
	private void PlayGuide(int type) {
		if(type == 2) {
			setSceneNum();
			setBitmap(1);
			startAudio();
			startMemo();
			startAction();
		}else if(type == 3) {
			if(GFH.isTransformable(type)) {
				stopAudio();
				setSceneNum();
				setBitmap(type);
				startAudio();
				startMemo();
				startAction();
			}
		}
		else {
			if(GFH.isTransformable(type)) {
				stopAudio();
				GFH.setCurrentSceneNum(type);
				setSceneNum();
				setBitmap(type);
				startAudio();
				startMemo();
				startAction();
			}else if(!GFH.isTransformable(type) && type ==0){
				Message msg = mHandler.obtainMessage(GuideMakerService.MSG_Guide);
				mHandler.sendMessage(msg);
			}
		}
	}
	
	private void setSceneNum() {
		tv_currentScene.setText("씬번호#"+ Integer.toString(GFH.getCurrentSceneNum()+1));
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

			for(int i = 0; i < imgV_Action.length; i++) {
				imgV_Action[i].setVisibility(View.GONE);
				anim.stopAnim_ImgV_action(imgV_Action[i]);
			}


			if(GFH.getAction().getType() == 0) {
				// touch
				imgV_Action[0].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[0], 0);
				rect = GFH.getAction().getRect();
			}else if(GFH.getAction().getType() == 1) {
				// long touch
				imgV_Action[1].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[1], 1);
				rect = GFH.getAction().getRect();
			}
			else if(GFH.getAction().getType() == 2) {
				// slide
				imgV_Action[2].setVisibility(View.VISIBLE);
				Log.d("orient", ""+GFH.getAction().getOrientation());
				if(GFH.getAction().getOrientation() == 0) imgV_Action[2].setBackgroundResource(R.drawable.slide_2);
				else if(GFH.getAction().getOrientation() == 1) imgV_Action[2].setBackgroundResource(R.drawable.slide_4);
				else if(GFH.getAction().getOrientation() == 2) imgV_Action[2].setBackgroundResource(R.drawable.slide_3);
				else if(GFH.getAction().getOrientation() == 3) imgV_Action[2].setBackgroundResource(R.drawable.slide_1);
				int animNum = 2+GFH.getAction().getOrientation();
				anim.startAnim_ImgV_action_start(imgV_Action[2], animNum);
				rect = new Rect( 3*(deviceWidth/10), 3*(deviceHeight/10), 7*(deviceWidth/10), 7*(deviceHeight/10));
			}
			else if(GFH.getAction().getType() == 3) {
				// menu
				imgV_Action[3].setVisibility(View.VISIBLE);
				anim.startAnim_ImgV_action_start(imgV_Action[3], 6);
				rect = new Rect( 1*(deviceWidth/10), 8*(deviceHeight/10), 2*(deviceWidth/10), 9*(deviceHeight/10));
			}
			else if(GFH.getAction().getType() == 4) {
				// back
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
			//tv_currentScene.setVisibility(View.GONE);
			/*btn_preScene.setVisibility(View.GONE);
			btn_nxtScene.setVisibility(View.GONE);
			btn_transMode.setVisibility(View.GONE);
			btn_exit.setVisibility(View.GONE);*/
			
			layout_ImageAction.setVisibility(View.GONE);
			layout_tvAction.setVisibility(View.GONE);
			for(int i = 0; i < imgV_Action.length; i++) {
				imgV_Action[i].setVisibility(View.GONE);
				if(i <3){
					layout_tvNote[i].setVisibility(View.GONE);
					tv_Note[i].setVisibility(View.GONE);
				}
			}
		}else if(visibility == View.VISIBLE) {
			layout_ImageBackground.setVisibility(View.VISIBLE);
			imgV_Background.setVisibility(View.VISIBLE);
			layout_Instruction.setVisibility(View.VISIBLE);
			/*tv_currentScene.setVisibility(View.VISIBLE);
			btn_preScene.setVisibility(View.VISIBLE);
			btn_nxtScene.setVisibility(View.VISIBLE);
			btn_transMode.setVisibility(View.VISIBLE);
			btn_exit.setVisibility(View.VISIBLE);*/
			layout_ImageAction.setVisibility(View.VISIBLE);
			layout_tvAction.setVisibility(View.VISIBLE);
			for(int i = 0; i < imgV_Action.length; i++) {
				imgV_Action[i].setVisibility(View.VISIBLE);
				if(i <3){
					layout_tvNote[i].setVisibility(View.VISIBLE);
					tv_Note[i].setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	public void setInstructionViewPos(Pos pos) {
		mParams_Instruction.x += (int) pos.x;
		mParams_Instruction.y += (int) pos.y;
		mWindowManager.updateViewLayout(layout_Instruction, mParams_Instruction);
	}
	
	public void setScene(int sceneNum) {
		Message msg = mHandler.obtainMessage(GuideMakerService.MSG_VIEWER);
		mHandler.sendMessage(msg);
	}
	
	
	public int getCurrentSceneNum() {
		return GFH.getCurrentSceneNum();
	}

	
	long st_time;
	long end_time;
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		curr_Pos.x = event.getRawX();
		curr_Pos.y = event.getRawY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			bef_Pos.x = curr_Pos.x;
			bef_Pos.y = curr_Pos.y;
			break;
		case MotionEvent.ACTION_UP:
			float deltaX = curr_Pos.x-bef_Pos.x;
			float deltaY = curr_Pos.y-bef_Pos.y;
			
			if(deltaX < -200) { // next
				PlayGuide(1);
			}else if(deltaX > 200){ // before
				// TODO Auto-generated method stub
				PlayGuide(0);
			}
			
			if(deltaY > 300) { // next
				if(GFH.getSceneSize() > 1) {
					Message msg = mHandler.obtainMessage(GuideMakerService.MSG_DELETE);
					mHandler.sendMessage(msg);
				}else 
					Toast.makeText(this.context,"더이상 삭제 할 수 없습니다.",Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
		
		
		// TODO Auto-generated method stub
		return false;
	}

	
	
	
	public void Remove() {
		// TODO Auto-generated method stub
		if(layout_Instruction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_Instruction);
            layout_Instruction = null;
        }
		if(layout_ImageBackground != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_ImageBackground);
            layout_ImageBackground = null;
        }
		if(layout_ImageAction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_ImageAction);
            layout_ImageAction = null;
        }
		if(layout_tvAction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
        {
            mWindowManager.removeView(layout_tvAction);
            layout_tvAction = null;
        }
	}

}


