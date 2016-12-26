package com.huishangyun.model;

public class IMGroup {
	private Integer ID;
	private String Name;
	private String Photo;
	private String Note;
	private Integer Type;
	private Integer Approval;
	private String Owner;//群主
	private String OpenID;//群ID
	private Integer Company_ID;
	private Boolean Status;
	private String AddDateTime;
	private String OperationTime;
	
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
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public Integer getApproval() {
		return Approval;
	}
	public void setApproval(Integer approval) {
		Approval = approval;
	}
	public String getOwner() {
		return Owner;
	}
	public void setOwner(String owner) {
		Owner = owner;
	}
	public String getOpenID() {
		return OpenID;
	}
	public void setOpenID(String openID) {
		OpenID = openID;
	}
	public Integer getCompany_ID() {
		return Company_ID;
	}
	public void setCompany_ID(Integer company_ID) {
		Company_ID = company_ID;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public String getOperationName() {
		return OperationName;
	}
	public void setOperationName(String operationName) {
		OperationName = operationName;
	}
	private String OperationName;
}
