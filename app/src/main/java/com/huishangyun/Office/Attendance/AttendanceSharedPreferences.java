package com.huishangyun.Office.Attendance;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

public class AttendanceSharedPreferences {
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
		    editor.putString("Administration", map.get("Administration")+"");
		    editor.putString("ScheduleID", map.get("ScheduleID")+"");
		    editor.putString("PhotoPath", map.get("PhotoPath")+"");
		    editor.putString("PhotoUrl", map.get("PhotoUrl")+"");
		    editor.putString("Note", map.get("Note")+"");
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
		map.put("Administration", sp.getString("Administration", ""));
		map.put("ScheduleID", sp.getString("ScheduleID", ""));
		map.put("PhotoPath", sp.getString("PhotoPath", ""));
		map.put("PhotoUrl", sp.getString("PhotoUrl", ""));
		map.put("Note", sp.getString("Note", ""));
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
	
	/**
	 * 存储当前手机分辨率
	 * @param context
	 * @param FileName
	 * @param HighRatio
	 * @param WidthRatio
	 * @return
	 */
	public static boolean savePhoneRatio(Context context,String FileName,int HighRatio,int WidthRatio){
		boolean flag = false;
		SharedPreferences  sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = sp.edit();
		    editor.putInt("HighRatio", HighRatio);
		    editor.putInt("WidthRatio", WidthRatio);
		    flag = editor.commit();
		  return flag;
	}
	
	/**
	 * 获取数据
	 * @return 返回一个map
	 */
	public static HashMap<String, Integer> getPhoneRatio(Context context,String FileName){
		HashMap<String, Integer> map = new HashMap<String,Integer>();
		SharedPreferences sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		map.put("HighRatio", sp.getInt("HighRatio", 0));
		map.put("WidthRatio", sp.getInt("WidthRatio", 0));
		return map;
	}
}
