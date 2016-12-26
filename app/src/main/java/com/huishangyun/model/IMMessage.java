package com.huishangyun.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.huishangyun.Util.DateUtil;

public class IMMessage implements Parcelable, Comparable<IMMessage> {
	public static final String IMMESSAGE_KEY = "immessage.key";
	public static final String KEY_TIME = "immessage.time";
	
	/**
	 * 消息属性
	 */
	public static final String KEY_TYPE = "immessage.msg";
	
	public static final String TYPE_IMAGES = "images";
	public static final String TYPE_FILE = "file";
	public static final String TYPE_SPARK = "spark";
	public static final int SUCCESS = 0;
	public static final int ERROR = 1;
	private int type;
	private String content;
	private String time;
	/**
	 * 消息类别
	 */
	private int messtype;
	private String Msg_Category;
	
	private String group_name;

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	/**
	 * 存在本地，表示与谁聊天
	 */
	private String fromSubJid;
	/**
	 * 0:接受 1：发送
	 */
	private int msgType = 0;
	
	private String from;

	public IMMessage() {
		this.type = SUCCESS;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFromSubJid() {
		return fromSubJid;
	}

	public void setFromSubJid(String fromSubJid) {
		this.fromSubJid = fromSubJid;
	}
	

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(fromSubJid);
		dest.writeInt(msgType);
		dest.writeInt(messtype);
		dest.writeString(group_name);
		dest.writeString(Msg_Category);
	}
	
	public int getMesstype() {
		return messtype;
	}

	public String getMsg_Category() {
		return Msg_Category;
	}

	public void setMsg_Category(String msg_Category) {
		Msg_Category = msg_Category;
	}

	public void setMesstype(int messtype) {
		this.messtype = messtype;
	}

	public static final Parcelable.Creator<IMMessage> CREATOR = new Parcelable.Creator<IMMessage>() {

		@Override
		public IMMessage createFromParcel(Parcel source) {
			IMMessage message = new IMMessage();
			message.setType(source.readInt());
			message.setContent(source.readString());
			message.setTime(source.readString());
			message.setFromSubJid(source.readString());
			message.setMsgType(source.readInt());
			message.setMesstype(source.readInt());
			message.setGroup_name(source.readString());
			message.setMsg_Category(source.readString());
			return message;
		}

		@Override
		public IMMessage[] newArray(int size) {
			return new IMMessage[size];
		}

	};

	/**
	 * 新消息的构造方法.
	 * 
	 * @param content
	 * @param time
	 */
	public IMMessage(String content, String time, String withSb, int msgType,int messtype,String group_name, String Msg_Category) {
		super();
		this.content = content;
		this.time = time;
		this.msgType = msgType;
		this.fromSubJid = withSb;
		this.messtype = messtype;
		this.group_name = group_name;
		this.Msg_Category = Msg_Category;
	}

	/**
	 * 按时间降序排列
	 */
	@Override
	public int compareTo(IMMessage oth) {
		if (null == this.getTime() || null == oth.getTime()) {
			return 0;
		}
		String format = null;
		String time1 = "";
		String time2 = "";
		if (this.getTime().length() == oth.getTime().length()
				&& this.getTime().length() == 23) {
			time1 = this.getTime();
			time2 = oth.getTime();
			format = Constant.MS_FORMART;
		} else {
			time1 = this.getTime().substring(0, 19);
			time2 = oth.getTime().substring(0, 19);
		}
		Date da1 = DateUtil.str2Date(time1, format);
		Date da2 = DateUtil.str2Date(time2, format);
		if (da1.before(da2)) {
			return -1;
		}
		if (da2.before(da1)) {
			return 1;
		}

		return 0;
	}
}
