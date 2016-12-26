package com.huishangyun.common;

import java.io.Serializable;

public class AddImage implements Serializable{
	private String image;//图片路径
	private String serverpath;//服务器返回的路径
	
	public AddImage(){
	}
	
	public AddImage(String image, String serverpath){
		this.image = image;
		this.serverpath = serverpath;
	}
	
	public String getImagepath() {
		return image;
	}
	public void setImagepath(String image) {
		this.image = image;
	}
	public String getServerpath() {
		return serverpath;
	}
	public void setServerpath(String serverpath) {
		this.serverpath = serverpath;
	}
}
