package com.huishangyun.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huishangyun.Util.Service;
import com.huishangyun.Util.ServiceMenu;
import com.huishangyun.db.SQLiteTemplate;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
	public static final String SERVICE_NAME = "Sys_Server";
	public static final String SERVICE_MENU_NAME = "Sys_ServerMenu";
	public static ServiceManager serviceManager = null;
	private static DBManager manager = null;
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private ServiceManager(Context context) {
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static ServiceManager getInstance(Context context) {

		if (serviceManager == null) {
			serviceManager = new ServiceManager(context);
		}

		return serviceManager;
	}
	
	/**
	 * 存储应用信息
	 * @param service
	 * @return
	 */
	public long saveService(Service service) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", service.getID());
		contentValues.put("Name", service.getName());
		contentValues.put("LoginName", service.getLoginName());
		contentValues.put("Photo", service.getPhoto());
		contentValues.put("Note", service.getNote());
		contentValues.put("Sequence", service.getSequence());
		if (service.getStatus()) {
			if (st.isExistsByField(SERVICE_NAME, "ID", service.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(SERVICE_NAME, contentValues, "ID = ?", new String[]{service.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(SERVICE_NAME, contentValues);
			}
		} else {
			return st.deleteByField(SERVICE_NAME, "ID", service.getID() + "");
		}
		
	}
	
	/**
	 * 存储应用菜单信息
	 * @param service
	 * @return
	 */
	public long saveServiceMenu(ServiceMenu service) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", service.getID());
		contentValues.put("Server_ID", service.getServer_ID());
		contentValues.put("Name", service.getName());
		contentValues.put("Url", service.getUrl());
		contentValues.put("ParentID", service.getParentID());
		contentValues.put("Sequence", service.getSequence());
		contentValues.put("OperationTime", service.getOperationTime());
		contentValues.put("IsLogin", service.getLogin());
		//Log.e("TAGS","IsLogin="+service.getLogin());
		if (service.getStatus()) {
			if (st.isExistsByField(SERVICE_MENU_NAME, "ID", service.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(SERVICE_MENU_NAME, contentValues, "ID = ?", new String[]{service.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(SERVICE_MENU_NAME, contentValues);
			}
		} else {
			return st.deleteByField(SERVICE_MENU_NAME, "ID", service.getID() + "");
		}
		
	}
	
	/**
	 * 获取轻应用列表
	 * @return
	 */
	public List<Service> getServices() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Service> mList = new ArrayList<Service>();
		mList = st.queryForList(new SQLiteTemplate.RowMapper<Service>(){

			@Override
			public Service mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Service service = new Service();
				service.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				service.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				service.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				service.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				service.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				service.setName(cursor.getString(cursor.getColumnIndex("Name")));
				return service;
			}
			
		}, "select * from " + SERVICE_NAME , new String[]{});
		return mList;
	}
	
	/**
	 * 获取一级菜单
	 * @param ID
	 * @return
	 */
	public List<ServiceMenu> getFristMenu(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ServiceMenu> mList = new ArrayList<ServiceMenu>();
		mList = st.queryForList(new SQLiteTemplate.RowMapper<ServiceMenu>(){

			@Override
			public ServiceMenu mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				ServiceMenu service = new ServiceMenu();
				service.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				service.setName(cursor.getString(cursor.getColumnIndex("Name")));
				service.setUrl(cursor.getString(cursor.getColumnIndex("Url")));
				int islogin=cursor.getInt(cursor.getColumnIndex("IsLogin"));
				//Log.e("TAGS","islogin值="+islogin);
				if (islogin==1){
					service.setLogin(true);
				}else{
					service.setLogin(false);
				}
				return service;
			}
			
		}, "select * from " + SERVICE_MENU_NAME + " where Server_ID = " + ID + 
		 	" and ParentID = 0", new String[]{});
		return mList;
	}
	
	/**
	 * 获取二级菜单
	 * @param ID
	 * @return
	 */
	public List<ServiceMenu> getSecondMenu(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ServiceMenu> mList = new ArrayList<ServiceMenu>();
		mList = st.queryForList(new SQLiteTemplate.RowMapper<ServiceMenu>(){

			@Override
			public ServiceMenu mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				ServiceMenu service = new ServiceMenu();
				service.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				service.setName(cursor.getString(cursor.getColumnIndex("Name")));
				service.setUrl(cursor.getString(cursor.getColumnIndex("Url")));
				int islogin=cursor.getInt(cursor.getColumnIndex("IsLogin"));
				if (islogin==1){
					service.setLogin(true);
				}else{
					service.setLogin(false);
				}
				return service;
			}
			
		}, "select * from " + SERVICE_MENU_NAME + " where ParentID = " + ID, new String[]{});
		return mList;
	}
	
	
	public Integer getServiceID(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Integer serviceID = st.queryForObject(new SQLiteTemplate.RowMapper<Integer>() {

			@Override
			public Integer mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				return cursor.getInt(cursor.getColumnIndex("ID"));
			}

			
		}, "select ID from " + SERVICE_NAME + " where LoginName = '" + OFUserName +"'", new String[]{});
		return serviceID;
	}
	
	public Service getService(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Service service = st.queryForObject(new SQLiteTemplate.RowMapper<Service>() {

			@Override
			public Service mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Service service = new Service();
				service.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				service.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				service.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				service.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				service.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				service.setName(cursor.getString(cursor.getColumnIndex("Name")));
				return service;
			}

			
		}, "select * from " + SERVICE_NAME + " where LoginName = '" + OFUserName +"'", new String[]{});
		return service;
	}
	
	public String getServiceName(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String Name = st.queryForObject(new SQLiteTemplate.RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				return cursor.getString(cursor.getColumnIndex("Name"));
			}

			
		}, "select Name from " + SERVICE_NAME + " where LoginName = '" + OFUserName +"'", new String[]{});
		return Name;
	}
	
	public String getServiceImg(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String Name = st.queryForObject(new SQLiteTemplate.RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				return cursor.getString(cursor.getColumnIndex("Photo"));
			}

			
		}, "select Photo from " + SERVICE_NAME + " where LoginName = '" + OFUserName +"'", new String[]{});
		return Name;
	}
}
