package com.huishangyun.Util;

public class ServiceMenu {
	private Integer ID;
	private Integer Server_ID;
	private String Name;
	private String Url;
	private Integer ParentID;
	private Integer Sequence;
	private Boolean Status;
	private String OperationTime;
	private Boolean IsLogin ;

	public Boolean getLogin() {
		return IsLogin;
	}

	public void setLogin(Boolean login) {
		IsLogin = login;
	}

	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getServer_ID() {
		return Server_ID;
	}
	public void setServer_ID(Integer server_ID) {
		Server_ID = server_ID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public Integer getSequence() {
		return Sequence;
	}
	public void setSequence(Integer sequence) {
		Sequence = sequence;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
}
