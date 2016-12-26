package com.huishangyun.Office.Summary;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJResponse;

/**
 * 日报listview数据保存
 * @author xsl
 *
 */
public class ListSharedPrefence {
	/**
	 * 保存、修改数据
	 * @param context 上下文对象
	 * @param FileName 文件名称
	 * @param map 传入map
	 * @return 保存成功返回true，失败false
	 */
	public static boolean saveData(Context context,String FileName,String str){
		boolean flag = false;
		SharedPreferences  sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = sp.edit();
		    editor.putString("ListData", str);
		    flag = editor.commit();
		  return flag;
		
	}
  
	
	/**
	 * 获取数据
	 * @return 返回一个list
	 */
	public static List<SummaryDateList> getData(Context context,String FileName){
		List<SummaryDateList> list = new ArrayList<SummaryDateList>();
		SharedPreferences sp = context.getSharedPreferences(FileName, 
				context.MODE_PRIVATE);
		String str = sp.getString("ListData", "");
		if (!str.equals("null") && str != null && !str.equals("")) {
			//得到一个json字符串，然后通过解析方法，将值装入集合，再返回
			// 获取对象的Type
			Type type = new TypeToken<ZJResponse<SummaryDateList>>() {
			}.getType();
			ZJResponse<SummaryDateList> zjResponse = JsonUtil.fromJson(str,
					type);
			// 获取对象列表
			list = zjResponse.getResults();
		}
		return list;
		
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
