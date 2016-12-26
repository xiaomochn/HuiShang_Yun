package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.Util.L;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;
import com.huishangyun.model.ClassModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Order_GoodsList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

public class ProductManager {
	public static ProductManager privateManager = null;
	private static DBManager manager = null;
	
	public static final String PRODUCT_TABLENAME = "Com_Product";
	
	public static final String CLASS_TABLENAME = "Com_Class";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private ProductManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static ProductManager getInstance(Context context) {

		if (privateManager == null) {
			privateManager = new ProductManager(context);
		}

		return privateManager;
	}
	
	/**
	 * 保存产品信息
	 * @param GoodsList
	 * @return
	 */
	public long saveProductInfo(Order_GoodsList GoodsList) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", GoodsList.getID());
		contentValues.put("Class_ID", GoodsList.getClass_ID());
		contentValues.put("Name", GoodsList.getName());
		contentValues.put("Unit_Name", GoodsList.getUnit_Name());
		contentValues.put("Info", GoodsList.getInfo());
		contentValues.put("SalePrice", GoodsList.getSalePrice());
		contentValues.put("SmallImg", GoodsList.getSmallImg());
		contentValues.put("BigImg", GoodsList.getBigImg());
		contentValues.put("Unit_ID", GoodsList.getUnit_ID());
		if (GoodsList.getStatus()) {
			if (st.isExistsByField(PRODUCT_TABLENAME, "ID", GoodsList.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				L.i("更新");
				return st.update(PRODUCT_TABLENAME, contentValues, "ID = ?", new String []{GoodsList.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				L.i("插入");
				return st.insert(PRODUCT_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(PRODUCT_TABLENAME, "ID", GoodsList.getID() + "");
		}
		
	}
	
	/**
	 * 保存产品分组
	 * @param classModel
	 * @return
	 */
	public long saveClass(ClassModel classModel) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", classModel.getID());
		contentValues.put("ParentID", classModel.getParentID());
		contentValues.put("Name", classModel.getName());
		contentValues.put("Path", classModel.getPath());
		contentValues.put("Note", classModel.getNote());
		contentValues.put("Sequence", classModel.getSequence());
		contentValues.put("OperationTime", classModel.getOperationTime());
		if (classModel.getStatus()) {
			if (st.isExistsByField(CLASS_TABLENAME, "ID", classModel.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(CLASS_TABLENAME, contentValues, "ID = ?", new String []{classModel.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(CLASS_TABLENAME, contentValues);
			}
		} else {
			return st.deleteByField(CLASS_TABLENAME, "ID", classModel.getID() + "");
		}
		
	}
	
	/**
	 * 根据Class_ID查找出所有产品
	 * @param Class_ID
	 * @return
	 */
	public List<Order_GoodsList> getProducts(int Class_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Order_GoodsList> mList = new ArrayList<Order_GoodsList>();
			mList = st.queryForList(new RowMapper<Order_GoodsList>() {

				@Override
				public Order_GoodsList mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					Order_GoodsList order_GoodsList = new Order_GoodsList();
					order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
					order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
					order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
					order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
					order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
					order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
					order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
					order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
					order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
					return order_GoodsList;
				}
			}, "select * from " + PRODUCT_TABLENAME 
					+ " where Class_ID = ?", new String[]{"" + Class_ID});
			L.v("size = " + mList.size());
			return mList;
		
	}
	
	/**
	 * 返回产品列表
	 * @param ID 传入的ID或者父级ID
	 * @param isParentID 传入的是否为父级ID
	 * @return
	 */
	public List<ClassModel> getClassModels(int ID, boolean isParentID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ClassModel> mList = new ArrayList<ClassModel>();
		if (!isParentID) {
			mList = st.queryForList(new RowMapper<ClassModel>() {

				@Override
				public ClassModel mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					ClassModel classModel = new ClassModel();
					classModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					classModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					classModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
					classModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					classModel.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					classModel.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					classModel.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return classModel;
				}
			}, "select * from " + CLASS_TABLENAME + " where ParentID = ?", new String[]{ID + ""});
		} else {
			mList = st.queryForList(new RowMapper<ClassModel>() {

				@Override
				public ClassModel mapRow(Cursor cursor, int index) {
					// TODO Auto-generated method stub
					ClassModel classModel = new ClassModel();
					classModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
					classModel.setParentID(cursor.getInt(cursor.getColumnIndex("ParentID")));
					classModel.setName(cursor.getString(cursor.getColumnIndex("Name")));
					classModel.setPath(cursor.getString(cursor.getColumnIndex("Path")));
					classModel.setNote(cursor.getString(cursor.getColumnIndex("Note")));
					classModel.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
					classModel.setOperationTime(cursor.getString(cursor.getColumnIndex("OperationTime")));
					return classModel;
				}
			}, "select * from " + CLASS_TABLENAME + " where ParentID = (select ParentID from " 
							+ CLASS_TABLENAME + " where ID = ?)", new String[]{ID + ""});
		}
		
		return mList;
		
	}
	
	/**
	 * 根据产品分类分页查询
	 * @param Class_ID 产品分类ID
	 * @param pageSize 页码
	 * @return
	 */
	public List<Order_GoodsList> getGoodsFromClass(int Class_ID, int pageSize) {
		int size = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Order_GoodsList> mList = new ArrayList<Order_GoodsList>();
		mList = st.queryForList(new RowMapper<Order_GoodsList>() {

			@Override
			public Order_GoodsList mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Order_GoodsList order_GoodsList = new Order_GoodsList();
				order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
				order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
				order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
				order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
				order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
				order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
				return order_GoodsList;
			}
		}, "select * from " + PRODUCT_TABLENAME + " where Class_ID = " + Class_ID, (pageSize - 1) * size, size);
		return mList;
	}
	
	/**
	 * 返回所有产品信息
	 * @return
	 */
	public List<Order_GoodsList> getGoodsLists() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Order_GoodsList> mList = new ArrayList<Order_GoodsList>();
		mList = st.queryForList(new RowMapper<Order_GoodsList> () {

			@Override
			public Order_GoodsList mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Order_GoodsList order_GoodsList = new Order_GoodsList();
				order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
				order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
				order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
				order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
				order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
				order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
				return order_GoodsList;
			}
			
		}, "select * from " + PRODUCT_TABLENAME, new String[]{});
		return mList;
	}
	
	/**
	 * 根据ID查询一条数据
	 * @param ID
	 * @return
	 */
	public Order_GoodsList getGoodsList(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		Order_GoodsList order_GoodsList = new Order_GoodsList();
		order_GoodsList= st.queryForObject(new RowMapper<Order_GoodsList>() {
			@Override
			public Order_GoodsList mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Order_GoodsList order_GoodsList = new Order_GoodsList();
				order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
				order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
				order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
				order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
				order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
				order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
				return order_GoodsList;
			}
		}, "select * from " + PRODUCT_TABLENAME + " where ID = ?", new String[]{ID + ""});
		return order_GoodsList;
	}
	
	
	/**
	 * 分页查询
	 * @return
	 */
	public List<Order_GoodsList> getGoodsLists(int pageSize) {
		int size = 10;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Order_GoodsList> mList = st.queryForList(new RowMapper<Order_GoodsList>() {
			@Override
			public Order_GoodsList mapRow(Cursor cursor, int index) {
				Order_GoodsList order_GoodsList = new Order_GoodsList();
				order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
				order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
				order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
				order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
				order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
				order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
				return order_GoodsList;
			}
		}, "select * from Com_Product", (pageSize - 1) * size, size);
		return mList;
	}
	
	/**
	 * 根据关键字查询产品
	 * @param keyWords
	 * @return
	 */
	public List<Order_GoodsList> searchGoodsLists(String keyWords) {
		List<Order_GoodsList> mList = new ArrayList<Order_GoodsList>();
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		mList = st.queryForList(new RowMapper<Order_GoodsList>() {

			@Override
			public Order_GoodsList mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				Order_GoodsList order_GoodsList = new Order_GoodsList();
				order_GoodsList.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				order_GoodsList.setClass_ID(cursor.getInt(cursor.getColumnIndex("Class_ID")));
				order_GoodsList.setName(cursor.getString(cursor.getColumnIndex("Name")));
				order_GoodsList.setNo(cursor.getString(cursor.getColumnIndex("No")));
				order_GoodsList.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				order_GoodsList.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				order_GoodsList.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
				order_GoodsList.setSequence(cursor.getInt(cursor.getColumnIndex("Sequence")));
				order_GoodsList.setSalePrice(cursor.getFloat(cursor.getColumnIndex("SalePrice")));
				order_GoodsList.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				order_GoodsList.setBigImg(cursor.getString(cursor.getColumnIndex("BigImg")));
				return order_GoodsList;
			}
		}, "select * from Com_Product where Name like '%" + keyWords + "%'", new String[]{});
		return mList;
	}
	
	
	public void deleteAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteAll(CLASS_TABLENAME);
		st.deleteAll(PRODUCT_TABLENAME);
	}
}
