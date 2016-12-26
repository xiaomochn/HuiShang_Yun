package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.Channel.Task.MenuMoths;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.model.MemberGroups;
import com.huishangyun.model.MemberLinks;
import com.huishangyun.model.Members;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;

/**
 * 客户数据表操作帮助类
 * @author Pan
 * @version 亿企云APP v1.0 2014-09-02 15:50
 */
public class MemberManager {
	public static MemberManager memberManager = null;
	private static DBManager manager = null;
	
	/**
	 * 客户表名
	 */
	public static final String MEMBER_TABLE_NAME = "Com_Member";
	
	/**
	 * 客户分组表名
	 */
	public static final String MEMBER_GROUP_TABLE_NAME = "Com_MemberGroup";
	
	
	public static final String MEMBER_LINKS_TABLE_NAME = "Com_MemberLink";
	
	
	public static final String MEMBER_IN_GROUP = "Com_MemberInGroup";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private MemberManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static MemberManager getInstance(Context context) {

		if (memberManager == null) {
			memberManager = new MemberManager(context);
		}

		return memberManager;
	}
	
	
/*	public long saveInGroup() {
		
	}*/
	
	/**
	 * 保存用户信息
	 * @param members
	 * @return
	 */
	public long saveMembers(Members members) {
		long result = 0;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", members.getID());
		contentValues.put("RealName", members.getRealName());
		contentValues.put("Level_ID", members.getLevel_ID());
		contentValues.put("Level_Name", members.getLevel_Name());
		contentValues.put("Group_ID", members.getGroup_ID());
		contentValues.put("Group_Name", members.getGroup_Name());
		contentValues.put("Type", members.getType());
		contentValues.put("Contact", members.getContact());
		contentValues.put("Address", members.getAddress());
		contentValues.put("Manager_ID", members.getManager_ID());
		contentValues.put("Manager_Name", members.getManager_Name());
		contentValues.put("Department_ID", members.getDepartment_ID());
		contentValues.put("Department_Name", members.getDepartment_Name());
		contentValues.put("Area_ID", members.getArea_ID());
		contentValues.put("Area_Name", members.getArea_Name());
		contentValues.put("Picture", members.getPicture());
		contentValues.put("Note", members.getNote());
		contentValues.put("LoginName", members.getLoginName());
		contentValues.put("OperationTime", members.getOperationTime());
		contentValues.put("VisitTime", members.getVisitTime());
		contentValues.put("Mobile", members.getMobile());
		contentValues.put("OFUserName", members.getOFUserName());
		contentValues.put("Photo", members.getPhoto());
		if (members.getStatus()) {
			if (st.isExistsByField(MEMBER_TABLE_NAME, "ID", members.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				st.execSQL("Delete from " + MEMBER_IN_GROUP + " where Member_ID = " + members.getID());
				st = SQLiteTemplate.getInstance(manager, false);
				result =  st.update(MEMBER_TABLE_NAME, contentValues, "ID = ?", new String[]{members.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				result =  st.insert(MEMBER_TABLE_NAME, contentValues);
			}
			int end = members.getGroup_ID().length();
			L.e("end = " + end + " getGroup_ID = " + members.getGroup_ID());
			if (end > 1) {
				String[] groupStrings = members.getGroup_ID().substring(1, end-1).split(",");
				for (int i = 0; i < groupStrings.length; i++) {
					st = SQLiteTemplate.getInstance(manager, false);
					ContentValues contentValues2 = new ContentValues();
					contentValues2.put("Member_ID", members.getID());
					contentValues2.put("Group_ID", groupStrings[i]);
					contentValues2.put("OperationTime", members.getOperationTime());
					st.insert(MEMBER_IN_GROUP, contentValues2);
				}
			} else if (end == 1) {
				st = SQLiteTemplate.getInstance(manager, false);
				ContentValues contentValues2 = new ContentValues();
				contentValues2.put("Member_ID", members.getID());
				contentValues2.put("Group_ID", members.getGroup_ID());
				contentValues2.put("OperationTime", members.getOperationTime());
				st.insert(MEMBER_IN_GROUP, contentValues2);
			}
			return result;
		} else {
			st.execSQL("Delete from " + MEMBER_IN_GROUP + " where Member_ID = " + members.getID());
			st = SQLiteTemplate.getInstance(manager, false);
			return st.deleteByField(MEMBER_TABLE_NAME, "ID", members.getID() + "");
		}
		
	}
	
	/**
	 * 保存客户分组
	 * @param groups
	 * @return
	 */
	public long saveGroups(MemberGroups groups) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", groups.getID());
		contentValues.put("ParentID", groups.getParentID());
		contentValues.put("Name", groups.getName());
		contentValues.put("Path", groups.getPath());
		contentValues.put("Sequence", groups.getSequence());
		contentValues.put("OperationTime", groups.getOperationTime());
		
		if (groups.getStatus()) {
			if (st.isExistsByField(MEMBER_GROUP_TABLE_NAME, "ID", groups.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(MEMBER_GROUP_TABLE_NAME, contentValues, "ID = ?", new String[]{groups.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(MEMBER_GROUP_TABLE_NAME, contentValues);
				
			}
		} else {
			return st.deleteByField(MEMBER_GROUP_TABLE_NAME, "ID", groups.getID() + "");
		}
		
	}
	
	/**
	 * 保存客户联系人
	 * @param memberLinks
	 * @return
	 */
	public long saveMemberLinks(MemberLinks memberLinks) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", memberLinks.getID());
		contentValues.put("Member_ID", memberLinks.getMember_ID());
		contentValues.put("Name", memberLinks.getName());
		contentValues.put("Mobile", memberLinks.getMobile());
		contentValues.put("Tel", memberLinks.getTel());
		contentValues.put("Job", memberLinks.getJob());
		contentValues.put("Address", memberLinks.getAddress());
		contentValues.put("ZipCode", memberLinks.getZipCode());
		contentValues.put("Email", memberLinks.getEmail());
		contentValues.put("OperationTime", memberLinks.getOperationTime());
		if (memberLinks.getStatus()) {
			if (st.isExistsByField(MEMBER_LINKS_TABLE_NAME, "ID", memberLinks.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(MEMBER_LINKS_TABLE_NAME, contentValues, "ID = ?", new String[]{memberLinks.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(MEMBER_LINKS_TABLE_NAME, contentValues);
				
			}
		} else {
			return st.deleteByField(MEMBER_LINKS_TABLE_NAME, "ID", memberLinks.getID() + "");
		}
	}
	
	/**
	 * 根据客户ID获取客户联系人
	 * @param Member_ID
	 * @return
	 */
	public List<MemberLinks> getMemberLinks(int Member_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<MemberLinks> mList = new ArrayList<MemberLinks>();
		mList = st.queryForList(new RowMapper<MemberLinks> () {

			@Override
			public MemberLinks mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				MemberLinks memberLinks = new MemberLinks();
				memberLinks.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				memberLinks.setMember_ID(cursor.getInt(cursor.getColumnIndex("Member_ID")));
				memberLinks.setName(cursor.getString(cursor.getColumnIndex("Name")));
				memberLinks.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				memberLinks.setTel(cursor.getString(cursor.getColumnIndex("Tel")));
				memberLinks.setJob(cursor.getString(cursor.getColumnIndex("Job")));
				memberLinks.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				memberLinks.setZipCode(cursor.getString(cursor.getColumnIndex("ZipCode")));
				memberLinks.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
				memberLinks.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				return memberLinks;
			}
			
		}, "select * from " + MEMBER_LINKS_TABLE_NAME + " where Member_ID = ?" ,new String[]{Member_ID + ""});
		return mList;
	}
	
	/**
	 * 根据ID和Department_ID获取人员信息
	 * @param ID 父级ID或者ID
	 * @param isParent 是否为父级ID
	 * @return
	 */
	public List<Members> getMembersForDepartment(int ID, boolean isParent, int Department_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (isParent) { //是父级ID则返回上一级的人员信息
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"
					+Department_ID+",%')) and ','||Group_ID||',' like '%,'||Cast((select ParentID from Com_MemberGroup where ID="+ ID +") as varchar)||',%'", new String[]{});
		} else { //不是父级ID就返回下一级的ID
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"+Department_ID+",%')) and ','||Group_ID||',' like '%,"+ ID+",%'", new String[]{});
		}
		return mList;
		
	}
	
	public List<Members> getTopMembersForDepartment(int ID, boolean isParent, int Department_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (isParent) { //是父级ID则返回上一级的人员信息
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"
					+Department_ID+",%')) and ','||Group_ID||',' like '%,'||Cast((select ParentID from Com_MemberGroup where ID="+ ID +") as varchar)||',%' and Type = 2", new String[]{});
		} else { //不是父级ID就返回下一级的ID
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"+Department_ID+",%')) and ','||Group_ID||',' like '%,"+ ID+",%' and Type = 2", new String[]{});
		}
		return mList;
		
	}
	
	
	/**
	 * 根据ID和Manager_ID获取人员信息
	 * @param ID 父级ID或者ID
	 * @param isParent 是否为父级ID
	 * @return
	 */
	public List<Members> getMembersForManager(int ID, boolean isParent, int Manager_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (isParent) { //是父级ID则返回上一级的人员信息
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' like '%,'||(select ParentID from "
					+ MEMBER_GROUP_TABLE_NAME + " where ID = ? )||',%' and Manager_ID = ?", new String[]{ID + "", "" + Manager_ID});
		} else { //不是父级ID就返回下一级的ID
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' like '%," + ID + ",%' and Manager_ID = ?", new String[]{"" + Manager_ID});
		}
		return mList;
		
	}
	
	/**
	 * 选择经销商客户
	 * @param ID
	 * @param isParent
	 * @param Manager_ID
	 * @return
	 */
	public List<Members> getTopMemberForManager(int ID, boolean isParent, int Manager_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (isParent) { //是父级ID则返回上一级的人员信息
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' like '%,'||(select ParentID from "
					+ MEMBER_GROUP_TABLE_NAME + " where ID = ? )||',%' and Manager_ID = ? and Type = 2", new String[]{ID + "", "" + Manager_ID});
		} else { //不是父级ID就返回下一级的ID
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' like '%," + ID + ",%' and Manager_ID = ? and Type = 2", new String[]{"" + Manager_ID});
		}
		return mList;
	}
	
	
	public List<Members> getMembers(int ID, boolean isParent) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (isParent) { //是父级ID则返回上一级的人员信息
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' in (select ParentID from "
					+ MEMBER_GROUP_TABLE_NAME + " where ID = ?)", new String[]{ID + ""});
		} else {
			mList = st.queryForList(new RowMapper<Members> () {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
				
			}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' like '%," + ID + ",%'", new String[]{});
		}
		return mList;
		
	}
	
	/**
	 * 查询客户信息
	 * @param ID 客户ID
	 * @return
	 */
	public Members getMembers(int ID){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Members members = st.queryForObject(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from " + MEMBER_TABLE_NAME + " where ID = ?", new String[]{ID + ""});
		return members;
		
	}
	
	/**
	 * 根据ParentID查询分组
	 * @param ID 当前ID或者父级ID
	 * @param isParent 是否传入父级ID
	 * @return
	 */
	public List<MemberGroups> getGroups(int ID, boolean isParent) {
		
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<MemberGroups> mGroups = new ArrayList<MemberGroups>();
		if (isParent) {//传入父级ID返回上一级分组
			mGroups = st.queryForList(new RowMapper<MemberGroups>() {

				@Override
				public MemberGroups mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					MemberGroups groups = new MemberGroups();
					groups.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					groups.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					groups.setName(cursor.getString(cursor.getColumnIndex("Name")));
					groups.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					groups.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					groups.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return groups;
				}
			}, "select * from " + MEMBER_GROUP_TABLE_NAME + " where ParentID in (select ParentID from " 
				+ MEMBER_GROUP_TABLE_NAME + " where ID = ?)", new String[]{ID + ""});
		} else {//传入的不是父级ID，返回下一级分组
			mGroups = st.queryForList(new RowMapper<MemberGroups>() {

				@Override
				public MemberGroups mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					MemberGroups groups = new MemberGroups();
					groups.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					groups.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					groups.setName(cursor.getString(cursor.getColumnIndex("Name")));
					groups.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					groups.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					groups.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return groups;
				}
			}, "select * from " + MEMBER_GROUP_TABLE_NAME + " where ParentID = ?", new String[]{ID + ""});
		}
		return mGroups;
	}
	
	
	public List<MemberGroups> getGroupList(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<MemberGroups> mGroups = new ArrayList<MemberGroups>();
		mGroups = st.queryForList(new RowMapper<MemberGroups>() {

			@Override
			public MemberGroups mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				MemberGroups groups = new MemberGroups();
				groups.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				groups.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
				groups.setName(cursor.getString(cursor.getColumnIndex("Name")));
				groups.setPath(cursor.getString(cursor.getColumnIndex("Path")));
				groups.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				groups.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				return groups;
			}
		}, "select * from " + MEMBER_GROUP_TABLE_NAME + " where ParentID in (select ParentID from " 
			+ MEMBER_GROUP_TABLE_NAME + " where ID = ?)", new String[]{ID + ""});
		return mGroups;
	}
	
	public List<Members> getMembersList(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members> () {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
			
		}, "select * from " + MEMBER_TABLE_NAME + " where ','||Group_ID||',' in (select ParentID from "
				+ MEMBER_GROUP_TABLE_NAME + " where ID = ?)", new String[]{ID + ""});
		return mList;
	}
	
	/**
	 * 查询全部客户或者部门客户
	 * @param Department_ID
	 * @param pageIndex
	 * @return
	 */
	public List<Members> getMembersForDID(int Department_ID, int pageIndex) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"
				+ Department_ID + ",%'))" + " order by OperationTime Desc", pageSize * (pageIndex - 1), pageSize);
		return mList;
		
	}
	
	/**
	 * 我的客户
	 * @param Manager_ID
	 * @param pageIndex
	 * @return
	 */
	public List<Members> getMembersForMID(int Manager_ID, int pageIndex) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from " + MEMBER_TABLE_NAME 
				+ " where Manager_ID = " + Manager_ID + " order by OperationTime Desc", pageSize * (pageIndex - 1), pageSize);
		return mList;
		
	}
	
	/**
	 * 我的客户
	 * @param Manager_ID
	 * @param pageIndex
	 * @return
	 */
	public List<Members> getGroupMembersForMID(int Manager_ID, int pageIndex, int Department_ID) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"
				+ Department_ID + ",%')) and Manager_ID = " + Manager_ID, pageSize * (pageIndex - 1), pageSize);
		return mList;
		
	}
	
	
	/**
	 * 查找我的客户
	 * @return
	 */
	public List<Members> searchMemberForMID(String keywords, int Manager_ID, int pageIndex) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from " + MEMBER_TABLE_NAME 
				+ " where RealName like '%" + keywords + "%' and Manager_ID = " + Manager_ID, pageSize * (pageIndex - 1), pageSize);
		return mList;
		
	}
	
	/**
	 * 查找部门客户
	 * @param keywords
	 * @param Department_ID
	 * @param pageIndex
	 * @return
	 */
	public List<Members> searchMemberForDID (String keywords, int Department_ID, int pageIndex) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from Com_Member where Manager_ID in (select ID from Com_Manager where Department_ID in (select ID from Com_Department  where [Path] like '%,"
				+ Department_ID + ",%')) and RealName like '%" + keywords + "%'", pageSize * (pageIndex - 1), pageSize);
		return mList;
		
	}
	
	
	public List<Members> searchMembers(String keywords) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		mList = st.queryForList(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				return members;
			}
		}, "select * from Com_Member where RealName like '%" + keywords + "%' or Contact like '%"  + keywords + "%'", new String[]{});
		return mList;
	}
	
	/**
	 * 
	 * @param Group_ID 分组的ID
	 * @param ID 部门或者用户ID
	 * @param pageIndex 页码
	 * @param haveType 是否有权限
	 * @return
	 */
	public List<Members> getMemberGroupForID(int Group_ID, int ID, int pageIndex, boolean haveType) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (haveType) {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			+ " where ','||Group_ID||',' like '%" + Group_ID + "%'" , pageSize * (pageIndex - 1), pageSize);
		} else {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			+ " where ','||Group_ID||',' like '%" + Group_ID + "%' and Manager_ID = " + ID, pageSize * (pageIndex - 1), pageSize);
		}
		
		return mList;
		
	}
	
	/**
	 * 根据客户等级获取客户列表
	 * @param Level_ID
	 * @param ID
	 * @param pageIndex
	 * @param haveType
	 * @return
	 */
	public List<Members> getMemberLevelForID(int Level_ID, int ID, int pageIndex, boolean haveType) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (haveType) {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			 + " where Level_ID = " + Level_ID + " and Department_ID in ( select ID from Com_Department where [Path] like '%,"+ID+",%') ", pageSize * (pageIndex - 1), pageSize);
		} else {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			+ " where Level_ID = " + Level_ID + " and Manager_ID = " + ID, pageSize * (pageIndex - 1), pageSize);
		}
		return mList;
		
	}
	
	/**
	 * 根据客户类型获取客户
	 * @param Type
	 * @param ID
	 * @param pageIndex
	 * @param haveType
	 * @return
	 */
	public List<Members> getMeneberTypeForID(int Type, int ID, int pageIndex, boolean haveType) {
		int pageSize = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Members> mList = new ArrayList<Members>();
		if (haveType) {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			 + " where Type = " + Type + " and Department_ID = " + ID , pageSize * (pageIndex - 1), pageSize);
		} else {
			mList = st.queryForList(new RowMapper<Members>() {

				@Override
				public Members mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Members members = new Members();
					members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
					members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
					members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
					members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
					members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
					members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
					members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
					members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
					members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
					members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
					members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
					members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
					members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
					members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
					members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
					members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
					members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
					members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
					members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
					members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
					return members;
				}
			}, "select * from " + MEMBER_TABLE_NAME 
			+ " where Type = " + Type + " and Manager_ID = " + ID, pageSize * (pageIndex - 1), pageSize);
		}
		return mList;
	}
	
	/**
	 * 查询客户分组及总数
	 * @return
	 */
	public List<MenuMoths> getMemberGroups(int ID, boolean haveType) {
		List<MenuMoths> mList = new ArrayList<MenuMoths>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String sql="";
		if (haveType) {
			 /*" AND b.Department_ID in ( select ID from Com_Department where [Path] like '%,"+ID+",%' ) ";*/
			sql="select a.Name,COUNT(*) as co,a.ID from Com_MemberGroup a left join Com_MemberInGroup b on a.[Path] like ','||CAST(b.Group_ID as varchar)||',' where a.ParentID=0 and b.Member_ID in (select ID from Com_Member where  Department_ID in (select ID from Com_Department where [Path] like '%,"+ID+",%')) group by a.Name,a.ID";
		} else {
			/*sql=" AND b.Manager_ID ="+ID;*/
			 sql = "select a.Name,COUNT(*) as co,a.ID from Com_MemberGroup a left join Com_MemberInGroup b on a.[Path] like '%,'||CAST(b.Group_ID as varchar)||',%' where a.ParentID=0 and b.Member_ID in (select ID from Com_Member where Manager_ID="+ID+") group by a.Name,a.ID";
		}
		L.e("sql = " + sql);
		mList = st.queryForList(new RowMapper<MenuMoths>() {

			@Override
			public MenuMoths mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				MenuMoths menuMoths = new MenuMoths();
				menuMoths.setTitle(cursor.getString(cursor.getColumnIndex("Name")));
				menuMoths.setCo(cursor.getInt(cursor.getColumnIndex("co")));
				menuMoths.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				return menuMoths;
			}
		}, sql, new String[]{});
		return mList;
	}
	
	/**
	 * 获取等级分类
	 * @return
	 */
	public List<MenuMoths> getLevelCount(int ID, boolean haveType) {
		List<MenuMoths> mList = new ArrayList<MenuMoths>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String sql;
		if (haveType) {
			sql = " Department_ID in ( select ID from Com_Department where [Path] like '%,"+ID+",%')";
		} else {
			sql = " Manager_ID = " + ID;
		}
		mList = st.queryForList(new RowMapper<MenuMoths>() {

			@Override
			public MenuMoths mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				MenuMoths menuMoths = new MenuMoths();
				menuMoths.setTitle(cursor.getString(cursor.getColumnIndex("Level_Name")));
				menuMoths.setCo(cursor.getInt(cursor.getColumnIndex("co")));
				menuMoths.setID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				return menuMoths;
			}
		}, "select Level_Name,COUNT(*) as co,Level_ID  from Com_Member where " + sql + " and Level_Name!='' group by Level_Name,Level_ID", new String[]{});
		return mList;
	}
	
	/**
	 * 获取客户类型
	 * @return
	 */
	public List<MenuMoths> getMemberType(int ID, boolean haveType) {
		List<MenuMoths> mList = new ArrayList<MenuMoths>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String sql;
		if (haveType) {
			sql = " Department_ID = " + ID;
		} else {
			sql = " Manager_ID = " + ID;
		}
		mList = st.queryForList(new RowMapper<MenuMoths>() {

			@Override
			public MenuMoths mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				MenuMoths menuMoths = new MenuMoths();
				switch (cursor.getInt(cursor.getColumnIndex("Type"))) {
				case 1:
					menuMoths.setTitle("终端客户");
					break;
				case 2:
					menuMoths.setTitle("经销商客户");
					break;
					
				default:
					menuMoths.setTitle("");
					break;
				}
				menuMoths.setCo(cursor.getInt(cursor.getColumnIndex("co")));
				menuMoths.setID(cursor.getInt(cursor.getColumnIndex("Type")));
				return menuMoths;
			}
		}, "select [Type],COUNT(*) as co from Com_Member where " + sql + " group by [Type]", new String[]{});
		return mList;
	}
	
	public String getMember(String JID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String name = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String name = cursor.getString(cursor.getColumnIndex("Contact"));
				return name;
			}
			
		}, "select Contact from Com_Member where OFUserName = '" + JID + "'", new String[]{});
		return name;
	}
	
	
	public String getMemberPhoto(String JID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		String photo = st.queryForObject(new RowMapper<String>() {

			@Override
			public String mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				String photo = cursor.getString(cursor.getColumnIndex("Photo"));
				return photo;
			}
			
		}, "select Photo from Com_Member where OFUserName = '" + JID + "'", new String[]{});
		return photo;
	}
	
	public Members getMemberInfo(String OFUserName) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Members members = st.queryForObject(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				return members;
			}
		}, "select * from Com_Member where OFUserName = '" + OFUserName + "'", new String[]{});
		return members;
		
	}
	
	public Members getMemberForPhone(String phoneNum) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Members members = st.queryForObject(new RowMapper<Members>() {

			@Override
			public Members mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Members members = new Members();
				members.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				members.setRealName(cursor.getString(cursor.getColumnIndex("RealName")));
				members.setLevel_ID(cursor.getInt(cursor.getColumnIndex("Level_ID")));
				members.setLevel_Name(cursor.getString(cursor.getColumnIndex("Level_Name")));
				members.setGroup_ID(cursor.getString(cursor.getColumnIndex("Group_ID")));
				members.setGroup_Name(cursor.getString(cursor.getColumnIndex("Group_Name")));
				members.setType(cursor.getInt(cursor.getColumnIndex("Type")));
				members.setContact(cursor.getString(cursor.getColumnIndex("Contact")));
				members.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
				members.setManager_ID(cursor.getInt(cursor.getColumnIndex("Manager_ID")));
				members.setManager_Name(cursor.getString(cursor.getColumnIndex("Manager_Name")));
				members.setDepartment_ID(cursor.getInt(cursor.getColumnIndex("Department_ID")));
				members.setDepartment_Name(cursor.getString(cursor.getColumnIndex("Department_Name")));
				members.setArea_ID(cursor.getInt(cursor.getColumnIndex("Area_ID")));
				members.setArea_Name(cursor.getString(cursor.getColumnIndex("Area_Name")));
				members.setPicture(cursor.getString(cursor.getColumnIndex("Picture")));
				members.setNote(cursor.getString(cursor.getColumnIndex("Note")));
				members.setLoginName(cursor.getString(cursor.getColumnIndex("LoginName")));
				members.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
				members.setVisitTime(cursor.getString(cursor.getColumnIndex("VisitTime")));
				members.setMobile(cursor.getString(cursor.getColumnIndex("Mobile")));
				members.setPhoto(cursor.getString(cursor.getColumnIndex("Photo")));
				members.setOFUserName(cursor.getString(cursor.getColumnIndex("OFUserName")));
				return members;
			}
		}, "select * from Com_Member where Mobile = '" + phoneNum + "'", new String[]{});
		return members;
		
	}
	
	
	/**
	 * 根据ID删除客户联系人
	 * @param ID 主键
	 * @return 返回值大于0表示删除成功
	 */
	public long deleteMemberLink(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		//st.execSQL("delete from " + MEMBER_TABLE_NAME + " where ID = " + ID);
		L.e("ID = " + ID);
		return st.deleteByField(MEMBER_TABLE_NAME, "ID", ID + "");
	}
	
	/**
	 * 删除所有表数据
	 */
	public void deleteAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(MEMBER_GROUP_TABLE_NAME);
		st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(MEMBER_LINKS_TABLE_NAME);
		st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(MEMBER_TABLE_NAME);
	}
}
