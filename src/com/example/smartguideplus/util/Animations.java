package com.example.smartguideplus.util;




import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.smartguideplus.R;



public class Animations {

	Context context;
	
	/////////////////////////////////////////////////////////////////////////////////
	//viewer
	Animation anim_ImgV_background_start;
	Animation anim_ImgV_background_end; 
	
	Animation[] anim_ImgV_Action_start;
	
	Animation anim_fadein;
	Animation anim_fadeout;
	
	/////////////////////////////////////////////////////////////////////////////////
	//maker
	Animation anim_activebar;
	Animation anim_recording;
	
	////////////////////////////////////////////////////////////////////////////////
	
	View m_view;
	public Animations(Context context) {
		this.context = context;
		
		anim_ImgV_background_start = AnimationUtils.loadAnimation(this.context, R.anim.imgvstart);
		anim_ImgV_background_end = AnimationUtils.loadAnimation(this.context, R.anim.imgvend);
		
		anim_fadein = AnimationUtils.loadAnimation(this.context, R.anim.fadein);
		anim_fadeout = AnimationUtils.loadAnimation(this.context, R.anim.fadeout);
		
		anim_ImgV_Action_start = new Animation[8];
		
		anim_ImgV_Action_start[0] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart1);
		anim_ImgV_Action_start[1] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart2);
		anim_ImgV_Action_start[2] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart31);
		anim_ImgV_Action_start[3] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart32);
		anim_ImgV_Action_start[4] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart33);
		anim_ImgV_Action_start[5] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart34);
		anim_ImgV_Action_start[6] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart4);
		anim_ImgV_Action_start[7] = AnimationUtils.loadAnimation(this.context, R.anim.imgvactionstart5);
		
		for(int i = 0; i < 8; i++)
			anim_ImgV_Action_start[i].setRepeatCount(1);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		anim_activebar = AnimationUtils.loadAnimation(this.context, R.anim.makerbar);
	
		anim_recording = AnimationUtils.loadAnimation(this.context, R.anim.recording);
		
	}
	
	public void startAnim_ImgV_background_start(View view) {
		this.m_view = view;
		this.m_view.startAnimation(anim_ImgV_background_start);
	}
	
	public void startAnim_ImgV_background_end(View view) {
		this.m_view = view;
		this.m_view.startAnimation(anim_ImgV_background_end);
	}
	
	public void startAnim_ImgV_action_start(View view, int type) {
		this.m_view = view;
		this.m_view.startAnimation(anim_ImgV_Action_start[type]);
	}
	
	public void stopAnim_ImgV_action(View view) {
		this.m_view = view;
		this.m_view.clearAnimation();
	}

	
	//////////////////////////////////////////////////////////////////////////////////
	public void startAnim_ActiveBar(View view) {
		this.m_view = view;
		this.m_view.startAnimation(anim_activebar);
	}
	
	public void startAnim_Recording(View view) {
		this.m_view = view;
		this.m_view.startAnimation(anim_recording);
	}
	/*anim_ImgV_start.setAnimationListener(new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {}			
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			m_view.startAnimation(anim_ImgV_end);
		}
	});*/
	
	/*	anim_ImgV_Action_start[0].setAnimationListener(new AnimationListener() {
	@Override
	public void onAnimationStart(Animation animation) {}			
	@Override
	public void onAnimationRepeat(Animation animation) {}
	@Override
	public void onAnimationEnd(Animation animation) {
		m_view.startAnimation(anim_fadein);
	}
});*/
}
