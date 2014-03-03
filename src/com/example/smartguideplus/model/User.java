package com.example.smartguideplus.model;

public class User {
	int uidx;
	String user_id;
	String name;
	String phone;
	String regitid;
	public int getUidx() {
		return uidx;
	}
	public void setUidx(int uidx) {
		this.uidx = uidx;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRegitid() {
		return regitid;
	}
	public void setRegitid(String regitid) {
		this.regitid = regitid;
	}
	@Override
	public String toString() {
		return "User [uidx=" + uidx + ", user_id=" + user_id + ", name=" + name
				+ ", phone=" + phone + ", regitid=" + regitid + "]";
	}

}
