package com.huishangyun.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetUtil {
	/**
	 * 无网络连接
	 */
	public static final int NETWORN_NONE = 0;
	/**
	 * //WIFI环境
	 */
	public static final int NETWORN_WIFI = 1;
	/**
	 * //3G环境
	 */
	public static final int NETWORN_MOBILE = 2;
	/**
	 * //网络不可用
	 */
	public static final int NETWORN_UNAVAILABLE = 3;
	/**
	 * 获取网络状态
	 * @param context
	 * @return
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			if (ping()) {
				return NETWORN_WIFI;
			}else {
				return NETWORN_UNAVAILABLE;
			}
			
		}

		// 3G
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			if (ping()) {
				return NETWORN_MOBILE;
			}else {
				return NETWORN_UNAVAILABLE;
			}
			
		}
		return NETWORN_NONE;
	}
	/**
	 * ping 百度首页
	 * @return
	 */
	private static final boolean ping() {

		String result = null;

		try {

		String ip = "www.baidu.com";

		Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//ping3次


		// 读取ping的内容，可不加。

		InputStream input = p.getInputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(input));

		StringBuffer stringBuffer = new StringBuffer();

		String content = "";

		while ((content = in.readLine()) != null) {

		stringBuffer.append(content);

		}

		L.i("TTT", "result content : " + stringBuffer.toString());


		// PING的状态

		int status = p.waitFor();

		if (status == 0) {

		result = "successful~";

		return true;

		} else {

		result = "failed~ cannot reach the IP address";

		}

		} catch (IOException e) {

		result = "failed~ IOException";

		} catch (InterruptedException e) {

		result = "failed~ InterruptedException";

		} finally {

		L.i("TTT", "result = " + result);

		}

		return false;

		}
}
