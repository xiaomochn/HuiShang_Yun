package com.huishangyun.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 锁屏实体类
 * @author Pan
 *
 */
public class ScreenEntity implements Serializable {
	private String Name;
	private String Time;
	private String Content;
	private String Size;
	private Boolean isGroup;
	private int ID;

	public Boolean getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(Boolean isGroup) {
		this.isGroup = isGroup;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}
/*
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Name);
		dest.writeString(Content);
		dest.writeString(Time);
		dest.writeString(Size);
	}

	public static final Parcelable.Creator<ScreenEntity> CREATOR = new Creator<ScreenEntity>() {
		// 实现从source中创建出类的实例的功能
		@Override
		public ScreenEntity createFromParcel(Parcel source) {
			ScreenEntity screenEntity = new ScreenEntity();
			screenEntity.Name = source.readString();
			screenEntity.Time = source.readString();
			screenEntity.Content = source.readString();
			screenEntity.Size = source.readString();
			return screenEntity;
		}

		// 创建一个类型为T，长度为size的数组
		@Override
		public ScreenEntity[] newArray(int size) {
			return new ScreenEntity[size];
		}
	};*/

}
