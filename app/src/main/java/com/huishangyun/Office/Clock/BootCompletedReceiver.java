package com.huishangyun.Office.Clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huishangyun.App.MyApplication;

/**
 * 开机启动广播类，启动后重新设置一次闹钟
 * @author xsl
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String Manager_ID = MyApplication.getInstance().getManagerID()+"";
//		Toast.makeText(context, Manager_ID , Toast.LENGTH_LONG).show();
		//避免无登入人时闹钟出错
		if (!Manager_ID.equals("") && Manager_ID != null) {
			new ReSetAlram(context, Manager_ID, 1, 1).setAlarm();
			new ReSetAlram(context, Manager_ID, 2, 2).setAlarm();
		}
		
	}
	
	
}
