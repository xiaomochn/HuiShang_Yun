package com.huishangyun.Office.Summary;
import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 通用数据存储工具类
 * @author xsl
 *
 */
public class SharedPrefenceTools {
	
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
		    editor.putString("TodayWorks", map.get("TodayWorks")+"");
		    editor.putString("experience", map.get("experience")+"");
		    editor.putString("TomorrowPlans", map.get("TomorrowPlans")+"");
		    editor.putString("IsReport", map.get("IsReport")+"");
		    editor.putString("ReportDate", map.get("ReportDate")+"");
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
		map.put("TodayWorks", sp.getString("TodayWorks", ""));
		map.put("experience", sp.getString("experience", ""));
		map.put("TomorrowPlans", sp.getString("TomorrowPlans", ""));
		map.put("IsReport", sp.getString("IsReport", ""));
		map.put("ReportDate", sp.getString("ReportDate", ""));
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
