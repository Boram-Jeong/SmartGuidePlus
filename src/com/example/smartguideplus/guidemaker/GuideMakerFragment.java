package com.example.smartguideplus.guidemaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartguideplus.R;

@SuppressLint("ValidFragment")
public class GuideMakerFragment extends Fragment {
	private Button btnStartGuideMaking;
	
	Context mContext;
	
	public GuideMakerFragment(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.guide_maker, null);

		btnStartGuideMaking = (Button) view.findViewById(R.id.btnStartGuideMaking);
		btnStartGuideMaking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new  Intent(getActivity(), addGuideInfoActivity.class);
				startActivity(intent);
			}
		});
		
    	return view;
	}

}
