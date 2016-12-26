package com.huishangyun.Channel.xiaoliang;

import java.util.Date;

public class PlanModle {
	Integer ID; // 销售上报ID
	private Integer Member_ID; // 客户ID
	private String Member_Name; // 客户名称
	private Integer Product_ID; // 产品ID
	private String Product_Name; // 产品名称
	private Integer Unit_ID; // 单位ID
	private String Unit_Name; // 单位名称
	private Integer Qty0; // 数量
	
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
	public Integer getQty0() {
		return Qty0;
	}
	public void setQty0(Integer qty0) {
		Qty0 = qty0;
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
	public String getCycleType() {
		return CycleType;
	}
	public void setCycleType(String cycleType) {
		CycleType = cycleType;
	}
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
	public Date getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(Date addDateTime) {
		AddDateTime = addDateTime;
	}
	private Integer Manager_ID; // 操作人ID
	private String Manager_Name;
	private Integer Department_ID; // 部门ID
	private String Department_Name;
	private Integer Company_ID; // 公司ID
	private String CycleType; // 周期 （day 或者 month）
	private String BelongDate; // 上报时间
	private Date AddDateTime;
}
