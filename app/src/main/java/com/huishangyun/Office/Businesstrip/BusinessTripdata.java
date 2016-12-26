package com.huishangyun.Office.Businesstrip;


public class BusinessTripdata {
	private String Action;
	private Integer ID;
	private Integer Flag;//0为提交1为出发2为到达
	private Integer Manager_ID;//操作人id
	private String Manager_Name;//操作人姓名
	private String Manager_Photo;//头像图片地址
	private Integer Department_ID;//部门id
	private String Department_Name;//部门名称
	private Integer Company_ID;//公司id
	private String StartCity;//出发城市
	private String EndCity;//到达城市
	private String StartTime;//出发时间
	private String EndTime;//结束时间
	private String Note;//备注
	private String StartNote;//出发备注
	private String StartPicture;//照片
	private Double StartLng;//经度
	private Double StartLat;//维度
	private String StartLoc;//地理位置
	private String StartDateTime;//出发添加时间
	private String ArriveDateTime;//到达时间
	private String ArriveNote;//到达备注
	private String ArrivePicture;//到达图片
	private Double ArriveLng;//到达经度
	private Double ArriveLat;//到达维度
	private String ArriveLoc;//到达地理位置
	private Boolean Status;//状态true or false
	private String AddDateTime;//添加时间
	private Float Days;//相差天数
	private String CancelNote;//取消事由
	private String Picture;//提交照片
	
	private Integer Array_ID;
	private Integer Err_Num;
	private Integer Err_Msg;
	
	
	
	public Float getDays() {
		return Days;
	}
	public void setDays(Float days) {
		Days = days;
	}
	public String getPicture() {
		return Picture;
	}
	public void setPicture(String picture) {
		Picture = picture;
	}
	public String getCancelNote() {
		return CancelNote;
	}
	public void setCancelNote(String cancelNote) {
		CancelNote = cancelNote;
	}
	public String getStartNote() {
		return StartNote;
	}
	public void setStartNote(String startNote) {
		StartNote = startNote;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getManager_Photo() {
		return Manager_Photo;
	}
	public void setManager_Photo(String manager_Photo) {
		Manager_Photo = manager_Photo;
	}
	
	public String getStartPicture() {
		return StartPicture;
	}
	public void setStartPicture(String startPicture) {
		StartPicture = startPicture;
	}
	public String getStartDateTime() {
		return StartDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		StartDateTime = startDateTime;
	}
	
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
	}
	public Double getStartLng() {
		return StartLng;
	}
	public void setStartLng(Double startLng) {
		StartLng = startLng;
	}
	public Double getStartLat() {
		return StartLat;
	}
	public void setStartLat(Double startLat) {
		StartLat = startLat;
	}
	public String getStartLoc() {
		return StartLoc;
	}
	public void setStartLoc(String startLoc) {
		StartLoc = startLoc;
	}
	public Integer getArray_ID() {
		return Array_ID;
	}
	public void setArray_ID(Integer array_ID) {
		Array_ID = array_ID;
	}
	public Integer getErr_Num() {
		return Err_Num;
	}
	public void setErr_Num(Integer err_Num) {
		Err_Num = err_Num;
	}
	public Integer getErr_Msg() {
		return Err_Msg;
	}
	public void setErr_Msg(Integer err_Msg) {
		Err_Msg = err_Msg;
	}
	
	public String getArriveLoc() {
		return ArriveLoc;
	}
	public void setArriveLoc(String arriveLoc) {
		ArriveLoc = arriveLoc;
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
	public String getStartCity() {
		return StartCity;
	}
	public void setStartCity(String startCity) {
		StartCity = startCity;
	}
	public String getEndCity() {
		return EndCity;
	}
	public void setEndCity(String endCity) {
		EndCity = endCity;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public String getArriveDateTime() {
		return ArriveDateTime;
	}
	public void setArriveDateTime(String arriveDateTime) {
		ArriveDateTime = arriveDateTime;
	}
	public String getArriveNote() {
		return ArriveNote;
	}
	public void setArriveNote(String arriveNote) {
		ArriveNote = arriveNote;
	}
	public String getArrivePicture() {
		return ArrivePicture;
	}
	public void setArrivePicture(String arrivePicture) {
		ArrivePicture = arrivePicture;
	}
	public Double getArriveLng() {
		return ArriveLng;
	}
	public void setArriveLng(Double arriveLng) {
		ArriveLng = arriveLng;
	}
	public Double getArriveLat() {
		return ArriveLat;
	}
	public void setArriveLat(Double arriveLat) {
		ArriveLat = arriveLat;
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
	
	
}
