package com.huishangyun.model;

/**
 * 产品价格
 * @author forong
 *
 */
public class ProductPrice {
	private Integer Product_ID;
	private Integer Unit_ID;
	private String Unit_Name;
	private Float Price;
	private Integer Group_ID;
	private Integer Level_ID;
	private Integer Member_ID;
	private Integer ID;
	private String OperationTime;
	private Boolean Status;
	
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public Integer getProduct_ID() {
		return Product_ID;
	}
	public void setProduct_ID(Integer product_ID) {
		Product_ID = product_ID;
	}
	public Integer getUnit_ID() {
		return Unit_ID;
	}
	public void setUnit_ID(Integer unit_ID) {
		Unit_ID = unit_ID;
	}
	public String getUnit_Name() {
		return Unit_Name;
	}
	public void setUnit_Name(String unit_Name) {
		Unit_Name = unit_Name;
	}
	public Float getPrice() {
		return Price;
	}
	public void setPrice(Float price) {
		Price = price;
	}
	public Integer getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(Integer group_ID) {
		Group_ID = group_ID;
	}
	public Integer getLevel_ID() {
		return Level_ID;
	}
	public void setLevel_ID(Integer level_ID) {
		Level_ID = level_ID;
	}
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	
	
}
