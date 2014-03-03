package com.example.smartguideplus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class SmartGuidePlusInfo{
	public static String baseUrl = "http://210.118.74.142:8080/SmartGuidePlusServer/resources/";
	public static String imageUrl = "http://210.118.74.142:8080/SmartGuidePlusServer/resources/images/";
	public static String hostUrl = "http://210.118.74.142:8080/SmartGuidePlusServer/";
	public static String guideInfoFile = "guideInfo.json";
	public static String saveDir = "/sdcard/SmartGuidePlus/";
	public static String MODEL = Build.MODEL;
	public static String OS = String.valueOf(Build.VERSION.RELEASE);
	
	public static String getUserID(Context mContext){
		String user_id = null;
		
		SharedPreferences pref = mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE);
	    user_id = pref.getString("user_id", "");
		
		return user_id;
		
	}

}
