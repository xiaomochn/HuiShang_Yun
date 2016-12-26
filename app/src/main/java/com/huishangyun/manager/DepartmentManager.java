package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Managers;
import com.huishangyun.Util.L;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;
import com.huishangyun.model.Departments;

/**
 * 人员数据库管理类
 * @author Pan
 * @version 亿企云APP v1.0 11:40
 */
public class DepartmentManager {
	public static DepartmentManager departmentManager = null;
	private static DBManager manager = null;
	
	/**
	 * 人员分组表名
	 */
	public static final String DEPARMENT_TABLENAME = "Com_Department";
	
	/**
	 * 人员表名
	 */
	public static final String MANAGER_TABLENAME = "Com_Manager";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private DepartmentManager(Context context) {
		manager = DBManager.getInstance(context);
	}
	
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static DepartmentManager getInstance(Context context) {

		if (departmentManager == null) {
			departmentManager = new DepartmentManager(context);
		}
		return departmentManager;
	}
	
	/**
	 * 保存人员分组
	 * @param departments
	 * @return
	 */
	public long saveDepartments(Departments departments) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", departments.getID());
		contentValues.put("ParentID", departments.getParentID());
		contentValues.put("Name", departments.getName());
		contentValues.put("Path", departments.getPath());
		contentValues.put("Sequence", departments.getSequence());
		contentValues.put("OperationTime", departments.getOperationTime());
		if (departments.getStatus()) {
			if (st.isExistsByField(DEPARMENT_TABLENAME, "ID", departments.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(DEPARMENT_TABLENAME, contentValues, "ID = ?", new String[]{departments.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(DEPARMENT_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(DEPARMENT_TABLENAME, "ID", departments.getID() + "");
		}
		
	}
	
	/**
	 * 保存人员信息
	 * @param managers
	 * @return
	 */
	public long saveManagers(Managers managers) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", managers.getID());
		contentValues.put("LoginName", managers.getLoginName());
		contentValues.put("RealName", managers.getRealName());
		contentValues.put("Sex", managers.getSex());
		contentValues.put("Email", managers.getEmail());
		contentValues.put("Mobile", managers.getMobile());
		contentValues.put("Weixin", managers.getWeixin());
		contentValues.put("Role_ID", managers.getRole_ID());
		contentValues.put("Role_Name", managers.getRole_Name());
		contentValues.put("Note", managers.getNote());
		contentValues.put("Photo", managers.getPhoto());
		contentValues.put("Sign", managers.getSign());
		contentValues.put("Department_ID", managers.getDepartment_ID());
		contentValues.put("Department_Name", managers.getDepartment_Name());
		contentValues.put("Type", managers.getType());
		contentValues.put("OFUserName", managers.getOFUserName());
		if (managers.getStatus()) {
			if (st.isExistsByField(MANAGER_TABLENAME, "ID", managers.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(MANAGER_TABLENAME, contentValues, "ID = ?", new String[]{managers.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(MANAGER_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(MANAGER_TABLENAME, "ID", managers.getID() + "");
		}
		
	}
	
	/**
	 * 返回人员信息
	 * @param ID
	 * @param isParent
	 * @return
	 */
	public List<Managers> getmManagers(int ID, boolean isParent) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Managers> mList = new ArrayList<Managers>();
		if (isParent) {
			mList = st.queryForList(new RowMapper<Managers>() {

				@Override
				public Managers mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Managers managers = new Managers();
					managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
					managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
					managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
					managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
					managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
					managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
					managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
					managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					return managers;
				}
			}, "select * from " + MANAGER_TABLENAME + " where Department_ID = (select ParentID from " 
					+ DEPARMENT_TABLENAME + " where ID = ?)", new String[]{ID + ""});
		} else {
			mList = st.queryForList(new RowMapper<Managers>() {

				@Override
				public Managers mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Managers managers = new Managers();
					managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
					managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
					managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
					managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
					managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
					managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
					managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
					managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					return managers;
				}
			}, "select * from " + MANAGER_TABLENAME + " where Department_ID = ?", new String[]{"" + ID});
		}
		return mList;
	}
	
	/**
	 * 返回人员分组
	 * @param ID
	 * @param isParent
	 * @return
	 */
	public List<Departments> getDepartments(int ID, boolean isParent) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Departments> mList = new ArrayList<Departments>();
		if (isParent) {
			mList = st.queryForList(new RowMapper<Departments>() {

				@Override
				public Departments mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Departments departments = new Departments();
					departments.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					departments.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					departments.setName(cursor.getString(cursor.getColumnIndex("Name")));
					departments.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					departments.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					departments.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return departments;
				}
			}, "select * from " + DEPARMENT_TABLENAME + " where ParentID = (select ParentID from "
					+ DEPARMENT_TABLENAME + " where ID = ?)", new String[]{ID + ""});
		} else {
			mList = st.queryForList(new RowMapper<Departments>() {

				@Override
				public Departments mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Departments departments = new Departments();
					departments.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					departments.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					departments.setName(cursor.getString(cursor.getColumnIndex("Name")));
					departments.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					departments.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					departments.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return departments;
				}
				
			},"select * from " + DEPARMENT_TABLENAME + " where ParentID = ?", new String[]{ID + ""});
		}
		return mList;
	}
	
	/**
	 * 返回用户具体数据
	 * @param ID
	 * @return
	 */
	public Managers getManagerInfo(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Managers name = st.queryForObject(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from " + MANAGER_TABLENAME + " where ID = ?", new String[]{ID + ""});
		return name;
		
	}
	
	/**
	 * 搜索人员
	 * @paramID
	 * @paramkeywords
	 * @return
	 */
	public List<Managers> searchManagers(String keywords, int Department_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		L.e("keywords = " + keywords + " Department_ID = " + Department_ID );
		List<Managers> mList = new ArrayList<Managers>();
		mList = st.queryForList(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from Com_Manager where Department_ID in ( select ID from Com_Department where [Path] like '%"+Department_ID+"%') and RealName like '%"+ keywords+ "%'", new String[]{});
		return mList;
		
	}
	
	
	public List<Managers> searchManagers(String keywords) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Managers> mList = new ArrayList<Managers>();
		mList = st.queryForList(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from Com_Manager where RealName like '%"+ keywords+ "%'", new String[]{});
		return mList;
	}
	
	
	
	public String getManager(String JID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String name = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String name = cursor.getString(cursor.getColumnIndex("RealName"));
				return name;
			}
		}, "select RealName from Com_Manager where OFUserName = '" + JID + "'", new String[]{});
		return name;
	}
	
	/**
	 * 获取头像
	 * @param JID
	 * @return
	 */
	public String getManagerPhoto(String JID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String photo = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String Photo = cursor.getString(cursor.getColumnIndex("Photo"));
				return Photo;
			}
			
		},"select Photo from Com_Manager where OFUserName = '" + JID + "'", new String[]{});
		return photo;
	}
	
	/**
	 * 获取用户资料
	 * @paramOFUseName
	 * @return
	 */
	public Managers getManagerInfo(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Managers Managers = st.queryForObject(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from Com_Manager where OFUserName = '" + OFUserName + "'", new String[]{});
		return Managers;
		
	}
	/**
	 * 获取用户资料
	 * @paramOFUseName
	 * @return
	 */
	public Managers getManagerForPhone(String phoneNum) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Managers Managers = st.queryForObject(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from Com_Manager where Mobile = '" + phoneNum + "'", new String[]{});
		return Managers;
		
	}
	
	/**
	 * 返回所有部门列表
	 * @return
	 */
	public List<Departments> getllDepartments() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Departments> mDepartments = new ArrayList<Departments>();
		int Department_ID= MyApplication.getInstance().getSharedPreferences().getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		String sql = "select ID,Name from "+DEPARMENT_TABLENAME+" where Path Like '%," + Department_ID + ",%'";
		Log.e("TAGS","sql==="+sql);
		//String sql = "select * from " + DEPARMENT_TABLENAME;
		mDepartments = st.queryForList(new RowMapper<Departments>() {

			@Override
			public Departments mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Departments departments = new Departments();
				departments.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				departments.setName(cursor.getString(cursor.getColumnIndex("Name")));
				//departments.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
//				departments.setPath(cursor.getString(cursor.getColumnIndex("Path")));
//				departments.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
//				departments.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				return departments;
			}
		}, sql, new String[]{});
		return mDepartments;
	}
	
	public List<Managers> getLightApp() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Managers> managers = new ArrayList<Managers>();
		managers = st.queryForList(new RowMapper<Managers>() {

			@Override
			public Managers mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Managers managers = new Managers();
				managers.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				managers.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				managers.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				managers.setSex(cursor.getString(cursor.getColumnIndex("Sex")));
				managers.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				managers.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				managers.setWeixin(cursor.getString(cursor.getColumnIndex("Weixin")));
				managers.setRole_ID(cursor.getString(cursor.getColumnIndex("Role_ID")));
				managers.setRole_Name(cursor.getString(cursor.getColumnIndex("Role_Name")));
				managers.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				managers.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				managers.setSign(cursor.getString(cursor.getColumnIndex("Sign")));
				managers.setDepartment_ID(cursor.getString(cursor.getColumnIndex("Department_ID")));
				managers.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				managers.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				managers.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				return managers;
			}
		}, "select * from " + MANAGER_TABLENAME 
			+ " where Type = 10", new String[]{});
		return managers;
	}
	
	/**
	 * 删除所有数据
	 */
	public void deleteAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(DEPARMENT_TABLENAME);
		st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(MANAGER_TABLENAME);
	}
	
}
