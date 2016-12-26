package com.huishangyun.model;

import android.R.integer;

/**
 * 产品列表实体类
 * @author Administrator
 *
 */
public class Order_GoodsList {
	
	private Integer ID;
	private String Name;
	private Float SalePrice;
	private String Unit_Name;
	private Integer Class_ID;
	private Integer Unit_ID;
	private String No;
	private String Info;	
	private String SmallImg;
	private String MiddleImg;
	private String BigImg;
	private Integer Sequence;
	private String OperationTime;
	private Boolean Status;
	
	public Float getSalePrice() {
		return SalePrice;
	}
	public void setSalePrice(Float salePrice) {
		SalePrice = salePrice;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public Integer getUnit_ID() {
		return Unit_ID;
	}
	public void setUnit_ID(Integer unit_ID) {
		Unit_ID = unit_ID;
	}
	public String getNo() {
		return No;
	}
	public void setNo(String no) {
		No = no;
	}
	public Integer getSequence() {
		return Sequence;
	}
	public void setSequence(Integer sequence) {
		Sequence = sequence;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	private Float Quantity;//清单列表获取到产品数量		
	public Float getQuantity() {
		return Quantity;
	}
	public void setQuantity(Float quantity) {
		Quantity = quantity;
	}
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getUnit_Name() {
		return Unit_Name;
	}
	public void setUnit_Name(String unit_Name) {
		Unit_Name = unit_Name;
	}
	
	
	public Integer getClass_ID() {
		return Class_ID;
	}
	public void setClass_ID(Integer class_ID) {
		Class_ID = class_ID;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	public String getSmallImg() {
		return SmallImg;
	}
	public void setSmallImg(String smallImg) {
		SmallImg = smallImg;
	}
	public String getMiddleImg() {
		return MiddleImg;
	}
	public void setMiddleImg(String middleImg) {
		MiddleImg = middleImg;
	}
	public String getBigImg() {
		return BigImg;
	}
	public void setBigImg(String bigImg) {
		BigImg = bigImg;
	}
		
}
