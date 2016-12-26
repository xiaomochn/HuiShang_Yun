package com.huishangyun.model;

public class Departments {
	private Integer ID;
	private Integer ParentID;
	private String Name;
	private String Path;
	private Integer Sequence;
	private String OperationTime;
	private Boolean Status;
	
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public Integer getSequence() {
		return Sequence;
	}
	public void setSequence(Integer sequence) {
		Sequence = sequence;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	
	
}
