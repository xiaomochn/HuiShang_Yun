package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.huishangyun.Channel.Competing.CompetingHostoryList;
import com.huishangyun.Util.L;
import com.huishangyun.db.SQLiteTemplate;

public class CompetingManager {

	public static CompetingManager competingManager = null;
	private static DBManager manager = null;
	public static final String COMPETING_TABLENAME = "Com_Competing";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private CompetingManager(Context context) {
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static CompetingManager getInstance(Context context) {

		if (competingManager == null) {
			competingManager = new CompetingManager(context);
		}

		return competingManager;
	}
	
	
	/**
	 * 保存、更新拜访数据
	 * @param competingList
	 * @return
	 */
	public long saveVisitData(CompetingHostoryList competingList) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", competingList.getID());
		contentValues.put("isSubmit", competingList.getIsSubmit());
		contentValues.put("ManagerID", competingList.getManagerID());
		contentValues.put("CustormerName", competingList.getCustormerName());
		contentValues.put("CustormerID", competingList.getCustormerID());
		contentValues.put("PhotoUrl", competingList.getPhotoUrl());
		contentValues.put("UpUrl", competingList.getUpUrl());
		contentValues.put("Note", competingList.getNote());
		contentValues.put("Brand", competingList.getBrand());
		contentValues.put("MyBrand", competingList.getMyBrand());
//		L.e("存储competingList.getPhotoUrl():" + competingList.getPhotoUrl());
//		L.e("存储competingList.getNote():" + competingList.getNote());
		L.e("存储competingList.getIsSubmit():" + competingList.getIsSubmit());
		L.e("存储competingList.getBrand():" + competingList.getBrand());
		L.e("存储competingList.getMyBrand():" + competingList.getMyBrand());
		//更新
		if (st.isExistsByField(COMPETING_TABLENAME, "ManagerID", competingList.getManagerID() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(COMPETING_TABLENAME, contentValues, "ManagerID = ?", new String[]{competingList.getManagerID() + ""});
		} else {
			//添加
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(COMPETING_TABLENAME, contentValues);
		}
		
	}
	
	public List<CompetingHostoryList> getVisitData(Integer ManagerID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<CompetingHostoryList> mList = new ArrayList<CompetingHostoryList>();
		st = SQLiteTemplate.getInstance(manager, false);
		if (st.isExistsByField(COMPETING_TABLENAME, "ManagerID", ManagerID + "")) {
			mList = st.queryForList(new SQLiteTemplate.RowMapper<CompetingHostoryList>() {
				@Override
				public CompetingHostoryList mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					CompetingHostoryList visitHostoryList = new CompetingHostoryList();
					visitHostoryList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					visitHostoryList.setIsSubmit(cursor.getInt(cursor.getColumnIndex("isSubmit")));
					visitHostoryList.setManagerID(cursor.getInt(cursor.getColumnIndex("ManagerID")));
					visitHostoryList.setCustormerName(cursor.getString(cursor.getColumnIndex("CustormerName")));
					visitHostoryList.setCustormerID(cursor.getInt(cursor.getColumnIndex("CustormerID")));
					visitHostoryList.setPhotoUrl(cursor.getString(cursor.getColumnIndex("PhotoUrl")));
					visitHostoryList.setUpUrl(cursor.getString(cursor.getColumnIndex("UpUrl")));
					visitHostoryList.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					visitHostoryList.setBrand(cursor.getString(cursor.getColumnIndex("Brand")));
					visitHostoryList.setMyBrand(cursor.getString(cursor.getColumnIndex("MyBrand")));
					return visitHostoryList;
				}
			}, "select * from " + COMPETING_TABLENAME + " where ManagerID = " + "?", new String[]{ManagerID + ""});
			
			return mList;
		} else {
			return null;
		}
	}
	
	public void Canaell(){
		competingManager = null;
		manager = null;
	}
	
}
