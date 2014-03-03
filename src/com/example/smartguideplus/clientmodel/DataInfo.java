package com.example.smartguideplus.clientmodel;

import android.graphics.Rect;


public class DataInfo {
	int type;
	String text;
	Rect rect;
	String fileName;

	public DataInfo(int type, String filename, String text, Rect rect){
		this.type = type;
		this.fileName = filename;
		this.text = text;
		this.rect = rect;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Rect getRect() {
		return rect;
	}
	public void setRect(int left, int top, int right, int bottom) {
		this.rect.left = left;
		this.rect.top = top;
		this.rect.right = right;
		this.rect.bottom = bottom;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "DataInfo [type=" + type + ", text=" + text + ", rect=" + rect
				+ ", fileName=" + fileName + "]";
	}
}
