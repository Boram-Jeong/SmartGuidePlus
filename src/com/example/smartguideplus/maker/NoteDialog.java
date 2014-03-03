package com.example.smartguideplus.maker;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.example.smartguideplus.R;

public class NoteDialog extends Activity {
	
	public static NoteDialog nt;
	
	private int gidx;
	private int sidx;
	private int noteCount;
	
	public static String text;
	
/*	private Button add;
	private Button delete;*/
	public static EditText et;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		nt = NoteDialog.this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
			
		setContentView(R.layout.activity_note_dialog);	//�̰� �� ���� ��ġ!!!!!!!
		
		et = (EditText)findViewById(R.id.textLine);
		
		et.setText("텍스트를 입력 하세요!");
		et.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et.setText(" ");
			}
		});
		Intent intent = getIntent(); 
		gidx = intent.getIntExtra("gidx", gidx);
		sidx = intent.getIntExtra("sceneNum", sidx);
		noteCount = intent.getIntExtra("noteCount", noteCount);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_dialog, menu);
		return true;
	}
	/*private class A implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.add:
				
				text = et.getText().toString(); 
				//saveText();
				finish();			
				break;
			case R.id.delete:
				//deleteText();
				finish();	
				
			}
			
		}*/
		
		
	
	

}
