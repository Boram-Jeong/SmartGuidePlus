package com.example.smartguideplus.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.util.Log;

public class ScreenShotHelper {

	
	public ScreenShotHelper() {
	}
	
	@SuppressLint("SdCardPath")
	public void ScreenCapture(String filename, int gidx) {
		
        String path = "/sdcard/SmartGuidePlus/";
		path = path + Integer.toString(gidx) + "/" + Integer.toString(gidx) + "/Image";
		File file = new File(path);
		if( !file.exists() ) 
            file.mkdirs();
			
		path = path+ "/" +filename + ".png";
		file = new File(path);
        
        if(file.exists())
        	file.delete();
        
        if( !file.exists() ) {
			execAsRoot("/system/bin/screencap -p "+path);
        }
	}
	
	public static boolean execAsRoot(String cmd)
	{

		if(cmd==null || cmd.equals(""))
			throw new IllegalArgumentException();

		boolean retval = false;
		try{
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = 
					new DataOutputStream(suProcess.getOutputStream());

			os.writeBytes(cmd + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();

			try{
				//int suProcessRetval = suProcess.waitFor();
				/*if (255 != suProcessRetval){
					// Root access granted
					retval = true;
				}else{
					// Root access denied
					retval = false;
				}*/
			}catch (Exception ex){
				Log.i("ROOT", "can't get root access");
			}

		}catch (IOException ex){
			Log.i("ROOT", "can't get root access");
		}catch (SecurityException ex){
			Log.i("ROOT", "can't get root access");
		}catch (Exception ex){
			Log.i("ROOT", "can't get root access");
		}
			
		return retval;
	}
}
