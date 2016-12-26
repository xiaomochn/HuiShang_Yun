package com.huishangyun.Channel.Orders;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.widget.TextView;

import com.huishangyun.App.MyApplication;
import com.huishangyun.manager.CartManager;
import com.huishangyun.manager.ProductFavManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.ProductFavs;

/**
 * 添加到购物车的通用方法
 * @author Administrator
 *
 */
public class Common {
	public static int hours = 17;
	public static int nub;
	
	public static String getPath(){
		String imgPath = imgPath = Constant.pathurl +
	new MyApplication().getCompanyID() + "/Product/100x100/";
		
		return imgPath;
	}
	
	public static void addNub(TextView tv_car) {
		
//		if (tv_car.getText().toString().equals("") || tv_car.getText().toString() == null) {
//			
//			count = 0;
//		}else {
//			
//			count = Integer.parseInt(tv_car.getText().toString());
//		}
		nub = Integer.parseInt(tv_car.getText().toString()) + 1;
		tv_car.setVisibility(View.VISIBLE);
		tv_car.setText(nub + "");
		
	}

	public static void deletNub(TextView tv_car) {
									
		nub = Integer.parseInt(tv_car.getText().toString()) - 1;
		if (nub != 0) {
			tv_car.setText(nub + "");
			
		}else {
			nub = 0;
			tv_car.setText(nub + "");
			tv_car.setVisibility(View.GONE);
		}											
	}
	
	/**
	 * 获取购物车数据库商品总数的方法
	 */
	public static void getCarCounts(Context context, TextView tv){
		List<CartModel> data2 = CartManager.getInstance(context).getCartModels();

		tv.setText(data2.size() + "");	
		if (data2.size() > 0) {
			tv.setVisibility(View.VISIBLE);
		}else {
			tv.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 获取收藏数据库商品总数的方法
	 */
	public static void getShouCangCounts(Context context, TextView tv){
		List<ProductFavs> data2 = ProductFavManager.getInstance(context).getProductFavs();

		tv.setText(data2.size() + "");	
		if (data2.size() > 0) {
			tv.setVisibility(View.VISIBLE);
		}else {
			tv.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
     * 获取图片缩略图的方法
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        // 获取这个图片的宽和高，注意此处的bitmap为null  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // 设为 false  
        // 计算缩放比  
        int h = options.outHeight;  
        int w = options.outWidth;  
        int beWidth = w / width;  
        int beHeight = h / height;  
        int be = 1;  
        if (beWidth < beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 0) {  
            be = 1;  
        }  
        options.inSampleSize = be;  
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
	
    
//	public static MySql mySql;
//	public static SQLiteDatabase db;
//	public static void getCarCounts(Context context, TextView tv){
//		mySql = new MySql(context, "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
//		//根据数据库辅助类得到数据库。
//		db = mySql.getWritableDatabase();		
//		
//		//每次都获取购物车数据库的总条数
//		Cursor cursor = db.rawQuery("select count(*)from shopping_cart", null);
//		//游标移到第一条记录准备获取数据  
//		cursor.moveToFirst();
//		// 获取数据中的int类型数据  
//		int count = cursor.getInt(0);
//		tv.setText(count + "");	
//		if (count > 0) {
//			tv.setVisibility(View.VISIBLE);
//		}else {
//			tv.setVisibility(View.INVISIBLE);
//		}
//	}
	
	
	/**
	 * 把bitmap对象转换为byte数组，将byte数组放入contentvalues，将contentvalues里面的值存入sqlite
	 * @param icon
	 * @return
	 */
	public static byte[] saveIcon(Bitmap icon) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		icon.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] result = baos.toByteArray();
		     		          
		return result;
	} 
	
	
	/**
	 * 把这次刷新时间存入
	 * @param content
	 */
	public static void postTime(String content){
		SharedPreferences preferences = MyApplication.preferences; 
		Editor editor = preferences.edit();
		String time = content;
		editor.putString("content", time);
		editor.commit();
	}
	
	/**
	 * 获取存入的时间
	 * @param content
	 */
	public static String getTime(){
		SharedPreferences preferences = MyApplication.preferences; 				
		String time = preferences.getString("content", "");
		
		return time;  
	}
	
}
