package com.huishangyun.model;

public class LoginResult {
	private String Action;
	private Integer Ret_ID;
	private Integer ID;
	private Integer CompanyID;
	private Integer DepartmentID;
	private String DepartmentName;
	private Integer Type;
	private String LoginName;
	private String Pass;
	private String Password;
	private String RealName;
	private String Sex;
	private String Email;
	private String Sign;
	private String Mobile;
	private String Weixin;
	private String Role_ID;
	private String Role_Name;
	private String Note;
	private String Photo;
	private String IDNumber;
	private String ApiID;
	private String IMEI;
	private String IMSI;
	private Boolean Status;
	private String AddDateTime;
	private String OperationTime;
	private String ManagerRole_ArrayID;
	private String OFUserName;
	private String OFPassword;
	private Integer IsUpdate;
	private String AppUrl;
	private LbsConfigs LbsConfig;

	public LbsConfigs getLbsConfig() {
		return LbsConfig;
	}

	public void setLbsConfig(LbsConfigs lbsConfig) {
		LbsConfig = lbsConfig;
	}

	/**
	 * 定位配置信息
	 */
	public class LbsConfigs {
		private Integer LocationType;
		private String StartDate;
		private String EndDate;
		private String WeekDay;
		private Integer Frequency;
		private String TimeList;

		public Integer getLocationType() {
			return LocationType;
		}

		public void setLocationType(Integer locationType) {
			LocationType = locationType;
		}

		public String getStartDate() {
			return StartDate;
		}

		public void setStartDate(String startDate) {
			StartDate = startDate;
		}

		public String getEndDate() {
			return EndDate;
		}

		public void setEndDate(String endDate) {
			EndDate = endDate;
		}

		public String getWeekDay() {
			return WeekDay;
		}

		public void setWeekDay(String weekDay) {
			WeekDay = weekDay;
		}

		public Integer getFrequency() {
			return Frequency;
		}

		public void setFrequency(Integer frequency) {
			Frequency = frequency;
		}

		public String getTimeList() {
			return TimeList;
		}

		public void setTimeList(String timeList) {
			TimeList = timeList;
		}
	}
	
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
	public Integer getCompanyID() {
		return CompanyID;
	}
	public void setCompanyID(Integer companyID) {
		CompanyID = companyID;
	}
	public Integer getDepartmentID() {
		return DepartmentID;
	}
	public void setDepartmentID(Integer departmentID) {
		DepartmentID = departmentID;
	}
	public String getDepartmentName() {
		return DepartmentName;
	}
	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public String getPass() {
		return Pass;
	}
	public void setPass(String pass) {
		Pass = pass;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public String getSex() {
		return Sex;
	}
	public void setSex(String sex) {
		Sex = sex;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getSign() {
		return Sign;
	}
	public void setSign(String sign) {
		Sign = sign;
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
	public String getRole_ID() {
		return Role_ID;
	}
	public void setRole_ID(String role_ID) {
		Role_ID = role_ID;
	}
	public String getRole_Name() {
		return Role_Name;
	}
	public void setRole_Name(String role_Name) {
		Role_Name = role_Name;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
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
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	public String getManagerRole_ArrayID() {
		return ManagerRole_ArrayID;
	}
	public void setManagerRole_ArrayID(String managerRole_ArrayID) {
		ManagerRole_ArrayID = managerRole_ArrayID;
	}
	public String getOFUserName() {
		return OFUserName;
	}
	public void setOFUserName(String oFUserName) {
		OFUserName = oFUserName;
	}
	public String getOFPassword() {
		return OFPassword;
	}
	public void setOFPassword(String oFPassword) {
		OFPassword = oFPassword;
	}
	public Integer getIsUpdate() {
		return IsUpdate;
	}
	public void setIsUpdate(Integer isUpdate) {
		IsUpdate = isUpdate;
	}
	public String getAppUrl() {
		return AppUrl;
	}
	public void setAppUrl(String appUrl) {
		AppUrl = appUrl;
	}
	
	
	
}
