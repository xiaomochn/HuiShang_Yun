package com.huishangyun.model;

public class CartModel {
	private Integer ID;
	private Integer Product_ID;
	private String Product_Name;
	private Float Quantity;
	private Float Price;
	private String Unit_Name;
	private Integer Unit_ID;
	private String AddDateTime;
	private String SmallImg;
	
	public String getSmallImg() {
		return SmallImg;
	}
	public void setSmallImg(String smallImg) {
		SmallImg = smallImg;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getProduct_ID() {
		return Product_ID;
	}
	public void setProduct_ID(Integer product_ID) {
		Product_ID = product_ID;
	}
	public String getProduct_Name() {
		return Product_Name;
	}
	public void setProduct_Name(String product_Name) {
		Product_Name = product_Name;
	}
	public Float getQuantity() {
		return Quantity;
	}
	public void setQuantity(Float quantity) {
		Quantity = quantity;
	}
	public Float getPrice() {
		return Price;
	}
	public void setPrice(Float price) {
		Price = price;
	}
	public String getUnit_Name() {
		return Unit_Name;
	}
	public void setUnit_Name(String unit_Name) {
		Unit_Name = unit_Name;
	}
	public Integer getUnit_ID() {
		return Unit_ID;
	}
	public void setUnit_ID(Integer unit_ID) {
		Unit_ID = unit_ID;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	
	
}
