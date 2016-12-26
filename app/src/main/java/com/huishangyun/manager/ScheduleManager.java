package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.App.MyApplication;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Schedule;

/**
 * 行政班管理类
 * @author Pan
 *
 */
public class ScheduleManager {
	public static ScheduleManager scheduleManager = null;
	private static DBManager manager = null;
	public static final String SCHE_TABLENAME = "Com_Attendance";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private ScheduleManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static ScheduleManager getInstance(Context context) {

		if (scheduleManager == null) {
			scheduleManager = new ScheduleManager(context);
		}

		return scheduleManager;
	}
	
	/**
	 * 保存行政班
	 * @param areaModel
	 * @return
	 */
	public long saveSche(Schedule schedule) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", schedule.getID());
		contentValues.put("Name", schedule.getName());
		contentValues.put("InTime", schedule.getInTime());
		contentValues.put("OutTime", schedule.getOutTime());
		contentValues.put("Company_ID", schedule.getCompany_ID());
		contentValues.put("OperationTime", schedule.getOperationTime());
		if (schedule.getStatus()) {
			if (st.isExistsByField(SCHE_TABLENAME, "ID", schedule.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(SCHE_TABLENAME, contentValues, "ID = ?", new String[]{schedule.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(SCHE_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(SCHE_TABLENAME, "ID", schedule.getID() + "");
		}
	}
	
	/**
	 * 获取所有行政班列表
	 * @return
	 */
	public List<Schedule> getSchedules() {
		List<Schedule> mList = new ArrayList<Schedule>();
		String sql = "select * from " + SCHE_TABLENAME + " where Company_ID = "
		+ MyApplication.getInstance().getCompanyID();
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		mList = st.queryForList(new SQLiteTemplate.RowMapper<Schedule>() {

				@Override
				public Schedule mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Schedule schedule = new Schedule();
					schedule.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					schedule.setName(cursor.getString(cursor.getColumnIndex("Name")));
					schedule.setInTime(cursor.getString(cursor.getColumnIndex("InTime")));
					schedule.setOutTime(cursor.getString(cursor.getColumnIndex("OutTime")));
					schedule.setCompany_ID(cursor.getInt(cursor.getColumnIndex("Company_ID")));
					schedule.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return schedule;
				}
			}, sql, new String[]{});
		return mList;
	}
	
	
	/**
	 * 根据名字查行政班id
	 * @return
	 */
	public List<Schedule> getSchedulesID(String Name) {
		List<Schedule> mList = new ArrayList<Schedule>();
		String sql = "select * from "+ SCHE_TABLENAME + " where Name = '" + Name+ "'";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		mList = st.queryForList(new SQLiteTemplate.RowMapper<Schedule>() {

				@Override
				public Schedule mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Schedule schedule = new Schedule();
					schedule.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					schedule.setName(cursor.getString(cursor.getColumnIndex("Name")));
					schedule.setInTime(cursor.getString(cursor.getColumnIndex("InTime")));
					schedule.setOutTime(cursor.getString(cursor.getColumnIndex("OutTime")));
					schedule.setCompany_ID(cursor.getInt(cursor.getColumnIndex("Company_ID")));
					schedule.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return schedule;
				}
			}, sql, new String[]{});
		return mList;
	}
}
