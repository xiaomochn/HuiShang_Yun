package com.huishangyun.common;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetHttp {

	public static InputStream getStream(String path) throws Exception {
		
		InputStream is = null;
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			
			is = conn.getInputStream();
		}

		return is;
	}
}
