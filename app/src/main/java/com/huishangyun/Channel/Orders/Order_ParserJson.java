package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Order_ParserJson {
	
	/**
	 * 解析收藏列表
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> shoucang_list(String json) throws Exception{
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		
		JSONObject jsonObject = new JSONObject(json);
		
		JSONArray jsonArray = jsonObject.getJSONArray("Code");
		
		return data;
	}	
}
