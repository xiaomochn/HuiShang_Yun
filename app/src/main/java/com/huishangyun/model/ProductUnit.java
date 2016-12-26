package com.huishangyun.model;

/**
 * 产品单位
 * @author forong
 *
 */
public class ProductUnit {
	private Integer Product_ID;
	private Integer Unit_ID;
	private String Unit_Name;
	private Float Quantity;
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
	public Float getQuantity() {
		return Quantity;
	}
	public void setQuantity(Float quantity) {
		Quantity = quantity;
	}
	
	
}
