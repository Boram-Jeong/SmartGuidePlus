package com.example.smartguideplus.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable{
	int rid;
	String user_id;
	String title;
	String body;
	boolean accept;
	String gidx;
	
	public Request(Parcel in) {
		readFromParcel(in);
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public boolean isAccept() {
		return accept;
	}
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	public String getGidx() {
		return gidx;
	}
	public void setGidx(String gidx) {
		this.gidx = gidx;
	}
	
	@Override
	public String toString() {
		return "Request [rid=" + rid + ", user_id=" + user_id + ", title="
				+ title + ", body=" + body + ", accept=" + accept + ", gidx="
				+ gidx + "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(rid);
		dest.writeString(user_id);
		dest.writeString(title);
		dest.writeString(body);
		dest.writeByte((byte) (accept ? 1 : 0));  
		dest.writeString(gidx);
	}
	
	private void readFromParcel(Parcel in) {
		rid = in.readInt();
		user_id = in.readString();
		title = in.readString();
		body = in.readString();
		accept = in.readByte() != 0;    
		gidx = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Request createFromParcel(Parcel in) {
			return new Request(in);
		}

		public Request[] newArray(int size) {
			return new Request[size];
		}
	};

}
