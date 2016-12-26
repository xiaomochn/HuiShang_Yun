package com.huishangyun.Office.Businesstrip;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 出差Ui数据存储
 * @author xsl
 *
 */
public class BusinessSharedPrefreces {

	
	/**
	 * 保存、修改数据
	 * @param context 上下文对象
	 * @param FileName 文件名称
	 * @param map 传入map
	 * @return 保存成功返回true，失败false
	 */
	public static boolean saveData(Context context,String FileName,HashMap<String, Object> map){
		boolean flag = false;
		SharedPreferences  sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = sp.edit();
		    editor.putString("isSubmit", map.get("isSubmit")+"");
		    editor.putString("SetOutAddress", map.get("SetOutAddress")+"");
		    editor.putString("ArriveAddress", map.get("ArriveAddress")+"");
		    editor.putString("SetOutDate", map.get("SetOutDate")+"");
		    editor.putString("ArriveDate", map.get("ArriveDate")+"");
		    editor.putString("note", map.get("note")+"");
		    editor.putString("PhotoPath", map.get("PhotoPath")+"");
		    editor.putString("PhotoUrl", map.get("PhotoUrl")+"");
		    flag = editor.commit();
		  return flag;
		
	}
  
	/**
	 * 获取数据
	 * @return 返回一个map
	 */
	public static HashMap<String, Object> getData(Context context,String FileName){
		HashMap<String, Object> map = new HashMap<String,Object>();
		SharedPreferences sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		map.put("isSubmit", sp.getString("isSubmit", ""));
		map.put("SetOutAddress", sp.getString("SetOutAddress", ""));
		map.put("ArriveAddress", sp.getString("ArriveAddress", ""));
		map.put("SetOutDate", sp.getString("SetOutDate", ""));
		map.put("ArriveDate", sp.getString("ArriveDate", ""));
		map.put("note", sp.getString("note", ""));
		map.put("PhotoPath", sp.getString("PhotoPath", ""));
		map.put("PhotoUrl", sp.getString("PhotoUrl", ""));
		return map;
		
	}
	

	
	/**
	 * 清除数据
	 * @param context 上下文对象
	 * @param FileName 文件名称
	 * @return 操作成功返回true，失败返回false。
	 */
	public static boolean ClearData(Context context,String FileName){
		boolean flag = false;
		SharedPreferences sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		flag = editor.commit();
		return flag;
		
	}
}
