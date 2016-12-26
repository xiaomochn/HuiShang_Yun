package com.huishangyun.model;

import android.R.integer;

public class ContactBean {
	private String JID;
	private String pinyin;
	private int ID;
	private String Name;
	private String letter;
	private boolean isgroup;
	private int contact_type; 
	private int G_ID;
	private String department;
	private String signature = "";
	private String phoneNum;
	private int isKehu; 
	private String RealName;
	private String Photo;
	
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public int getIsKehu() {
		return isKehu;
	}
	public void setIsKehu(int isKehu) {
		this.isKehu = isKehu;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public int getG_ID() {
		return G_ID;
	}
	public void setG_ID(int g_ID) {
		G_ID = g_ID;
	}
	public int getContact_type() {
		return contact_type;
	}
	public void setContact_type(int contact_type) {
		this.contact_type = contact_type;
	}
	public boolean isIsgroup() {
		return isgroup;
	}
	public void setIsgroup(boolean isgroup) {
		this.isgroup = isgroup;
	}
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public String getJID() {
		return JID;
	}
	public void setJID(String jID) {
		JID = jID;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
}
