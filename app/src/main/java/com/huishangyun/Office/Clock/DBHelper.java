package com.huishangyun.Office.Clock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	//数据库的名字
   private static final String db_name = "my_colck.db";
   //数据库的版本
   private static final int version = 1;
	public DBHelper(Context context) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//建表执行语句, 表mClock(pid,MemberID,isOpan,alarmTime,repeatDays);
        String sql = "create table mClock(pid integer primary key autoincrement,"
        		+ " mIndex, ManagerID , isOpen, alarmTime, repeatDays, pickUri, Volume,MusicName,Vib)";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//插入一个字段,当版本号更新时会执行
		String sql = "alter table mClock add Wensday integer";
		db.execSQL(sql);

	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
	}

}
