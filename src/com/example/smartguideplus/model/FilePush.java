package com.example.smartguideplus.model;

public class FilePush {
	int fidx;
	String sender;
	String receiver;
	String gidx;
//	String time;
	public int getFidx() {
		return fidx;
	}
	public void setFidx(int fidx) {
		this.fidx = fidx;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getGidx() {
		return gidx;
	}
	public void setGidx(String gidx) {
		this.gidx = gidx;
	}
	
	@Override
	public String toString() {
		return "FilePush [fidx=" + fidx + ", sender=" + sender + ", receiver="
				+ receiver + ", gidx=" + gidx + "]";
	}
//	public String getTime() {
//		return time;
//	}
//	public void setTime(String time) {
//		this.time = time;
//	}
//	@Override
//	public String toString() {
//		return "FilePush [fidx=" + fidx + ", sender=" + sender + ", receiver="
//				+ receiver + ", gidx=" + gidx + ", time=" + time + "]";
//	}
	
	
	
}
