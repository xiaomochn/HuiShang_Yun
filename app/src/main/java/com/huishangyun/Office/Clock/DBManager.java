package com.huishangyun.Office.Clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huishangyun.Util.L;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作类
 * 
 * @author xsl
 * 
 */
public class DBManager {
	private static final String TAG = null;
	private DBHelper dbHelper;
	private SQLiteDatabase database;

	public DBManager(Context context) {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * 操作数据库(实现对数据库的添加、删除、修改功能)
	 * 
	 * @param sql
	 *            SQL语句
	 * @param bindArgs
	 * @return
	 */
	public boolean updataBySQL(String sql, Object[] bindArgs) {
		boolean flag = false;
		try {
			database.execSQL(sql, bindArgs);
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (database != null) {
				database.close();
			}
		}

		return flag;
	}

	/**
	 * 数据库查询(查询返回单条数据)
	 * 
	 * @param sql
	 * @param selectionArgs
	 */
	public Map<String, String> queryBySQL(String sql, String[] selectionArgs) {
		Map<String, String> map = new HashMap<String, String>();
		Cursor cursor = database.rawQuery(sql, selectionArgs);
		// 计算数据条数
		int cols_len = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Log.e(TAG,
					"===>:" + cursor.getInt(cursor.getColumnIndex("ManagerID")));
			for (int i = 0; i < cols_len; i++) {
				String cols_name = cursor.getColumnName(i);
				String cols_value = cursor.getString(cursor
						.getColumnIndex(cols_name));
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;
	}

	/**
	 * 查询返回多条数据
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public List<Map<String, Integer>> queryMultiMaps(String sql,
			String[] selectionArgs) {
		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
		Cursor cursor = database.rawQuery(sql, selectionArgs);
		int cols_len = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i < cols_len; i++) {
				String cols_name = cursor.getColumnName(i);
				int cols_value = cursor
						.getInt(cursor.getColumnIndex(cols_name));
				if (cols_value == 0) {
					cols_value = 0;
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
		return list;
	}

	/**
	 *  闹钟添加数据
	 * @param mIndex
	 * @param ManagerID
	 * @param isOpen
	 * @param alarmTime
	 * @param repeatDays
	 * @param pickUri
	 * @param Volume
	 * @param MusicName
	 * @return
	 */
	public boolean clockAdd(String mIndex, String ManagerID, String isOpen,
			String alarmTime, String repeatDays, String pickUri,
			String Volume,String MusicName, String Vib) {
		ContentValues values = new ContentValues();
		values.put("mIndex", mIndex);
		values.put("ManagerID", ManagerID);
		values.put("isOpen", isOpen);
		values.put("alarmTime", alarmTime);
		values.put("repeatDays", repeatDays);
		values.put("pickUri", pickUri);
		values.put("Volume", Volume);
		values.put("MusicName", MusicName);
		values.put("Vib", Vib);
		long rowid = database.insert("mClock", null, values);
		if (rowid == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 从数据库删除的方法
	 */
	public boolean clockDelete(String mIndex,String ManagerID) {
		// TODO Auto-generated method stub
		String where = "mIndex = " + "?" + "and ManagerID = " + "?";
		int row = database.delete("mClock", where, new String[] {mIndex,ManagerID});
		Log.e("========删除===", "row:" + row);
		if (row > 0) {
			return true;
		} else {
			return false;
		}
	}
     
	/**
	 * 修改所以数据
	 * @param mIndex
	 * @param ManagerID
	 * @param isOpen
	 * @param alarmTime
	 * @param repeatDays
	 * @param pickUri
	 * @param Volume
	 * @param MusicName
	 * @return
	 */
	public boolean clockUpdate(String mIndex,String ManagerID,
			String isOpen,String alarmTime,String repeatDays,String pickUri,
			String Volume,String MusicName,String Vib) {
		// TODO Auto-generated method stub
		String whereClause = "mIndex = " + "?" + " and ManagerID = " + "?";
		Log.e("========修改===", "where:" + whereClause);
		ContentValues values = new ContentValues();
		values.put("mIndex", mIndex);
		values.put("isOpen", isOpen);
		values.put("alarmTime", alarmTime);
		values.put("repeatDays", repeatDays);
		values.put("pickUri", pickUri);
		values.put("Volume", Volume);
		values.put("MusicName", MusicName);
		values.put("Vib", Vib);
		String[] whereArgs = {mIndex, ManagerID };
		int row = database.update("mClock", values, whereClause, whereArgs);
		L.e("row = " + row);
		if (row > 0) {// 失败返回0，成功返回1
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 修改相应表单数据
	 * @param mIndex
	 * @param ManagerID
	 * @param isOpen
	 * @return
	 */
	public boolean clockOnOff(String ManagerID, String isOpen) {
		// TODO Auto-generated method stub
		String whereClause = "ManagerID = " + "?";
		Log.e("========修改===", "where:" + whereClause);
		ContentValues values = new ContentValues();
		values.put("isOpen", isOpen);
		String[] whereArgs = {ManagerID };
		int row = database.update("mClock", values, whereClause, whereArgs);
		L.e("row = " + row);
		if (row > 0) {// 失败返回0，成功返回1
			return true;
		} else {
			return false;
		}
	}

}
