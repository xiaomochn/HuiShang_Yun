package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;
import com.huishangyun.model.ProductPrice;
import com.huishangyun.model.ProductUnit;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

/**
 * 产品单位,价格管理类
 * @author forong
 *
 */
public class UnitPriceManager {
	public static UnitPriceManager unitPriceManager = null;
	private static DBManager manager = null;
	
	/**
	 * 人员分组表名
	 */
	public static final String UNIT_NAME = "Sys_ProductUnit";
	
	/**
	 * 人员表名
	 */
	public static final String PRICE_NAME = "Sys_ProductPrice";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private UnitPriceManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static UnitPriceManager getInstance(Context context) {

		if (unitPriceManager == null) {
			unitPriceManager = new UnitPriceManager(context);
		}
		return unitPriceManager;
	}
	
	/**
	 * 保存单位
	 * @param productUnit
	 * @return
	 */
	public long saveUnit(ProductUnit productUnit) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", productUnit.getID());
		contentValues.put("Unit_ID", productUnit.getUnit_ID());
		contentValues.put("Unit_Name", productUnit.getUnit_Name());
		contentValues.put("Quantity", productUnit.getQuantity());
		contentValues.put("Product_ID", productUnit.getProduct_ID());
		if (productUnit.getStatus()) {
			if (st.isExistsByField(UNIT_NAME, "ID", productUnit.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(UNIT_NAME, contentValues, "ID = ?", new String[]{productUnit.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(UNIT_NAME, contentValues);
			}
		} else {
			return st.deleteByField(UNIT_NAME, "ID", productUnit.getID() + "");
		}
	}
	
	/**
	 * 保存价格
	 * @param productUnit
	 * @return
	 */
	public long savePrice(ProductPrice productUnit) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", productUnit.getID());
		contentValues.put("Product_ID", productUnit.getProduct_ID());
		contentValues.put("Unit_ID", productUnit.getUnit_ID());
		contentValues.put("Unit_Name", productUnit.getUnit_Name());
		contentValues.put("Price", productUnit.getPrice());
		contentValues.put("Group_ID", productUnit.getGroup_ID());
		contentValues.put("Level_ID", productUnit.getLevel_ID());
		contentValues.put("Member_ID", productUnit.getMember_ID());
		if (productUnit.getStatus()) {
			if (st.isExistsByField(PRICE_NAME, "ID", productUnit.getID() + "")) {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.update(PRICE_NAME, contentValues, "ID = ?", new String[]{productUnit.getID() + ""});
			} else {
				st = SQLiteTemplate.getInstance(manager, false);
				return st.insert(PRICE_NAME, contentValues);
			}
		} else {
			return st.deleteByField(PRICE_NAME, "ID", productUnit.getID() + "");
		}
	}
	
	/**
	 * 根据产品ID获取单位列表
	 * @param Product_ID
	 * @return
	 */
	public List<ProductUnit> getProductUnits(int Product_ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ProductUnit> units = new ArrayList<ProductUnit>();
		units = st.queryForList(new SQLiteTemplate.RowMapper<ProductUnit>() {

			@Override
			public ProductUnit mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				ProductUnit productUnit = new ProductUnit();
				productUnit.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				productUnit.setProduct_ID(cursor.getInt(cursor.getColumnIndex("Product_ID")));
				productUnit.setUnit_ID(cursor.getInt(cursor.getColumnIndex("Unit_ID")));
				productUnit.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				productUnit.setQuantity(cursor.getFloat(cursor.getColumnIndex("Quantity")));
				return productUnit;
			}
			
		}, "select * from " + UNIT_NAME + " where Product_ID = " + Product_ID, new String[]{});
		
		return units;
		
	}
	
	/**
	 * 获取价格
	 * @param Product_ID 产品ID
	 * @param Unit_ID 单位ID
	 * @param Level_ID 等级ID
	 * @param Member_ID 客户ID
	 * @param GroupID 客户当前分组ID集合
	 * @return
	 */
	public float getPrice(int Product_ID, int Unit_ID, int Level_ID, int Member_ID, String GroupID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		float price = 0;
		price = st.queryForObject(new SQLiteTemplate.RowMapper<Float>() {

			@Override
			public Float mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				return cursor.getFloat(cursor.getColumnIndex("Price"));
			}
		}, "select MIN(Price) from Sys_ProductPrice where  Product_ID= "+ Product_ID +
			" and Unit_ID=" + Unit_ID + " and (Level_ID=0 Or Level_ID=" + Level_ID + ")" +
				"And (Group_ID=0 Or Group_ID In (" + GroupID + ")) And (Member_ID=0 Or Member_ID="+Member_ID+")", new String[]{});
		return price;
	}
	
	
	
	
}
