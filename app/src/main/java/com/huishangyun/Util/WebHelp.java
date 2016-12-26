package com.huishangyun.Util;

import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

import android.content.Context;
import android.util.Log;

public class WebHelp {
	public static String callWebservice(Context context) {
		String url= MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_WEBDOMAIN, "http://webapp.huishangyun.com")+"/Adminlogin.aspx?";
		String httpurl = url
				+ "d=" + MyApplication.preferences.getInt(Content.COMPS_ID, 0)
				+ "&n=" + MyApplication.preferences.getString(Constant.USERNAME, "")
				+ "&p=" + MyApplication.preferences.getString(Constant.PASSWORD, "") 
				+ "&u=";

		Log.e("TAGS","httpurl代办消息="+httpurl);
		return httpurl;
	}
}
