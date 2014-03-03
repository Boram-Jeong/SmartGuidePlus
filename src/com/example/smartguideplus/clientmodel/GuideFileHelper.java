package com.example.smartguideplus.clientmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;

// ���̵� ��� ����
// 1. currentscene�� ��� �����ؾ� �Ѵ�. ��ȯ��ư�� currentScene�� ���� �ٲ���ض�!
// 2. Scene�߰� �� ����
//    Scene�߰��� addScene() ȣ��. -> Scene�� �߰��Ǹ� ���ο� Scene���� ȭ���� ���;� �Ǹ�, currentScene�� �ڵ������� ���� Scene���� �Ѿ
//    Scene������ deleteScene() ȣ�� -> Scene�� �����Ǹ� �� ���� Scene�̾�� Scene ȭ���� ���;ߵǸ�, �װ��� CurrentScene���� ��
// 3. Action �߰�
//    maker - addAction(type, rect, orientation)�Լ�ȣ��. ���� ������� �ʴ� ��� null����������ϸ� ��
//    viewer - getAction() ȣ���� ���� action�� ������, ��� �� �˾Ƽ� parsing�ؼ� ����ؾ� ��.
public class GuideFileHelper {
	
	GuideFile guidefile;
	
	int currentSceneNum;
	
	public GuideFileHelper() {
		
		init();
	}
	
	public void init() {
		guidefile = new GuideFile();
		currentSceneNum = -1;
	}
	
	////////////////////////////////////
	//Scene
	
	// �߰� �� ��� ���� ���̴� Scene �տ� ������� ����!
	public void addScene() {
		currentSceneNum++;
		guidefile.addScene(currentSceneNum);
	}
	public void addScene(Scene scene) {
		currentSceneNum++;
		guidefile.addScene(currentSceneNum, scene);
	}
	
	// ���� ��� ���� Scene�� currentScene���� �ڵ���ȯ�ǵ��� ���� �� ��!
	public boolean deleteScene() {
		boolean res = false;
		if(!guidefile.isEmpty()) {
			
			guidefile.deleteScene(currentSceneNum);
			if(currentSceneNum >0)
				currentSceneNum --;
			else if(guidefile.getSceneSize()==0 && currentSceneNum == 0)
				currentSceneNum --;
				
			res = true;
		}else {
			res = false;
			System.out.println("�����Ͱ� ����ϴ�.");
		}
		return res;
	}
	
	public boolean isTransformable(int transDirection) {
		boolean res = false;
		if(transDirection == 0) {
			if(!guidefile.isEmpty() && currentSceneNum > 0) {
				res = true;
			}
		} else {
			if(guidefile.getSceneSize() != (currentSceneNum+1)) {
				res = true;
			}
		}
		return res;
	}
	//before -> 0  // next -> 1   
	public void setCurrentSceneNum(int transDirection) {
		if(transDirection == 0) {
			if(isTransformable(transDirection)) {
				currentSceneNum--;
			}
		} else {
			if(isTransformable(transDirection)) {
				currentSceneNum++;
			}
		}
	}
	
	public int getCurrentSceneNum() {
			
			return currentSceneNum; 
		}
	///////////////////////////////////
	
	
	////////////////////////////////////
	//Action Mode
	public void addAction(int type, Rect rect, int orientation)
	{
		guidefile.getScene(currentSceneNum).addActionMode(type,  rect,  orientation);
	}
	
	public ActionMode getAction()
	{
		return guidefile.getScene(currentSceneNum).getActionMode();
	}
	
	public boolean isActionMode() {
		boolean res = false;
		res = guidefile.getScene(currentSceneNum).isActionMode();
		return res;
	}
	
	public void deleteActionMode()
	{
		guidefile.getScene(currentSceneNum).deleteActionMode();
	}
	////////////////////////////////////
	//Data Info
	
	public void addCapture(String fileName) {
		guidefile.getScene(currentSceneNum).addDataInfo(0, fileName, "", null);
	}
	public void addVoice(String fileName) {
		guidefile.getScene(currentSceneNum).addDataInfo(1, fileName, "", null);
	}
	public void addNote(String fileName, String text, Rect rect) {
		guidefile.getScene(currentSceneNum).addDataInfo(2, fileName, text, rect);
	}
	public void addNote_1(String fileName, String text, Rect rect) {
		guidefile.getScene(currentSceneNum).addDataInfo(3, fileName, text, rect);
	}
	public void addNote_2(String fileName, String text, Rect rect) {
		guidefile.getScene(currentSceneNum).addDataInfo(4, fileName, text, rect);
	}
	
	public boolean isDataInfo(int type) {
		return guidefile.getScene(currentSceneNum).isDataInfo(type);
	}
	
	public DataInfo getDataInfo(int type)
	{
		return guidefile.getScene(currentSceneNum).getDataInfo(type);
	}
	
	public void deleteDataInfo(int type) {
		guidefile.getScene(currentSceneNum).deleteDataInfo(type);
	}
	
	////////////////////////////////////
	
	

	@SuppressLint("SdCardPath")
	public void save(int gidx) {
		String saveText = "";
		for(int i = 0; i < guidefile.getSceneSize(); i++) {
			saveText += new Gson().toJson(guidefile.getScene(i));
			saveText = saveText+"*";
		}
		
		
		String str = Environment.getExternalStorageState();
		String dirPath = "";
        if ( str.equals(Environment.MEDIA_MOUNTED)) {
        
          dirPath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx; 
          File file = new File(dirPath); 
          if( !file.exists() ) 
            file.mkdirs();
        }
        
        String fileName = "GuideFile.txt";
        String filePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/"; 
        
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
	}
	
	public void setMaxScene() {
		for(int i = guidefile.getSceneSize()-(getCurrentSceneNum()+1); i < guidefile.getSceneSize(); i++)
			setCurrentSceneNum(1);
	}
	
	public int getSceneSize() {
		return guidefile.getSceneSize();
	}
	@SuppressLint("SdCardPath")
	public void load(int gidx) {
		
		String saveText = "";
		        
        String fileName = "GuideFile.txt";
        String filePath = "/sdcard/SmartGuidePlus/"+gidx+"/"+gidx +"/"; 

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

        	Log.i("LoadTest", saveText);
        	String[] splitSaveText = saveText.split("\\*");
        	
        	for(int i = 0; i < splitSaveText.length; i++) {
        		Log.i("splitSaveText", splitSaveText[i]);
        		Scene scene = new Gson().fromJson(splitSaveText[i], Scene.class);
    			addScene(scene);
        	}
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        } 
		
		
		for(int i = 0; i < guidefile.getSceneSize(); i++)
			setCurrentSceneNum(0);
	}
	
	
	public void testPrint() {
		for(int i = 0; i< guidefile.getSceneSize(); i++)
			System.out.println(i+" ");
		System.out.println("curScene : "+ getCurrentSceneNum() );
		System.out.println("\n");
	}
	
}
