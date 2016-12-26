package com.huishangyun.model;

import android.R.integer;

/**
 * 收藏列表实体类
 * @author Administrator
 *
 */
public class Order_ProductFav {
	private Integer Product_ID;
	private Integer img;
	private double price;
	private String units;
		
	private String Product_Name;
	private String AddDateTime;
	
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
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
}
