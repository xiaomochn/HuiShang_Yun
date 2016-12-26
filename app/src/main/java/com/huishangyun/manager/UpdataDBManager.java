package com.huishangyun.manager;

import com.huishangyun.Util.UpdataUtil;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

public class UpdataDBManager {
	private static UpdataDBManager updataDBManager;
	private static DBManager manager;
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private UpdataDBManager(Context context){
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		String databaseName = sharedPre.getString(Constant.XMPP_LOGIN_NAME, null);
		manager = DBManager.getInstance(context);
	}
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static UpdataDBManager getInstance(Context context){
		if (updataDBManager == null) {
			updataDBManager = new UpdataDBManager(context);
		}
		return updataDBManager;
	}
	/**
	 * 存储更新时间
	 * @param updataUtil
	 * @return
	 */
	public long saveUpdataTime(UpdataUtil updataUtil){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", updataUtil.getID());
		contentValues.put("updata_time", updataUtil.getTime());
		if (updataUtil.getType() == 0) {//判断是否为同事
			if (st.isExistsByField("im_updata_kehu", "ID", updataUtil.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				//判读数据库是否有该条的数据，有更新则更新，无更新则插入，下同！
				return st.update("im_updata_kehu", contentValues, "ID=?", new String[]{updataUtil.getID() + ""});
			}else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert("im_updata_kehu", contentValues);
			}
		}else {
			if (st.isExistsByField("im_updata_contact", "ID", updataUtil.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update("im_updata_contact", contentValues, "ID=?", new String[]{updataUtil.getID() + ""});
			}else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert("im_updata_contact", contentValues);
			}
		}
	}
	/**
	 * 获取更新的时间戳
	 * @param ID
	 * @param type
	 * @return
	 */
	public String getUpdataTime(int ID,int type){
		if (type == 0) {//判断是否
			return getKehuTime(ID);
		}else {
			return getContactTime(ID);
		}
	}
	/**
	 * 获取同事的更新时间戳
	 * @param ID
	 * @return
	 */
	private String getKehuTime(int ID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (!st.isExistsByField("im_updata_kehu", "ID", ID + "")) {
			//判断是否有该条数据
			return 0 + "";
		}
		st = SQLiteTemplate.getInstance(manager, false);
		String time =  st.queryForObject(new SQLiteTemplate.RowMapper<String>(){

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String time;
				time = cursor.getString(cursor.getColumnIndex("updata_time"));
				return time;
			}
			
		},"select * from im_updata_kehu where ID = ?", new String[]{ID+""});
		return time;
	}
	/**
	 * 获取客户的更新时间戳
	 * @param ID
	 * @return
	 */
	private String getContactTime(int ID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (!st.isExistsByField("im_updata_contact", "ID", ID + "")) {
			//判断是否有该条数据
			return 0+"";
		}
		st = SQLiteTemplate.getInstance(manager, false);
		String time =  st.queryForObject(new SQLiteTemplate.RowMapper<String>(){

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String time;
				time = cursor.getString(cursor.getColumnIndex("updata_time"));
				return time;
			}
			
		},"select * from im_updata_contact where ID = ?", new String[]{ID+""});
		return time;
	}
	
	/**
	 * 删除同事更新时间
	 * @return
	 */
	public int deledtKehuTime(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteAll("im_updata_kehu");
	}
	
	/**
	 * 删除客户更新时间
	 * @return
	 */
	public int deleteContactTime(){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteAll("im_updata_contact");
	}
		
}
