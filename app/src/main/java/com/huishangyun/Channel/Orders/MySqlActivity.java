package com.huishangyun.Channel.Orders;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

public class MySqlActivity extends Activity {
	/**
	 * 获取购物车数据库的数据
	 */
	private MySql mySql;
	private SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mySql = new MySql(this, "mement.db", null, 1);
		db = mySql.getWritableDatabase();
	}
	
	/**
	 * 添加进数据库的方法
	 * @param bitmap
	 * @param names
	 * @param prices
	 * @param nubs
	 */
	public void dbAdd(Bitmap bitmap, String names, Double prices, int nubs) {  		
		ContentValues values = new ContentValues();
		
		//values.put("img", saveIcon(bitmap));
		values.put("name", names);
		values.put("price", prices);
		values.put("nub", nubs);//个数添加的时候默认是1，后面要能添加减少
		
		long rowid = db.insert("shopping_cart", null, values);
		if(rowid == -1){
			Toast.makeText(MySqlActivity.this, "未添加成功！",
					Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(MySqlActivity.this, "已成功添加到购物车！",
					Toast.LENGTH_LONG).show();
		}
	}
	
}
