package com.huishangyun.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.huishangyun.Util.L;

/**
 * 数据库帮助类
 * @author Pan
 * @see 
 * :基础版本 版本3<br>
 * 版本4:im_msg_his表添加字段[Msg_Category],表示消息类别<br>
 * 版本5:im_notice表添加字段[Msg_Category],表示消息类别<br>
 * 		
 */
public class DataBaseHelper extends SDCardSQLiteOpenHelper {
	
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	/**
	 * 版本3:基础版本 <br>
     * 版本4:im_msg_his表添加字段[Msg_Category],表示消息类别<br>
     * 版本5:im_notice表添加字段[Msg_Category],表示消息类别<br>
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		L.d("创建数据库");
		//历史记录
		db.execSQL("CREATE TABLE [im_msg_his] ([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
				"[content] NVARCHAR, [msg_from] NVARCHAR, [msg_to] NVARCHAR, [msg_time] TEXT, [msg_type] INTEGER, " +
				"[is_group] INTEGER, [group_username] TEXT,[Msg_Category] VARCHAR(50))");
		
		//通知
		db.execSQL("CREATE TABLE [im_notice]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [type] INTEGER, " +
				"[title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, " +
				"[notice_time] TEXT, [status] INTEGER, [Msg_Category] VARCHAR(50))");

		// 省市县
		db.execSQL("CREATE TABLE [Sys_Area] ([ID] INTEGER NOT NULL PRIMARY KEY, [Name] VARCHAR(50), [ParentID] INTEGER, [Path] VARCHAR(200))");

		// 人员分组
		db.execSQL("CREATE TABLE [Com_Department] ([ID] INTEGER NOT NULL PRIMARY KEY, [ParentID] INTEGER, [Name] VARCHAR(100),"
				+ " [Path] VARCHAR(200), [Sequence] smallint, [OperationTime] VARCHAR(25))");

		// 人员
		db.execSQL("CREATE TABLE [Com_Manager] ([ID] INTEGER NOT NULL PRIMARY KEY, [LoginName] VARCHAR(50), [OFUserName] VARCHAR(50), [RealName] VARCHAR(50), [Sex] CHAR(1),"
				+ " [Email] VARCHAR(100), [Mobile] VARCHAR(11), [Weixin] VARCHAR(50), [Role_ID] VARCHAR(50), [Role_Name] VARCHAR(200), [Note] VARCHAR(200),"
				+ " [Photo] VARCHAT(200), [Sign] VARCHAT(200), [Department_ID] VARCHAR(100), [Department_Name] VARCHAR(50), [Type] INTEGER)");

		// 客户分组
		db.execSQL("CREATE TABLE [Com_MemberGroup] ([ID] INTEGER NOT NULL PRIMARY KEY, [ParentID] INTEGER, [Name] VARCHAR(50), [Path] VARCHAR(200),"
				+ " [Sequence] smallint, [OperationTime] VARCHAR(50))");

		// 客户
		db.execSQL("CREATE TABLE [Com_Member] ([ID] INTEGER NOT NULL PRIMARY KEY, [RealName] VARCHAR(50),[Level_ID] INTEGER,"
				+ " [Level_Name] VARCHAR(50), [Group_ID] VARCHAR(100), [Group_Name] VARCHAR(200),"
				+ " [Type] INTEGER, [Contact] VARCHAR(50), [Address] VARCHAR(100), [OFUserName] VARCHAR(50),"
				+ " [Manager_ID] INTEGER, [Manager_Name] VARCHAR(50), [Department_ID] INTEGER, [Department_Name] VARCHAR(50),"
				+ " [Area_ID] INTEGER, [Area_Name] VARCHAR(50), [Photo] VARCHAT(200), "
				+ " [Picture] VARCHAR(200), [Mobile] VARCHAR(11),"
				+ " [Note] VARCHAR(200),[LoginName] VARCHAR(50),"
				+ " [OperationTime] VARCHAR(50),[VisitTime] VARCHAR(50))");

		// 客户联系人
		db.execSQL("CREATE TABLE [Com_MemberLink] ([ID] INTEGER NOT NULL PRIMARY KEY, [Member_ID] INTEGER, [Name] VARCHAR(20), [Mobile] VARCHAR(11),"
				+ " [Tel] VARCHAR(20), [Job] VARCHAR(50), [Address] VARCHAR(100), [ZipCode] VARCHAR(6), [Email] VARCHAR(50), [OperationTime] VHARCHAR(50))");

		// 产品分类
		db.execSQL("CREATE TABLE [Com_Class] ([ID] INTEGER NOT NULL PRIMARY KEY, [ParentID] INTEGER, [Name] VARCHAR(100), [Path] VARCHAR(200),"
				+ " [Note] VARCHAR(200), [Sequence] INTEGER, [OperationTime] VARCHAR(50))");

		// 产品
		db.execSQL("CREATE TABLE [Com_Product] ([ID] INTEGER NOT NULL PRIMARY KEY, [Class_ID] INTEGER, [Name] VARCHAR(200), [No] VARCHAR(50),"
				+ " [Unit_ID] INTEGER,[Unit_Name] INTEGER, [Info] VARCHAR(250), [Sequence] INTEGER"
				+ " [OperationTime] VHARCHAR(50), [SalePrice] FLOAT, [SmallImg] VARCHAR(200), [MiddleImg] VARCHAR(200), [BigImg] VARCHAR(200))");

		// 数据字典
		db.execSQL("CREATE TABLE [Sys_Enum] ([MyPK] VARCHAR(50) NOT NULL PRIMARY KEY, [Lab] VARCHAR(50), [EnumKey] VARCHAR(50), [IntKey]VARCHAR(50),[OperationTime] VHARCHAR(50))");

		// 购物车
		db.execSQL("CREATE TABLE [Crm_Cart] ([ID] INTEGER NOT NULL PRIMARY KEY, [Product_ID] INTEGER, [Product_Name] VARCHAR(50), [Quantity] FLOAT,"
				+ " [Price] FLOAT, [Unit_Name] VARCHAR(10), [Unit_ID] INTEGER, [AddDateTime] VARCHAR(25), [SmallImg] VARCHAR(200))");

		// 产品收藏
		db.execSQL("CREATE TABLE [Crm_ProductFav] ([ID] INTEGER NOT NULL PRIMARY KEY, [Product_ID] INTEGER, [SmallImg] VARCHAR(200), [Product_Name] VARCHAR(50)," +
				"[Price] FLOAT, [Unit_Name] VARCHAR(10))");
		
		//最近文件列表
		db.execSQL("CREATE TABLE [Sys_FileList] ([ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [File_Name] VARCHAR(100), [File_Path] VARCHAR(200), [Time] BIGINT)");
		
		//更新时间
		db.execSQL("CREATE TABLE [Sys_OperationTime] ([ID] INTEGER, [Table_Name] VHARCHAR(50)," +
				" [OperationTime]  VHARCHAR(50) )");
		
		db.execSQL("CREATE TABLE [Com_MemberInGroup] ([Member_ID] INTEGER, [Group_ID] VHARCHAR(50), [OperationTime] VHARCHAR(50))");
		
		db.execSQL("CREATE TABLE [Com_Attendance]([ID] INTEGER, [Name] VARCHAR(50), [InTime] VARCHAR(50), " +
				"[OutTime] VARCHAR(50),[Company_ID] VARCHAR(50),[OperationTime] VARCHAR(50))");
		
		//拜访新增数据保存
		db.execSQL("CREATE TABLE [Com_Visit]([ID] INTEGER,[isSubmit] INTEGER,[ManagerID] INTEGER,[CustormerName] VARCHAR(50),"
				+ " [CustormerID] INTEGER,[isReport] INTEGER,[ReportDate] VARCHAR(50),[PhotoUrl] VARCHAR(150),"
				+ "[UpUrl] VARCHAR(50),[Note] VARCHAR(200),SignLat VARCHAR(50),SignLng VARCHAR(50),SignLoc VARCHAR(50),SignTime VARCHAR(50),Tags VARCHAR(50),TagID VARCHAR(50))");

		//陈列新增数据保存
		db.execSQL("CREATE TABLE [Com_Display]([ID] INTEGER,[isSubmit] INTEGER,[ManagerID] INTEGER,[CustormerName] VARCHAR(50),"
				+ " [CustormerID] INTEGER,[PhotoUrl] VARCHAR(150),[UpUrl] VARCHAR(50),[Note] VARCHAR(200))");
		
		//竞品新增数据保存
		db.execSQL("CREATE TABLE [Com_Competing]([ID] INTEGER,[isSubmit] INTEGER,[ManagerID] INTEGER,[CustormerName] VARCHAR(50),"
				+ " [CustormerID] INTEGER,[Brand] VARCHAR(50),[MyBrand] VARCHAR(50),[PhotoUrl] VARCHAR(150),[UpUrl] VARCHAR(150),[Note] VARCHAR(200))");
		
		//轻应用
		db.execSQL("CREATE TABLE [Sys_Server]([ID] INTEGER, [Name] [varchar](50)," +
				" [LoginName] [VARCHAR](50), [Password] [VARCHAR](50), [Photo] [VARCHAR](50), [Note] [VARCHAR](200) ," +
				" [Sequence] [INTEGER], [Company_ID] [INTEGER], [Status] [bit], [AddDateTime] [VARCHAR](50) )");
		
		//轻应用菜单
		db.execSQL("CREATE TABLE [Sys_ServerMenu]([ID] INTEGER, [Server_ID] [INTEGER]," +
				" [Name] [VARCHAR](20), [Url] [VARCHAR](50), [ParentID] [int], [Sequence] [INTEGER],[IsLogin] [bit], [Status] [bit]," +
				" [AddDateTime] [VARCHAR](50), [OperationTime] [VARCHAR](50))");
		
		//IM群
		db.execSQL("CREATE TABLE [Com_Group] ([ID] INTEGER, [Name] VARCHAR(50), [Photo] VARCHAR(50), [Note] VARCHAR(200), " +
				" [Type] INTEGER, [Approval] INTEGER, [Owner] VARCHAR(50), [OpenID] VARCHAR(50))");
		//群成员表
		db.execSQL("CREATE TABLE [Com_GroupUser] ([Group_ID] INTEGER, [User_ID] INTEGER)");
		
		//产品单位表
		db.execSQL("CREATE TABLE [Sys_ProductUnit]([ID] INTEGER, [Product_ID] INTEGER, [Unit_ID] INTEGER, [Unit_Name] VARCHAR[50], [Quantity] FLOAT)");
		
		//产品价格表
		db.execSQL("CREATE TABLE [Sys_ProductPrice]([ID] INTEGER, [Product_ID] INTEGER, [Unit_ID] INTEGER, [Unit_Name] VARCHAR[50], [Price] FLOAT, [Group_ID] INTEGER," +
				" [Level_ID] INTEGER, [Member_ID] INTEGER)");
	}
	@Override
	public void onDrop(SQLiteDatabase db) {

		try {
			//历史记录
			db.execSQL("DROP TABLE [im_msg_his]");

			//通知
			db.execSQL("DROP TABLE [im_notice]");

			// 省市县
			db.execSQL("DROP TABLE [Sys_Area]");

			// 人员分组
			db.execSQL("DROP TABLE [Com_Department]");

			// 人员
			db.execSQL("DROP TABLE [Com_Manager]");

			// 客户分组
			db.execSQL("DROP TABLE [Com_MemberGroup]");

			// 客户
			db.execSQL("DROP TABLE [Com_Member]");

			// 客户联系人
			db.execSQL("DROP TABLE [Com_MemberLink]");

			// 产品分类
			db.execSQL("DROP TABLE [Com_Class]");

			// 产品
			db.execSQL("DROP TABLE [Com_Product]");
			// 数据字典
			db.execSQL("DROP TABLE [Sys_Enum]");

			// 购物车
			db.execSQL("DROP TABLE [Crm_Cart]");
			// 产品收藏
			db.execSQL("DROP TABLE [Crm_ProductFav]");

			//最近文件列表
			db.execSQL("DROP TABLE [Sys_FileList]");

			//更新时间
			db.execSQL("DROP TABLE [Sys_OperationTime]");

			db.execSQL("DROP TABLE [Com_MemberInGroup]");

			db.execSQL("DROP TABLE [Com_Attendance]");

			//拜访新增数据保存
			db.execSQL("DROP TABLE [Com_Visit]");

			//陈列新增数据保存
			db.execSQL("DROP TABLE [Com_Display]");

			//竞品新增数据保存
			db.execSQL("DROP TABLE [Com_Competing]");

			//轻应用
			db.execSQL("DROP TABLE [Sys_Server]");

			//轻应用菜单
			db.execSQL("DROP TABLE [Sys_ServerMenu]");

			//IM群
			db.execSQL("DROP TABLE [Com_Group]");
			//群成员表
			db.execSQL("DROP TABLE [Com_GroupUser]");

			//产品单位表
			db.execSQL("DROP TABLE [Sys_ProductUnit]");

			//产品价格表
			db.execSQL("DROP TABLE [Sys_ProductPrice]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 数据库升级操作
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//进行迭代升级操作,后续升级请往后添加,版本号更改请到DBManager.java中更改
		for (int i = oldVersion; i <= newVersion; i++) {
			upgradeDataBase(db, i);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
	
	/**
	 * 迭代升级方法,升级请继续往后添加
	 * @param db
	 * @param oldVersion
	 */
	private void upgradeDataBase(SQLiteDatabase db, int oldVersion) {
		switch (oldVersion) {
			case 1://修改数据库字段或创建表语句
			//db.execSQL("ALTER TABLE im_msg_his RENAME TO _temp_im_msg_his");
			break;
			default:
				break;
		}
	}
}
