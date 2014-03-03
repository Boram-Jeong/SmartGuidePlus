package com.example.smartguideplus.clientmodel;

import android.graphics.Rect;


public class Scene {
	int sidx;
	ActionMode actionMode;
	// datainfo ->  0 : ĸ���̹���, 1 : ��������, 2 : �޸�����
	DataInfo Dinfo_capture;
	DataInfo Dinfo_Voice;
	DataInfo Dinfo_Note;
	DataInfo Dinfo_Note_1;
	DataInfo Dinfo_Note_2;
	boolean is_Action;
	boolean is_Capture;
	boolean is_Voice;
	boolean is_Note;
	boolean is_Note_1;
	boolean is_Note_2;
	
	public Scene(int sidx){
		this.sidx = sidx;
		is_Action = false;
		is_Capture = false;
		is_Voice = false;
		is_Note = false;
		is_Note_1 = false;
		is_Note_2 = false;
	}
	
	
	public int getSidx() {
		return sidx;
	}
	public void setSidx(int sidx) {
		this.sidx = sidx;
	}
	
	
	
	public ActionMode getActionMode() {
		return actionMode;
	}
	
	public void deleteActionMode() {
		is_Action = false;
	}
	
	public void addActionMode(int type, Rect rect, int orientation) {
		is_Action = true;
		if(actionMode == null)
			actionMode = new ActionMode(type, rect, orientation);
	}
	
	public boolean isActionMode() {
		boolean res = false;
		res = is_Action;
		
		return res;
	}
	
	
	public void addDataInfo(int type, String fileName, String text, Rect rect) {
		
		switch(type) {
		case 0 :
			is_Capture = true;
			if(Dinfo_capture == null)
				Dinfo_capture = new DataInfo(0, "", "", null);
			Dinfo_capture.setFileName(fileName);
			break;
		case 1 :
			is_Voice = true;
			if(Dinfo_Voice == null)	
				Dinfo_Voice = new DataInfo(1, "", "", null);
			Dinfo_Voice.setFileName(fileName);
			break;
		case 2 :
			is_Note = true;
			if(Dinfo_Note == null)
				Dinfo_Note = new DataInfo(2, "", "", rect);
			Dinfo_Note.setFileName(fileName);
			Dinfo_Note.setText(text);
			Dinfo_Note.setRect(rect.left, rect.top, rect.right, rect.bottom);
			break;
		case 3 :
			is_Note_1 = true;
			if(Dinfo_Note_1 == null)
				Dinfo_Note_1 = new DataInfo(3, "", "", rect);
			Dinfo_Note_1.setFileName(fileName);
			Dinfo_Note_1.setText(text);
			Dinfo_Note_1.setRect(rect.left, rect.top, rect.right, rect.bottom);
			break;
		case 4 :
			is_Note_2 = true;
			if(Dinfo_Note_2 == null)
				Dinfo_Note_2 = new DataInfo(4, "", "", rect);
			Dinfo_Note_2.setFileName(fileName);
			Dinfo_Note_2.setText(text);
			Dinfo_Note_2.setRect(rect.left, rect.top, rect.right, rect.bottom);
			break;
		}
	}
	
	// datainfo�� �����Ѵ�. �ϳ��� Scene�� �ִ� ���� 1���� ĸ���̹���, ��������, ��Ʈ������ ���� �� �� �ִ�.
	// 0 : CaptureImage
	// 1 : Voice Record
	// 2 : Note
	public void deleteDataInfo(int type) {
		switch(type) {
		case 0 :
			is_Capture = false;
			break;
		case 1 :
			is_Voice = false;
			break;
		case 2 :
			is_Note = false;
			break;
		case 3 :
			is_Note_1 = false;
			break;
		case 4 :
			is_Note_2 = false;
			break;
		}
	}
	
	public boolean isDataInfo(int type) {
		boolean res = false;
		
		switch(type) {
			case 0 :
				res = is_Capture;
				break;
			case 1 :
				res = is_Voice;
				break;
			case 2 :
				res = is_Note;
				break;
			case 3 :

				res = is_Note_1;

				break;
			case 4 :
				res = is_Note_2;
				break;
		}

		return res;
	}
	public DataInfo getDataInfo(int type) {
		switch(type) {
		case 0 :
			return Dinfo_capture;
		case 1 :
			return Dinfo_Voice;
		case 2 :
			return Dinfo_Note;
		case 3 :
			return Dinfo_Note_1;
		case 4 :
			return Dinfo_Note_2;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "abc";
	}	
}
