package com.huishangyun.Channel.Visit;

/**
 * 拜访历史数据保存
 * @author xsl
 *
 */
public class VisitHostoryList {
	private Integer ID;
	private Integer isSubmit;
	private Integer ManagerID;
	private String CustormerName;
	private Integer CustormerID;
	private Integer isReport;
	private String ReportDate;
	private String PhotoUrl;
	private String UpUrl;
	private String Note;
	private String Brand;//品牌
	private String MyBrand;//本品
	private String SignTime;//签到时间
	private String SignLoc;//签到位置
	private String Tags;//标签名称
	private String TagID;//标签ID
	private String Result;//返回结果

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getSignLoc() {
		return SignLoc;
	}

	public void setSignLoc(String signLoc) {
		SignLoc = signLoc;
	}

	public String getTags() {
		return Tags;
	}

	public void setTags(String tags) {
		Tags = tags;
	}

	public String getTagID() {
		return TagID;
	}

	public void setTagID(String tagID) {
		TagID = tagID;
	}

	public String getSignTime() {
		return SignTime;
	}

	public void setSignTime(String signTime) {
		SignTime = signTime;
	}

	public String getBrand() {
		return Brand;
	}
	public void setBrand(String brand) {
		Brand = brand;
	}
	public String getMyBrand() {
		return MyBrand;
	}
	public void setMyBrand(String myBrand) {
		MyBrand = myBrand;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getIsSubmit() {
		return isSubmit;
	}
	public void setIsSubmit(Integer isSubmit) {
		this.isSubmit = isSubmit;
	}
	public Integer getManagerID() {
		return ManagerID;
	}
	public void setManagerID(Integer managerID) {
		ManagerID = managerID;
	}
	public String getCustormerName() {
		return CustormerName;
	}
	public void setCustormerName(String custormerName) {
		CustormerName = custormerName;
	}
	public Integer getCustormerID() {
		return CustormerID;
	}
	public void setCustormerID(Integer custormerID) {
		CustormerID = custormerID;
	}
	public Integer getIsReport() {
		return isReport;
	}
	public void setIsReport(Integer isReport) {
		this.isReport = isReport;
	}
	public String getReportDate() {
		return ReportDate;
	}
	public void setReportDate(String reportDate) {
		ReportDate = reportDate;
	}
	public String getPhotoUrl() {
		return PhotoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		PhotoUrl = photoUrl;
	}
	public String getUpUrl() {
		return UpUrl;
	}
	public void setUpUrl(String upUrl) {
		UpUrl = upUrl;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	
	
}
