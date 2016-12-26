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
import com.huishangyun.model.Enum;

/**
 * 数据字典管理类
 * @author Pan
 *
 */
public class EnumManager {
	public static EnumManager enumManager = null;
	private static DBManager manager = null;
	
	public static final String EMUN_TABLENAME = "Sys_Enum";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private EnumManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static EnumManager getInstance(Context context) {

		if (enumManager == null) {
			enumManager = new EnumManager(context);
		}

		return enumManager;
	}
	
	/**
	 * 保存数据字典
	 * @param emun
	 * @return
	 * Lab 说明 
	 * EnumKey 键值
	 * IntKey 
	 */
	public long saveEmun(Enum emun) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("MyPK", emun.getMyPK());
		contentValues.put("Lab", emun.getLab());
		contentValues.put("EnumKey", emun.getEnumKey());
		contentValues.put("IntKey", emun.getIntKey());
		contentValues.put("OperationTime", emun.getOperationTime());
		if (emun.getStatus()) {
			if (st.isExistsByField(EMUN_TABLENAME, "MyPK", emun.getMyPK())) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(EMUN_TABLENAME, contentValues, "MyPK = ?", new String[]{emun.getMyPK()});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(EMUN_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(EMUN_TABLENAME, "MyPK", emun.getMyPK());
		}
		
	}
	
	/**
	 * 根据MyPK获取其他字典数据
	 * @param EnumKey
	 * @param IntKey
	 * @return
	 */
	public Enum getEmunForIntKey(String EnumKey, String IntKey) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Enum emun = st.queryForObject(new RowMapper<Enum>() {

			@Override
			public Enum mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Enum emun = new Enum();
				emun.setMyPK(cursor.getString(cursor.getColumnIndex("MyPK")));
				emun.setLab(cursor.getString(cursor.getColumnIndex("Lab")));
				emun.setEnumKey(cursor.getString(cursor.getColumnIndex("EnumKey")));
				emun.setIntKey(cursor.getString(cursor.getColumnIndex("IntKey")));
				emun.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				return emun;
			}
			
		}, "select * from " + EMUN_TABLENAME + " where IntKey = ? and EnumKey = ?", new String[]{IntKey, EnumKey});
		return emun;
	}
	
	/**
	 * 根据状态获取值
	 * @param Lab
	 * @param EnumKey
	 * @return
	 */
	public String getIntKey(String Lab, String EnumKey) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String IntKey = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String IntKey = cursor.getString(cursor.getColumnIndex("IntKey"));
				return IntKey;
			}
			
		}, "select * from " + EMUN_TABLENAME + " where Lab = ? and EnumKey = ?", new String[]{Lab, EnumKey});
		return IntKey;
	}
	
	/**
	 * 获取所有的状态
	 * @param EnumKey
	 * @return
	 */
	public List<Enum> getAllEnums(String EnumKey) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Enum> mEnums = new ArrayList<Enum>();
		String sql = "select * from " + EMUN_TABLENAME + " where EnumKey = ?";
		mEnums = st.queryForList(new RowMapper<Enum>() {

			@Override
			public Enum mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Enum emun = new Enum();
				emun.setMyPK(cursor.getString(cursor.getColumnIndex("MyPK")));
				emun.setLab(cursor.getString(cursor.getColumnIndex("Lab")));
				emun.setEnumKey(cursor.getString(cursor.getColumnIndex("EnumKey")));
				emun.setIntKey(cursor.getString(cursor.getColumnIndex("IntKey")));
				emun.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				return emun;
			}
		}, sql, new String[]{EnumKey});
		return mEnums;
		
	}
	
	public void deleteAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(EMUN_TABLENAME);
	}
}
