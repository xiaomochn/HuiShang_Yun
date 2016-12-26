package com.huishangyun.Channel.Plan;

import java.io.Serializable;


public class SortModel implements Serializable{
    
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -8236876960453517446L;
	private Integer ID;//客户id
	private String name;//显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private String company_name;//公司名称
	private String person_img;// 用户头像	
	private Integer ParentID;
	private String Group_ID;//组id或者部门id
	private String Manger_Name;//负责人
	private String Code;//模式字段
	private Integer select;//单双选标识	
	private String role_Name;//角色名称
	private String Note;//动态
	private String Department_Name;//部门名称
    private Integer Product_Unit_ID;//产品id
    private String product_Unit_Name;//产品单位名称
    private String Mobile;//人员电话
    private String OFUserName;
	private String Sign_Time ;//签到时间

	public String getSign_Time() {
		return Sign_Time;
	}

	public void setSign_Time(String sign_Time) {
		Sign_Time = sign_Time;
	}

	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getPerson_img() {
		return person_img;
	}
	public void setPerson_img(String person_img) {
		this.person_img = person_img;
	}
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public String getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(String group_ID) {
		Group_ID = group_ID;
	}
	public String getManger_Name() {
		return Manger_Name;
	}
	public void setManger_Name(String manger_Name) {
		Manger_Name = manger_Name;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public Integer getSelect() {
		return select;
	}
	public void setSelect(Integer select) {
		this.select = select;
	}
	public String getRole_Name() {
		return role_Name;
	}
	public void setRole_Name(String role_Name) {
		this.role_Name = role_Name;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getDepartment_Name() {
		return Department_Name;
	}
	public void setDepartment_Name(String department_Name) {
		Department_Name = department_Name;
	}
	public Integer getProduct_Unit_ID() {
		return Product_Unit_ID;
	}
	public void setProduct_Unit_ID(Integer product_Unit_ID) {
		Product_Unit_ID = product_Unit_ID;
	}
	public String getProduct_Unit_Name() {
		return product_Unit_Name;
	}
	public void setProduct_Unit_Name(String product_Unit_Name) {
		this.product_Unit_Name = product_Unit_Name;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getOFUserName() {
		return OFUserName;
	}
	public void setOFUserName(String oFUserName) {
		OFUserName = oFUserName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
  
}
