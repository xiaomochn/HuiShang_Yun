package com.huishangyun.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huishangyun.App.MyApplication;
import com.huishangyun.db.DataBaseHelper;
import com.huishangyun.model.Constant;
public class DBManager {
	/**
	 * 数据库版本
	 */
	private int version = 1;
	
	/**
	 * 数据库名称
	 */
	private static String databaseName = null;;

	private static DBManager dBManager = null;
	
	/**
	 * 构造函数
	 * 
	 * @parammContext
	 */
	private DBManager() {
		super();
		databaseName = MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, "");
		
	}

	public static DBManager getInstance(Context mContext) {
		if (dBManager == null) {
			dBManager = new DBManager();
		}
		return dBManager;
	}

	/**
	 * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public SQLiteDatabase openDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public DataBaseHelper getDatabaseHelper() {
		return new DataBaseHelper(MyApplication.getInstance().mContext, databaseName, null,
				this.version);
	}
	
	public void Canaell(){
		dBManager = null;
		databaseName = null;
	}
}
