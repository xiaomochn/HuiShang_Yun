package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;
import com.huishangyun.model.GroupUser;
import com.huishangyun.model.IMGroup;
import com.huishangyun.db.SQLiteTemplate.RowMapper;

/**
 * 群组管理类
 * @author forong
 *
 */
public class GroupManager {
	public static GroupManager groupManager = null;
	private static DBManager manager = null;
	
	public static final String GROUP_TABLENAME = "Com_Group";
	//群成员
	public static final String GROUP_USER_TABLENAME = "Com_GroupUser";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private GroupManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static GroupManager getInstance(Context context) {

		if (groupManager == null) {
			groupManager = new GroupManager(context);
		}

		return groupManager;
	}
	
	/**
	 * 保存群组信息
	 * @param imGroup
	 * @return
	 */
	public long saveGroup(IMGroup imGroup) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", imGroup.getID());
		contentValues.put("Name", imGroup.getName());
		contentValues.put("Photo", imGroup.getPhoto());
		contentValues.put("Note", imGroup.getNote());
		contentValues.put("Type", imGroup.getType());
		contentValues.put("Approval", imGroup.getApproval());
		contentValues.put("Owner", imGroup.getOwner());
		contentValues.put("OpenID", imGroup.getOpenID());
		if (imGroup.getStatus()) {
			if (st.isExistsByField(GROUP_TABLENAME, "ID", imGroup.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(GROUP_TABLENAME, contentValues, "ID = ?", new String[]{imGroup.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(GROUP_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(GROUP_TABLENAME, "ID", imGroup.getID() + "");
		}
	}
	
	/**
	 * 保存群用户
	 * @param groupUser
	 * @return
	 */
	public long saveGroupUser(GroupUser groupUser) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("Group_ID", groupUser.getGroup_ID());
		contentValues.put("User_ID", groupUser.getUser_ID());
		if (groupUser.getStatus()) {
			if (st.isExistsBySQL("select * from " + GROUP_USER_TABLENAME + " where Group_ID = ? and User_ID = ?",
					new String[]{groupUser.getGroup_ID() +"", groupUser.getUser_ID() + ""})) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(GROUP_USER_TABLENAME, contentValues, "Group_ID = ? and User_ID = ?", 
						new String[]{groupUser.getGroup_ID() + "", groupUser.getUser_ID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(GROUP_USER_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByCondition(GROUP_USER_TABLENAME, "Group_ID = ? and User_ID = ?",
					new String[]{groupUser.getGroup_ID() + "", groupUser.getUser_ID() + ""});
		}
	}
	
	/**
	 * 根据群ID获取群头像
	 * @param OpenID
	 * @return
	 */
	public String getPhoto(String OpenID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String photo = st.queryForObject(new RowMapper<String>() {
			@Override
			public String mapRow(Cursor cursor, int index) {
				return cursor.getString(cursor.getColumnIndex("Photo"));
			}
		}, "select Photo from " + GROUP_TABLENAME + " where OpenID = '" + OpenID + "'", new String[]{});
		return photo;
	}
	
	/**
	 * 获取群列表
	 * @return
	 */
	public List<IMGroup> getImGroups() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<IMGroup> imGroups = new ArrayList<IMGroup>();
		imGroups = st.queryForList(new RowMapper<IMGroup>() {

			@Override
			public IMGroup mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				IMGroup imGroup = new IMGroup();
				imGroup.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				imGroup.setName(cursor.getString(cursor.getColumnIndex("Name")));
				imGroup.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				imGroup.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				imGroup.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				imGroup.setApproval(cursor.getInt(cursor.getColumnIndex("Approval")));
				imGroup.setOwner(cursor.getString(cursor.getColumnIndex("Owner")));
				imGroup.setOpenID(cursor.getString(cursor.getColumnIndex("OpenID")));
				return imGroup;
			}
		}, "select * from " + GROUP_TABLENAME , new String[]{});
		return imGroups;
	}
	
	/**
	 * 获取群名称
	 * @param OpenID
	 * @return
	 */
	public String getGroupName(String OpenID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String Name = st.queryForObject(new RowMapper<String>() {
			@Override
			public String mapRow(Cursor cursor, int index) {
				return cursor.getString(cursor.getColumnIndex("Name"));
			}
		}, "select Name from " + GROUP_TABLENAME + " where OpenID = '" + OpenID + "'", new String[]{});
		return Name;
	}
	
	/**
	 * 获取群ID
	 * @param OpenID
	 * @return
	 */
	public Integer getGroupID(String OpenID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.queryForObject(new RowMapper<Integer>() {

			@Override
			public Integer mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				return cursor.getInt(cursor.getColumnIndex("ID"));
			}
		}, "select ID from " + GROUP_TABLENAME + " where OpenID = " + OpenID, new String[]{});
	}
	
	/**
	 * 获取群成员
	 * @param Group_ID
	 * @return
	 */
	public List<GroupUser> getGroupUsers(String Group_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<GroupUser> mList = new ArrayList<>();
		mList = st.queryForList(new RowMapper<GroupUser>() {

			@Override
			public GroupUser mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				GroupUser groupUser = new GroupUser();
				groupUser.setGroup_ID(cursor.getInt(cursor.getColumnIndex("Group_ID")));
				groupUser.setUser_ID(cursor.getInt(cursor.getColumnIndex("User_ID")));
				return groupUser;
			}
		}, "select * from " + GROUP_USER_TABLENAME + " where Group_ID = " + Group_ID, new String[]{});
		return mList;
		
	}
	
}
