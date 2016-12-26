package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;

/**
 * 购物车数据库操作帮助类
 * @author Pan
 *
 */
public class CartManager {	
	public static CartManager cartManager = null;
	private static DBManager manager = null;
	
	private static final String CART_TABLENAME = "Crm_Cart";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private CartManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static CartManager getInstance(Context context) {

		if (cartManager == null) {
			cartManager = new CartManager(context);
		}

		return cartManager;
	}
	
	/**
	 * 保存购物车数据
	 * @param cartModel
	 * @return
	 */
	public long saveCarts(CartModel cartModel) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", cartModel.getID());
		contentValues.put("Product_ID", cartModel.getProduct_ID());
		contentValues.put("Product_Name", cartModel.getProduct_Name());
		contentValues.put("Quantity", cartModel.getQuantity());
		contentValues.put("Price", cartModel.getPrice());
		contentValues.put("Unit_Name", cartModel.getUnit_Name());
		contentValues.put("Unit_ID", cartModel.getUnit_ID());
		contentValues.put("AddDateTime", cartModel.getAddDateTime());
		contentValues.put("SmallImg", cartModel.getSmallImg());
		
		if (st.isExistsByField(CART_TABLENAME, "Product_ID", cartModel.getProduct_ID() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(CART_TABLENAME, contentValues, "Product_ID = ?", new String []{cartModel.getProduct_ID() + ""});
		} else {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(CART_TABLENAME, contentValues);
		}
		
	}
	
	/**
	 * 返回购物车数据
	 * @return
	 */
	public List<CartModel> getCartModels() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<CartModel> cartModels = new ArrayList<CartModel>();
		cartModels = st.queryForList(new SQLiteTemplate.RowMapper<CartModel>() {

			@Override
			public CartModel mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				CartModel cartModel = new CartModel();
				cartModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				cartModel.setProduct_ID(cursor.getInt(cursor.getColumnIndex("Product_ID")));
				cartModel.setProduct_Name(cursor.getString(cursor.getColumnIndex("Product_Name")));
				cartModel.setQuantity(cursor.getFloat(cursor.getColumnIndex("Quantity")));
				cartModel.setPrice(cursor.getFloat(cursor.getColumnIndex("Price")));
				cartModel.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				cartModel.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				cartModel.setAddDateTime(cursor.getString(cursor.getColumnIndex("AddDateTime")));
				cartModel.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				return cartModel;
			}
		}, "select * from " + CART_TABLENAME, new String[]{});
		return cartModels;
	}
	
	
	/**
	 * 根据ID删除数据
	 * @param ID
	 * @return
	 */
	public long deleteCart(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByField(CART_TABLENAME, "Product_ID", ID + "");
	}
	
	/**
	 * 获取数据库数据条数
	 * @return
	 */
//	public int getSize() {
//		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
//		return st.getCount("select * from " + CART_TABLENAME, new String[]{});
//		
//	}
	
	/**
	 * 更具ID获取购物车数据
	 * @param Product_ID
	 * @return
	 */
	public CartModel getModelForID(int Product_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		CartModel cartModel = new CartModel();
		cartModel = st.queryForObject(new SQLiteTemplate.RowMapper<CartModel>() {

			@Override
			public CartModel mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				CartModel cartModel = new CartModel();
				cartModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				cartModel.setProduct_ID(cursor.getInt(cursor.getColumnIndex("Product_ID")));
				cartModel.setProduct_Name(cursor.getString(cursor.getColumnIndex("Product_Name")));
				cartModel.setQuantity(cursor.getFloat(cursor.getColumnIndex("Quantity")));
				cartModel.setPrice(cursor.getFloat(cursor.getColumnIndex("Price")));
				cartModel.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				cartModel.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				cartModel.setAddDateTime(cursor.getString(cursor.getColumnIndex("AddDateTime")));
				cartModel.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				return cartModel;
			}
		}, "select * from " + CART_TABLENAME 
				+ " where Product_ID = ?", new String[]{Product_ID + ""});
		return cartModel;
	}
	
	/**
	 * 删除所有数据
	 * @return 大于0表示删除成功
	 */
	public long deleteAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteAll(CART_TABLENAME);
	}
}
