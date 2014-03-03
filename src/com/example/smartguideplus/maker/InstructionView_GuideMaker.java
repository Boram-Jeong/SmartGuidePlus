package com.example.smartguideplus.maker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartguideplus.R;
import com.example.smartguideplus.clientmodel.GuideFileHelper;
import com.example.smartguideplus.util.Animations;
import com.example.smartguideplus.util.DrawingView;
import com.example.smartguideplus.util.RecordHelper;
import com.example.smartguideplus.util.ScreenShotHelper;

public class InstructionView_GuideMaker implements OnTouchListener, OnClickListener{

	public class Pos {
		float x;
		float y;
		public Pos(){}
		public Pos(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	// service info
	private WindowManager mWindowManager;
	private Context context;
	private Handler mHandler;
	private int gidx;

	// display info
	private int deviceWidth;
	private int deviceHeight;

	// layout
	private LinearLayout layout_background; // �׼Ǳ׸��� �� ��ġ ���� ���̾ƿ�

	private LinearLayout layout_mInstruction;
	private LinearLayout layout_mInstructionBar;			// �⺻��ɹ�
	private LinearLayout layout_mActionBar;				// �׼ǹ�
	private LinearLayout layout_mRecordBar;				// ������
	private LinearLayout[] layout_mNote;			// ������
	private LinearLayout layout_mNote_Btn;			// ������

	// layoutParams
	private WindowManager.LayoutParams mParams_layout_background;
	private WindowManager.LayoutParams mParams_mInstruction;
	private WindowManager.LayoutParams mParams_mInstructionBar;
	private WindowManager.LayoutParams mParams_mActionBar;
	private WindowManager.LayoutParams mParams_mRecordBar;
	private WindowManager.LayoutParams[] mParams_mNote;
	private WindowManager.LayoutParams mParams_mNote_Btn;


	// button
	// �⺻��ɹٹ�ư
	private Button btn_transMode;
	private Button btn_action;
	private Button btn_note;
	private Button btn_recordVoice;
	private Button btn_capture;
	private Button btn_management;

	// �׼ǹ� ��ư
	private Button btn_action_touch;
	private Button btn_action_longtouch;
	private Button btn_action_slide;
	private Button btn_action_menu;
	private Button btn_action_back;

	// ������ ��ư
	private Button btn_record_record;
	private Button btn_record_recordcancel;

	// ����â ��ư

	private Button btn_note_add;
	private Button btn_note_delete;

	private NoteDialog nt;

	// �ؽ�Ʈ
	private TextView txtV_SceneInfo;
	private TextView[] txtV_Note;


	private Pos bef_Pos1;
	private Pos curr_Pos1;
	private Pos bef_Pos2;
	private Pos curr_Pos2;

	private boolean actionbar_active;
	private boolean recordbar_active;

	private boolean recordBtnState;
	private boolean recordPBtnState;

	private int currentNoteCount;

	private int currentActionMode;

	
	private boolean ViewerAvaliable;
	
	//Action type;
	static final int touch = 0;							
	static final int longtouch = 1;
	static final int slide = 2;
	//Action orientation
	static final int right = 1;
	static final int up = 2;
	static final int left = 3;
	static final int down = 4;

	private Animations Anim;
	private GuideFileHelper GFH;
	private RecordHelper recorder;
	private ScreenShotHelper SSH;
	private DrawingView DV;						//Rect�� �׸��� class
	private float START_X, START_Y;					//�����̱� ���� ��ġ�� ���� ��
	private int PREV_X, PREV_Y;						//�����̱� ���� �䰡 ��ġ�� ��
	private int MAX_X = -1, MAX_Y = -1;				//���� ��ġ �ִ� ��
	private int mode = DRAW;						//���� drawing ��� Ȯ��
	private int touchmode = touch;					//���� ��ġ ��� Ȯ��
	long Start = 0;									//��ġ ���۽ð�
	long End = 0;									//��ġ ����ð� ��� �� ��ġ ���� Ȯ��

	//private ArrayList<Scene> scene;				//����� scene array	
	private DrawingView dv;							//Rect�� �׸� Drawing View
	private Rect savedRect;							//��ġ�� �׷��� Rect
	private View touchView;
	private float width, height;					//Rect ������ ���� �ϱ����� oriental ��ǥ
	private float rLeft, rRight, rTop, rButtom;

	static final int WAIT = -1;						//��ġ ��ο� ���	
	static final int DRAW = 0;						//��ġ ��ο� ���	
	static final int DRAG = 1;						//��ġ �巡�� ���
	static final int ZOOM = 2;						//��ġ ������ ���� ���
	static final int SLIDE	= 3;					//�����̵� �׸��� ���
	static final int SLIDEDRAG = 4;					//�����̵� �巡�� ���


	@SuppressWarnings("deprecation")
	public InstructionView_GuideMaker(Context context, WindowManager WindowManager, int gidx, Handler Handler) {
		this.context = context;
		this.mWindowManager = WindowManager;
		this.gidx = gidx;
		this.mHandler = Handler;

		Display display = mWindowManager.getDefaultDisplay();
		deviceWidth = display.getWidth();
		deviceHeight = display.getHeight();

		initData();
	}

	public void initData() {

		initGuide();
		initAudio();
		initAnimation();
		initButton();
		initAction();
		initLayout();
		initScreenShot();
		initNote();
		initComponentTag();
		bef_Pos1 = new Pos();
		curr_Pos1 = new Pos();
		bef_Pos2 = new Pos();
		curr_Pos2 = new Pos();

		actionbar_active = false;
		recordbar_active = false;

		recordBtnState = false;
		recordPBtnState = false;
		currentNoteCount = 0;
		ViewerAvaliable = false;
	}

	private void initAction(){		
		dv = new DrawingView(this.context);		
		savedRect = new Rect();
		touchView = new View(this.context);

		dv.setVisibility(View.GONE);
		touchView.setVisibility(View.GONE);
		touchView.setBackgroundColor(Color.alpha(0));
		touchView.setOnTouchListener(this);
		mode = WAIT;
	}

	private void initScreenShot() {
		SSH = new ScreenShotHelper();
	}
	private void initGuide()
	{
		GFH = new GuideFileHelper();
	}

	private void initAudio() {
		recorder = new RecordHelper();
	}

	private void initAnimation() {
		Anim = new Animations(this.context);
	}

	private void initLayout() {
		layout_background = new LinearLayout(this.context);
		layout_mInstruction = new LinearLayout(this.context);
		layout_mInstructionBar = new LinearLayout(this.context);
		layout_mActionBar = new LinearLayout(this.context);
		layout_mRecordBar = new LinearLayout(this.context);
		layout_mNote = new LinearLayout[3];
		layout_mNote_Btn  = new LinearLayout(this.context);
		for(int i = 0 ; i < layout_mNote.length; i++) 
			layout_mNote[i] = new LinearLayout(this.context);

		// layoutParams
		layout_background.setBackgroundColor(Color.argb(150, 80, 80, 80));
		mParams_layout_background = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_layout_background.gravity = Gravity.LEFT | Gravity.TOP;

		mParams_mInstruction = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_mInstruction.gravity = Gravity.LEFT | Gravity.TOP;

		mParams_mActionBar = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_mActionBar.gravity = Gravity.LEFT | Gravity.TOP;

		layout_mRecordBar.setBackgroundColor(Color.argb(200, 20, 20, 20));
		mParams_mRecordBar = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_mRecordBar.gravity = Gravity.CENTER | Gravity.CENTER;


		mParams_mInstructionBar = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_mInstructionBar.gravity = Gravity.LEFT | Gravity.TOP;

		mParams_mNote = new WindowManager.LayoutParams[3];
		for(int i = 0; i< mParams_mNote.length; i++) {
			mParams_mNote[i] = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.RGBA_8888);
			mParams_mNote[i].gravity = Gravity.CENTER | Gravity.CENTER;
		}

		mParams_mNote_Btn = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888);
		mParams_mNote_Btn.gravity = Gravity.TOP | Gravity.CENTER;


		txtV_Note = new TextView[3];
		for(int i = 0; i<mParams_mNote.length;i++) {
			txtV_Note[i] = new TextView(this.context);
			txtV_Note[i].setText("");
			txtV_Note[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			txtV_Note[i].setGravity(Gravity.CENTER);
			txtV_Note[i].setTextColor(Color.BLACK);
			txtV_Note[i].setBackgroundColor(Color.argb(180, 255, 255, 0));
			txtV_Note[i].setShadowLayer(3, 0, 0, Color.WHITE);
			txtV_Note[i].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT ));
		}
		///////////////////////////////////////////////////////////////////
		//txtV_SceneInfo.setText(Integer.toString(GFH.getCurrentSceneNum()+1));
		txtV_SceneInfo = new TextView(this.context);	
		txtV_SceneInfo.setText("Guide Maker!  Scene#1");
		txtV_SceneInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		txtV_SceneInfo.setGravity(Gravity.CENTER);
		txtV_SceneInfo.setTextColor(Color.WHITE);
		txtV_SceneInfo.setBackgroundColor(Color.argb(200, 200, 200, 200));
		txtV_SceneInfo.setShadowLayer(3, 0, 0, Color.BLACK);
		///////////////////////////////////////////////////////////////////


		layout_mInstruction.setOrientation(LinearLayout.VERTICAL);
		layout_mInstruction.addView(txtV_SceneInfo);
		layout_mInstruction.addView(layout_mInstructionBar, mParams_mInstructionBar);
		layout_mInstruction.addView(layout_mActionBar, mParams_mActionBar);
		//layout_mInstruction.addView(layout_mRecordBar, mParams_mRecordBar);
		layout_mActionBar.setVisibility(View.GONE);
		layout_mRecordBar.setVisibility(View.GONE);
		layout_mNote_Btn.setVisibility(View.GONE);

		layout_mInstructionBar.addView(btn_transMode);
		layout_mInstructionBar.addView(btn_action);
		layout_mInstructionBar.addView(btn_note);
		layout_mInstructionBar.addView(btn_recordVoice);
		layout_mInstructionBar.addView(btn_capture);
		//layout_mInstructionBar.addView(btn_management);

		layout_mActionBar.addView(btn_action_touch);
		layout_mActionBar.addView(btn_action_longtouch);
		layout_mActionBar.addView(btn_action_slide);
		layout_mActionBar.addView(btn_action_menu);
		layout_mActionBar.addView(btn_action_back);

		layout_mRecordBar.addView(btn_record_record);
		layout_mRecordBar.addView(btn_record_recordcancel);
		layout_mRecordBar.setGravity(Gravity.CENTER);
		layout_mRecordBar.setOrientation(LinearLayout.VERTICAL);
		
		layout_mNote_Btn.addView(btn_note_add);
		layout_mNote_Btn.addView(btn_note_delete);
		layout_mNote_Btn.setGravity(Gravity.CENTER); 

		for(int i = 0; i< layout_mNote.length;i++) {
			layout_mNote[i].addView(txtV_Note[i]);
			layout_mNote[i].setVisibility(View.GONE);
			txtV_Note[i].setOnTouchListener(this);
			txtV_Note[i].setOnClickListener(this);
		}
		txtV_SceneInfo.setOnTouchListener(this);
		txtV_SceneInfo.setOnClickListener(this);

		// touchlistener
		layout_background.setOnTouchListener(this);
		layout_mInstruction.setOnTouchListener(this);
		layout_mInstructionBar.setOnTouchListener(this);
		layout_mActionBar.setOnTouchListener(this);
		

		// window view arrange
		mParams_mInstruction.x = 100;
		mParams_mInstruction.y = 600;
		mWindowManager.addView(layout_background, mParams_layout_background);
		mWindowManager.addView(dv, mParams_layout_background);
		mWindowManager.addView(touchView, mParams_layout_background);
		for(int i = 0; i<layout_mNote.length;i++)
			mWindowManager.addView(layout_mNote[i], mParams_mNote[i]);

		mWindowManager.addView(layout_mInstruction, mParams_mInstruction);

		mWindowManager.addView(layout_mRecordBar, mParams_mRecordBar);
		mWindowManager.addView(layout_mNote_Btn, mParams_mNote_Btn);
	}



	private void initNote() {
		for(int i = 0; i< txtV_Note.length; i++) {
			txtV_Note[i].setText("");
			layout_mNote[i].setVisibility(View.GONE);
		}

	}

	private void initComponentTag() {

		layout_background.setTag("layout_background");

		layout_mInstructionBar.setTag("layout_mInstruction");
		layout_mInstructionBar.setTag("layout_mInstructionBar");
		layout_mActionBar.setTag("layout_mActionBar");

		for(int i = 0; i<layout_mNote.length; i++) {
			layout_mNote[i].setTag("layout_mNote"+i);
			txtV_Note[i].setTag("txtV_Note"+i);
		}
		btn_transMode.setTag("btn_transMode");
		btn_action.setTag("btn_action");
		btn_note.setTag("btn_note");
		btn_recordVoice.setTag("btn_recordVoice");
		btn_capture.setTag("btn_capture");
		btn_management.setTag("btn_management");
		txtV_SceneInfo.setTag("txtV_SceneInfo");

		// �׼ǹ� ��ư
		btn_action_touch.setTag("btn_action_touch");
		btn_action_longtouch.setTag("btn_action_longtouch");
		btn_action_slide.setTag("btn_action_slide");
		btn_action_menu.setTag("btn_action_menu");
		btn_action_back.setTag("btn_action_back");

		// ����
		btn_record_record.setTag("btn_record_record");
		btn_record_recordcancel.setTag("btn_record_recordcancel");
		// ����â ��ư

		layout_mNote_Btn.setTag("layout_mNote_Btn");
		btn_note_add.setTag("btn_note_add");
		btn_note_delete.setTag("btn_note_delete");

		touchView.setTag("touch_view");
		dv.setTag("dv");
	}

	private void initButton() {

		btn_transMode		 = new Button(this.context);
		btn_action			 = new Button(this.context);
		btn_note			 = new Button(this.context);
		btn_recordVoice		 = new Button(this.context);
		btn_capture			 = new Button(this.context);
		btn_management			 = new Button(this.context);
		btn_action_touch	 = new Button(this.context);
		btn_action_longtouch = new Button(this.context);
		btn_action_slide	 = new Button(this.context);
		btn_action_menu		 = new Button(this.context);
		btn_action_back		 = new Button(this.context);
		btn_record_record	 = new Button(this.context);
		btn_record_recordcancel = new Button(this.context);
		btn_note_add = new Button(this.context);
		btn_note_delete = new Button(this.context);

		btn_transMode.setText("[전환]");
		btn_transMode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_transMode.setTextColor(Color.WHITE);
		btn_transMode.setBackgroundColor(Color.BLACK);
		btn_transMode.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_action.setText("[액션]");
		btn_action.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_action.setTextColor(Color.WHITE);
		btn_action.setBackgroundColor(Color.BLACK);
		btn_action.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_note.setText("[노트]");
		btn_note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_note.setTextColor(Color.WHITE);
		btn_note.setBackgroundColor(Color.BLACK);
		btn_note.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_recordVoice.setText("[녹음]");
		btn_recordVoice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_recordVoice.setTextColor(Color.WHITE);
		btn_recordVoice.setBackgroundColor(Color.BLACK);
		btn_recordVoice.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_capture.setText("[캡쳐]");
		btn_capture.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		btn_capture.setTextColor(Color.WHITE);
		btn_capture.setBackgroundColor(Color.BLACK);
		btn_capture.setShadowLayer(3, 0, 0, Color.WHITE);


		btn_action_touch.setText("[touch]");
		btn_action_touch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btn_action_touch.setTextColor(Color.WHITE);
		btn_action_touch.setBackgroundColor(Color.BLUE);
		btn_action_touch.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_action_longtouch.setText("[long]");
		btn_action_longtouch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btn_action_longtouch.setTextColor(Color.YELLOW);
		btn_action_longtouch.setBackgroundColor(Color.BLUE);
		btn_action_longtouch.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_action_slide.setText("[slide]");
		btn_action_slide.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btn_action_slide.setTextColor(Color.YELLOW);
		btn_action_slide.setBackgroundColor(Color.BLUE);
		btn_action_slide.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_action_menu.setText("[menu]");
		btn_action_menu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btn_action_menu.setTextColor(Color.YELLOW);
		btn_action_menu.setBackgroundColor(Color.BLUE);
		btn_action_menu.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_action_back.setText("[back]");
		btn_action_back.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btn_action_back.setTextColor(Color.YELLOW);
		btn_action_back.setBackgroundColor(Color.BLUE);
		btn_action_back.setShadowLayer(3, 0, 0, Color.WHITE);

		btn_record_record.setBackgroundResource(R.drawable.record_start);
		btn_record_record.setLayoutParams(new LayoutParams(100*deviceWidth/334, 100*deviceWidth/334));

		btn_record_recordcancel.setBackgroundResource(R.drawable.record_cancel);
		btn_record_recordcancel.setLayoutParams(new LayoutParams(50*deviceWidth/195, 50*deviceWidth/578));

		btn_note_add.setText("노트추가");
		btn_note_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		btn_note_add.setTextColor(Color.YELLOW);
		btn_note_add.setLayoutParams(new LayoutParams(100*deviceWidth/334, 50*deviceWidth/334));

		btn_note_delete.setText("노드삭제");
		btn_note_delete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		btn_note_delete.setTextColor(Color.YELLOW);
		btn_note_delete.setLayoutParams(new LayoutParams(100*deviceWidth/334, 50*deviceWidth/334));

		btn_transMode.setOnTouchListener(this);
		btn_action.setOnTouchListener(this);
		btn_note.setOnTouchListener(this);
		btn_recordVoice.setOnTouchListener(this);
		btn_capture.setOnTouchListener(this);
		btn_management.setOnTouchListener(this);

		/*btn_action_touch.setOnTouchListener(this);
		btn_action_longtouch.setOnTouchListener(this);
		btn_action_slide.setOnTouchListener(this);
		btn_action_menu.setOnTouchListener(this);
		btn_action_back.setOnTouchListener(this);

		btn_setting_preScene.setOnTouchListener(this);
		btn_setting_nxtScene.setOnTouchListener(this);
		btn_setting_saveScene.setOnTouchListener(this);
		btn_setting_saveGuide.setOnTouchListener(this);
		btn_setting_exit.setOnTouchListener(this);*/

		btn_transMode.setOnClickListener(this);
		btn_action.setOnClickListener(this);
		btn_note.setOnClickListener(this);
		btn_recordVoice.setOnClickListener(this);
		btn_capture.setOnClickListener(this);
		btn_management.setOnClickListener(this);

		btn_action_touch.setOnClickListener(this);
		btn_action_longtouch.setOnClickListener(this);
		btn_action_slide.setOnClickListener(this);
		btn_action_menu.setOnClickListener(this);
		btn_action_back.setOnClickListener(this);

		btn_record_record.setOnClickListener(this);
		btn_record_recordcancel.setOnClickListener(this);

		btn_note_add.setOnClickListener(this);
		btn_note_delete.setOnClickListener(this);
	}

	public boolean isGuideDataInput() {
		boolean res = false;

		if(GFH.isActionMode() || GFH.isDataInfo(0)|| GFH.isDataInfo(1)
				|| GFH.isDataInfo(2)|| GFH.isDataInfo(3)|| GFH.isDataInfo(4)) {
			res = true;
		}

		return res;
	}

	public boolean isGuideCaptureInput() {
		boolean res = false;

		if( GFH.isDataInfo(0)) {
			res = true;
		}

		return res;
	}


	public void addScene() {
		if(GFH.getCurrentSceneNum() < 31) {
			GFH.addScene();
			setSceneNum();
			initNote();
			mode = WAIT;

			Toast.makeText(this.context,"씬이 추가되었습니다.",Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(this.context,"씬을 더이상 추가 할 수 없습니다.",Toast.LENGTH_SHORT).show();
		}
	}

	public void deleteScene(int sceneNum) {
		//saveGuide();

		Log.d("testdeleting", "a "+GFH.getCurrentSceneNum());
		for(int i = 0; i < GFH.getSceneSize(); i++)
			GFH.setCurrentSceneNum(0);

		for(int i = 0; i < sceneNum; i++)
			GFH.setCurrentSceneNum(1);
		GFH.deleteScene();
		for(int i = 0; i < sceneNum; i++)
			GFH.setCurrentSceneNum(0);
		for(int i = 0; i < GFH.getSceneSize(); i++)
			GFH.setCurrentSceneNum(1);
		setSceneNum();
		saveGuide(1);
	}

	public void saveGuide(int type) {

		saveActionData();

		GFH.save(gidx);

		if(type == 0) Toast.makeText(this.context,"저장되었습니다.",Toast.LENGTH_SHORT).show();
		if(type == 1) Toast.makeText(this.context,"삭제되었습니다.",Toast.LENGTH_SHORT).show();
	}

	private void saveActionData() {
		Rect rect = new Rect();
		rect.left = dv.getRect().left;
		rect.right = dv.getRect().right;
		rect.bottom = dv.getRect().bottom;
		rect.top = dv.getRect().top;

		if(rect.top > rect.bottom){
			int temp = rect.top;
			rect.top = rect.bottom;
			rect.bottom = temp;
		}

		int orientation = dv.getOrientation();
		GFH.addAction(currentActionMode, rect, orientation);

		dv.setRect(new Rect(0, 0, 0, 0));
		dv.setOrientation(0);
		currentActionMode = -1;

	}

	@SuppressLint("SdCardPath")
	private void deleteText(int noteCount) {
		if(GFH.isDataInfo(noteCount+2)) {
			String fileName = GFH.getCurrentSceneNum()+"_"+noteCount+".txt";
			String filePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/Note/"; 
			File file = new File(filePath + fileName);

			if(file.exists())
				file.delete();
			GFH.deleteDataInfo(noteCount+2);
			Toast.makeText(this.context,"노트가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(this.context,"삭제 할 노트가 없습니다.",Toast.LENGTH_SHORT).show();
		}

		nt = NoteDialog.nt;
		nt.finish();
		updateNote();
	}


	@SuppressLint("SdCardPath")
	private void saveText(int noteCount) {

		if(GFH.isActionMode()) {
			dv.setVisibility(View.VISIBLE);
			touchView.setVisibility(View.VISIBLE);
		}
		String saveText = NoteDialog.et.getText().toString();
		Log.d("hwnote", ""+saveText);
		if(saveText != null) {
			String str = Environment.getExternalStorageState();
			String dirPath = "";
			if ( str.equals(Environment.MEDIA_MOUNTED)) {

				dirPath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/Note"; 
				File file = new File(dirPath); 
				if( !file.exists() )  // ���ϴ� ��ο� ����� �ִ��� Ȯ��
					file.mkdirs();

			}

			String fileName = GFH.getCurrentSceneNum()+"_"+noteCount+".txt";
			String filePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/Note/"; 

			FileOutputStream fos = null;
			File file = new File(filePath + fileName);

			if(file.exists())
				file.delete();

			if( !file.exists() ) {
				try {
					fos = new FileOutputStream(file,true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.write(saveText.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Toast.makeText(this.context,"노트가 삽입되었습니다.",Toast.LENGTH_SHORT).show();

			nt = NoteDialog.nt;
			nt.finish();
			updateNote();
		}else {
			Toast.makeText(this.context,"텍스트를 입력해주세요.",Toast.LENGTH_SHORT).show();
		}
	}
	@SuppressLint("SdCardPath")
	public void updateNote() {
		layout_mInstruction.setVisibility(View.VISIBLE);
		dv.setVisibility(View.VISIBLE);
		touchView.setVisibility(View.VISIBLE);
		layout_mNote_Btn.setVisibility(View.GONE);
		for(int i = 2; i<5; i++)
		{	
			if(GFH.isDataInfo(i)) {

				layout_mNote[i-2].setVisibility(View.VISIBLE);

				String saveText = "";

				String fileName = GFH.getDataInfo(i).getFileName();
				Log.d("notetest", ""+i+fileName);
				String filePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/Note/"; 
				byte data[] = null;
				FileInputStream open;  
				try {
					open = new FileInputStream(filePath + fileName);
					data = new byte[open.available()]; // �׸��� ũ�� ����
					while(open.read(data)!=-1){ //EOF �߻���� ��� �о�´�
						;
						
					}
					open.close(); // �پ� ��Ʈ���� �ݾ��ְ�
					saveText=new String(data); // ��Ʈ�� ��ȯ!!
				} catch (Exception e) {
					// TODO Auto-generated catch block
				} 

				txtV_Note[i-2].setText(saveText);
				saveNote(i-2);
			}else {
				layout_mNote[i-2].setVisibility(View.GONE);
			}

		}
	}


	private boolean saveNote(int noteCount) {
		boolean res = false;
		if(noteCount <= 2) {
			if(noteCount == 0) {
				Log.d("note_c", "x : "+ mParams_mNote[0].x + "y : " + mParams_mNote[0].y);
				Rect rect = new Rect(mParams_mNote[0].x, mParams_mNote[0].y, 0, 0);
				GFH.addNote( (Integer.toString(GFH.getCurrentSceneNum()) + "_" + noteCount+".txt"), txtV_Note[0].getText().toString(), rect);
			}else if(noteCount == 1) {
				Log.d("note_c", "x : "+ mParams_mNote[1].x + "y : " + mParams_mNote[1].y);
				Rect rect = new Rect(mParams_mNote[1].x, mParams_mNote[1].y, 0, 0);
				GFH.addNote_1((Integer.toString(GFH.getCurrentSceneNum())+ "_" + noteCount+".txt"), txtV_Note[1].getText().toString(), rect);
			}else if(noteCount == 2) {
				Log.d("note_c", "x : "+ mParams_mNote[2].x + "y : " + mParams_mNote[2].y);
				Rect rect = new Rect(mParams_mNote[2].x, mParams_mNote[2].y, 0, 0);
				GFH.addNote_2((Integer.toString(GFH.getCurrentSceneNum())+ "_" + noteCount+".txt"), txtV_Note[2].getText().toString(), rect);
			}
			res = true;
		}else
			res = false;
		return res;
	}


	private void makeNote(int noteCount) {
		layout_mInstruction.setVisibility(View.GONE);
		layout_background.setVisibility(View.GONE);
		dv.setVisibility(View.GONE);
		touchView.setVisibility(View.GONE);
		for(int i = 0; i< 3; i++) 
			layout_mNote[i].setVisibility(View.GONE);
		layout_mNote_Btn.setVisibility(View.VISIBLE);
		//setVisibility(View.GONE);
		Bundle bun = new Bundle();
		Intent popupIntent = new Intent(this.context, NoteDialog.class);

		popupIntent.putExtras(bun);
		/*popupIntent.putExtra("gidx", gidx);
		popupIntent.putExtra("noteCount", noteCount);
		popupIntent.putExtra("sceneNum", GFH.getCurrentSceneNum());*/

		PendingIntent pie = PendingIntent.getActivity(this.context,  0,  popupIntent,  PendingIntent.FLAG_ONE_SHOT);

		try{
			pie.send();
		}
		catch(CanceledException e){
			Log.d("why!?", "error : " + e);
		}

	}
	private void setSceneNum() {
		txtV_SceneInfo.setText("Guide Maker!  Scene#"+Integer.toString(GFH.getCurrentSceneNum()+1));
	}

	private void recordControl() { // true - recording
		if(recordBtnState)
		{//������
			setinActiveRecordBar();
			stopRecord();
			recordBtnState = false;
			btn_record_record.setBackgroundResource(R.drawable.record_start);
		}else
		{//�����
			startRecord();
			recordBtnState = true;
			btn_record_record.setBackgroundResource(R.drawable.record_stop);
		}
	}
	private void recordCancel() {
		setinActiveRecordBar();
		if(recordBtnState) {
			stopRecord();
			recordBtnState = false;
			btn_record_record.setBackgroundResource(R.drawable.record_start);
		}
	}

	private void startRecord() {
		Toast.makeText(this.context,"녹음이 시작되었습니다.",Toast.LENGTH_SHORT).show();
		GFH.addVoice(Integer.toString(GFH.getCurrentSceneNum()));
		recorder.startRec(GFH.getDataInfo(1).getFileName(), gidx);
	}

	private void stopRecord() {
		if(GFH.isDataInfo(1)) {
			Toast.makeText(this.context,"녹음이 저장되었습니다.",Toast.LENGTH_SHORT).show();
			recorder.stopRec();
		}
	}

	@SuppressLint("SdCardPath")
	private void startAudio() {

		Log.d("playA", GFH.getDataInfo(1).getFileName());
		if(GFH.isDataInfo(1)) {
			Log.d("playA", "AA");
			recorder.playAudio(GFH.getDataInfo(1).getFileName(), gidx);
		}
	}

	public int getCurrentSceneNum() {
		return GFH.getCurrentSceneNum();
	}
	private void setInstructionViewPos(Pos pos) {
		mParams_mInstruction.x += (int) pos.x;
		mParams_mInstruction.y += (int) pos.y;

		mWindowManager.updateViewLayout(layout_mInstruction, mParams_mInstruction);
	}

	private void setNotePos(Pos pos, int NoteNum) {
		mParams_mNote[NoteNum].x += (int) pos.x;
		mParams_mNote[NoteNum].y += (int) pos.y;

		mWindowManager.updateViewLayout(layout_mNote[NoteNum], mParams_mNote[NoteNum]);
	}

	// 0 actionbar 1 settingbar
	private void setActiveBar(int type){
		layout_mActionBar.setVisibility(View.GONE);
		layout_mRecordBar.setVisibility(View.GONE);

		if(type == 0) {
			if(!actionbar_active) {
				layout_mActionBar.setVisibility(View.VISIBLE);
				Anim.startAnim_ActiveBar(layout_mActionBar);
			}
		}else if(type ==  1) {
			if(!actionbar_active) {
			}
		}else if(type == 2) {
			if(!recordbar_active) {
				layout_mRecordBar.setVisibility(View.VISIBLE);
			}

		}
	}


	public void setVisibility(int visibility) {
		if(visibility == View.GONE) {
			layout_background.setVisibility(View.GONE);
			layout_mInstruction.setVisibility(View.GONE);
			for(int i = 0; i<layout_mNote.length; i++)
				layout_mNote[i].setVisibility(View.GONE);
			dv.setVisibility(View.GONE);
			touchView.setVisibility(View.GONE);
		}else if(visibility == View.VISIBLE) {
			layout_background.setVisibility(View.VISIBLE);
			layout_mInstruction.setVisibility(View.VISIBLE);
			for(int i = 0; i<layout_mNote.length; i++)
				layout_mNote[i].setVisibility(View.VISIBLE);
			dv.setVisibility(View.VISIBLE);
			touchView.setVisibility(View.VISIBLE);
		}
	}

	// 0 actionbar 1 settingbar
	private void setinActiveBar(){
		layout_mActionBar.setVisibility(View.GONE);
		//layout_mManagementBar.setVisibility(View.GONE);
	}

	private void setinActiveRecordBar(){
		layout_mRecordBar.setVisibility(View.GONE);
	}

	
	public void setViewerAvaliable() {
		ViewerAvaliable = true;
	}

	long st_time;
	long end_time;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("touchtest", v.getTag().toString());

		// TODO Auto-generated method stub

		layout_mInstruction.setVisibility(View.VISIBLE);
		setinActiveBar();
		Log.d("afaf : ", ""+event.getX(0));

		if(     ("btn_transMode".equals(v.getTag())) ||
				("btn_action".equals(v.getTag())) ||
				("btn_note".equals(v.getTag())) ||
				("btn_recordVoice".equals(v.getTag())) ||
				("btn_capture".equals(v.getTag())) ||
				("btn_management".equals(v.getTag())) ||
				("txtV_SceneInfo".equals(v.getTag()))
				)
		{
			curr_Pos1.x = event.getRawX();
			curr_Pos1.y = event.getRawY();
			v.setClickable(true);

			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				st_time = System.currentTimeMillis();
				bef_Pos1.x = curr_Pos1.x;
				bef_Pos1.y = curr_Pos1.y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = curr_Pos1.x-bef_Pos1.x;
				float deltaY = curr_Pos1.y-bef_Pos1.y;
				setInstructionViewPos(new Pos(deltaX, deltaY));
				bef_Pos1.x = curr_Pos1.x;
				bef_Pos1.y = curr_Pos1.y;

				break;	
			case MotionEvent.ACTION_UP:
				end_time = System.currentTimeMillis();
				if(end_time - st_time > 100) v.setClickable(false);
				break;
			}
		}

		else if(("txtV_Note0".equals(v.getTag())) ||
				("txtV_Note1".equals(v.getTag())) ||
				("txtV_Note2".equals(v.getTag())) 
				)
		{
			curr_Pos1.x = event.getRawX();
			curr_Pos1.y = event.getRawY();
			v.setClickable(true);

			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				st_time = System.currentTimeMillis();
				bef_Pos1.x = curr_Pos1.x;
				bef_Pos1.y = curr_Pos1.y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = curr_Pos1.x-bef_Pos1.x;
				float deltaY = curr_Pos1.y-bef_Pos1.y;
				int noteNum=0;
				if(("txtV_Note0".equals(v.getTag())))  noteNum = 0;
				if(("txtV_Note1".equals(v.getTag())))  noteNum = 1;
				if(("txtV_Note2".equals(v.getTag())))  noteNum = 2;

				setNotePos((new Pos(deltaX, deltaY)), noteNum);
				saveNote(currentNoteCount);
				bef_Pos1.x = curr_Pos1.x;
				bef_Pos1.y = curr_Pos1.y;

				break;	
			case MotionEvent.ACTION_UP:
				end_time = System.currentTimeMillis();
				if(end_time - st_time > 100) v.setClickable(false);
				break;
			}
		}
		else {
			if(("touch_view".equals(v.getTag())) ||
					("dv".equals(v.getTag())) )
			{
				if(currentActionMode != 3 && currentActionMode != 4)
					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if(mode == DRAW){
							savedRect.left = (int)(event.getRawX());
							savedRect.top = (int)(event.getRawY());
							savedRect.right = (int)(event.getRawX());
							savedRect.bottom = (int)(event.getRawY());		              
						}
						else if(mode == DRAG){
							START_X = event.getRawX();					//��ġ ���� ��
							START_Y = event.getRawY();					//��ġ ���� ��						
							rLeft = savedRect.left;
							rRight = savedRect.right;
							rTop = savedRect.top;
							rButtom = savedRect.bottom;     
						}
						else if(mode == ZOOM){
							mode = DRAW;
						}
						else if(mode == SLIDE){
							savedRect.left = (int)(event.getRawX());
							savedRect.top = (int)(event.getRawY());
							savedRect.right = (int)(event.getRawX());
							savedRect.bottom = (int)(event.getRawY());	      	
						}
	
						break;
					case MotionEvent.ACTION_MOVE:
						if(mode == DRAW){
							savedRect.right = (int)(event.getRawX());
							savedRect.bottom = (int)(event.getRawY());	            		
							dv.setRect(savedRect);
						}
						else if(mode == DRAG){	   
							int x = (int)(event.getRawX() - START_X);	//�̵��� �Ÿ�
							int y = (int)(event.getRawY() - START_Y);	//�̵��� �Ÿ�						
							//��ġ�ؼ� �̵��� ��ŭ �̵� ��Ų��
							savedRect.left = (int) (rLeft + x);
							savedRect.right = (int) (rRight + x);
							savedRect.top = (int) (rTop + y);
							savedRect.bottom = (int) (rButtom + y);
							dv.setRect(savedRect);
							//setMaxRectPosition(); 	//rect��ġ ����ȭ�ϰ�ʹ�!!!!!!!!!!!!!!
							//optimizePosition();		//���� ��ġ ����ȭ
						}
						else if(mode == ZOOM){	
							if(event.getPointerCount() == 2){
								float _width = (event.getX(0) - event.getX(1));
								float _height = (event.getY(0) - event.getY(1));	 
								_width = FloatMath.sqrt(_width * _width);
								_height = FloatMath.sqrt(_height * _height);
	
								if(width < 50 ){
									break;
								}
								if(height < 50){
									break;
								}
								if(savedRect.left < savedRect.right){
									if((width-_width) > 0){
										//������ �ٿ�
										Log.d("size down", ""+(width-_width));
										savedRect.left += 4;
										savedRect.right -= 4;
									}
									else if((width-_width) < 0){
										//������ ��
										Log.d("size up", ""+(width-_width));
										savedRect.left -= 4;
										savedRect.right += 4;
									}
								}
								else if(savedRect.left > savedRect.right){
									if((width - _width) < 0){
										//������ �ٿ�
										savedRect.left -= 4;
										savedRect.right += 4;
									}
									else if((width - _width) > 0){
										savedRect.left += 4;
										savedRect.right -= 4;
									}
								}
								if(savedRect.top < savedRect.bottom){
									if((height - _height) > 0){
										savedRect.top += 4;
										savedRect.bottom -= 4;
									}
									else if((height - _height) < 0){
										savedRect.top -= 4;
										savedRect.bottom += 4;
									}			            		
								}
								else if(savedRect.top > savedRect.bottom){
									if((height - _height) < 0){
										savedRect.top += 4;
										savedRect.bottom -= 4;
									}
									else if((height - _height) > 0){
										savedRect.top -= 4;
										savedRect.bottom += 4;
									}			            		
								}
								width = event.getX(0) - event.getX(1);
								height = event.getY(0) - event.getY(1);
								width = FloatMath.sqrt(width*width);
								height = FloatMath.sqrt(height*height);
							}	            		
							dv.setRect(savedRect);	
							Log.d("Rect","left : "+savedRect.left + "right : "+savedRect.right);
							Log.d("Rect","top : "+savedRect.top + "bottom : "+savedRect.bottom);
						}
						else if(mode == SLIDE){
							savedRect.right = (int)(event.getRawX()) + mParams_layout_background.x;
							savedRect.bottom = (int)(event.getRawY()) + mParams_layout_background.y;	            		
						}
	
						break;	            	
					case MotionEvent.ACTION_UP:
						if(mode == DRAW){
							savedRect.right = (int)(event.getRawX()) + mParams_layout_background.x;
							savedRect.bottom = (int)(event.getRawY()) + mParams_layout_background.y;
							//rectList.add(savedRect);			//Rect�� ������ �ϰ� ���� �� �߰�	            		          	    
							dv.setRect(savedRect);	
							mode = DRAG;
						}	         
						else if(mode == DRAG){
							mode = DRAG;         		            		
						}
						else if(mode == ZOOM){
							mode = DRAG;
						}
						else if (mode == SLIDE){	            		
							dv.setLine(savedRect);
							mode = SLIDE;
						}
	
	
						break;
					case MotionEvent.ACTION_POINTER_2_DOWN: 
						if(mode == SLIDEDRAG){
							break;
						}
						mode = ZOOM;
						width = event.getX(0) - event.getX(1);
						height = event.getY(0) - event.getY(1);	            	
						width = FloatMath.sqrt(width*width);
						height = FloatMath.sqrt(height*height);	            	
						break;
					}
			}
			
			int action = event.getAction();
			switch(action & MotionEvent.ACTION_MASK) {
			
			case MotionEvent.ACTION_DOWN:    //첫번째 손가락 터치(드래그 용도)
				bef_Pos2.x = event.getX(0);
				Log.d("첫번째 손1", ""+bef_Pos2.x);
				break;

			case MotionEvent.ACTION_UP:    // 첫번째 손가락을 떼었을 경우
				curr_Pos2.x = event.getX(0);
				//Log.d("첫번째 손2", ""+curr_Pos2.x);
				break;
			case MotionEvent.ACTION_POINTER_UP:  // 두번째 손가락을 떼었을 경우
				curr_Pos2.y = event.getX(1);
				Log.d("첫번째 손2", ""+curr_Pos2.x);
				Log.d("두번째 손2", ""+curr_Pos2.y);
				float deltaX1 = curr_Pos2.x-bef_Pos2.x;
				float deltaX2 = curr_Pos2.y-bef_Pos2.y;
				if(deltaX1 < -200 && deltaX2 < -200) { // next
					if(ViewerAvaliable) {
						if(GFH.getSceneSize() >= 1) {
							Message msg = mHandler.obtainMessage(GuideMakerService.MSG_VIEWER);
							mHandler.sendMessage(msg);
						}
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:  
				//두번째 손가락 터치(손가락 2개를 인식하였기 때문에 핀치 줌으로 판별)				
				bef_Pos2.y = event.getX(1);
				Log.d("두번째 손1",""+ bef_Pos2.y);
				break;
			case MotionEvent.ACTION_CANCEL:
			default : 
				break;
			}
		}

		return false;
	}



	public void Capture() {

		GFH.addCapture(Integer.toString(GFH.getCurrentSceneNum()));
		SSH.ScreenCapture(GFH.getDataInfo(0).getFileName(), gidx);
	}
	@Override
	public void onClick(View v) {
		setinActiveBar();
		if("btn_transMode".equals(v.getTag())){
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_Action);
			mHandler.sendMessage(msg);
		}else if("btn_action".equals(v.getTag())){
			setActiveBar(0);
		}else if("btn_note".equals(v.getTag())){


			boolean b = false;
			for(int i = 0; i < 3; i++)
			{
				if(!GFH.isDataInfo(i+2)){
					currentNoteCount = i;
					makeNote(currentNoteCount);
					b = true;
					break;
				}
			}
			if(!b)
				Toast.makeText(this.context,"최대 노트수를 초과하였습니다.",Toast.LENGTH_SHORT).show();

		}else if("btn_recordVoice".equals(v.getTag())){
			setActiveBar(2);
		}else if("btn_capture".equals(v.getTag())){
			setVisibility(View.GONE);
			//setVisibility(View.GONE);
			Message msg = mHandler.obtainMessage(GuideMakerService.MSG_CAPTURE);
			mHandler.sendMessage(msg);
			Capture();
			//setVisibility(View.VISIBLE);
			Toast.makeText(this.context,"캡쳐되었습니다.",Toast.LENGTH_SHORT).show();

		}else if("btn_management".equals(v.getTag())){
			setActiveBar(1);
		}else if("btn_action_touch".equals(v.getTag())){
			currentActionMode = 0;
			Toast.makeText(this.context, "사각형을 그려주세요", Toast.LENGTH_LONG).show();
			touchView.setVisibility(View.VISIBLE);
			dv.setVisibility(View.VISIBLE);
			mode = DRAW;

		}else if("btn_action_longtouch".equals(v.getTag())){
			currentActionMode = 1;
			Toast.makeText(this.context, "사각형을 그려주세요", Toast.LENGTH_LONG).show();
			touchView.setVisibility(View.VISIBLE);
			dv.setVisibility(View.VISIBLE);
			mode = DRAW;
		}else if("btn_action_slide".equals(v.getTag())){
			currentActionMode = 2;
			Toast.makeText(this.context, "슬라이드 방향을 표시해 주세요", Toast.LENGTH_LONG).show();
			touchView.setVisibility(View.VISIBLE);
			dv.setVisibility(View.VISIBLE);
			mode = SLIDE;
		}else if("btn_action_menu".equals(v.getTag())){
			currentActionMode = 3;
			touchView.setVisibility(View.GONE);
			dv.setVisibility(View.GONE);
			Toast.makeText(this.context,"메뉴 액션이 등록되었습니다.",Toast.LENGTH_SHORT).show();
		}else if("btn_action_back".equals(v.getTag())){
			currentActionMode = 4;
			touchView.setVisibility(View.GONE);
			dv.setVisibility(View.GONE);
			Toast.makeText(this.context,"뒤로가기 액션이 등록되었습니다.",Toast.LENGTH_SHORT).show();
		}else if("btn_record_record".equals(v.getTag())){
			recordControl();
		}else if("btn_record_recordcancel".equals(v.getTag())){
			recordCancel();
		}else if("btn_management_preScene".equals(v.getTag())){
			setSceneNum();
		}else if("btn_management_nxtScene".equals(v.getTag())){
			setSceneNum();
		}else if("btn_management_deleteScene".equals(v.getTag())){
		}

		else if("txtV_Note0".equals(v.getTag())){
			currentNoteCount = 0;
			makeNote(currentNoteCount);
		}else if("txtV_Note1".equals(v.getTag())){
			currentNoteCount = 1;
			makeNote(currentNoteCount);
		}else if("txtV_Note2".equals(v.getTag())){
			currentNoteCount = 2;
			makeNote(currentNoteCount);
		}else if("btn_note_add".equals(v.getTag())) {

			saveNote(currentNoteCount);
			saveText(currentNoteCount);
			Log.d("hwnote", ""+GFH.isDataInfo(2) + " " +GFH.isDataInfo(3) + " " +GFH.isDataInfo(4));
		}else if("btn_note_delete".equals(v.getTag())) {

			saveNote(currentNoteCount);
			deleteText(currentNoteCount);
			Log.d("hwnote", ""+GFH.isDataInfo(2) + " " +GFH.isDataInfo(3) + " " +GFH.isDataInfo(4));
		}


	}

	public void Remove() {
		if(layout_mInstruction != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		{
			mWindowManager.removeView(layout_mInstruction);
			layout_mInstruction = null;
		}
		for(int i = 0; i< layout_mNote.length; i++)
			if(layout_mNote[i] != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
			{
				mWindowManager.removeView(layout_mNote[i]);
				layout_mNote[i] = null;
			}
		if(layout_mNote_Btn != null)        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		{
			mWindowManager.removeView(layout_mNote_Btn);
			layout_mNote_Btn = null;
		}

	}

}
