package com.example.smartguideplus.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Guide implements Parcelable {
	int idx;

	String gidx;

	String creator;
	String date;

	String image;
	String name;
	String description;

	String device;
	String os;

	int width;
	int height;

	int download;

	Bitmap btmImage;
	int limit;

	public Guide() {
		// TODO Auto-generated constructor stub
	}

	public void setBtmImage(Bitmap btmImage) {
		this.btmImage = btmImage;
	}
	
	public Bitmap getBtmImage() {
		return btmImage;
	}

	public Guide(Parcel in) {
		readFromParcel(in);
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public String getGidx() {
		return gidx;
	}

	public void setGidx(String gidx) {
		this.gidx = gidx;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "Guide [idx=" + idx + ", gidx=" + gidx + ", creator=" + creator
				+ ", date=" + date + ", image=" + image + ", name=" + name
				+ ", description=" + description + ", device=" + device
				+ ", os=" + os + ", width=" + width + ", height=" + height
				+ ", download=" + download + ", btmImage=" + btmImage
				+ ", limit=" + limit + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idx);
		dest.writeString(gidx);
		dest.writeString(creator);
		dest.writeString(date);
		dest.writeString(image);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(device);
		dest.writeString(os);
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeInt(download);
	}

	private void readFromParcel(Parcel in) {
		idx = in.readInt();
		gidx = in.readString();
		creator = in.readString();
		date = in.readString();
		image = in.readString();
		name = in.readString();
		description = in.readString();
		device = in.readString();
		os = in.readString();
		width = in.readInt();
		height = in.readInt();
		download = in.readInt();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Guide createFromParcel(Parcel in) {
			return new Guide(in);
		}

		public Guide[] newArray(int size) {
			return new Guide[size];
		}
	};

}
