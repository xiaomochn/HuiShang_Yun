package com.huishangyun.Channel.Task;

/**
 * 添加任务帮助类
 * @author Pan
 *
 */
public class AddTaskModel {
	private Integer ID;//编号
	private String Title;//主题
	private String Action;//
	private Integer ExeManager_ID;//执行人编号
	private Integer Parent_ID;//子任务中上级任务编号
	private Integer Proportion;//占比
	private Boolean IsSplit;//是否可拆分子任务
	private Integer Flag;//优先级
	private String StartTime;//开始时间
	private String EndTime;//结束时间
	private String Note;//备注
	private Integer Status;//状态（0：取消；1：待处理；2：处理中；3：失败、4：成功）
	private Integer ProgressNum;//完成进度
	private String CancelMsg;//取消说明
	private Integer CheckManager_ID;//验收人编号
	private Integer Manager_ID;//人员编号
	private Integer Member_ID;//客户编号
	private Integer Business_ID;//商机编号
	
	public Integer getMember_ID() {
		return Member_ID;
	}
	public void setMember_ID(Integer member_ID) {
		Member_ID = member_ID;
	}
	public Integer getBussiness_ID() {
		return Business_ID;
	}
	public void setBussiness_ID(Integer bussiness_ID) {
		Business_ID = bussiness_ID;
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
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public Integer getExeManager_ID() {
		return ExeManager_ID;
	}
	public void setExeManager_ID(Integer exeManager_ID) {
		ExeManager_ID = exeManager_ID;
	}
	public Integer getParent_ID() {
		return Parent_ID;
	}
	public void setParent_ID(Integer parent_ID) {
		Parent_ID = parent_ID;
	}
	public Integer getProportion() {
		return Proportion;
	}
	public void setProportion(Integer proportion) {
		Proportion = proportion;
	}
	public Boolean getIsSplit() {
		return IsSplit;
	}
	public void setIsSplit(Boolean isSplit) {
		IsSplit = isSplit;
	}
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
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
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	public Integer getProgressNum() {
		return ProgressNum;
	}
	public void setProgressNum(Integer progressNum) {
		ProgressNum = progressNum;
	}
	public String getCancelMsg() {
		return CancelMsg;
	}
	public void setCancelMsg(String cancelMsg) {
		CancelMsg = cancelMsg;
	}
	public Integer getCheckManager_ID() {
		return CheckManager_ID;
	}
	public void setCheckManager_ID(Integer checkManager_ID) {
		CheckManager_ID = checkManager_ID;
	}
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	
}
