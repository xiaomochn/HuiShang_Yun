package com.huishangyun.Channel.Customers;

import java.io.Serializable;

public class GroupModel implements Serializable{
	private String Name;
	private String Title;
	private boolean isLine;
	private Integer ID;
	private boolean isSelect;
	private String Pinyin;
	private Integer ParentID;
	
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public String getPinyin() {
		return Pinyin;
	}
	public void setPinyin(String pinyin) {
		Pinyin = pinyin;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public boolean isLine() {
		return isLine;
	}
	public void setLine(boolean isLine) {
		this.isLine = isLine;
	}
	
	
}
