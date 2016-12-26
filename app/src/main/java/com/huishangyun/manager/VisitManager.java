package com.huishangyun.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.huishangyun.Channel.Visit.VisitHostoryList;
import com.huishangyun.db.SQLiteTemplate;

import java.util.ArrayList;
import java.util.List;

public class VisitManager {

	public static VisitManager visitManager = null;
	private static DBManager manager = null;
	public static final String VISIT_TABLENAME = "Com_Visit";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private VisitManager(Context context) {
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static VisitManager getInstance(Context context) {

		if (visitManager == null) {
			visitManager = new VisitManager(context);
		}

		return visitManager;
	}
	
	
	/**
	 * 保存、更新拜访数据
	 * @param vistlist
	 * @return
	 */
	public long saveVisitData(VisitHostoryList vistlist) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", vistlist.getID());
		contentValues.put("isSubmit", vistlist.getIsSubmit());
		contentValues.put("ManagerID", vistlist.getManagerID());
		contentValues.put("CustormerName", vistlist.getCustormerName());
		contentValues.put("CustormerID", vistlist.getCustormerID());
		contentValues.put("isReport", vistlist.getIsReport());
		contentValues.put("ReportDate", vistlist.getReportDate());
		contentValues.put("PhotoUrl", vistlist.getPhotoUrl());
		contentValues.put("UpUrl", vistlist.getUpUrl());
		contentValues.put("Note", vistlist.getNote());
		contentValues.put("SignTime",vistlist.getSignTime());
		contentValues.put("SignLoc",vistlist.getSignLoc());
		contentValues.put("Tags",vistlist.getTags());
		contentValues.put("TagID",vistlist.getTagID());
		
		//更新
		if (st.isExistsByField(VISIT_TABLENAME, "ManagerID", vistlist.getManagerID() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(VISIT_TABLENAME, contentValues, "ManagerID = ?", new String[]{vistlist.getManagerID() + ""});
		} else {
			//添加
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(VISIT_TABLENAME, contentValues);
		}
		
	}
	
	public List<VisitHostoryList> getVisitData(Integer ManagerID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<VisitHostoryList> mList = new ArrayList<VisitHostoryList>();
		st = SQLiteTemplate.getInstance(manager, false);
		if (st.isExistsByField(VISIT_TABLENAME, "ManagerID", ManagerID + "")) {
			mList = st.queryForList(new SQLiteTemplate.RowMapper<VisitHostoryList>() {
				@Override
				public VisitHostoryList mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					VisitHostoryList visitHostoryList = new VisitHostoryList();
					visitHostoryList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					visitHostoryList.setIsSubmit(cursor.getInt(cursor.getColumnIndex("isSubmit")));
					visitHostoryList.setManagerID(cursor.getInt(cursor.getColumnIndex("ManagerID")));
					visitHostoryList.setCustormerName(cursor.getString(cursor.getColumnIndex("CustormerName")));
					visitHostoryList.setCustormerID(cursor.getInt(cursor.getColumnIndex("CustormerID")));
					visitHostoryList.setIsReport(cursor.getInt(cursor.getColumnIndex("isReport")));
					visitHostoryList.setReportDate(cursor.getString(cursor.getColumnIndex("ReportDate")));
					visitHostoryList.setPhotoUrl(cursor.getString(cursor.getColumnIndex("PhotoUrl")));
					visitHostoryList.setUpUrl(cursor.getString(cursor.getColumnIndex("UpUrl")));
					visitHostoryList.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					visitHostoryList.setSignTime(cursor.getString(cursor.getColumnIndex("SignTime")));
					visitHostoryList.setSignLoc(cursor.getString(cursor.getColumnIndex("SignLoc")));
					visitHostoryList.setTags(cursor.getString(cursor.getColumnIndex("Tags")));
					visitHostoryList.setTagID(cursor.getString(cursor.getColumnIndex("TagID")));
					return visitHostoryList;
				}
			}, "select * from " + VISIT_TABLENAME + " where ManagerID = " + "?", new String[]{ManagerID + ""});
			
			return mList;
		} else {
			return null;
		}
	}
	
	public void Canaell(){
		visitManager = null;
		manager = null;
	}
}
