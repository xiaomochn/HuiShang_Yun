package com.huishangyun.Channel.Plan;

import java.io.Serializable;
import java.util.Date;

public class PlanList implements Serializable{

	private static final long serialVersionUID = 149301752464403198L;
	private Integer ID;
	private String Title;
	private String AddDateTime;
	private String Manager_Name;	
	
	private String Action;
	private String tittle;//主题
	private String StartDate;//开始时间
	private String EndDate;//结束时间
	private Integer Manager_ID;
	
	private Integer Plan_ID;//计划id
	private String BelongDate;//计划日期
	private String Member_IDS;//客户编号组
	
	private String OperationTime;//查询时间
	private String Member_Name;//客户名称
	private Integer Member_ID;//客户编号
	private String Address;//客户地址
	private boolean IsVisited;
	private String Array_ID;
	
	
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getArray_ID() {
		return Array_ID;
	}
	public void setArray_ID(String array_ID) {
		Array_ID = array_ID;
	}
	public String getMember_Name() {
		return Member_Name;
	}
	public void setMember_Name(String member_Name) {
		Member_Name = member_Name;
	}
	public int getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(int member_ID) {
		Member_ID = member_ID;
	}
	public boolean isIsVisited() {
		return IsVisited;
	}
	public void setIsVisited(boolean isVisited) {
		IsVisited = isVisited;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public Integer getPlan_ID() {
		return Plan_ID;
	}
	public void setPlan_ID(Integer plan_ID) {
		Plan_ID = plan_ID;
	}
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
	public String getMember_IDS() {
		return Member_IDS;
	}
	public void setMember_IDS(String member_IDS) {
		Member_IDS = member_IDS;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public String getTittle() {
		return tittle;
	}
	public void setTittle(String tittle) {
		this.tittle = tittle;
	}
	
	public String getStartDate() {
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}

}
