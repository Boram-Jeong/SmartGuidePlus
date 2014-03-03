package com.example.smartguideplus.clientmodel;

import android.graphics.Rect;


public class ActionMode {
	int type;
	Rect rect;
	int orientation;
	public ActionMode(int type, Rect rect, int orientation){
		this.type = type;
		this.rect = rect;
		this.orientation = orientation;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "ActionMode [type=" + type + ", rect=" + rect + ", orientation="
				+ orientation + "]";
	}
}
