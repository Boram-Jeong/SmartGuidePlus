package com.example.smartguideplus.guidemaker;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class DrawingView extends View {
    private Rect mtemp = null;
    private Rect mRect = null;
    private ArrayList<Rect> mRec2;
    private int count= 0;
    public DrawingView(Context context) {
       super(context);
       mRect = new Rect();
       mtemp = new Rect();
       mRec2 = new ArrayList<Rect>();
    }
    
    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
       super(context, attrs, defStyle);
    }
    
    public DrawingView(Context context, AttributeSet attrs) {
       super(context, attrs);
    }

    public void setRect(Rect rect) {
       mRect.left = rect.left;
       mRect.right = rect.right;
       mRect.top = rect.top;
       mRect.bottom = rect.bottom;

       //Log.i("Rect", Integer.toString(mRect.right));
       invalidate();
    }
    public void pix(Rect rect){
    	mtemp.left = rect.left;
    	mtemp.right = rect.right;
    	mtemp.top = rect.top;
    	mtemp.bottom = rect.bottom;
    	mRec2.add(mtemp);
    	count++;
    	invalidate(); 
    }
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
       super.onDraw(canvas);
 
       Paint paint = new Paint();
       paint.setStyle(Paint.Style.STROKE);
       paint.setColor(Color.RED);
       paint.setStrokeWidth(5.0f);
       //�簢��
       canvas.drawRect(mRect, paint);
       for(int i=0; i<count; i++){
    	   canvas.drawRect(mRec2.get(i), paint);
       }
    }
 }
