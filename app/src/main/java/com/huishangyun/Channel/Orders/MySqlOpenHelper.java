package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.huishangyun.Util.L;

public class MySqlOpenHelper {
	/**
	 * 数据库操作
	 */
	private MySql mySql;	
	private static SQLiteDatabase db;
	private static Context context;
	
	private MySqlOpenHelper(Context context){

		mySql  = new MySql(context, "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
		db = mySql.getWritableDatabase();//根据数据库辅助类得到数据库。
	}
	
	/**
	 * 添加进数据库的方法
	 * @param bitmap
	 * @param names
	 * @param prices
	 * @param nubs
	 */
	public static void dbAdd(Bitmap bitmap, int id, String names, Double prices, String unit, int nubs, int class_ID) {  		
		ContentValues values = new ContentValues();
		
		values.put("id", id);
		values.put("img", Common.saveIcon(bitmap));
		values.put("name", names);
		values.put("price", prices);
		values.put("unit", unit);
		values.put("nub", nubs);//个数添加的时候默认是1，后面要能添加减少
		values.put("class_ID", class_ID);
		
		long rowid = db.insert("shopping_cart", null, values);
		if(rowid == -1){
			Toast.makeText(context, "未添加成功！",
					Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(context, "已成功添加到购物车！",
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 查询到加入数据库的商品id，并放入一个集合
	 * 后面加上这些id的商品购物都为选中的颜色。
	 */
	public List<Integer> dbFindID() {
		List<Integer> list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的	
		
		Cursor cursor = db.query("shopping_cart", null, null, null, null, null,
				"_id ASC");
		cursor.moveToFirst();				
		while (!cursor.isAfterLast()) {	
			
			list_id.add(cursor.getInt(1));
			cursor.moveToNext();			
		}
		L.e("（先查一遍数据库商品的id）list_id:" + list_id.toString());
		
		return list_id;
		
//		for (int i = 0; i < data.size(); i++) {
//			L.e("id:" + data.get(i).get("map_id").toString());
//			if (list_id.contains(Integer.valueOf(data.get(i).get("map_id").toString()))) {				
//				
//				ImageView oView = (ImageView)listView.getChildAt(i).findViewById(R.id.imgcar);								
//				oView.setImageResource(R.drawable.shopping_car_blue);
//				
//				Toast.makeText(getActivity(), i +"项已被选中", Toast.LENGTH_LONG).show();
//			}else {		
//			}
//		}
	}
	
	/**
	 * 从数据库删除的方法
	 */
	protected void dbDel(int id_goods) {
		// TODO Auto-generated method stub
		String where = "id = " + id_goods;
		L.e("where:" + where);
		int i = db.delete("shopping_cart", where, null);
		if(i > 0){
			L.i("数据删除成功！");
		}else{
			L.i("数据未删除！");
		}		
	}
	
	
}
