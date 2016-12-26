package com.huishangyun.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class SaomiaoImpl {
	
	// 解析工厂生产线。
	public static List<Map<String, Object>> parserjson(String json) throws Exception {

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("Msg");

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject object = jsonArray.getJSONObject(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", object.getInt("ID"));			
			map.put("Name", object.getString("Name"));
						
			Log.i("-------------------",
					"工厂 线的ID：" + object.getString("ID"));
			Log.i("-------------------",
					"工厂 线的Name：" + object.getString("Name"));
			data.add(map);
		}		
		return data;
	}

	// 解析上传
	public static int submitParserjson(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		int result = jsonObject.getInt("Status");		
		Log.i("-------------------","得到上传的结果：" + result);
	
		return result;
	}
	
	// 解析装机计划提交结果
	public static int submit2Parserjson(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		int result = jsonObject.getInt("status");		
		Log.i("-------------------","得到上传的结果：" + result);
	
		return result;
	}
	
	//解析上月库存
	public static Map<String, Object> parserkucun(String json)throws Exception{
		JSONObject jsonObject = new JSONObject(json); 
		JSONArray jsonArray = jsonObject.getJSONArray("Msg");

		Map<String, Object> map = new HashMap<String, Object>();
		if (!jsonArray.equals("")) {			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				
				map.put("Value5", object.getString("Value5"));
				map.put("ID", object.getInt("ID"));
			}
		}		
		
		return map;		
	}
		
	// 客户名称Json数据解析
	public static List<Map<String, Object>> parserjsonCustomer(String json) throws Exception {

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("Msg");

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject object = jsonArray.getJSONObject(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", object.getInt("ID"));
			map.put("DName", object.getString("RealName"));
			
			Log.i("-------------------",
					"客户名称的ID：" + object.getInt("ID"));
			Log.i("-------------------",
					"客户名称的DName：" + object.getString("RealName"));
			data.add(map);
		}

		return data;
	}
	
	
	//产品型号Json数据解析<br>
	public static List<Map<String, Object>> parserjsonTypes(String json) throws Exception {

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("Msg");

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject object = jsonArray.getJSONObject(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", object.getInt("ID"));
			map.put("Name", object.getString("Name"));
			
			Log.i("-------------------",
					"产品型号的ID：" + object.getInt("ID"));
			Log.i("-------------------",
					"产品型号的Name：" + object.getString("Name"));
			data.add(map);
		}		

		return data;
	}
	
	//出库扫描查询的解析
		public static Map<String, Object> parserjsonQuery1(String json) throws Exception {

			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("Msg");
			
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject object = jsonArray.getJSONObject(i);
				
				map.put("ID", object.getInt("ID"));
				map.put("Value1", object.getString("Value1"));
				
			}		

			return map;
		}
		
	
	//信息维护查询的解析
	public static Map<String, Object> parserjsonQuery(String json) throws Exception {

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("Msg");
		
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject object = jsonArray.getJSONObject(i);
			
			map.put("ID", object.getInt("ID"));
			map.put("Manager_Name", object.getString("Manager_Name"));
			map.put("Value1", object.getInt("Value1"));
			map.put("Value2", object.getString("Value2"));
			map.put("Value3", object.getInt("Value3"));
			map.put("Value4", object.getString("Value4"));
			map.put("Value5", object.getString("Value5"));
			map.put("Value6", object.getString("Value6"));
			map.put("Value7", object.getString("Value7"));
			map.put("Value8", object.getString("Value8"));
			map.put("Value9", object.getString("Value9"));
			map.put("Value10", object.getString("Value10"));
			Log.i("-------------------",
					"查询登录人名称：" + object.getString("Manager_Name"));
			Log.i("-------------------",
					"查询客户名称：" + object.getString("Value2"));
			Log.i("-------------------",
					"查询产品型号：" + object.getString("Value4"));
			Log.i("-------------------",
					"查询日期：" + object.getString("Value5"));
			//data.add(map);
		}		

		return map;
	}
	
	//解析信息维护未提交内容的查询
	public static List<Map<String, String>> parserSubmitnot(String json) throws Exception{
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		JSONObject jsonObject = new JSONObject(json);
		Log.i("------------", "status:"+jsonObject.getString("status"));
		if (jsonObject.getString("status").equals("0")) {
			Log.i("------------", "进入判断");
			JSONArray jsonArray = jsonObject.getJSONArray("Msg");
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject object = jsonArray.getJSONObject(i);
				String riqi =  object.getString("Rq").substring(0, 10);
				map.put("time", riqi);
				map.put("week", object.getString("Xq"));
				data.add(map);
			}
			return data;
		}
		Log.i("------------", "进入return null");
		return null;
	}
}
