package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Files;

public class FileManager {
	public static FileManager fileManager = null;
	private static DBManager manager = null;
	
	/**
	 * 文件列表表名
	 */
	private static final String FILE_TABLE_NAME = "Sys_FileList";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private FileManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static FileManager getInstance(Context context) {

		if (fileManager == null) {
			fileManager = new FileManager(context);
		}

		return fileManager;
	}
	
	/**
	 * 保存最近文件列表
	 * @param files
	 * @return
	 */
	public long saveFiles(Files files) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("File_Name", files.getFile_Name());
		contentValues.put("File_Path", files.getFile_Path());
		contentValues.put("Time", files.getTime());
		//判断文件路径是否相同
		if (st.isExistsByField(FILE_TABLE_NAME, "File_Path", files.getFile_Path() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(FILE_TABLE_NAME, contentValues, "File_Path = ?", new String[]{files.getFile_Path()});
		} else {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(FILE_TABLE_NAME, contentValues);
		}
	}
	
	/**
	 * 获取文件列表
	 * @return
	 */
	public List<Files> getFileLsit() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Files> list = new ArrayList<Files>();
		list = st.queryForList(new RowMapper<Files> () {

			@Override
			public Files mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Files files = new Files();
				files.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				files.setFile_Name(cursor.getString(cursor.getColumnIndex("File_Name")));
				files.setFile_Path(cursor.getString(cursor.getColumnIndex("File_Path")));
				files.setTime(cursor.getLong(cursor.getColumnIndex("Time")));
				return files;
			}
		}, "select * from " + FILE_TABLE_NAME, new String[]{});
		return list;
	}
}
