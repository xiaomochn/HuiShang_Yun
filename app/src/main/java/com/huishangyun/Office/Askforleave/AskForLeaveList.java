package com.huishangyun.Office.Askforleave;

public class AskForLeaveList {

	private Integer ID;
	private Integer Manager_ID;//登入人id
	private String Action;
	private String Type;//请假类型：病假、婚假。。。。。。。。
	private String StartTime;//起始时间
	private String EndTime;//结束时间
	private Float Days;//天数
	private String Note;//事由
	private String Picture;//照片
	private String AddDateTime;//添加时间
	private String Manager_Name;//登入人姓名
	private String Manager_Photo;//人员头像地址
	
	
	public Float getDays() {
		return Days;
	}
	public void setDays(Float days) {
		Days = days;
	}
	public String getManager_Photo() {
		return Manager_Photo;
	}
	public void setManager_Photo(String manager_Photo) {
		Manager_Photo = manager_Photo;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
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
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	
	

}
