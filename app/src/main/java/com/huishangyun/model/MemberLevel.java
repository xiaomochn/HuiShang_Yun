package com.huishangyun.model;

import java.io.Serializable;

/**
 * 客户等级实体类
 * @author Pan
 *
 */
public class MemberLevel {
	private Integer ID;//等级ID
	private String Name;//等级名称
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
}
