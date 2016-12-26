package com.huishangyun.common;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.util.Log;
import android.widget.ListView;

public class ChangXianService {

	public static boolean save(String ActionType, String CompanyID, String Template_ID, String Member_ID, String Created_ID,
			String tiaoma,String idchang, String chang, String idxian, String xian,String time, String Albums) throws Exception{

		String path = "http://newws.huishangyun.com/MicroWeb/MicroWeb.asmx?op=Delivery";
						
		HashMap<String, String> item = new HashMap<String, String>();
		
		item.put("ActionType", ActionType);//这里的键要和服务端的一样
		item.put("CompanyID", CompanyID);
		item.put("Template_ID", Template_ID);
		item.put("Member_ID", Template_ID);
		item.put("Created_ID", Template_ID);
		
		item.put("value1", tiaoma);
		item.put("value2", idchang);		
		item.put("value3", chang);
		item.put("value4", idxian);
		item.put("value5", xian);
		item.put("value6", time);
		item.put("Albums", Albums);
		
		//return sendGETRequest(path,item);
		return sendPOSTRequest(path,item);
	}

	//用post方法发送数据。username=kit&password=123形式
	public static boolean sendPOSTRequest(String path,
			HashMap<String, String> item) throws Exception{
		
		StringBuffer buffer = new StringBuffer(); 
		
		//这里要判断非空。
		if (item != null && !item.isEmpty()) {
			for (Map.Entry<String, String> entry : item.entrySet()) {
				buffer.append(entry.getKey()).append("=");
				buffer.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				buffer.append("&");
			}
			buffer.deleteCharAt(buffer.length()-1);
		}
		Log.i("---------------------", "打印path:"+buffer.toString());		
		//生成实体数据，string类型要换成byte类型用这种方式。
		byte[] data = buffer.toString().getBytes();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		
		conn.setDoOutput(true);//允许被访问
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//匹配头
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		
		OutputStream oStream = conn.getOutputStream();//相对客户端是输出数据所以用输出流。
		oStream.write(data);
		
		if (conn.getResponseCode()==200) {
			return true;
		}	
		return false;
	}


	//使用get方法,这样和上面的一个方法进行分工合作，使得这个方法就可以复用了
	public static boolean sendGETRequest(String path,
			HashMap<String, String> item) throws Exception{
	//?id=1022&type=1&where=&nub=10&field=ID
		
		StringBuilder builder = new StringBuilder(path);
		builder.append("?");
		//迭代map元素集合entrySet()，前面是map元素对象
		for (Map.Entry<String, String> entry : item.entrySet()) {
			//entry.getKey()添加键的名称，这里键和服务端是一样的。
			builder.append(entry.getKey()).append("=");
			
			//URLEncoder.encode()方法转换编码格式
			builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			builder.append("&");
		}
		builder.deleteCharAt(builder.length()-1);
		
		//这里传入拼接好的字符串
		URL url = new URL(builder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		
		if (conn.getResponseCode()==200) {
						
			return true;
//			if (data.equals("插入成功")) {
//				
//			}			
		}
		return false;
	}

	
//	public static boolean get(String path) throws Exception{
//				
//		URL url = new URL(path);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setConnectTimeout(5000);
//		conn.setRequestMethod("GET");
//		
//		if (conn.getResponseCode()==200) {						
//			return true;
//		}
//		return false;
//		
//	}
	
}
