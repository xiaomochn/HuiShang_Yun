package com.huishangyun.model;

public class MessageDatas<T>{
	//消息类型
	private String messageCategory;
	//消息内容
	private T messageContent;
	
	public String getMessageCategory() {
		return messageCategory;
	}
	public void setMessageCategory(String messageCategory) {
		this.messageCategory = messageCategory;
	}
	public T getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(T messageContent) {
		this.messageContent = messageContent;
	}
	
	
}
