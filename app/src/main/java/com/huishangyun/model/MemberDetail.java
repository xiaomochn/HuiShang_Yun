package com.huishangyun.model;

public class MemberDetail {
	private Integer Ret_ID;//返回ID	
	private String IMEI;
	private String IMSI;
	private Integer ID;
	
	private Integer Manager_ID;//人员
	private String Manager_Name;
	private Integer Department_ID;//部门ID
	private String Department_Name;
	private Integer Company_ID;//企业编号
	private String Contact;//负责人
	private String Tel;
	
	public String getTel() {
		return Tel;
	}
	public void setTel(String tel) {
		Tel = tel;
	}
	//基本信息
	private String RealName;//客戶名称
	private Integer Level_ID;//等級ID
	private String Level_Name;
	private String Group_ID;//分組ID
	private String Group_Name;	
	private Integer Type;//类型		
	private String Mobile;
	private String Picture;
	
	//登陆信息
	private String LoginName;	
	private String Password;
	
	//扩展信息
	private String IDNumber;//证件编号	
	private String ApiID;//客户编号	
	private String Channel;//经营渠道	
	private String Range;//覆盖范围	
	private Integer Parent_ID;
	private String Parent_Name;//上级经销商
	private String Brand;// 合作品牌	
	private String Photo;
	
	//	private String Picture;//照片	
	private String Note;// 备注
	
	//联系人信息	
	private String Address;//地址	
		
	private String Weixin;	
	private String Email;
	
	private Integer ProvinceID;
	private Integer CityID;
	private Integer AreaID;//客户编号
	
	private String FinanceDate;
	private Integer LoginNum;	
	private Integer InviteMember_ID;
	private String AddDateTime;	
	private String LastLoginDateTime;
	private String LastLoginIP;
	private String OperationTime;
	private Double Lat;
	private Double Lng;
	private String Loc;
	
	
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	
	public Double getLat() {
		return Lat;
	}
	public void setLat(Double lat) {
		Lat = lat;
	}
	public Double getLng() {
		return Lng;
	}
	public void setLng(Double lng) {
		Lng = lng;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
	}
	private String Action;
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	
	public Integer getRet_ID() {
		return Ret_ID;
	}
	public void setRet_ID(Integer ret_ID) {
		Ret_ID = ret_ID;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public Integer getLevel_ID() {
		return Level_ID;
	}
	public void setLevel_ID(Integer level_ID) {
		Level_ID = level_ID;
	}
	public String getLevel_Name() {
		return Level_Name;
	}
	public void setLevel_Name(String level_Name) {
		Level_Name = level_Name;
	}
	public String getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(String group_ID) {
		Group_ID = group_ID;
	}
	public String getGroup_Name() {
		return Group_Name;
	}
	public void setGroup_Name(String group_Name) {
		Group_Name = group_Name;
	}
	public Integer getParent_ID() {
		return Parent_ID;
	}
	public void setParent_ID(Integer parent_ID) {
		Parent_ID = parent_ID;
	}
	public String getParent_Name() {
		return Parent_Name;
	}
	public void setParent_Name(String parent_Name) {
		Parent_Name = parent_Name;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public String getContact() {
		return Contact;
	}
	public void setContact(String contact) {
		Contact = contact;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getWeixin() {
		return Weixin;
	}
	public void setWeixin(String weixin) {
		Weixin = weixin;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getIMSI() {
		return IMSI;
	}
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
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
	public Integer getProvinceID() {
		return ProvinceID;
	}
	public void setProvinceID(Integer provinceID) {
		ProvinceID = provinceID;
	}
	public Integer getCityID() {
		return CityID;
	}
	public void setCityID(Integer cityID) {
		CityID = cityID;
	}
	public Integer getAreaID() {
		return AreaID;
	}
	public void setAreaID(Integer areaID) {
		AreaID = areaID;
	}
	public String getIDNumber() {
		return IDNumber;
	}
	public void setIDNumber(String iDNumber) {
		IDNumber = iDNumber;
	}
	public String getApiID() {
		return ApiID;
	}
	public void setApiID(String apiID) {
		ApiID = apiID;
	}
	public String getBrand() {
		return Brand;
	}
	public void setBrand(String brand) {
		Brand = brand;
	}
	public String getChannel() {
		return Channel;
	}
	public void setChannel(String channel) {
		Channel = channel;
	}
	public String getRange() {
		return Range;
	}
	public void setRange(String range) {
		Range = range;
	}
	
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getFinanceDate() {
		return FinanceDate;
	}
	public void setFinanceDate(String financeDate) {
		FinanceDate = financeDate;
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
	public Integer getInviteMember_ID() {
		return InviteMember_ID;
	}
	public void setInviteMember_ID(Integer inviteMember_ID) {
		InviteMember_ID = inviteMember_ID;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public Integer getLoginNum() {
		return LoginNum;
	}
	public void setLoginNum(Integer loginNum) {
		LoginNum = loginNum;
	}
	public String getLastLoginDateTime() {
		return LastLoginDateTime;
	}
	public void setLastLoginDateTime(String lastLoginDateTime) {
		LastLoginDateTime = lastLoginDateTime;
	}
	public String getLastLoginIP() {
		return LastLoginIP;
	}
	public void setLastLoginIP(String lastLoginIP) {
		LastLoginIP = lastLoginIP;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	
	

}
