package com.huishangyun.Office.Location;

import java.io.Serializable;

public class LocationList implements Serializable{

	private Double Longitude;//经度
	private Double Latitude;//维度
	private Integer Type;//Type = 1;固定值表示 手机定位
	private Integer Manager_ID;//当前用户ID;
	private String AddTime;//获取位置时间 （格式：2015-03-08 14:00:01）
	private String Address;//定位获取的地址信息
	private Integer Status;//（0：失败，1：成功）;
	private String Reason;//失败原因;
	private Double Lat;//未纠偏GPS原始经度
	private Double Lng;//未纠偏GPS原始纬度
	
	//ID为用户ID，RealName为用名姓名，Location为定位地址，AddDatetime为定位时间，LocationType:为定位类型（先不考虑现实，1：手机定位，2：基站定位）
	//这里的基站指的是运营商基站蜂窝定位。

	private Integer ID;
	private String RealName;
	private String Photo;
	private Integer LocationType;
	private String Mobile;
	private String Department_Name;
	private String Location;
	private String AddDateTime;
	
	
	
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public Double getLongitude() {
		return Longitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public String getAddTime() {
		return AddTime;
	}
	public void setAddTime(String addTime) {
		AddTime = addTime;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	public String getReason() {
		return Reason;
	}
	public void setReason(String reason) {
		Reason = reason;
	}
	public Double getLat() {
		return Lat;
	}
	public void setLat(Double lat) {
		Lat = lat;
	}
	public Double getLng() {
		return Lng;
	}
	public void setLng(Double lng) {
		Lng = lng;
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
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	public Integer getLocationType() {
		return LocationType;
	}
	public void setLocationType(Integer locationType) {
		LocationType = locationType;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getDepartment_Name() {
		return Department_Name;
	}
	public void setDepartment_Name(String department_Name) {
		Department_Name = department_Name;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	
	
	
}
