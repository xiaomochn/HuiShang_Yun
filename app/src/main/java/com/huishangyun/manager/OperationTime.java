package com.huishangyun.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;
import com.huishangyun.model.Constant;
import com.huishangyun.model.OperTime;

public class OperationTime {
	public static OperationTime operationTime = null;
	private static DBManager manager = null;
	
	/**
	 * 客户表名
	 */
	private static final String OPERTIME_TITLE = "Sys_OperationTime";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private OperationTime(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static OperationTime getInstance(Context context) {

		if (operationTime == null) {
			operationTime = new OperationTime(context);
		}

		return operationTime;
	}
	
	/**
	 * 保存时间信息
	 * @param operTime
	 * @return
	 */
	public long saveTime(OperTime operTime) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", operTime.getID());
		contentValues.put("Table_Name", operTime.getTable_Name());
		contentValues.put("OperationTime", operTime.getOperationTime());
		
		if (st.isExistsByField(OPERTIME_TITLE, "Table_Name", operTime.getTable_Name())) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(OPERTIME_TITLE, contentValues, "Table_Name = ?", new String[]{operTime.getTable_Name()});
		} else {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(OPERTIME_TITLE, contentValues);
		}
		
	}
	
	/**
	 * 根据Table_Name获取更新时间
	 * @param Table_Name
	 * @return
	 */
	public OperTime getOperTime(String Table_Name) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		OperTime operTime = new OperTime();
		operTime = st.queryForObject(new RowMapper<OperTime> () {

			@Override
			public OperTime mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				OperTime operTime = new OperTime();
				operTime.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				operTime.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				operTime.setTable_Name(cursor.getString(cursor.getColumnIndex("Table_Name")));
				return operTime;
			}
			
		}, "select * from " + OPERTIME_TITLE + " where Table_Name = ?", new String[]{Table_Name});
		return operTime;
	}
}
