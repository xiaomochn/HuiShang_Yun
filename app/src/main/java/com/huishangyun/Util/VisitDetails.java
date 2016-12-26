package com.huishangyun.Util;

import java.io.Serializable;


public class VisitDetails implements Serializable {
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -9134166128339618453L;
	private Integer ID;
	private String Member_Name;
	private String Member_Adress;
	private String Loc;
	private String Note;
	private String Picture;
	private String BelongDate;
	private String AddDateTime;
	private String Manager_Name;
	private String Picture1;
	private String Picture2;
	private String Picture3;
	private Integer index;
	private Boolean IsAdd;
	private String Type;

	private String Tags;//标签集合
	private String Result;//返回结果

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getTags() {
		return Tags;
	}

	public void setTags(String tags) {
		Tags = tags;
	}

	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public Boolean getIsAdd() {
		return IsAdd;
	}
	public void setIsAdd(Boolean isAdd) {
		IsAdd = isAdd;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getPicture1() {
		return Picture1;
	}
	public void setPicture1(String picture1) {
		Picture1 = picture1;
	}
	public String getPicture2() {
		return Picture2;
	}
	public void setPicture2(String picture2) {
		Picture2 = picture2;
	}
	public String getPicture3() {
		return Picture3;
	}
	public void setPicture3(String picture3) {
		Picture3 = picture3;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getMember_Name() {
		return Member_Name;
	}
	public void setMember_Name(String member_Name) {
		Member_Name = member_Name;
	}
	public String getMember_Adress() {
		return Member_Adress;
	}
	public void setMember_Adress(String member_Adress) {
		Member_Adress = member_Adress;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
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
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
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
	
	

}
