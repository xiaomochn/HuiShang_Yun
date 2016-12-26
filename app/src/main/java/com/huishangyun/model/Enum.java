package com.huishangyun.model;

/**
 * 数据字典实体类
 * @author Pan
 *
 */
public class Enum {
	private String MyPK;
	private String Lab;
	private String EnumKey;
	private String IntKey;
	private String OperationTime;
	private Boolean Status;
	
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		Status = status;
	}
	public String getMyPK() {
		return MyPK;
	}
	public void setMyPK(String myPK) {
		MyPK = myPK;
	}
	public String getLab() {
		return Lab;
	}
	public void setLab(String lab) {
		Lab = lab;
	}
	public String getEnumKey() {
		return EnumKey;
	}
	public void setEnumKey(String enumKey) {
		EnumKey = enumKey;
	}
	public String getIntKey() {
		return IntKey;
	}
	public void setIntKey(String intKey) {
		IntKey = intKey;
	}
	public String getOperationTime() {
		return OperationTime;
	}
	public void setOperationTime(String operationTime) {
		OperationTime = operationTime;
	}
	
	
}
