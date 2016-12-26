package com.huishangyun.Office.Summary;

public class SummaryDateList {
	private String Action;
	private Integer ID;
	private Integer Manager_ID;//登入人id
	private String Works;//当日工作
	private String Plans;//明日计划
	private String Tips;//心得体会
	private String BelongDate;//所属日期
	private Boolean IsAdd;//是否补报
	private String Manager_Name;
	private String Manager_Photo;
	private String AddDateTime;
	private String OFUserName;

	public String getOFUserName() {
		return OFUserName;
	}

	public void setOFUserName(String OFUserName) {
		this.OFUserName = OFUserName;
	}

	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public String getManager_Photo() {
		return Manager_Photo;
	}
	public void setManager_Photo(String manager_Photo) {
		Manager_Photo = manager_Photo;
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
	public String getWorks() {
		return Works;
	}
	public void setWorks(String works) {
		Works = works;
	}
	public String getPlans() {
		return Plans;
	}
	public void setPlans(String plans) {
		Plans = plans;
	}
	public String getTips() {
		return Tips;
	}
	public void setTips(String tips) {
		Tips = tips;
	}
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
	public Boolean getIsAdd() {
		return IsAdd;
	}
	public void setIsAdd(Boolean isAdd) {
		IsAdd = isAdd;
	}
	
    
}
