package com.huishangyun.Channel.Visit;
/**
 * 
 * @author xsl
 *
 */
public class VistList {
	
	private String Action;
	//新增数据
	private Integer ID;//编号
	private Integer Member_ID;//客户编号
	private String Note;//备注
	private Boolean IsAdd;//是否补报
	private String BelongDate;//上报日期
	private Double Lng;//经度
	private Double Lat;//纬度
	private String Loc;//地理位置
	private String Picture;//图片地址#分隔
	private Integer Manager_ID;//人员编号
	private String Type;//拜访类型

	private String TagID;//标签ID集合
	private String Tags ;//标签集合

	private Double SignLat;//签到维度
	private Double SignLng;//签到经度
	private String SignTime;//签到时间
	private String Result;//返回结果

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getSignTime() {
		return SignTime;
	}

	public void setSignTime(String signTime) {
		SignTime = signTime;
	}

	public Double getSignLat() {
		return SignLat;
	}

	public void setSignLat(Double signLat) {
		SignLat = signLat;
	}

	public Double getSignLng() {
		return SignLng;
	}

	public void setSignLng(Double signLng) {
		SignLng = signLng;
	}

	public String getTagID() {
		return TagID;
	}

	public void setTagID(String tagID) {
		TagID = tagID;
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
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public boolean isIsAdd() {
		return IsAdd;
	}
	public void setIsAdd(boolean isAdd) {
		IsAdd = isAdd;
	}
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
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
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	
	
}
