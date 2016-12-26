package com.huishangyun.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;
import com.huishangyun.model.ProductFavs;

/**
 * 产品收藏夹数据库操作帮助类
 * @author Pan
 *
 */
public class ProductFavManager {
	public static ProductFavManager productFavManager = null;
	private static DBManager manager = null;
	
	private static final String PRO_TABLENAME = "Crm_ProductFav";
	
	/**
	 * 私有化构造方法
	 * @param context
	 */
	private ProductFavManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		manager = DBManager.getInstance(context);
	}
	
	/**
	 * 获取唯一实例
	 * @param context
	 * @return
	 */
	public static ProductFavManager getInstance(Context context) {

		if (productFavManager == null) {
			productFavManager = new ProductFavManager(context);
		}

		return productFavManager;
	}
	
	/**
	 * 保存收藏夹
	 * @param productFavs
	 * @return
	 */
	public long saveProducts(ProductFavs productFavs) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", productFavs.getID());
		contentValues.put("Product_ID", productFavs.getProduct_ID());
		contentValues.put("Product_Name", productFavs.getProduct_Name());
		contentValues.put("SmallImg", productFavs.getSmallImg());
		contentValues.put("Price", productFavs.getPrice());
		contentValues.put("Unit_Name", productFavs.getUnit_Name());
		if (st.isExistsByField(PRO_TABLENAME, "ID", productFavs.getID() + "")) {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.update(PRO_TABLENAME, contentValues, "ID = ?", new String []{productFavs.getID() + ""});
		} else {
			st = SQLiteTemplate.getInstance(manager, false);
			return st.insert(PRO_TABLENAME, contentValues);
		}
	}
	
	/**
	 * 获取收藏夹数据
	 * @return
	 * private String SmallImg;
		private float Price;
		private String Unit_Name;
	 */
	public List<ProductFavs> getProductFavs() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ProductFavs> productFavs = new ArrayList<ProductFavs>();
		productFavs = st.queryForList(new SQLiteTemplate.RowMapper<ProductFavs>() {

			@Override
			public ProductFavs mapRow(Cursor cursor, int index) {
				// TODO Auto-generated method stub
				ProductFavs productFavs = new ProductFavs();
				productFavs.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				productFavs.setProduct_ID(cursor.getInt(cursor.getColumnIndex("Product_ID")));
				productFavs.setProduct_Name(cursor.getString(cursor.getColumnIndex("Product_Name")));
				productFavs.setSmallImg(cursor.getString(cursor.getColumnIndex("SmallImg")));
				productFavs.setPrice(cursor.getInt(cursor.getColumnIndex("Price")));
				productFavs.setUnit_Name(cursor.getString(cursor.getColumnIndex("Unit_Name")));
				return productFavs;
			}
		}, "select * from " + PRO_TABLENAME, new String[]{});
		return productFavs;
	}
	
	/**
	 * 根据ID删除收藏夹数据
	 * @param ID 
	 * @return
	 */
	public long deleteProductFav(int ID) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByField(PRO_TABLENAME, "Product_ID", ID + "");
	}
}
