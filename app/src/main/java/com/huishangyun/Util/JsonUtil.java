package com.huishangyun.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * 统一的JSON与对象互转帮组类
 * @author Pan
 * @version 亿企云 V1.0 2014-08-15
 *
 */
public class JsonUtil {
	
	/**
	 * 将构造方法私有化，不允许实例化该类
	 */
	private JsonUtil() {
		
	}
	
	/**
	 * 将对象转为JSON
	 * @param obj 需要转换成JSON的类
	 * @return JSON数据
	 */
	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	/**
	 * 将JSON字符串转成对象
	 * @see {ZJResponse zjResponse = JsonUtil.fromJson(jsonStr, ZJResponse.class);}
	 * @param str-要转换的JSON字符串
	 * @param type-要转换的对象类型
	 * @return 对象
	 */
	public static <T> T fromJson(String str, Type type) {
		Gson gson = new Gson();  
        return gson.fromJson(str, type);  
	}

//	public static<T> T fromJsons(String str, Class<T> type) {
//		Gson gson = new Gson();
//		return gson.fromJson(str, type);
//	}

	/**
	 * 将JSON数组转换成对象集合
	 * @param zjResponse-返回结果集
	 * @param type-要转换的对象Class
	 * @
	 * @return 对象集合
	 */
	/*@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonArray(ZJResponse zjResponse, Type type) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < zjResponse.getResults().size(); i++) {
			L.d("getResults = " + zjResponse.getResults().get(i).toString());
			list.add((T) fromJson(zjResponse.getResults().get(i).toString(), type));
		}
		return list;
	}*/
}
