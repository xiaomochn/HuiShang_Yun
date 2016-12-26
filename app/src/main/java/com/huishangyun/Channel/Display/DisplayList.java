package com.huishangyun.Channel.Display;

public class DisplayList {
	
	private String Action;
	//新增数据
	private Integer ID;//编号
	private Integer Member_ID;//客户编号
	private String Note;//备注
	private Float Lng;//经度
	private Float Lat;//纬度
	private String Loc;//地理位置
	private String Picture;//图片地址#分隔
	private Integer Manager_ID;//人员编号
	
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public Float getLng() {
		return Lng;
	}
	public void setLng(Float lng) {
		Lng = lng;
	}
	public Float getLat() {
		return Lat;
	}
	public void setLat(Float lat) {
		Lat = lat;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
	}
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	
	

}
