package com.huishangyun.Channel.Display;

/**
 * 陈列历史数据保存
 * @author xsl
 *
 */
public class DisplayHostoryList {
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
