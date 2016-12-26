package com.huishangyun.Util;

/**
 * 任务进度实体类
 * @author Pan
 *
 */
public class TaskProgerUtil {
	private String Action;
	private Integer ID;
	private Integer Task_ID;
	private Integer ProgressNum;
	private String Note;
	private String Attachment;
	private String AddDateTime;
	private String Manager_Name;
	private Integer Manager_ID;
	private String AttachmentPath;
	
	public String getAttachmentPath() {
		return AttachmentPath;
	}
	public void setAttachmentPath(String attachmentPath) {
		AttachmentPath = attachmentPath;
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
	public Integer getManager_ID() {
		return Manager_ID;
	}
	public void setManager_ID(Integer manager_ID) {
		Manager_ID = manager_ID;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public Integer getTask_ID() {
		return Task_ID;
	}
	public void setTask_ID(Integer task_ID) {
		Task_ID = task_ID;
	}
	public Integer getProgressNum() {
		return ProgressNum;
	}
	public void setProgressNum(Integer progressNum) {
		ProgressNum = progressNum;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
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
	
}
