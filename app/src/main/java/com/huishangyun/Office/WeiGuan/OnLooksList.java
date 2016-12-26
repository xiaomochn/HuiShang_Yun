package com.huishangyun.Office.WeiGuan;

public class OnLooksList {
	private String Action;
	private String Topic;//主题
	private String Note;//内容
	private String Attachment;//原附件名#分隔
    private String AttachmentPath;//附件Img接口返回文件名#分隔
    private Integer Flag;//抄送范围(0:全公司,1:仅自己,2:所在部门,3:组织架构层级)
    private Double Lng;//定位经度
    private Double Lat;//定位纬度
    private String Loc;//定位地址
    private Integer Manager_ID;//发表人ID
    private String Manager_Name;//发表人名
    private Integer Department_ID;//部门id
    private Integer Company_ID;//公司id
    private Integer ID;//主题id
    private String Title;//主题
    private String AddDateTime;//添加时间
    private Integer Comment;//评论条数
    private Integer Good;//点赞条数
    private String Photo;//人物头像地址
    private String RealName;//人名
    private String Sign;//职位标记
    private String Array_ID;
    private Integer OperationID;
    private String OperationName;
    private Integer Err_Num;
    private String Err_Msg;
    private String ReplyTo;//回复谁谁谁
    private Integer Action_ID;
    private String Action_Note;
    private String Action_Time;
    private Boolean ShowGps;//是否显示地理位置 
    
    
    private Integer isWho;
	private String RecordAbsolutePath;
	private String RecordName;
	private String PictureAbsolutePath;
	private String PictureName;
	private String takePhotoName;
	private String takePhotoAbsolutePath;
    
    
	
	public Integer getIsWho() {
		return isWho;
	}
	public void setIsWho(Integer isWho) {
		this.isWho = isWho;
	}
	public String getRecordAbsolutePath() {
		return RecordAbsolutePath;
	}
	public void setRecordAbsolutePath(String recordAbsolutePath) {
		RecordAbsolutePath = recordAbsolutePath;
	}
	public String getRecordName() {
		return RecordName;
	}
	public void setRecordName(String recordName) {
		RecordName = recordName;
	}
	public String getPictureAbsolutePath() {
		return PictureAbsolutePath;
	}
	public void setPictureAbsolutePath(String pictureAbsolutePath) {
		PictureAbsolutePath = pictureAbsolutePath;
	}
	public String getPictureName() {
		return PictureName;
	}
	public void setPictureName(String pictureName) {
		PictureName = pictureName;
	}
	public String getTakePhotoName() {
		return takePhotoName;
	}
	public void setTakePhotoName(String takePhotoName) {
		this.takePhotoName = takePhotoName;
	}
	public String getTakePhotoAbsolutePath() {
		return takePhotoAbsolutePath;
	}
	public void setTakePhotoAbsolutePath(String takePhotoAbsolutePath) {
		this.takePhotoAbsolutePath = takePhotoAbsolutePath;
	}
	public Boolean getShowGps() {
		return ShowGps;
	}
	public void setShowGps(Boolean showGps) {
		ShowGps = showGps;
	}
	public Integer getAction_ID() {
		return Action_ID;
	}
	public void setAction_ID(Integer action_ID) {
		Action_ID = action_ID;
	}
	public String getAction_Note() {
		return Action_Note;
	}
	public void setAction_Note(String action_Note) {
		Action_Note = action_Note;
	}
	public String getAction_Time() {
		return Action_Time;
	}
	public void setAction_Time(String action_Time) {
		Action_Time = action_Time;
	}
	
	public String getReplyTo() {
		return ReplyTo;
	}
	public void setReplyTo(String replyTo) {
		ReplyTo = replyTo;
	}
	public String getArray_ID() {
		return Array_ID;
	}
	public void setArray_ID(String array_ID) {
		Array_ID = array_ID;
	}
	public String getErr_Msg() {
		return Err_Msg;
	}
	public void setErr_Msg(String err_Msg) {
		Err_Msg = err_Msg;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
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
	public Integer getCompany_ID() {
		return Company_ID;
	}
	public void setCompany_ID(Integer company_ID) {
		Company_ID = company_ID;
	}
	
	public Integer getOperationID() {
		return OperationID;
	}
	public void setOperationID(Integer operationID) {
		OperationID = operationID;
	}
	public String getOperationName() {
		return OperationName;
	}
	public void setOperationName(String operationName) {
		OperationName = operationName;
	}
	public Integer getErr_Num() {
		return Err_Num;
	}
	public void setErr_Num(Integer err_Num) {
		Err_Num = err_Num;
	}
	
	public String getAttachment() {
		return Attachment;
	}
	public void setAttachment(String attachment) {
		Attachment = attachment;
	}
	public String getAddDateTime() {
		return AddDateTime;
	}
	public void setAddDateTime(String addDateTime) {
		AddDateTime = addDateTime;
	}
	public Integer getComment() {
		return Comment;
	}
	public void setComment(Integer comment) {
		Comment = comment;
	}
	public Integer getGood() {
		return Good;
	}
	public void setGood(Integer good) {
		Good = good;
	}
	public String getPhoto() {
		return Photo;
	}
	public void setPhoto(String photo) {
		Photo = photo;
	}
	public String getRealName() {
		return RealName;
	}
	public void setRealName(String realName) {
		RealName = realName;
	}
	public String getSign() {
		return Sign;
	}
	public void setSign(String sign) {
		Sign = sign;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getTopic() {
		return Topic;
	}
	public void setTopic(String topic) {
		Topic = topic;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	
	public String getAttachmentPath() {
		return AttachmentPath;
	}
	public void setAttachmentPath(String attachmentPath) {
		AttachmentPath = attachmentPath;
	}
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
	}
	public Double getLng() {
		return Lng;
	}
	public void setLng(Double lng) {
		Lng = lng;
	}
	public Double getLat() {
		return Lat;
	}
	public void setLat(Double lat) {
		Lat = lat;
	}
	public String getLoc() {
		return Loc;
	}
	public void setLoc(String loc) {
		Loc = loc;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
    
    
}
