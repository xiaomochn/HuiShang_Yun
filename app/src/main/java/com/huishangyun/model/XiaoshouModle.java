package com.huishangyun.model;

import java.util.Date;

public class XiaoshouModle {

	Integer ID; // 销售上报ID

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

	public Float getQty0() {
		return Qty0;
	}

	public void setQty0(Float qty0) {
		Qty0 = qty0;
	}

	public Float getQty1() {
		return Qty1;
	}

	public void setQty1(Float qty1) {
		Qty1 = qty1;
	}

	public Float getQty2() {
		return Qty2;
	}

	public void setQty2(Float qty2) {
		Qty2 = qty2;
	}

	public Float getQty3() {
		return Qty3;
	}

	public void setQty3(Float qty3) {
		Qty3 = qty3;
	}

	public Float getQty4() {
		return Qty4;
	}

	public void setQty4(Float qty4) {
		Qty4 = qty4;
	}

	public Float getQty5() {
		return Qty5;
	}

	public void setQty5(Float qty5) {
		Qty5 = qty5;
	}

	public Float getQty6() {
		return Qty6;
	}

	public void setQty6(Float qty6) {
		Qty6 = qty6;
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

	private Integer Member_ID; // 客户ID
	private String Member_Name; // 客户名称
	private Integer Product_ID; // 产品ID
	private String Product_Name; // 产品名称
	private Integer Unit_ID; // 单位ID
	private String Unit_Name; // 单位名称
	private Float Qty0; // 数量
	private Float Qty1;// 实际数量
	private Float Qty2;// 到货数量
	private Float Qty3;// 竞品数量
	private Float Qty4;
	private Float Qty5;// 退货数量
	private Float Qty6;
	private Integer Manager_ID; // 操作人ID
	private String Manager_Name;
	private Integer Department_ID; // 部门ID
	private String Department_Name;
	private Integer Company_ID; // 公司ID
	private String CycleType; // 周期 （day 或者 month）
	private String BelongDate; // 上报时间
	private Date AddDateTime;

}
