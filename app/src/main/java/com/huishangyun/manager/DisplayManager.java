package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.huishangyun.Channel.Display.DisplayHostoryList;
import com.huishangyun.Util.L;
import com.huishangyun.db.SQLiteTemplate;

public class DisplayManager {

	public static DisplayManager displayManager = null;
	private static DBManager manager = null;
	public static final String COMPETING_TABLENAME = "Com_Competing";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private DisplayManager(Context context) {
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static DisplayManager getInstance(Context context) {

		if (displayManager == null) {
			displayManager = new DisplayManager(context);
		}

		return displayManager;
	}
	
	
	/**
	 * 保存、更新拜访数据
	 * @param displayList
	 * @return
	 */
	public long saveVisitData(DisplayHostoryList displayList) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", displayList.getID());
		contentValues.put("isSubmit", displayList.getIsSubmit());
		contentValues.put("ManagerID", displayList.getManagerID());
		contentValues.put("CustormerName", displayList.getCustormerName());
		contentValues.put("CustormerID", displayList.getCustormerID());
		contentValues.put("PhotoUrl", displayList.getPhotoUrl());
		contentValues.put("UpUrl", displayList.getUpUrl());
		contentValues.put("Note", displayList.getNote());
	
//		L.e("存储displayList.getPhotoUrl():" + displayList.getPhotoUrl());
//		L.e("存储displayList.getNote():" + displayList.getNote());
		L.e("存储displayList.getIsSubmit():" + displayList.getIsSubmit());
		
		//更新
		if (st.isExistsByField(COMPETING_TABLENAME, "ManagerID", displayList.getManagerID() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(COMPETING_TABLENAME, contentValues, "ManagerID = ?", new String[]{displayList.getManagerID() + ""});
		} else {
			//添加
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(COMPETING_TABLENAME, contentValues);
		}
		
	}
	
	public List<DisplayHostoryList> getVisitData(Integer ManagerID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<DisplayHostoryList> mList = new ArrayList<DisplayHostoryList>();
		st = SQLiteTemplate.getInstance(manager, false);
		if (st.isExistsByField(COMPETING_TABLENAME, "ManagerID", ManagerID + "")) {
			mList = st.queryForList(new SQLiteTemplate.RowMapper<DisplayHostoryList>() {
				@Override
				public DisplayHostoryList mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					DisplayHostoryList visitHostoryList = new DisplayHostoryList();
					visitHostoryList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					visitHostoryList.setIsSubmit(cursor.getInt(cursor.getColumnIndex("isSubmit")));
					visitHostoryList.setManagerID(cursor.getInt(cursor.getColumnIndex("ManagerID")));
					visitHostoryList.setCustormerName(cursor.getString(cursor.getColumnIndex("CustormerName")));
					visitHostoryList.setCustormerID(cursor.getInt(cursor.getColumnIndex("CustormerID")));
					visitHostoryList.setPhotoUrl(cursor.getString(cursor.getColumnIndex("PhotoUrl")));
					visitHostoryList.setUpUrl(cursor.getString(cursor.getColumnIndex("UpUrl")));
					visitHostoryList.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					return visitHostoryList;
				}
			}, "select * from " + COMPETING_TABLENAME + " where ManagerID = " + "?", new String[]{ManagerID + ""});
			
			return mList;
		} else {
			return null;
		}
	}
	
	public void Canaell(){
		displayManager = null;
		manager = null;
	}
	
}
