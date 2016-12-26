package com.huishangyun.model;

public class Order_address {
	private String Action;
	
	private Integer ID;
	private Integer Member_ID;
	private String Name;
	private String Job;
	private String Mobile;
	private String Tel;
	private String IMID;
	private String Email;
	private String Address;
	private String ZipCode;
	private String Post;
	private String Note;
	private boolean IsDefault;
	private boolean Status;
	private Integer Area_ID;
	
	
	public int getArea_ID() {
		return Area_ID;
	}
	public void setArea_ID(int area_ID) {
		Area_ID = area_ID;
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
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getJob() {
		return Job;
	}
	public void setJob(String job) {
		Job = job;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getTel() {
		return Tel;
	}
	public void setTel(String tel) {
		Tel = tel;
	}
	public String getIMID() {
		return IMID;
	}
	public void setIMID(String iMID) {
		IMID = iMID;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getZipCode() {
		return ZipCode;
	}
	public void setZipCode(String zipCode) {
		ZipCode = zipCode;
	}
	public String getPost() {
		return Post;
	}
	public void setPost(String post) {
		Post = post;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public boolean isIsDefault() {
		return IsDefault;
	}
	public void setIsDefault(boolean isDefault) {
		IsDefault = isDefault;
	}
	public boolean isStatus() {
		return Status;
	}
	public void setStatus(boolean status) {
		Status = status;
	}

		

}
