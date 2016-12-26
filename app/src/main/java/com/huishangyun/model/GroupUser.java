package com.huishangyun.model;

public class GroupUser {
	private Integer Group_ID;
	private Integer User_ID;
	private String OperationTime;
	private Boolean Status;
	
	public Integer getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(Integer group_ID) {
		Group_ID = group_ID;
	}
	public Integer getUser_ID() {
		return User_ID;
	}
	public void setUser_ID(Integer user_ID) {
		User_ID = user_ID;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
}
