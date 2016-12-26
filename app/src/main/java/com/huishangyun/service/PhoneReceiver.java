package com.huishangyun.service;

import com.huishangyun.Util.L;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

public class PhoneReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {//打电话
			String phoneNumber = intent 
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 
			WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;    
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;    
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; 
            
			L.e("拨出号码为:" + phoneNumber);
		} else { //接电话
		}
	}
	
	

}
