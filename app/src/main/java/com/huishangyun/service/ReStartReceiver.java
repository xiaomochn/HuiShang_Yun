package com.huishangyun.service;

import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机广播接收器
 * @author Pan
 *
 */
public class ReStartReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//判断有无openfire登录名和密码
		if (!MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, "").equals("") &&  
				!MyApplication.preferences.getString(Constant.XMPP_LOGIN_PASSWORD, "").equals("")) {
			context.startService(new Intent(context, HSChatService.class));
			context.startService(new Intent(context, PhoneService.class));
			if (MyApplication.preferences.getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
				Intent locationIntent = new Intent(context, LocationService.class);
				context.startService(locationIntent);
			}
			//return super.onStartCommand(intent, flags, startId);
		}
		
	}

}
