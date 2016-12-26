package com.huishangyun.Channel.Competing;

/**
 * 竞品历史数据保存
 * @author xsl
 *
 */
public class CompetingHostoryList {
	private Integer ID;
	private Integer isSubmit;
	private Integer ManagerID;
	private String CustormerName;
	private Integer CustormerID;
	private String PhotoUrl;
	private String UpUrl;
	private String Note;
	private String Brand;//品牌
	private String MyBrand;//本品
	
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
