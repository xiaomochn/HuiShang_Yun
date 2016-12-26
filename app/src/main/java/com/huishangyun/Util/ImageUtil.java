package com.huishangyun.Util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {
	/**
	 * 获取网络address地址对应的图片
	 * 
	 * @param address
	 * @return bitmap的类型
	 * @author pan
	 */
	public static Bitmap getImage(String address) throws Exception {
		// 通过代码 模拟器浏览器访问图片的流程
		URL url = new URL("http://tws.huishangyun.com" + address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		// 获取服务器返回回来的流
		InputStream is = conn.getInputStream();
		if (is == null) {
			return null;
		}
		byte[] imagebytes = StreamTool.getBytes(is);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imagebytes, 0,
				imagebytes.length);
		return bitmap;
	}
}
