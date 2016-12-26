package com.huishangyun.model;

import java.io.Serializable;

public class Group implements Serializable{
	private String groupAccount;//房间的jid
	private String groupName;
	
	
	public String getGroupName() {
		return groupName;
	}


	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getGroupAccount() {
		return groupAccount;
	}

	
	public void setGroupAccount(String groupAccount) {
		this.groupAccount = groupAccount;
	}
}
