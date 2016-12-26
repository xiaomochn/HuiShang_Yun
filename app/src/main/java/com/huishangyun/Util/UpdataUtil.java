package com.huishangyun.Util;

import android.R.integer;

/**
 * 更新时间帮助类
 * @author pan
 *
 */
public class UpdataUtil {
	private int ID;
	private String time;
	private int type;//0为同事，1为客户
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
