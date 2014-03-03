package com.example.smartguideplus.util;

import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class RootHelper {
	
	public static boolean execAsRoot(){

		boolean retval = false;

		try{
			Process suProcess = Runtime.getRuntime().exec("su\n");
			DataOutputStream os = 
					new DataOutputStream(suProcess.getOutputStream());

			//os.writeBytes(cmd + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();

			try{
				int suProcessRetval = suProcess.waitFor();
				if (255 != suProcessRetval){
					// Root access granted
					retval = true;
				}else{
					// Root access denied
					retval = false;
				}
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
