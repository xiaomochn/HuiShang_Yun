package com.huishangyun.model;

/**
 * 客户资料实体类
 * @author Pan
 *
 */
public class Members {
	private Integer ID;//id编号
	private String RealName;
	private Integer Level_ID;
	private String Level_Name;
	private String Group_ID;//组id
	private String Group_Name;//组名
	private Integer Type;//权限类型
	private String Contact;//联系人
	private String Address;//地址
	private Integer Manager_ID;
	private String Manager_Name;
	private Integer Department_ID;//部门id
	private String Department_Name;//部门名称
	private Integer Area_ID;
	private String Area_Name;
	private String Picture;//头像
	private String Note;//内容或者描述
	private String LoginName;//登录名
	private String OperationTime;//最后操作时间
	private String VisitTime;//拜访时间
	private String Mobile;
	private Boolean Status;
	private String OFUserName;
	private String Photo;
	
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	public String getOFUserName() {
		return OFUserName;
	}
	public void setOFUserName(String oFUserName) {
		OFUserName = oFUserName;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public Integer getLevel_ID() {
		return Level_ID;
	}
	public void setLevel_ID(Integer level_ID) {
		Level_ID = level_ID;
	}
	public String getLevel_Name() {
		return Level_Name;
	}
	public void setLevel_Name(String level_Name) {
		Level_Name = level_Name;
	}
	public String getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(String group_ID) {
		Group_ID = group_ID;
	}
	public String getGroup_Name() {
		return Group_Name;
	}
	public void setGroup_Name(String group_Name) {
		Group_Name = group_Name;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public String getContact() {
		return Contact;
	}
	public void setContact(String contact) {
		Contact = contact;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public Integer getDepartment_ID() {
		return Department_ID;
	}
	public void setDepartment_ID(Integer department_ID) {
		Department_ID = department_ID;
	}
	public String getDepartment_Name() {
		return Department_Name;
	}
	public void setDepartment_Name(String department_Name) {
		Department_Name = department_Name;
	}
	public Integer getArea_ID() {
		return Area_ID;
	}
	public void setArea_ID(Integer area_ID) {
		Area_ID = area_ID;
	}
	public String getArea_Name() {
		return Area_Name;
	}
	public void setArea_Name(String area_Name) {
		Area_Name = area_Name;
	}
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public String getVisitTime() {
		return VisitTime;
	}
	public void setVisitTime(String visitTime) {
		VisitTime = visitTime;
	}
	
}
