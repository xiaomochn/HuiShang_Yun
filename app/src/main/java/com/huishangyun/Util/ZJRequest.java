package com.huishangyun.Util;

import java.util.List;

import android.R.integer;

/**
 * 
 * 统一的接口提交数据传递类
 * @author Pan
 * @see无
 * @version 亿企云 V1.0 2014-08-15
 * @param <T> 泛型
 * 
 */
public class ZJRequest<T> {
	private Integer ID ;//数据编号
	private Integer Manager_ID;//用户编号
	private Integer Department_ID;//部门编号
	private Integer Company_ID;//企业编号
	private String Keywords;//关键字
	private String OperationTime;//最后操作时间
	private String OperationName;
	private Integer Member_ID;//客户编号
	private Integer Class_ID;//产品分类编号
	private Integer Product_ID;//产品编号
	private Integer Year ;//年
	private Integer Month;//月
	private Integer Day;//日
	private Integer PageIndex;// 页码
	private Integer PageSize;//每页显示条数
	private String LoginName;//登录账号
	private String Password;//密码
	private String Imei;//终端编码
	private String Imsi;//卡机身码
	private Integer Type;
	private Integer Status;
    private Integer Flag;
	private Integer Stage;
	private String BelongDate;
	private String Money;
	private String OrderID ;
	private Integer ParentID;
	private Integer PlanID;
	private String Note;
	private String Action;
	private String Version;//版本号
	private String Manager_Name;
	private String OFUserName;
	private String Client;

	public String getClient() {
		return Client;
	}

	public void setClient(String client) {
		Client = client;
	}
	
	
	public String getOperationName() {
		return OperationName;
	}
	public void setOperationName(String operationName) {
		OperationName = operationName;
	}
	public String getOFUserName() {
		return OFUserName;
	}
	public void setOFUserName(String oFUserName) {
		OFUserName = oFUserName;
	}
	public String getManager_Name() {
		return Manager_Name;
	}
	public void setManager_Name(String manager_Name) {
		Manager_Name = manager_Name;
	}
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public String getBelongDate() {
		return BelongDate;
	}
	public void setBelongDate(String belongDate) {
		BelongDate = belongDate;
	}
	public Integer getParentID() {
		return ParentID;
	}
	public void setParentID(Integer parentID) {
		ParentID = parentID;
	}
	public Integer getPlanID() {
		return PlanID;
	}
	public void setPlanID(Integer planID) {
		PlanID = planID;
	}
	public int getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
	}
	public Integer getStage() {
		return Stage;
	}
	public void setStage(Integer stage) {
		Stage = stage;
	}
	public String getMoney() {
		return Money;
	}
	public void setMoney(String money) {
		Money = money;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}
	private T Data;//对象
	private List<T> Datas;//对象列表
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public Integer getDepartment_ID() {
		return Department_ID;
	}
	public void setDepartment_ID(Integer department_ID) {
		Department_ID = department_ID;
	}
	public Integer getCompany_ID() {
		return Company_ID;
	}
	public void setCompany_ID(Integer company_ID) {
		Company_ID = company_ID;
	}
	public String getKeywords() {
		return Keywords;
	}
	public void setKeywords(String keywords) {
		Keywords = keywords;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public Integer getClass_ID() {
		return Class_ID;
	}
	public void setClass_ID(Integer class_ID) {
		Class_ID = class_ID;
	}
	public Integer getProduct_ID() {
		return Product_ID;
	}
	public void setProduct_ID(Integer product_ID) {
		Product_ID = product_ID;
	}
	public Integer getYear() {
		return Year;
	}
	public void setYear(Integer year) {
		Year = year;
	}
	public Integer getMonth() {
		return Month;
	}
	public void setMonth(Integer month) {
		Month = month;
	}
	public Integer getDay() {
		return Day;
	}
	public void setDay(Integer day) {
		Day = day;
	}
	public Integer getPageIndex() {
		return PageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		PageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return PageSize;
	}
	public void setPageSize(Integer pageSize) {
		PageSize = pageSize;
	}
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getImei() {
		return Imei;
	}
	public void setImei(String imei) {
		Imei = imei;
	}
	public String getImsi() {
		return Imsi;
	}
	public void setImsi(String imsi) {
		Imsi = imsi;
	}
	public <T> T getData() {
		return (T) Data;
	}
	public void setData(T data) {
		Data = data;
	}
	public <T> List<T> getDatas() {
		return (List<T>) Datas;
	}
	public void setDatas(List<T> datas) {
		Datas = datas;
	}
}
