package com.huishangyun.Channel.stock;

import java.lang.reflect.Array;
import java.util.Arrays;

public class StockList {
	private String Action;
	private Integer ID;
	private Integer Member_ID;//客户编号
	private String Member_Name;//客户名称
	private Integer Product_ID;//产品编号
    private String Product_Name;//String
    private Float Quantity;//数量
    private String CycleType;//周期类型
    private Integer Unit_ID;//单位编号
    private String Unit_Name;//单位名称
    private String BelongDate;//上报日期
    private Integer Manager_ID;//人员编号
    private String Manager_Name;//人员姓名
    private Integer Department_ID;//部门编号
    private String Department_Name;//部门名称
    private Integer Company_ID;//企业编号
    private String Array_IDs;//多条上报集合
    
    
	
	public String getArray_IDs() {
		return Array_IDs;
	}
	public void setArray_IDs(String array_IDs) {
		Array_IDs = array_IDs;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public String getMember_Name() {
		return Member_Name;
	}
	public void setMember_Name(String member_Name) {
		Member_Name = member_Name;
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
	public String getCycleType() {
		return CycleType;
	}
	public void setCycleType(String cycleType) {
		CycleType = cycleType;
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
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public Integer getDepartment_ID() {
		return Department_ID;
	}
	public void setDepartment_ID(Integer department_ID) {
		Department_ID = department_ID;
	}
	public String getDepartment_Name() {
		return Department_Name;
	}
	public void setDepartment_Name(String department_Name) {
		Department_Name = department_Name;
	}
	public Integer getCompany_ID() {
		return Company_ID;
	}
	public void setCompany_ID(Integer company_ID) {
		Company_ID = company_ID;
	}
    
    
    
}
