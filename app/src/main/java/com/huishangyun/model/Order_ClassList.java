package com.huishangyun.model;

import android.R.integer;

/**
 * 产品分类列表实体类
 * @author Administrator
 *
 */
public class Order_ClassList {
	private Integer ID;
	private String Name;
	private Integer PartentID;
	
	public Integer getPartentID() {
		return PartentID;
	}
	public void setPartentID(Integer partentID) {
		PartentID = partentID;
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
			
}
