package com.huishangyun.Office.Attendance;

public class OfficeList {
	private Integer ID;//id
	private Integer Company_ID;//公司id
	private Integer Manager_ID;//操作人
	private String Operate;//考勤类型
	private String Note;//备注
	private String Picture;//图片
	private Integer Schedule_ID;//行政班id
	private String Action;//操作Insert：新增Update：修改
	private String Manager_Name;
	private String BelongMonth;//所属月份
	private Integer Days;//总天数
	private Float Late;//迟到天数
	private Float Early;//早退
	private Float Leave;//请假
	private Float Travel;//出差
	private Float Normal;//正常出勤天数
	private Double Lng;//经度
	private Double Lat;//维度
	private String Loc;//地理位置
	
	
	
	public Double getLng() {
		return Lng;
	}
	public void setLng(Double lng) {
		Lng = lng;
	}
	public Double getLat() {
		return Lat;
	}
	public void setLat(Double lat) {
		Lat = lat;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public String getBelongMonth() {
		return BelongMonth;
	}
	public void setBelongMonth(String belongMonth) {
		BelongMonth = belongMonth;
	}
	public Integer getDays() {
		return Days;
	}
	public void setDays(Integer days) {
		Days = days;
	}
	public float getLate() {
		return Late;
	}
	public void setLate(float late) {
		Late = late;
	}
	public float getEarly() {
		return Early;
	}
	public void setEarly(float early) {
		Early = early;
	}
	public float getLeave() {
		return Leave;
	}
	public void setLeave(float leave) {
		Leave = leave;
	}
	public float getTravel() {
		return Travel;
	}
	public void setTravel(float travel) {
		Travel = travel;
	}
	public float getNormal() {
		return Normal;
	}
	public void setNormal(float normal) {
		Normal = normal;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public Integer getCompany_ID() {
		return Company_ID;
	}
	public void setCompany_ID(Integer company_ID) {
		Company_ID = company_ID;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public String getOperate() {
		return Operate;
	}
	public void setOperate(String operate) {
		Operate = operate;
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
	public Integer getSchedule_ID() {
		return Schedule_ID;
	}
	public void setSchedule_ID(Integer schedule_ID) {
		Schedule_ID = schedule_ID;
	}
	
	
}
