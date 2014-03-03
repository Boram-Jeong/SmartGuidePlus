package com.example.smartguideplus.util;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

public class RecordHelper {

	MediaRecorder m_recorder;
	MediaPlayer m_player;
	
	public RecordHelper() {
	}
	
	@SuppressLint("SdCardPath")
	public void startRec(String filename, int gidx) {
		
		
		String str = Environment.getExternalStorageState();
		String dirPath = null;
        if ( str.equals(Environment.MEDIA_MOUNTED)) {
        
        	dirPath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx + "/Record"; 
          File file = new File(dirPath); 
          if( !file.exists() )  // ���ϴ� ��ο� ����� �ִ��� Ȯ��
            file.mkdirs();
        }
        
		if(m_recorder == null)
		{
			m_recorder = new MediaRecorder();
			m_recorder.reset();
		}else {
			m_recorder.reset();
		}
		
		try {
		    m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    m_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    m_recorder.setOutputFile(dirPath+"/"+filename+".3gp");
		    m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			m_recorder.prepare();
		    m_recorder.start();
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
	}
	
	// ��������
	public void stopRec() {
		
		try {
			m_recorder.reset();
			m_recorder.stop();
		} catch (Exception e) {
		} finally {
			m_recorder.release();
			m_recorder = null;
		}
	}

    @SuppressLint("SdCardPath")
	public void playAudio(String filename, int gidx) {

    	String filePath = "/sdcard/SmartGuidePlus/";
		filePath = filePath + Integer.toString(gidx) + "/" + Integer.toString(gidx) + "/Record/"+filename + ".3gp";
    	
    	if (m_player == null)
    		m_player = new MediaPlayer();
    	else
    		m_player.reset();

    	try {
    		m_player.setDataSource(filePath);
    		m_player.prepare();
    		m_player.start();

    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    	} catch (SecurityException e) {
    		e.printStackTrace();
    	} catch (IllegalStateException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void stopAudio() {
    	try {
    		m_player.reset();
    		m_player.stop();
		} catch (Exception e) {
		} finally {
			m_player.release();
			m_player = null;
		}
    }
	
}
