package com.example.smartguideplus.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;


public class DrawingView extends View {
    private Rect mRect = null;
    static final int RECT = 0;
    static final int LINE = 1;
    int mode = RECT;
    float x1, x2, y1, y2;
	int orientation;
	
	
    public DrawingView(Context context) {
       super(context);
       mRect = new Rect();
       orientation = 0;
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
       mode = RECT;
		invalidate();
    }
    
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        invalidate();
     }
    
    public void setLine(Rect rect){
    	mRect.left = rect.left;
        mRect.right = rect.right;
        mRect.top = rect.top;
        mRect.bottom = rect.bottom;
        mode = LINE;
        
        this.x1 =  mRect.left;	//���� x��ǥ
  		this.x2 = mRect.right;	//�� x��ǥ
  		this.y1 =  mRect.top;	//���� y��ǥ
  		this.y2 = mRect.bottom;	//�� y��ǥ
  		
  		float deltaX = FloatMath.sqrt((x2-x1)*(x2-x1));
  		float deltaY = FloatMath.sqrt((y2-y1)*(y2-y1));
  		
  		if(deltaX > deltaY){
  			mRect.bottom = mRect.top;
  			if(x2 > x1){
  				orientation = 0;		//������ ������� ȭ��ǥ
  			}
  			else{
  				orientation = 1;		//���� ������� ȭ��ǥ
  			}
  		}
  		else{
  			mRect.right = mRect.left;
  			if(y2 > y1){
  				orientation = 2;		//�Ʒ��� ������� ȭ��ǥ
  			}
  			else
  				orientation = 3;		//���� ������� ȭ��ǥ
  		}		
        invalidate();
    }
    
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
       super.onDraw(canvas);
       if(mode == RECT){	       
	       //�簢��
    	   Paint paint = new Paint();
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(Color.BLACK);
           paint.setStrokeWidth(15.0f);
	       canvas.drawRect(mRect, paint);
	       
	       paint.setColor(Color.CYAN);
           paint.setStrokeWidth(5.0f);
	       canvas.drawRect(mRect, paint);	       
       }
       else if(mode == LINE){
    	   Paint paint = new Paint();
           paint.setStyle(Paint.Style.STROKE);
           paint.setColor(Color.BLACK);
    	   paint.setStrokeWidth(15.0f);
    	   if(orientation == 0){   		   
    		   mRect.left = this.getWidth()/2 - this.getWidth()/4;
        	   mRect.top = this.getHeight()/2;
        	   mRect.right = this.getWidth()/2 + this.getWidth()/4;
        	   mRect.bottom = this.getHeight()/2;
        	   canvas.drawLine(mRect.left-5, mRect.top, mRect.right+5, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right+5, mRect.bottom+5, mRect.right-22, mRect.bottom-22, paint);
    		   canvas.drawLine(mRect.right+5, mRect.bottom-5, mRect.right-22, mRect.bottom+22, paint);
    		   paint.setColor(Color.CYAN);
        	   paint.setStrokeWidth(7.0f); 
        	   canvas.drawLine(mRect.left, mRect.top, mRect.right, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right-20, mRect.bottom-20, paint);
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right-20, mRect.bottom+20, paint);    		   
    	   }
    	   else if(orientation == 1){
    		   mRect.left = this.getWidth()/2 + this.getWidth()/4;
        	   mRect.top = this.getHeight()/2;
        	   mRect.right = this.getWidth()/2 - this.getWidth()/4;
        	   mRect.bottom = this.getHeight()/2;
        	   canvas.drawLine(mRect.left+5, mRect.top, mRect.right-5, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right-5, mRect.bottom+5, mRect.right+22, mRect.bottom-22, paint);
    		   canvas.drawLine(mRect.right-5, mRect.bottom-5, mRect.right+22, mRect.bottom+22, paint);
        	   paint.setColor(Color.CYAN);
        	   paint.setStrokeWidth(7.0f); 
        	   canvas.drawLine(mRect.left, mRect.top, mRect.right, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right+20, mRect.bottom-20, paint);
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right+20, mRect.bottom+20, paint);
    	   }
    	   else if(orientation == 2){
    		   mRect.left = this.getWidth()/2;
        	   mRect.top = this.getHeight()/2 - this.getHeight()/4;
        	   mRect.right = this.getWidth()/2;
        	   mRect.bottom = this.getHeight()/2 + this.getHeight()/4;
        	   canvas.drawLine(mRect.left, mRect.top-5, mRect.right, mRect.bottom+5, paint); 
    		   canvas.drawLine(mRect.right-5, mRect.bottom+5, mRect.right+22, mRect.bottom-22, paint);
    		   canvas.drawLine(mRect.right+5, mRect.bottom+5, mRect.right-22, mRect.bottom-22, paint);
    		   paint.setColor(Color.CYAN);
        	   paint.setStrokeWidth(7.0f); 
        	   canvas.drawLine(mRect.left, mRect.top, mRect.right, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right+20, mRect.bottom-20, paint);
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right-20, mRect.bottom-20, paint);
    	   }
    	   else{
    		   mRect.left = this.getWidth()/2;
        	   mRect.top = this.getHeight()/2 + this.getHeight()/4;
        	   mRect.right = this.getWidth()/2;
        	   mRect.bottom = this.getHeight()/2 - this.getHeight()/4;
        	   canvas.drawLine(mRect.left, mRect.top+5, mRect.right, mRect.bottom-5, paint); 
    		   canvas.drawLine(mRect.right-5, mRect.bottom-5, mRect.right+22, mRect.bottom+22, paint);
    		   canvas.drawLine(mRect.right+5, mRect.bottom-5, mRect.right-22, mRect.bottom+22, paint);
    		   paint.setColor(Color.CYAN);
        	   paint.setStrokeWidth(7.0f);  
    		   canvas.drawLine(mRect.left, mRect.top, mRect.right, mRect.bottom, paint); 
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right+20, mRect.bottom+20, paint);
    		   canvas.drawLine(mRect.right, mRect.bottom, mRect.right-20, mRect.bottom+20, paint);
    	   }
       } 
    }
    
    public Rect getRect() {
		return mRect;
	}
    public int getOrientation() {
		return orientation;
	}
	
 }