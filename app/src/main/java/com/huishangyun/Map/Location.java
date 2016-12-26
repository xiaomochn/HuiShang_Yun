package com.huishangyun.Map;

import java.io.Serializable;

import android.R.integer;

public class Location implements Serializable{
	private Double Latitude; //维度
	private Double Longitude; //经度
	private String Type;
	private String OFUserName;
	
	public String getOFUserName() {
		return OFUserName;
	}
	public void setOFUserName(String oFUserName) {
		OFUserName = oFUserName;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	public Double getLongitude() {
		return Longitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}
	
	/**
	 * 生成字符串
	 * @return
	 */
	public String getLocationStr() {
		return getLatitude() + "*" + getLongitude() + "*" + getType() + "*" + getOFUserName();
	}
	
	
}
