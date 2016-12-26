package com.huishangyun.model;

public class ProductFavs {
	private Integer ID;
	private Integer Product_ID;
	private String Product_Name;
	
	private String SmallImg;
	private float Price;
	private String Unit_Name;
		
	public String getSmallImg() {
		return SmallImg;
	}
	public void setSmallImg(String smallImg) {
		SmallImg = smallImg;
	}
	public float getPrice() {
		return Price;
	}
	public void setPrice(float price) {
		Price = price;
	}
	public String getUnit_Name() {
		return Unit_Name;
	}
	public void setUnit_Name(String unit_Name) {
		Unit_Name = unit_Name;
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
	
}
