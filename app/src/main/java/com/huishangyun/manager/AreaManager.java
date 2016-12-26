package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.L;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.*;

public class AreaManager {
	public static AreaManager areaManager = null;
	
	public static final String AREA_TABLENAME = "Sys_Area";
	//private static DBManager manager = null;
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private AreaManager(Context context) {
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static AreaManager getInstance(Context context) {

		if (areaManager == null) {
			areaManager = new AreaManager(context);
			//manager = DBManager.getInstance(context);
		}

		return areaManager;
	}


	/**
	 * 保存省市县
	 * @param areaModel
	 * @return
	 */
	/*public long saveArea(AreaModel areaModel) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", areaModel.getID());
		contentValues.put("Name", areaModel.getName());
		contentValues.put("ParentID", areaModel.getParentID());
		contentValues.put("Path", areaModel.getPath());
		return st.insert(AREA_TABLENAME, contentValues);

	}*/
	
	/**
	 * 获取省市县
	 * @param ID 
	 * @param isParentID
	 * @return
	 */
	/*public List<AreaModel> getAreas(int ID, boolean isParentID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<AreaModel> mList = new ArrayList<AreaModel>();
		if (isParentID) {
			mList = st.queryForList(new RowMapper<AreaModel>() {

				@Override
				public AreaModel mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					AreaModel areaModel = new AreaModel();
					areaModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					areaModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
					areaModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					areaModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					return areaModel;
				}
			}, "select * from " + AREA_TABLENAME + " where ParentID = (select ParentID from "
				+ AREA_TABLENAME + " where ID = ?)", new String[]{ID + ""});
		} else {
			mList = st.queryForList(new RowMapper<AreaModel>() {

				@Override
				public AreaModel mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					AreaModel areaModel = new AreaModel();
					areaModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					areaModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
					areaModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					areaModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					return areaModel;
				}
			}, "select * from " + AREA_TABLENAME + " where ParentID = " + ID, new String[]{});
		}
		L.e("mList.size = " + mList.size());
		return mList;
	}*/
	
	
	public List<AreaModel> getAreas(int ID, boolean isParentID) {
		List<AreaModel> mList = new ArrayList<AreaModel>();
		SQLiteDatabase db = MyApplication.getInstance().getCitySqLiteDatabase();
		if (isParentID) {
			Cursor cursor = db.rawQuery("select * from " + AREA_TABLENAME + " where ParentID = (select ParentID from "
				+ AREA_TABLENAME + " where ID = ?)", new String[]{ID + ""});
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				AreaModel areaModel = new AreaModel();
				areaModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				areaModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
				areaModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
				areaModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
				mList.add(areaModel);
				cursor.moveToNext();
			}
			cursor.close();
		} else {
			Cursor cursor = db.rawQuery("select * from " + AREA_TABLENAME + " where ParentID = " + ID, new String[]{});
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				AreaModel areaModel = new AreaModel();
				areaModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				areaModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
				areaModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
				areaModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
				mList.add(areaModel);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return mList;
	}
	
	/**
	 * 根据ID获取名称
	 * @param ID
	 * @return
	 */
	/*public String getName(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String name = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				ParentID = cursor.getInt(cursor.getColumnIndex("ParentID"));
				L.e("name = " + cursor.getString(cursor.getColumnIndex("Name")));
				return cursor.getString(cursor.getColumnIndex("Name"));
				
			}
		}, "select * from " + AREA_TABLENAME + " where ID = ?", new String[]{ID + ""});
		return name;		
	}*/
	public String getName(int ID) {
		SQLiteDatabase db = MyApplication.getInstance().getCitySqLiteDatabase();
		Cursor cursor = db.rawQuery("select * from " + AREA_TABLENAME + " where ID = ?", new String[]{ID + ""});
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex("Name"));
		cursor.close();
		return name;		
	}
	
	
	private static int ParentID = 0;
	private static int IDs = 0;
	
	/**
	 * 获取省市县名称
	 * @param ID
	 * @return
	 */
	public String getAddress(int ID) {
		if (ID == -1) {
			return "";
		}
		String result = "";
		ParentID = 0;
		this.IDs = ID;
		result = getName(ID);
		L.e("result = " + result);
		do {
			result = getNameForID(this.IDs) + result;
			L.e("result = " + result);
		} while (ParentID != 0); 
		return result;
		
	}
	
	/**
	 * 根据ID获取名称
	 * @param ID
	 * @return
	 */
	private String getNameForID(int ID) {
		SQLiteDatabase db = MyApplication.getInstance().getCitySqLiteDatabase();
		Cursor cursor = db.rawQuery("select * from " + AREA_TABLENAME + 
				" where ID in (select ParentID from "+ AREA_TABLENAME + " where ID=?)", new String[]{"" + ID});
		cursor.moveToFirst();
		String Name = cursor.getString(cursor.getColumnIndex("Name"));
		ParentID = cursor.getInt(cursor.getColumnIndex("ParentID"));
		IDs = cursor.getInt(cursor.getColumnIndex("ID"));
		L.e("Name = " + Name);
		cursor.close();
		return Name;
		
	}
	
	
	
}
